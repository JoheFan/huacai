package com.huacai.loan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huacai.common.exception.BusinessException;
import com.huacai.common.model.PageResponse;
import com.huacai.customer.entity.CustCustomer;
import com.huacai.customer.mapper.CustomerMapper;
import com.huacai.loan.dto.LoanCustomerSummarySaveRequest;
import com.huacai.loan.dto.LoanOrderSaveRequest;
import com.huacai.loan.dto.LoanRepaymentSaveRequest;
import com.huacai.loan.entity.LoanCustomerSummary;
import com.huacai.loan.entity.LoanOrder;
import com.huacai.loan.entity.LoanRepayment;
import com.huacai.loan.mapper.LoanCustomerSummaryMapper;
import com.huacai.loan.mapper.LoanOrderMapper;
import com.huacai.loan.mapper.LoanRepaymentMapper;
import com.huacai.loan.query.LoanOrderPageQuery;
import com.huacai.loan.query.LoanRepaymentPageQuery;
import com.huacai.loan.service.LoanManageService;
import com.huacai.loan.vo.LoanCustomerSummaryVO;
import com.huacai.loan.vo.LoanOrderOverviewVO;
import com.huacai.loan.vo.LoanOrderVO;
import com.huacai.loan.vo.LoanRepaymentVO;
import com.huacai.loan.vo.LoanRepaymentSummaryVO;
import com.huacai.security.AuthUser;
import com.huacai.security.CurrentUserProvider;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class LoanManageServiceImpl implements LoanManageService {

    private final LoanOrderMapper loanOrderMapper;
    private final LoanRepaymentMapper loanRepaymentMapper;
    private final LoanCustomerSummaryMapper loanCustomerSummaryMapper;
    private final CustomerMapper customerMapper;
    private final CurrentUserProvider currentUserProvider;

    public LoanManageServiceImpl(
            LoanOrderMapper loanOrderMapper,
            LoanRepaymentMapper loanRepaymentMapper,
            LoanCustomerSummaryMapper loanCustomerSummaryMapper,
            CustomerMapper customerMapper,
            CurrentUserProvider currentUserProvider
    ) {
        this.loanOrderMapper = loanOrderMapper;
        this.loanRepaymentMapper = loanRepaymentMapper;
        this.loanCustomerSummaryMapper = loanCustomerSummaryMapper;
        this.customerMapper = customerMapper;
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    public PageResponse<LoanOrderVO> pageOrders(LoanOrderPageQuery query) {
        Page<LoanOrder> page = new Page<>(normalizePageNum(query.getPageNum()), normalizePageSize(query.getPageSize()));
        LambdaQueryWrapper<LoanOrder> wrapper = buildLoanOrderWrapper(query);

        Page<LoanOrder> result = loanOrderMapper.selectPage(page, wrapper);
        Map<Long, CustCustomer> customerMap = loadCustomerMap(result.getRecords().stream()
                .map(LoanOrder::getCustomerId)
                .filter(Objects::nonNull)
                .toList());
        List<LoanOrderVO> records = result.getRecords().stream()
                .map(order -> toLoanOrderVO(order, customerMap.get(order.getCustomerId())))
                .toList();
        return new PageResponse<>(records, result.getTotal(), (int) result.getCurrent(), (int) result.getSize());
    }

    @Override
    public PageResponse<LoanOrderOverviewVO> pageOrderOverview(LoanOrderPageQuery query) {
        List<LoanOrder> matchedOrders = loanOrderMapper.selectList(buildLoanOrderWrapper(query));
        if (matchedOrders.isEmpty()) {
            return new PageResponse<>(List.of(), 0L, normalizePageNum(query.getPageNum()), normalizePageSize(query.getPageSize()));
        }

        Map<Long, CustCustomer> customerMap = loadCustomerMap(matchedOrders.stream()
                .map(LoanOrder::getCustomerId)
                .filter(Objects::nonNull)
                .toList());
        Map<Long, List<LoanRepayment>> repaymentsByOrderId = loadRepaymentsByOrderId(matchedOrders.stream()
                .map(LoanOrder::getId)
                .filter(Objects::nonNull)
                .toList());

        Map<LoanOverviewGroupKey, List<LoanOrder>> groupedOrders = new LinkedHashMap<>();
        for (LoanOrder order : matchedOrders) {
            LoanOverviewGroupKey key = new LoanOverviewGroupKey(order.getCustomerId(), order.getCapitalSourceType());
            groupedOrders.computeIfAbsent(key, ignored -> new ArrayList<>()).add(order);
        }

        // 加载持久化的客户汇总数据
        List<LoanOverviewGroupKey> groupKeys = new ArrayList<>(groupedOrders.keySet());
        List<LoanCustomerSummary> summaries = loadPersistedSummaries(groupKeys, query.getCapitalSourceType());

        List<LoanOrderOverviewVO> overviewRecords = groupedOrders.entrySet().stream()
                .map(entry -> toLoanOrderOverviewVO(
                        entry.getKey(),
                        entry.getValue(),
                        customerMap.get(entry.getKey().customerId()),
                        repaymentsByOrderId,
                        summaries.stream()
                                .filter(s -> Objects.equals(s.getCustomerId(), entry.getKey().customerId())
                                        && Objects.equals(s.getCapitalSourceType(), entry.getKey().capitalSourceType()))
                                .findFirst()
                                .orElse(null)
                ))
                .toList();

        int pageNum = normalizePageNum(query.getPageNum());
        int pageSize = normalizePageSize(query.getPageSize());
        int fromIndex = Math.min((pageNum - 1) * pageSize, overviewRecords.size());
        int toIndex = Math.min(fromIndex + pageSize, overviewRecords.size());

        return new PageResponse<>(
                overviewRecords.subList(fromIndex, toIndex),
                overviewRecords.size(),
                pageNum,
                pageSize
        );
    }

    @Override
    public LoanOrderVO orderDetail(Long id) {
        LoanOrder order = getLoanOrderOrThrow(id);
        validateCapitalSourceAccess(order.getCapitalSourceType());
        CustCustomer customer = getCustomerOrThrow(order.getCustomerId());
        return toLoanOrderVO(order, customer);
    }

    @Override
    @Transactional
    public void createOrder(LoanOrderSaveRequest request) {
        CustCustomer customer = getCustomerOrThrow(request.customerId());
        LoanOrder order = new LoanOrder();
        fillLoanOrderForCreate(order, request);
        order.setCreatedBy(currentUserProvider.getCurrentUserId());
        order.setUpdatedBy(currentUserProvider.getCurrentUserId());
        loanOrderMapper.insert(order);
        // 创建后统一走回算逻辑，确保余额和状态一致
        refreshLoanOrderSummary(order.getId());
        refreshCustomerLoanStatus(customer.getId());
    }

    @Override
    @Transactional
    public void updateOrder(Long id, LoanOrderSaveRequest request) {
        LoanOrder order = getLoanOrderOrThrow(id);
        Long originalCustomerId = order.getCustomerId();
        getCustomerOrThrow(request.customerId());
        fillLoanOrder(order, request);
        order.setUpdatedBy(currentUserProvider.getCurrentUserId());
        loanOrderMapper.updateById(order);
        refreshLoanOrderSummary(order.getId());
        refreshCustomerLoanStatus(originalCustomerId);
        if (!Objects.equals(originalCustomerId, order.getCustomerId())) {
            refreshCustomerLoanStatus(order.getCustomerId());
        }
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        LoanOrder order = getLoanOrderOrThrow(id);
        Long customerId = order.getCustomerId();
        // 逻辑删除借贷单
        loanOrderMapper.deleteById(id);
        // 删除关联的还款记录
        loanRepaymentMapper.delete(new LambdaQueryWrapper<LoanRepayment>()
                .eq(LoanRepayment::getLoanOrderId, id));
        // 刷新客户借贷状态
        refreshCustomerLoanStatus(customerId);
    }

    @Override
    public PageResponse<LoanRepaymentVO> pageRepayments(LoanRepaymentPageQuery query) {
        Page<LoanRepayment> page = new Page<>(normalizePageNum(query.getPageNum()), normalizePageSize(query.getPageSize()));
        LambdaQueryWrapper<LoanRepayment> wrapper = buildLoanRepaymentWrapper(query);

        Page<LoanRepayment> result = loanRepaymentMapper.selectPage(page, wrapper);
        Map<Long, CustCustomer> customerMap = loadCustomerMap(result.getRecords().stream()
                .map(LoanRepayment::getCustomerId)
                .filter(Objects::nonNull)
                .toList());
        List<LoanRepaymentVO> records = result.getRecords().stream()
                .map(repayment -> toLoanRepaymentVO(repayment, customerMap.get(repayment.getCustomerId())))
                .toList();
        return new PageResponse<>(records, result.getTotal(), (int) result.getCurrent(), (int) result.getSize());
    }

    @Override
    public LoanRepaymentSummaryVO getRepaymentSummary(LoanRepaymentPageQuery query) {
        List<LoanRepayment> repayments = loanRepaymentMapper.selectList(buildLoanRepaymentWrapper(query));
        BigDecimal repaymentAmountTotal = repayments.stream()
                .map(LoanRepayment::getRepaymentAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal principalAmountTotal = repayments.stream()
                .map(LoanRepayment::getPrincipalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal interestAmountTotal = repayments.stream()
                .map(LoanRepayment::getInterestAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Long customerId = query.getCustomerId();
        Long loanOrderId = query.getLoanOrderId();
        String capitalSourceType = query.getCapitalSourceType();
        String customerName = "";

        if (loanOrderId != null) {
            LoanOrder order = loanOrderMapper.selectById(loanOrderId);
            if (order != null) {
                customerId = order.getCustomerId();
                if (!StringUtils.hasText(capitalSourceType)) {
                    capitalSourceType = order.getCapitalSourceType();
                }
            }
        }
        if (customerId != null) {
            CustCustomer customer = customerMapper.selectById(customerId);
            if (customer != null) {
                customerName = customer.getCustomerName();
            }
        }
        if (!StringUtils.hasText(capitalSourceType) && !repayments.isEmpty()) {
            capitalSourceType = repayments.get(0).getCapitalSourceType();
        }

        return new LoanRepaymentSummaryVO(
                customerId,
                customerName,
                loanOrderId,
                capitalSourceType,
                repayments.size(),
                repaymentAmountTotal,
                principalAmountTotal,
                interestAmountTotal
        );
    }

    @Override
    public LoanRepaymentVO repaymentDetail(Long pathLoanOrderId, Long id) {
        LoanRepayment repayment = getLoanRepaymentOrThrow(id);
        validateRepaymentOwnership(pathLoanOrderId, repayment.getLoanOrderId());
        CustCustomer customer = getCustomerOrThrow(repayment.getCustomerId());
        return toLoanRepaymentVO(repayment, customer);
    }

    @Override
    @Transactional
    public void createRepayment(Long pathLoanOrderId, LoanRepaymentSaveRequest request) {
        Long effectiveLoanOrderId = resolveAndValidateLoanOrderId(pathLoanOrderId, request.loanOrderId());
        LoanOrder order = getLoanOrderOrThrow(effectiveLoanOrderId);
        LoanRepayment repayment = new LoanRepayment();
        fillLoanRepayment(repayment, request, order);
        repayment.setCreatedBy(currentUserProvider.getCurrentUserId());
        repayment.setUpdatedBy(currentUserProvider.getCurrentUserId());
        loanRepaymentMapper.insert(repayment);
        refreshLoanOrderSummary(order.getId());
    }

    @Override
    @Transactional
    public void updateRepayment(Long pathLoanOrderId, Long id, LoanRepaymentSaveRequest request) {
        LoanRepayment repayment = getLoanRepaymentOrThrow(id);
        Long oldLoanOrderId = repayment.getLoanOrderId();
        // 更新时必须校验原记录归属与 path 一致
        if (pathLoanOrderId != null && !Objects.equals(pathLoanOrderId, oldLoanOrderId)) {
            throw new BusinessException("还款记录不属于该借贷单");
        }
        Long effectiveLoanOrderId = resolveAndValidateLoanOrderIdForUpdate(pathLoanOrderId, request.loanOrderId(), oldLoanOrderId);
        LoanOrder order = getLoanOrderOrThrow(effectiveLoanOrderId);
        fillLoanRepayment(repayment, request, order);
        repayment.setUpdatedBy(currentUserProvider.getCurrentUserId());
        loanRepaymentMapper.updateById(repayment);
        refreshLoanOrderSummary(oldLoanOrderId);
        if (!Objects.equals(oldLoanOrderId, order.getId())) {
            refreshLoanOrderSummary(order.getId());
        }
    }

    @Override
    @Transactional
    public void deleteRepayment(Long pathLoanOrderId, Long id) {
        LoanRepayment repayment = getLoanRepaymentOrThrow(id);
        Long loanOrderId = repayment.getLoanOrderId();
        // 校验归属
        if (pathLoanOrderId != null && !Objects.equals(pathLoanOrderId, loanOrderId)) {
            throw new BusinessException("还款记录不属于该借贷单");
        }
        // 逻辑删除还款记录
        loanRepaymentMapper.deleteById(id);
        // 刷新借贷单汇总
        refreshLoanOrderSummary(loanOrderId);
    }

    // ===================== 客户汇总台账 =====================

    @Override
    public LoanCustomerSummaryVO getCustomerSummary(Long customerId, String capitalSourceType) {
        LambdaQueryWrapper<LoanCustomerSummary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LoanCustomerSummary::getCustomerId, customerId)
                .eq(LoanCustomerSummary::getCapitalSourceType, capitalSourceType);
        LoanCustomerSummary summary = loanCustomerSummaryMapper.selectOne(wrapper);
        if (summary == null) {
            return null;
        }
        CustCustomer customer = customerMapper.selectById(customerId);
        return toLoanCustomerSummaryVO(summary, customer);
    }

    @Override
    @Transactional
    public void saveCustomerSummary(LoanCustomerSummarySaveRequest request) {
        getCustomerOrThrow(request.customerId());
        LambdaQueryWrapper<LoanCustomerSummary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LoanCustomerSummary::getCustomerId, request.customerId())
                .eq(LoanCustomerSummary::getCapitalSourceType, request.capitalSourceType());
        LoanCustomerSummary existing = loanCustomerSummaryMapper.selectOne(wrapper);
        LoanCustomerSummary summary = new LoanCustomerSummary();
        summary.setCustomerId(request.customerId());
        summary.setCapitalSourceType(request.capitalSourceType());
        summary.setTotalIncrementAmount(request.totalIncrementAmount());
        summary.setIncrementCount(request.incrementCount());
        summary.setYearsTerm(request.yearsTerm());
        summary.setChannelRate(request.channelRate());
        summary.setChannelFee(request.channelFee());
        summary.setReferrer(request.referrer());
        summary.setSelfTotalLoanAmount(request.selfTotalLoanAmount());
        summary.setBankTotalLoanAmount(request.bankTotalLoanAmount());
        summary.setRemark(request.remark());
        if (existing != null) {
            summary.setId(existing.getId());
            summary.setUpdatedBy(currentUserProvider.getCurrentUserId());
            loanCustomerSummaryMapper.updateById(summary);
        } else {
            summary.setCreatedBy(currentUserProvider.getCurrentUserId());
            summary.setUpdatedBy(currentUserProvider.getCurrentUserId());
            loanCustomerSummaryMapper.insert(summary);
        }
    }

    @Override
    @Transactional
    public void deleteCustomerSummary(Long customerId, String capitalSourceType) {
        LambdaQueryWrapper<LoanCustomerSummary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LoanCustomerSummary::getCustomerId, customerId)
                .eq(LoanCustomerSummary::getCapitalSourceType, capitalSourceType);
        loanCustomerSummaryMapper.delete(wrapper);
    }

    private LoanCustomerSummaryVO toLoanCustomerSummaryVO(LoanCustomerSummary summary, CustCustomer customer) {
        return new LoanCustomerSummaryVO(
                summary.getId(),
                summary.getCustomerId(),
                customer == null ? "" : customer.getCustomerNo(),
                customer == null ? "" : customer.getCustomerName(),
                customer == null ? "" : customer.getMobile(),
                customer == null ? "" : customer.getCompanyName(),
                summary.getCapitalSourceType(),
                summary.getTotalIncrementAmount(),
                summary.getIncrementCount(),
                summary.getYearsTerm(),
                summary.getChannelRate(),
                summary.getChannelFee(),
                summary.getReferrer(),
                summary.getSelfTotalLoanAmount(),
                summary.getBankTotalLoanAmount(),
                summary.getRemark()
        );
    }

    private void fillLoanOrder(LoanOrder order, LoanOrderSaveRequest request) {
        order.setCustomerId(request.customerId());
        order.setCapitalSourceType(StringUtils.hasText(request.capitalSourceType()) ? request.capitalSourceType() : "SELF");
        order.setBankName(request.bankName());
        order.setLoanDate(request.loanDate());
        order.setDepositGoldAmount(request.depositGoldAmount());
        order.setCreditCardRepaymentAmount(request.creditCardRepaymentAmount());
        order.setLoanAmount(defaultIfNull(request.loanAmount()));
        order.setBalanceAmount(request.balanceAmount() == null ? defaultIfNull(request.loanAmount()) : request.balanceAmount());
        order.setMonthlyInterestAmount(request.monthlyInterestAmount());
        order.setLoanCount(request.loanCount());
        order.setStatus(StringUtils.hasText(request.status()) ? request.status() : "ACTIVE");
        order.setRemark(request.remark());
    }

    /**
     * 创建借贷单时填充字段
     * 不信任前端传入的 balanceAmount 和 status，由后端回算逻辑统一计算
     */
    private void fillLoanOrderForCreate(LoanOrder order, LoanOrderSaveRequest request) {
        order.setCustomerId(request.customerId());
        order.setCapitalSourceType(StringUtils.hasText(request.capitalSourceType()) ? request.capitalSourceType() : "SELF");
        order.setBankName(request.bankName());
        order.setLoanDate(request.loanDate());
        order.setDepositGoldAmount(request.depositGoldAmount());
        order.setCreditCardRepaymentAmount(request.creditCardRepaymentAmount());
        order.setLoanAmount(defaultIfNull(request.loanAmount()));
        // 创建时初始余额等于借款金额，后续由回算逻辑统一更新
        order.setBalanceAmount(defaultIfNull(request.loanAmount()));
        order.setMonthlyInterestAmount(request.monthlyInterestAmount());
        order.setLoanCount(request.loanCount());
        // 创建时初始状态为 ACTIVE，后续由回算逻辑统一更新
        order.setStatus("ACTIVE");
        order.setRemark(request.remark());
    }

    private void fillLoanRepayment(LoanRepayment repayment, LoanRepaymentSaveRequest request, LoanOrder order) {
        repayment.setLoanOrderId(order.getId());
        repayment.setCustomerId(order.getCustomerId());
        repayment.setCapitalSourceType(order.getCapitalSourceType());
        repayment.setRepaymentDate(request.repaymentDate());
        repayment.setRepaymentAmount(request.repaymentAmount());
        repayment.setPrincipalAmount(defaultIfNull(request.principalAmount()));
        repayment.setInterestAmount(defaultIfNull(request.interestAmount()));
        repayment.setRepaymentChannel(request.repaymentChannel());
        repayment.setRemark(request.remark());
    }

    private void refreshLoanOrderSummary(Long loanOrderId) {
        LoanOrder order = loanOrderMapper.selectById(loanOrderId);
        if (order == null) {
            return;
        }
        List<LoanRepayment> repayments = loanRepaymentMapper.selectList(new LambdaQueryWrapper<LoanRepayment>()
                .eq(LoanRepayment::getLoanOrderId, loanOrderId));
        BigDecimal principalTotal = repayments.stream()
                .map(LoanRepayment::getPrincipalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal loanAmount = defaultIfNull(order.getLoanAmount());
        BigDecimal balance = loanAmount.subtract(principalTotal);
        if (balance.signum() < 0) {
            balance = BigDecimal.ZERO;
        }
        order.setBalanceAmount(balance);
        order.setStatus(balance.compareTo(BigDecimal.ZERO) == 0 && loanAmount.compareTo(BigDecimal.ZERO) > 0 ? "SETTLED" : "ACTIVE");
        order.setUpdatedBy(currentUserProvider.getCurrentUserId());
        loanOrderMapper.updateById(order);
        refreshCustomerLoanStatus(order.getCustomerId());
    }

    private void refreshCustomerLoanStatus(Long customerId) {
        if (customerId == null) {
            return;
        }
        CustCustomer customer = customerMapper.selectById(customerId);
        if (customer == null) {
            return;
        }
        Long total = loanOrderMapper.selectCount(new LambdaQueryWrapper<LoanOrder>()
                .eq(LoanOrder::getCustomerId, customerId));
        if (total == 0) {
            customer.setLoanStatus("NOT_STARTED");
        } else {
            Long running = loanOrderMapper.selectCount(new LambdaQueryWrapper<LoanOrder>()
                    .eq(LoanOrder::getCustomerId, customerId)
                    .ne(LoanOrder::getStatus, "SETTLED"));
            customer.setLoanStatus(running > 0 ? "RUNNING" : "SETTLED");
        }
        customer.setUpdatedBy(currentUserProvider.getCurrentUserId());
        customerMapper.updateById(customer);
    }

    private LambdaQueryWrapper<LoanOrder> buildLoanOrderWrapper(LoanOrderPageQuery query) {
        LambdaQueryWrapper<LoanOrder> wrapper = new LambdaQueryWrapper<LoanOrder>()
                .orderByDesc(LoanOrder::getLoanDate)
                .orderByDesc(LoanOrder::getCreatedAt);

        String effectiveCapitalSourceType = resolveEffectiveCapitalSourceType(query.getCapitalSourceType());
        if (effectiveCapitalSourceType != null) {
            wrapper.eq(LoanOrder::getCapitalSourceType, effectiveCapitalSourceType);
        }

        if (query.getCustomerId() != null) {
            wrapper.eq(LoanOrder::getCustomerId, query.getCustomerId());
        }
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.eq(LoanOrder::getStatus, query.getStatus());
        }
        applyKeywordToLoanOrderWrapper(wrapper, query.getKeyword());
        return wrapper;
    }

    private LambdaQueryWrapper<LoanRepayment> buildLoanRepaymentWrapper(LoanRepaymentPageQuery query) {
        LambdaQueryWrapper<LoanRepayment> wrapper = new LambdaQueryWrapper<LoanRepayment>()
                .orderByDesc(LoanRepayment::getRepaymentDate)
                .orderByDesc(LoanRepayment::getCreatedAt);

        String effectiveCapitalSourceType = resolveEffectiveCapitalSourceType(query.getCapitalSourceType());
        if (effectiveCapitalSourceType != null) {
            wrapper.eq(LoanRepayment::getCapitalSourceType, effectiveCapitalSourceType);
        }

        if (query.getLoanOrderId() != null) {
            wrapper.eq(LoanRepayment::getLoanOrderId, query.getLoanOrderId());
        }
        if (query.getCustomerId() != null) {
            wrapper.eq(LoanRepayment::getCustomerId, query.getCustomerId());
        }
        applyKeywordToLoanRepaymentWrapper(wrapper, query.getKeyword());
        return wrapper;
    }

    private void applyKeywordToLoanOrderWrapper(LambdaQueryWrapper<LoanOrder> wrapper, String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return;
        }
        Set<Long> customerIds = findCustomerIdsByKeyword(keyword);
        wrapper.and(w -> {
            boolean applied = false;
            if (!customerIds.isEmpty()) {
                w.in(LoanOrder::getCustomerId, customerIds);
                applied = true;
            }
            if (StringUtils.hasText(keyword)) {
                if (applied) {
                    w.or();
                }
                w.like(LoanOrder::getBankName, keyword.trim());
            }
        });
    }

    private void applyKeywordToLoanRepaymentWrapper(LambdaQueryWrapper<LoanRepayment> wrapper, String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return;
        }
        Set<Long> customerIds = findCustomerIdsByKeyword(keyword);
        if (customerIds.isEmpty()) {
            wrapper.like(LoanRepayment::getRepaymentChannel, keyword.trim());
            return;
        }
        wrapper.and(w -> w.in(LoanRepayment::getCustomerId, customerIds)
                .or()
                .like(LoanRepayment::getRepaymentChannel, keyword.trim()));
    }

    private Set<Long> findCustomerIdsByKeyword(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return Collections.emptySet();
        }
        String trimmed = keyword.trim();
        return customerMapper.selectList(new LambdaQueryWrapper<CustCustomer>()
                        .select(CustCustomer::getId)
                        .and(w -> w.like(CustCustomer::getCustomerName, trimmed)
                                .or()
                                .like(CustCustomer::getCustomerNo, trimmed)
                                .or()
                                .like(CustCustomer::getCompanyName, trimmed)
                                .or()
                                .like(CustCustomer::getMobile, trimmed)))
                .stream()
                .map(CustCustomer::getId)
                .collect(Collectors.toSet());
    }

    private Map<Long, CustCustomer> loadCustomerMap(Collection<Long> customerIds) {
        List<Long> ids = customerIds.stream().filter(Objects::nonNull).distinct().toList();
        if (ids.isEmpty()) {
            return Collections.emptyMap();
        }
        return customerMapper.selectBatchIds(ids).stream()
                .collect(Collectors.toMap(CustCustomer::getId, Function.identity()));
    }

    private Map<Long, List<LoanRepayment>> loadRepaymentsByOrderId(Collection<Long> loanOrderIds) {
        List<Long> ids = loanOrderIds.stream().filter(Objects::nonNull).distinct().toList();
        if (ids.isEmpty()) {
            return Collections.emptyMap();
        }
        return loanRepaymentMapper.selectList(new LambdaQueryWrapper<LoanRepayment>()
                        .in(LoanRepayment::getLoanOrderId, ids))
                .stream()
                .collect(Collectors.groupingBy(LoanRepayment::getLoanOrderId));
    }

    private List<LoanCustomerSummary> loadPersistedSummaries(List<LoanOverviewGroupKey> groupKeys, String capitalSourceType) {
        if (groupKeys.isEmpty()) {
            return List.of();
        }
        Set<Long> customerIds = groupKeys.stream()
                .map(LoanOverviewGroupKey::customerId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (customerIds.isEmpty()) {
            return List.of();
        }
        LambdaQueryWrapper<LoanCustomerSummary> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(LoanCustomerSummary::getCustomerId, customerIds);
        if (StringUtils.hasText(capitalSourceType)) {
            wrapper.eq(LoanCustomerSummary::getCapitalSourceType, capitalSourceType);
        }
        return loanCustomerSummaryMapper.selectList(wrapper);
    }

    private LoanOrder getLoanOrderOrThrow(Long id) {
        LoanOrder order = loanOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("借贷单不存在");
        }
        return order;
    }

    private LoanRepayment getLoanRepaymentOrThrow(Long id) {
        LoanRepayment repayment = loanRepaymentMapper.selectById(id);
        if (repayment == null) {
            throw new BusinessException("还款记录不存在");
        }
        return repayment;
    }

    private CustCustomer getCustomerOrThrow(Long customerId) {
        CustCustomer customer = customerMapper.selectById(customerId);
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }
        return customer;
    }

    private LoanOrderVO toLoanOrderVO(LoanOrder order, CustCustomer customer) {
        return new LoanOrderVO(
                order.getId(),
                order.getCustomerId(),
                customer == null ? "" : customer.getCustomerName(),
                customer == null ? "" : customer.getCustomerNo(),
                order.getCapitalSourceType(),
                order.getBankName(),
                order.getLoanDate(),
                order.getDepositGoldAmount(),
                order.getCreditCardRepaymentAmount(),
                order.getLoanAmount(),
                order.getBalanceAmount(),
                order.getMonthlyInterestAmount(),
                order.getLoanCount(),
                order.getStatus(),
                order.getRemark(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }

    private LoanOrderOverviewVO toLoanOrderOverviewVO(
            LoanOverviewGroupKey key,
            List<LoanOrder> orders,
            CustCustomer customer,
            Map<Long, List<LoanRepayment>> repaymentsByOrderId,
            LoanCustomerSummary summary
    ) {
        BigDecimal depositGoldAmount = orders.stream()
                .map(LoanOrder::getDepositGoldAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal creditCardRepaymentAmount = orders.stream()
                .map(LoanOrder::getCreditCardRepaymentAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal balanceAmount = orders.stream()
                .map(LoanOrder::getBalanceAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal monthlyInterestAmount = orders.stream()
                .map(LoanOrder::getMonthlyInterestAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalLoanAmount = orders.stream()
                .map(LoanOrder::getLoanAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int configuredLoanCount = orders.stream()
                .map(LoanOrder::getLoanCount)
                .filter(Objects::nonNull)
                .reduce(0, Integer::sum);
        int loanCount = configuredLoanCount > 0 ? configuredLoanCount : orders.size();
        LocalDate firstLoanDate = orders.stream()
                .map(LoanOrder::getLoanDate)
                .filter(Objects::nonNull)
                .min(LocalDate::compareTo)
                .orElse(null);
        BigDecimal repaidAmount = orders.stream()
                .flatMap(order -> repaymentsByOrderId.getOrDefault(order.getId(), List.of()).stream())
                .map(LoanRepayment::getRepaymentAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        String remark = orders.stream()
                .map(LoanOrder::getRemark)
                .filter(StringUtils::hasText)
                .findFirst()
                .orElse("");
        String status = orders.stream().allMatch(order -> Objects.equals(order.getStatus(), "SETTLED")) ? "SETTLED" : "ACTIVE";

        // 银行口径字段：优先使用持久化值
        BigDecimal totalIncrementAmount = summary != null ? summary.getTotalIncrementAmount() : null;
        Integer incrementCount = summary != null ? summary.getIncrementCount() : null;
        String yearsTerm = summary != null ? summary.getYearsTerm() : null;
        BigDecimal channelRate = summary != null ? summary.getChannelRate() : null;
        BigDecimal channelFee = summary != null ? summary.getChannelFee() : null;
        String referrer = summary != null ? summary.getReferrer() : null;
        BigDecimal selfTotalLoanAmount = summary != null ? summary.getSelfTotalLoanAmount() : null;
        BigDecimal bankTotalLoanAmount = summary != null ? summary.getBankTotalLoanAmount() : null;

        return new LoanOrderOverviewVO(
                key.customerId(),
                customer == null ? "" : customer.getCustomerNo(),
                customer == null ? "" : customer.getCustomerName(),
                customer == null ? "" : customer.getMobile(),
                customer == null ? "" : customer.getCompanyName(),
                key.capitalSourceType(),
                depositGoldAmount,
                creditCardRepaymentAmount,
                balanceAmount,
                loanCount,
                monthlyInterestAmount,
                totalLoanAmount,
                firstLoanDate,
                repaidAmount,
                balanceAmount,
                remark,
                status,
                totalIncrementAmount,
                incrementCount,
                yearsTerm,
                channelRate,
                channelFee,
                referrer,
                selfTotalLoanAmount,
                bankTotalLoanAmount
        );
    }

    private LoanRepaymentVO toLoanRepaymentVO(LoanRepayment repayment, CustCustomer customer) {
        return new LoanRepaymentVO(
                repayment.getId(),
                repayment.getLoanOrderId(),
                repayment.getCustomerId(),
                customer == null ? "" : customer.getCustomerName(),
                customer == null ? "" : customer.getCustomerNo(),
                repayment.getCapitalSourceType(),
                repayment.getRepaymentDate(),
                repayment.getRepaymentAmount(),
                repayment.getPrincipalAmount(),
                repayment.getInterestAmount(),
                repayment.getRepaymentChannel(),
                repayment.getRemark(),
                repayment.getCreatedAt(),
                repayment.getUpdatedAt()
        );
    }

    private Long resolveLoanOrderId(Long pathLoanOrderId, Long requestLoanOrderId) {
        if (pathLoanOrderId != null) {
            return pathLoanOrderId;
        }
        if (requestLoanOrderId != null) {
            return requestLoanOrderId;
        }
        throw new BusinessException("还款记录缺少借贷单ID");
    }

    private Long resolveLoanOrderId(Long pathLoanOrderId, Long requestLoanOrderId, Long fallbackLoanOrderId) {
        if (pathLoanOrderId != null) {
            return pathLoanOrderId;
        }
        if (requestLoanOrderId != null) {
            return requestLoanOrderId;
        }
        return fallbackLoanOrderId;
    }

    /**
     * 校验还款记录归属是否与路径参数一致
     */
    private void validateRepaymentOwnership(Long pathLoanOrderId, Long actualLoanOrderId) {
        if (pathLoanOrderId != null && !Objects.equals(pathLoanOrderId, actualLoanOrderId)) {
            throw new BusinessException("还款记录不属于该借贷单");
        }
    }

    /**
     * 新建还款时解析并校验 loanOrderId
     * - path 和 body 都没有：报错
     * - path 有，body 没有：用 path
     * - path 没有，body 有：用 body
     * - path 和 body 都有但不一致：报错
     */
    private Long resolveAndValidateLoanOrderId(Long pathLoanOrderId, Long requestLoanOrderId) {
        if (pathLoanOrderId == null && requestLoanOrderId == null) {
            throw new BusinessException("还款记录缺少借贷单ID");
        }
        if (pathLoanOrderId != null && requestLoanOrderId != null && !Objects.equals(pathLoanOrderId, requestLoanOrderId)) {
            throw new BusinessException("路径中的借贷单ID与请求体中的借贷单ID不一致");
        }
        return pathLoanOrderId != null ? pathLoanOrderId : requestLoanOrderId;
    }

    /**
     * 更新还款时解析并校验 loanOrderId
     * - 如果 body 中指定了新的 loanOrderId，需要校验与 path 一致（如果 path 有值）
     * - 如果 body 没有指定，保持原值
     */
    private Long resolveAndValidateLoanOrderIdForUpdate(Long pathLoanOrderId, Long requestLoanOrderId, Long originalLoanOrderId) {
        if (requestLoanOrderId == null) {
            // body 没指定，用原值，但需要与 path 一致
            if (pathLoanOrderId != null && !Objects.equals(pathLoanOrderId, originalLoanOrderId)) {
                throw new BusinessException("还款记录不属于该借贷单");
            }
            return originalLoanOrderId;
        }
        // body 指定了新值
        if (pathLoanOrderId != null && !Objects.equals(pathLoanOrderId, requestLoanOrderId)) {
            throw new BusinessException("路径中的借贷单ID与请求体中的借贷单ID不一致");
        }
        return requestLoanOrderId;
    }

    private int normalizePageNum(Integer pageNum) {
        return pageNum == null || pageNum < 1 ? 1 : pageNum;
    }

    /**
     * 按 capitalSourceType 做二次授权
     * 只有 SELF 权限的用户，请求 BANK 数据必须被拒绝
     */
    private void validateCapitalSourceAccess(String capitalSourceType) {
        if (!StringUtils.hasText(capitalSourceType)) {
            return;
        }
        String allowedType = resolveEffectiveCapitalSourceType(capitalSourceType);
        if (!capitalSourceType.equals(allowedType)) {
            throw new BusinessException("无权访问此类型的借贷数据");
        }
    }

    /**
     * 根据用户权限解析实际可用的 capitalSourceType
     * - 请求指定类型：用户有权限返回该类型，无权限返回 null
     * - 请求未指定类型：同时拥有 SELF+BANK 返回 null（不过滤），只有其中一个返回那个类型
     */
    private String resolveEffectiveCapitalSourceType(String requestedType) {
        AuthUser user = currentUserProvider.getCurrentUser().orElse(null);
        if (user == null || user.isSuperAdmin()) {
            return requestedType;
        }

        Map<String, String> dataScopes = user.getDataScopes();
        boolean hasSelfAccess = dataScopes != null && dataScopes.containsKey("loan-orders-self");
        boolean hasBankAccess = dataScopes != null && dataScopes.containsKey("loan-orders-bank");

        if (!StringUtils.hasText(requestedType)) {
            // 未指定类型
            if (hasSelfAccess && hasBankAccess) {
                return null; // 两者都有，不过滤
            }
            if (hasSelfAccess) {
                return "SELF";
            }
            if (hasBankAccess) {
                return "BANK";
            }
            return "SELF"; // 无配置默认 SELF
        }

        // 指定了类型
        if ("SELF".equals(requestedType)) {
            return hasSelfAccess ? requestedType : null;
        }
        if ("BANK".equals(requestedType)) {
            return hasBankAccess ? requestedType : null;
        }
        return null;
    }

    private int normalizePageSize(Integer pageSize) {
        return pageSize == null || pageSize < 1 ? 20 : Math.min(pageSize, 200);
    }

    private BigDecimal defaultIfNull(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private record LoanOverviewGroupKey(Long customerId, String capitalSourceType) {
    }
}
