package com.huacai.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huacai.common.exception.BusinessException;
import com.huacai.common.model.PageResponse;
import com.huacai.customer.dto.CustomerArchiveSaveRequest;
import com.huacai.customer.dto.CustomerContractSaveRequest;
import com.huacai.customer.dto.CustomerDebtSaveRequest;
import com.huacai.customer.dto.CustomerProfileFields;
import com.huacai.customer.dto.CustomerSaveRequest;
import com.huacai.customer.dto.CustomerScoreSaveRequest;
import com.huacai.customer.dto.CustomerTradeSaveRequest;
import com.huacai.customer.entity.CustCustomer;
import com.huacai.customer.entity.CustCustomerContract;
import com.huacai.customer.entity.CustCustomerDebt;
import com.huacai.customer.entity.CustCustomerScore;
import com.huacai.customer.entity.CustCustomerStatusLog;
import com.huacai.customer.entity.CustCustomerTrade;
import com.huacai.customer.mapper.CustomerContractMapper;
import com.huacai.customer.mapper.CustomerDebtMapper;
import com.huacai.customer.mapper.CustomerMapper;
import com.huacai.customer.mapper.CustomerScoreMapper;
import com.huacai.customer.mapper.CustomerStatusLogMapper;
import com.huacai.customer.mapper.CustomerTradeMapper;
import com.huacai.customer.query.CustomerDebtPageQuery;
import com.huacai.customer.query.CustomerPageQuery;
import com.huacai.customer.query.CustomerRiskPageQuery;
import com.huacai.customer.service.CustomerService;
import com.huacai.customer.vo.CustomerArchiveVO;
import com.huacai.customer.vo.CustomerAttachmentVO;
import com.huacai.customer.vo.CustomerContractVO;
import com.huacai.customer.vo.CustomerDebtVO;
import com.huacai.customer.vo.CustomerRiskVO;
import com.huacai.customer.vo.CustomerStatusLogVO;
import com.huacai.customer.vo.CustomerTradeVO;
import com.huacai.customer.vo.CustomerVO;
import com.huacai.file.entity.SysFile;
import com.huacai.file.mapper.FileMapper;
import com.huacai.security.CurrentUserProvider;
import com.huacai.system.entity.SysUser;
import com.huacai.system.mapper.UserMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class CustomerServiceImpl implements CustomerService {

    private static final DateTimeFormatter CUSTOMER_NO_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final String CUSTOMER_ARCHIVE_BIZ_TYPE = "CUSTOMER_ARCHIVE";
    private static final String CUSTOMER_CONTRACT_BIZ_TYPE = "CUSTOMER_CONTRACT";

    private final CustomerMapper customerMapper;
    private final CustomerScoreMapper customerScoreMapper;
    private final CustomerDebtMapper customerDebtMapper;
    private final CustomerContractMapper customerContractMapper;
    private final CustomerStatusLogMapper customerStatusLogMapper;
    private final CustomerTradeMapper customerTradeMapper;
    private final FileMapper fileMapper;
    private final UserMapper userMapper;
    private final CurrentUserProvider currentUserProvider;

    public CustomerServiceImpl(
            CustomerMapper customerMapper,
            CustomerScoreMapper customerScoreMapper,
            CustomerDebtMapper customerDebtMapper,
            CustomerContractMapper customerContractMapper,
            CustomerStatusLogMapper customerStatusLogMapper,
            CustomerTradeMapper customerTradeMapper,
            FileMapper fileMapper,
            UserMapper userMapper,
            CurrentUserProvider currentUserProvider
    ) {
        this.customerMapper = customerMapper;
        this.customerScoreMapper = customerScoreMapper;
        this.customerDebtMapper = customerDebtMapper;
        this.customerContractMapper = customerContractMapper;
        this.customerStatusLogMapper = customerStatusLogMapper;
        this.customerTradeMapper = customerTradeMapper;
        this.fileMapper = fileMapper;
        this.userMapper = userMapper;
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    public PageResponse<CustomerVO> page(CustomerPageQuery query) {
        Page<CustCustomer> page = new Page<>(normalizePageNum(query.getPageNum()), normalizePageSize(query.getPageSize()));
        LambdaQueryWrapper<CustCustomer> wrapper = new LambdaQueryWrapper<CustCustomer>()
                .orderByDesc(CustCustomer::getCreatedAt);
        applyCustomerKeyword(wrapper, query.getKeyword());
        if (StringUtils.hasText(query.getAuditStatus())) {
            wrapper.eq(CustCustomer::getAuditStatus, query.getAuditStatus());
        }
        if (StringUtils.hasText(query.getLoanStatus())) {
            wrapper.eq(CustCustomer::getLoanStatus, query.getLoanStatus());
        }
        currentUserProvider.getCurrentUser().ifPresent(authUser -> {
            if (!authUser.isSuperAdmin()) {
                wrapper.eq(CustCustomer::getCreatedBy, authUser.getUserId());
            }
        });
        Page<CustCustomer> result = customerMapper.selectPage(page, wrapper);
        Map<Long, CustCustomerScore> latestRiskMap = loadLatestRiskMap(result.getRecords().stream()
                .map(CustCustomer::getId)
                .toList());
        List<CustomerVO> records = result.getRecords().stream()
                .map(customer -> toVO(customer, latestRiskMap.get(customer.getId())))
                .toList();
        return new PageResponse<>(records, result.getTotal(), (int) result.getCurrent(), (int) result.getSize());
    }

    @Override
    public CustomerVO detail(Long id) {
        CustCustomer customer = getCustomerOrThrow(id);
        validateCustomerDataScope(customer);
        CustCustomerScore latestRisk = loadLatestRiskMap(List.of(id)).get(id);
        return toVO(customer, latestRisk);
    }

    private void validateCustomerDataScope(CustCustomer customer) {
        currentUserProvider.getCurrentUser().ifPresent(authUser -> {
            if (!authUser.isSuperAdmin() && !Objects.equals(customer.getCreatedBy(), authUser.getUserId())) {
                throw new BusinessException("无权访问此客户数据");
            }
        });
    }

    /**
     * 把风险/负债等子记录的查询限制在当前用户可见的客户范围内，
     * 与客户列表 {@link #page} 的口径保持一致（非超管仅能看到本人创建的客户）。
     * 缺少该过滤会导致风险评估、负债登记列表越权暴露全部客户的敏感数据。
     */
    private <T> void applyAccessibleCustomerScope(LambdaQueryWrapper<T> wrapper, SFunction<T, Long> customerIdColumn) {
        currentUserProvider.getCurrentUser().ifPresent(authUser -> {
            if (authUser.isSuperAdmin()) {
                return;
            }
            // 用子查询代替"先查全部可见客户 ID 再 IN(...)"：
            // 避免客户量大时把成百上千个 ID 拼成超长 IN 列表（可能触碰数据库参数上限并拖慢查询），
            // 交由数据库一次完成关联。authUser.getUserId() 来自鉴权上下文（可信 Long），无注入风险。
            wrapper.inSql(customerIdColumn,
                    "SELECT id FROM cust_customer WHERE created_by = " + authUser.getUserId());
        });
    }

    @Override
    public CustomerArchiveVO detailArchive(Long id) {
        CustCustomer customer = getCustomerOrThrow(id);
        validateCustomerDataScope(customer);
        return toArchiveVO(
                customer,
                loadAttachmentsByBiz(CUSTOMER_ARCHIVE_BIZ_TYPE, List.of(id)).getOrDefault(id, List.of()),
                listRisksByCustomer(id),
                listDebtsByCustomer(id),
                listContractsByCustomer(id)
        );
    }

    @Override
    public PageResponse<CustomerRiskVO> pageRisks(CustomerRiskPageQuery query) {
        Page<CustCustomerScore> page = new Page<>(normalizePageNum(query.getPageNum()), normalizePageSize(query.getPageSize()));
        LambdaQueryWrapper<CustCustomerScore> wrapper = new LambdaQueryWrapper<CustCustomerScore>()
                .orderByDesc(CustCustomerScore::getTestDate)
                .orderByDesc(CustCustomerScore::getUpdatedAt);
        applyRiskQueryFilter(wrapper, query.getCustomerId(), query.getKeyword());
        applyAccessibleCustomerScope(wrapper, CustCustomerScore::getCustomerId);
        Page<CustCustomerScore> result = customerScoreMapper.selectPage(page, wrapper);
        Map<Long, CustCustomer> customerMap = loadCustomerMap(result.getRecords().stream()
                .map(CustCustomerScore::getCustomerId)
                .toList());
        List<CustomerRiskVO> records = result.getRecords().stream()
                .map(record -> toRiskVO(record, customerMap.get(record.getCustomerId())))
                .toList();
        return new PageResponse<>(records, result.getTotal(), (int) result.getCurrent(), (int) result.getSize());
    }

    @Override
    public PageResponse<CustomerDebtVO> pageDebts(CustomerDebtPageQuery query) {
        Page<CustCustomerDebt> page = new Page<>(normalizePageNum(query.getPageNum()), normalizePageSize(query.getPageSize()));
        LambdaQueryWrapper<CustCustomerDebt> wrapper = new LambdaQueryWrapper<CustCustomerDebt>()
                .orderByDesc(CustCustomerDebt::getUpdatedAt);
        applyDebtQueryFilter(wrapper, query.getCustomerId(), query.getKeyword());
        applyAccessibleCustomerScope(wrapper, CustCustomerDebt::getCustomerId);
        Page<CustCustomerDebt> result = customerDebtMapper.selectPage(page, wrapper);
        Map<Long, CustCustomer> customerMap = loadCustomerMap(result.getRecords().stream()
                .map(CustCustomerDebt::getCustomerId)
                .toList());
        List<CustomerDebtVO> records = result.getRecords().stream()
                .map(record -> toDebtVO(record, customerMap.get(record.getCustomerId())))
                .toList();
        return new PageResponse<>(records, result.getTotal(), (int) result.getCurrent(), (int) result.getSize());
    }

    @Override
    public List<CustomerRiskVO> listRisksByCustomer(Long customerId) {
        CustCustomer customer = getCustomerOrThrow(customerId);
        validateCustomerDataScope(customer);
        return customerScoreMapper.selectList(new LambdaQueryWrapper<CustCustomerScore>()
                        .eq(CustCustomerScore::getCustomerId, customerId)
                        .orderByDesc(CustCustomerScore::getTestDate)
                        .orderByDesc(CustCustomerScore::getUpdatedAt))
                .stream()
                .map(record -> toRiskVO(record, customer))
                .toList();
    }

    @Override
    public List<CustomerDebtVO> listDebtsByCustomer(Long customerId) {
        CustCustomer customer = getCustomerOrThrow(customerId);
        validateCustomerDataScope(customer);
        return customerDebtMapper.selectList(new LambdaQueryWrapper<CustCustomerDebt>()
                        .eq(CustCustomerDebt::getCustomerId, customerId)
                        .orderByDesc(CustCustomerDebt::getUpdatedAt))
                .stream()
                .map(record -> toDebtVO(record, customer))
                .toList();
    }

    @Override
    public List<CustomerStatusLogVO> listStatusLogs(Long customerId) {
        CustCustomer customer = getCustomerOrThrow(customerId);
        validateCustomerDataScope(customer);
        List<CustCustomerStatusLog> logs = customerStatusLogMapper.selectList(
                new LambdaQueryWrapper<CustCustomerStatusLog>()
                        .eq(CustCustomerStatusLog::getCustomerId, customerId)
                        .orderByDesc(CustCustomerStatusLog::getChangedAt)
        );
        List<Long> userIds = logs.stream()
                .map(CustCustomerStatusLog::getChangedBy)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        Map<Long, String> userNameMap = loadUserNameMap(userIds);
        return logs.stream()
                .map(log -> toStatusLogVO(log, userNameMap.get(log.getChangedBy())))
                .toList();
    }

    public List<CustomerContractVO> listContractsByCustomer(Long customerId) {
        CustCustomer customer = getCustomerOrThrow(customerId);
        validateCustomerDataScope(customer);
        List<CustCustomerContract> contracts = customerContractMapper.selectList(new LambdaQueryWrapper<CustCustomerContract>()
                .eq(CustCustomerContract::getCustomerId, customerId)
                .orderByDesc(CustCustomerContract::getUpdatedAt));
        Map<Long, List<CustomerAttachmentVO>> attachmentMap = loadAttachmentsByBiz(CUSTOMER_CONTRACT_BIZ_TYPE, contracts.stream()
                .map(CustCustomerContract::getId)
                .toList());
        return contracts.stream()
                .map(contract -> toContractVO(contract, attachmentMap.getOrDefault(contract.getId(), List.of())))
                .toList();
    }

    @Override
    @Transactional
    public void create(CustomerSaveRequest request) {
        CustCustomer customer = new CustCustomer();
        String customerNo = StringUtils.hasText(request.customerNo()) ? request.customerNo().trim() : generateCustomerNo();
        validateCustomerNoUnique(customerNo, null);
        fillCustomer(customer, request);
        customer.setCustomerNo(customerNo);
        customer.setCreatedBy(currentUserProvider.getCurrentUserId());
        customer.setUpdatedBy(currentUserProvider.getCurrentUserId());
        customerMapper.insert(customer);
    }

    @Override
    @Transactional
    public void update(Long id, CustomerSaveRequest request) {
        CustCustomer customer = getCustomerOrThrow(id);
        if (StringUtils.hasText(request.customerNo())) {
            String customerNo = request.customerNo().trim();
            validateCustomerNoUnique(customerNo, id);
            customer.setCustomerNo(customerNo);
        }
        fillCustomer(customer, request);
        customer.setUpdatedBy(currentUserProvider.getCurrentUserId());
        customerMapper.updateById(customer);
    }

    @Override
    @Transactional
    public void createArchive(CustomerArchiveSaveRequest request) {
        CustCustomer customer = new CustCustomer();
        String customerNo = StringUtils.hasText(request.customerNo()) ? request.customerNo().trim() : generateCustomerNo();
        validateCustomerNoUnique(customerNo, null);
        fillCustomer(customer, request);
        customer.setCustomerNo(customerNo);
        customer.setCreatedBy(currentUserProvider.getCurrentUserId());
        customer.setUpdatedBy(currentUserProvider.getCurrentUserId());
        customerMapper.insert(customer);
        replaceArchiveChildren(customer.getId(), request);
    }

    @Override
    @Transactional
    public void updateArchive(Long id, CustomerArchiveSaveRequest request) {
        CustCustomer customer = getCustomerOrThrow(id);
        if (StringUtils.hasText(request.customerNo())) {
            String customerNo = request.customerNo().trim();
            validateCustomerNoUnique(customerNo, id);
            customer.setCustomerNo(customerNo);
        }
        fillCustomer(customer, request);
        customer.setUpdatedBy(currentUserProvider.getCurrentUserId());
        customerMapper.updateById(customer);
        replaceArchiveChildren(id, request);
    }

    @Override
    @Transactional
    public void updateCustomerStatus(Long id, String status, String statusName) {
        CustCustomer customer = getCustomerOrThrow(id);
        customer.setAuditStatus(status);
        customer.setUpdatedBy(currentUserProvider.getCurrentUserId());
        customerMapper.updateById(customer);
        CustCustomerStatusLog log = new CustCustomerStatusLog();
        log.setCustomerId(id);
        log.setStatusCode(status);
        log.setStatusName(statusName);
        log.setChangedAt(LocalDateTime.now());
        log.setChangedBy(currentUserProvider.getCurrentUserId());
        customerStatusLogMapper.insert(log);
    }

    @Override
    public CustomerRiskVO getRisk(Long id) {
        CustCustomerScore score = getRiskOrThrow(id);
        CustCustomer customer = getCustomerOrThrow(score.getCustomerId());
        return toRiskVO(score, customer);
    }

    @Override
    @Transactional
    public void createRisk(CustomerScoreSaveRequest request) {
        Long customerId = requireCustomerId(request.customerId());
        validateCustomerDataScope(getCustomerOrThrow(customerId));
        CustCustomerScore score = new CustCustomerScore();
        fillScore(score, request, customerId);
        score.setCreatedBy(currentUserProvider.getCurrentUserId());
        score.setUpdatedBy(currentUserProvider.getCurrentUserId());
        customerScoreMapper.insert(score);
    }

    @Override
    @Transactional
    public void updateRisk(Long id, CustomerScoreSaveRequest request) {
        // getRiskOrThrow 已校验记录存在且属于当前用户可见的客户。
        // 归属客户固定使用记录原本的 customerId，忽略请求体里的 customerId，
        // 避免把记录改挂到其他客户（即便目标可见也不应通过编辑接口迁移归属）。
        CustCustomerScore score = getRiskOrThrow(id);
        fillScore(score, request, score.getCustomerId());
        score.setUpdatedBy(currentUserProvider.getCurrentUserId());
        customerScoreMapper.updateById(score);
    }

    @Override
    @Transactional
    public void deleteRisk(Long id) {
        getRiskOrThrow(id);
        customerScoreMapper.deleteById(id);
    }

    @Override
    public CustomerDebtVO getDebt(Long id) {
        CustCustomerDebt debt = getDebtOrThrow(id);
        CustCustomer customer = getCustomerOrThrow(debt.getCustomerId());
        return toDebtVO(debt, customer);
    }

    @Override
    @Transactional
    public void createDebt(CustomerDebtSaveRequest request) {
        Long customerId = requireCustomerId(request.customerId());
        validateCustomerDataScope(getCustomerOrThrow(customerId));
        CustCustomerDebt debt = new CustCustomerDebt();
        fillDebt(debt, request, customerId);
        debt.setCreatedBy(currentUserProvider.getCurrentUserId());
        debt.setUpdatedBy(currentUserProvider.getCurrentUserId());
        customerDebtMapper.insert(debt);
    }

    @Override
    @Transactional
    public void updateDebt(Long id, CustomerDebtSaveRequest request) {
        // 归属客户固定使用记录原本的 customerId（getDebtOrThrow 已校验归属），
        // 忽略请求体里的 customerId，避免通过编辑接口迁移记录归属。
        CustCustomerDebt debt = getDebtOrThrow(id);
        fillDebt(debt, request, debt.getCustomerId());
        debt.setUpdatedBy(currentUserProvider.getCurrentUserId());
        customerDebtMapper.updateById(debt);
    }

    @Override
    @Transactional
    public void deleteDebt(Long id) {
        getDebtOrThrow(id);
        customerDebtMapper.deleteById(id);
    }

    @Override
    public CustomerContractVO getContract(Long id) {
        CustCustomerContract contract = getContractOrThrow(id);
        CustCustomer customer = getCustomerOrThrow(contract.getCustomerId());
        validateCustomerDataScope(customer);
        List<CustomerAttachmentVO> attachments = loadAttachmentsByBiz(CUSTOMER_CONTRACT_BIZ_TYPE, List.of(id))
                .getOrDefault(id, List.of());
        return toContractVO(contract, attachments);
    }

    @Override
    @Transactional
    public void createContract(Long customerId, CustomerContractSaveRequest request) {
        getCustomerOrThrow(customerId);
        CustCustomerContract contract = new CustCustomerContract();
        fillContract(contract, request, customerId);
        contract.setCreatedBy(currentUserProvider.getCurrentUserId());
        contract.setUpdatedBy(currentUserProvider.getCurrentUserId());
        customerContractMapper.insert(contract);
        bindFiles(CUSTOMER_CONTRACT_BIZ_TYPE, contract.getId(), request.fileIds());
    }

    @Override
    @Transactional
    public void updateContract(Long id, CustomerContractSaveRequest request) {
        CustCustomerContract contract = getContractOrThrow(id);
        fillContract(contract, request, contract.getCustomerId());
        contract.setUpdatedBy(currentUserProvider.getCurrentUserId());
        customerContractMapper.updateById(contract);
        clearFileBindings(CUSTOMER_CONTRACT_BIZ_TYPE, List.of(id));
        bindFiles(CUSTOMER_CONTRACT_BIZ_TYPE, id, request.fileIds());
    }

    @Override
    @Transactional
    public void deleteContract(Long id) {
        getContractOrThrow(id);
        clearFileBindings(CUSTOMER_CONTRACT_BIZ_TYPE, List.of(id));
        customerContractMapper.deleteById(id);
    }

    @Override
    public List<CustomerTradeVO> listTradesByCustomer(Long customerId) {
        CustCustomer customer = getCustomerOrThrow(customerId);
        validateCustomerDataScope(customer);
        return customerTradeMapper.selectList(new LambdaQueryWrapper<CustCustomerTrade>()
                        .eq(CustCustomerTrade::getCustomerId, customerId)
                        .orderByDesc(CustCustomerTrade::getTradeDate)
                        .orderByDesc(CustCustomerTrade::getUpdatedAt))
                .stream()
                .map(trade -> toTradeVO(trade, customer))
                .toList();
    }

    @Override
    public CustomerTradeVO getTrade(Long id) {
        CustCustomerTrade trade = getTradeOrThrow(id);
        CustCustomer customer = getCustomerOrThrow(trade.getCustomerId());
        validateCustomerDataScope(customer);
        return toTradeVO(trade, customer);
    }

    @Override
    @Transactional
    public void createTrade(Long customerId, CustomerTradeSaveRequest request) {
        getCustomerOrThrow(customerId);
        CustCustomerTrade trade = new CustCustomerTrade();
        fillTrade(trade, request, customerId);
        trade.setCreatedBy(currentUserProvider.getCurrentUserId());
        trade.setUpdatedBy(currentUserProvider.getCurrentUserId());
        customerTradeMapper.insert(trade);
    }

    @Override
    @Transactional
    public void updateTrade(Long id, CustomerTradeSaveRequest request) {
        CustCustomerTrade trade = getTradeOrThrow(id);
        fillTrade(trade, request, trade.getCustomerId());
        trade.setUpdatedBy(currentUserProvider.getCurrentUserId());
        customerTradeMapper.updateById(trade);
    }

    @Override
    @Transactional
    public void deleteTrade(Long id) {
        getTradeOrThrow(id);
        customerTradeMapper.deleteById(id);
    }

    /**
     * 按 id 增量同步档案子记录：表单里带 id 的更新、不带 id 的新增、表单里已不存在的才删除。
     * 不再"全删重建"——避免独立列表页(风险/负债)新增的记录被档案保存静默清空，
     * 也避免逻辑删除行堆积和子记录 id 每次保存都漂移。
     */
    private void replaceArchiveChildren(Long customerId, CustomerArchiveSaveRequest request) {
        clearFileBindings(CUSTOMER_ARCHIVE_BIZ_TYPE, List.of(customerId));
        bindFiles(CUSTOMER_ARCHIVE_BIZ_TYPE, customerId, request.archiveFileIds());

        upsertScores(customerId, safeList(request.riskRecords()));
        upsertDebts(customerId, safeList(request.debtRecords()));
        upsertContracts(customerId, safeList(request.contractRecords()));
    }

    private void upsertScores(Long customerId, List<CustomerScoreSaveRequest> records) {
        List<Long> existingIds = customerScoreMapper.selectList(new LambdaQueryWrapper<CustCustomerScore>()
                        .select(CustCustomerScore::getId)
                        .eq(CustCustomerScore::getCustomerId, customerId))
                .stream().map(CustCustomerScore::getId).toList();
        Set<Long> keepIds = collectKeepIds(records, CustomerScoreSaveRequest::id);
        assertChildIdsBelong(existingIds, keepIds, "风险评估记录");
        List<Long> removedIds = existingIds.stream().filter(id -> !keepIds.contains(id)).toList();
        if (!removedIds.isEmpty()) {
            customerScoreMapper.deleteBatchIds(removedIds);
        }
        Long currentUserId = currentUserProvider.getCurrentUserId();
        for (CustomerScoreSaveRequest record : records) {
            CustCustomerScore score = new CustCustomerScore();
            fillScore(score, record, customerId);
            score.setUpdatedBy(currentUserId);
            if (record.id() != null) {
                score.setId(record.id());
                customerScoreMapper.updateById(score);
            } else {
                score.setCreatedBy(currentUserId);
                customerScoreMapper.insert(score);
            }
        }
    }

    private void upsertDebts(Long customerId, List<CustomerDebtSaveRequest> records) {
        List<Long> existingIds = customerDebtMapper.selectList(new LambdaQueryWrapper<CustCustomerDebt>()
                        .select(CustCustomerDebt::getId)
                        .eq(CustCustomerDebt::getCustomerId, customerId))
                .stream().map(CustCustomerDebt::getId).toList();
        Set<Long> keepIds = collectKeepIds(records, CustomerDebtSaveRequest::id);
        assertChildIdsBelong(existingIds, keepIds, "负债登记记录");
        List<Long> removedIds = existingIds.stream().filter(id -> !keepIds.contains(id)).toList();
        if (!removedIds.isEmpty()) {
            customerDebtMapper.deleteBatchIds(removedIds);
        }
        Long currentUserId = currentUserProvider.getCurrentUserId();
        for (CustomerDebtSaveRequest record : records) {
            CustCustomerDebt debt = new CustCustomerDebt();
            fillDebt(debt, record, customerId);
            debt.setUpdatedBy(currentUserId);
            if (record.id() != null) {
                debt.setId(record.id());
                customerDebtMapper.updateById(debt);
            } else {
                debt.setCreatedBy(currentUserId);
                customerDebtMapper.insert(debt);
            }
        }
    }

    private void upsertContracts(Long customerId, List<CustomerContractSaveRequest> records) {
        List<Long> existingIds = customerContractMapper.selectList(new LambdaQueryWrapper<CustCustomerContract>()
                        .select(CustCustomerContract::getId)
                        .eq(CustCustomerContract::getCustomerId, customerId))
                .stream().map(CustCustomerContract::getId).toList();
        Set<Long> keepIds = collectKeepIds(records, CustomerContractSaveRequest::id);
        assertChildIdsBelong(existingIds, keepIds, "合同");
        List<Long> removedIds = existingIds.stream().filter(id -> !keepIds.contains(id)).toList();
        if (!removedIds.isEmpty()) {
            clearFileBindings(CUSTOMER_CONTRACT_BIZ_TYPE, removedIds);
            customerContractMapper.deleteBatchIds(removedIds);
        }
        Long currentUserId = currentUserProvider.getCurrentUserId();
        for (CustomerContractSaveRequest record : records) {
            CustCustomerContract contract = new CustCustomerContract();
            fillContract(contract, record, customerId);
            contract.setUpdatedBy(currentUserId);
            if (record.id() != null) {
                contract.setId(record.id());
                customerContractMapper.updateById(contract);
                clearFileBindings(CUSTOMER_CONTRACT_BIZ_TYPE, List.of(record.id()));
                bindFiles(CUSTOMER_CONTRACT_BIZ_TYPE, record.id(), record.fileIds());
            } else {
                contract.setCreatedBy(currentUserId);
                customerContractMapper.insert(contract);
                bindFiles(CUSTOMER_CONTRACT_BIZ_TYPE, contract.getId(), record.fileIds());
            }
        }
    }

    private <T> Set<Long> collectKeepIds(List<T> records, Function<T, Long> idGetter) {
        return records.stream().map(idGetter).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    /**
     * 表单回传的子记录 id 必须确实属于该客户，否则拒绝——防止通过伪造 id 改写他人客户的子记录。
     */
    private void assertChildIdsBelong(Collection<Long> existingIds, Collection<Long> requestedIds, String label) {
        for (Long id : requestedIds) {
            if (!existingIds.contains(id)) {
                throw new BusinessException(label + "不存在或不属于该客户");
            }
        }
    }

    private void bindFiles(String bizType, Long bizId, List<Long> fileIds) {
        List<Long> ids = safeList(fileIds).stream().filter(Objects::nonNull).distinct().toList();
        if (ids.isEmpty()) {
            return;
        }
        Long currentUserId = currentUserProvider.getCurrentUserId();
        List<SysFile> files = fileMapper.selectBatchIds(ids);
        for (SysFile file : files) {
            if (currentUserId != null && !currentUserId.equals(file.getUploaderId())) {
                throw new BusinessException("无权绑定他人上传的文件");
            }
            file.setBizType(bizType);
            file.setBizId(bizId);
            fileMapper.updateById(file);
        }
    }

    private void clearFileBindings(String bizType, Collection<Long> bizIds) {
        List<Long> ids = bizIds.stream().filter(Objects::nonNull).distinct().toList();
        if (ids.isEmpty()) {
            return;
        }
        fileMapper.update(
                null,
                new UpdateWrapper<SysFile>()
                        .eq("biz_type", bizType)
                        .in("biz_id", ids)
                        .set("biz_type", null)
                        .set("biz_id", null)
        );
    }

    private void applyCustomerKeyword(LambdaQueryWrapper<CustCustomer> wrapper, String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return;
        }
        String trimmed = keyword.trim();
        wrapper.and(w -> w.like(CustCustomer::getCustomerName, trimmed)
                .or()
                .like(CustCustomer::getCustomerNo, trimmed)
                .or()
                .like(CustCustomer::getCompanyName, trimmed)
                .or()
                .like(CustCustomer::getMobile, trimmed));
    }

    private void applyRiskQueryFilter(LambdaQueryWrapper<CustCustomerScore> wrapper, Long customerId, String keyword) {
        if (customerId != null) {
            wrapper.eq(CustCustomerScore::getCustomerId, customerId);
        }
        if (!StringUtils.hasText(keyword)) {
            return;
        }
        List<Long> customerIds = findCustomerIdsByKeyword(keyword);
        if (!customerIds.isEmpty()) {
            wrapper.in(CustCustomerScore::getCustomerId, customerIds);
            return;
        }
        wrapper.like(CustCustomerScore::getRemark, keyword.trim());
    }

    private void applyDebtQueryFilter(LambdaQueryWrapper<CustCustomerDebt> wrapper, Long customerId, String keyword) {
        if (customerId != null) {
            wrapper.eq(CustCustomerDebt::getCustomerId, customerId);
        }
        if (!StringUtils.hasText(keyword)) {
            return;
        }
        List<Long> customerIds = findCustomerIdsByKeyword(keyword);
        if (!customerIds.isEmpty()) {
            wrapper.in(CustCustomerDebt::getCustomerId, customerIds);
            return;
        }
        wrapper.like(CustCustomerDebt::getDebtType, keyword.trim());
    }

    private List<Long> findCustomerIdsByKeyword(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return Collections.emptyList();
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
                .toList();
    }

    private Map<Long, CustCustomer> loadCustomerMap(Collection<Long> customerIds) {
        List<Long> ids = customerIds.stream().filter(Objects::nonNull).distinct().toList();
        if (ids.isEmpty()) {
            return Collections.emptyMap();
        }
        return customerMapper.selectBatchIds(ids).stream()
                .collect(Collectors.toMap(CustCustomer::getId, Function.identity()));
    }

    private Map<Long, CustCustomerScore> loadLatestRiskMap(Collection<Long> customerIds) {
        List<Long> ids = customerIds.stream().filter(Objects::nonNull).distinct().toList();
        if (ids.isEmpty()) {
            return Collections.emptyMap();
        }
        return customerScoreMapper.selectList(new LambdaQueryWrapper<CustCustomerScore>()
                        .in(CustCustomerScore::getCustomerId, ids)
                        .orderByDesc(CustCustomerScore::getTestDate)
                        .orderByDesc(CustCustomerScore::getUpdatedAt)
                        .orderByDesc(CustCustomerScore::getId))
                .stream()
                .filter(record -> record.getCustomerId() != null)
                .sorted(Comparator
                        .comparing(CustCustomerScore::getTestDate, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(CustCustomerScore::getUpdatedAt, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(CustCustomerScore::getId, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toMap(
                        CustCustomerScore::getCustomerId,
                        Function.identity(),
                        (first, ignored) -> first
                ));
    }

    private Map<Long, List<CustomerAttachmentVO>> loadAttachmentsByBiz(String bizType, Collection<Long> bizIds) {
        List<Long> ids = bizIds.stream().filter(Objects::nonNull).distinct().toList();
        if (ids.isEmpty()) {
            return Collections.emptyMap();
        }
        return fileMapper.selectList(new QueryWrapper<SysFile>()
                        .eq("biz_type", bizType)
                        .in("biz_id", ids)
                        .orderByDesc("created_at"))
                .stream()
                .collect(Collectors.groupingBy(
                        SysFile::getBizId,
                        Collectors.mapping(this::toAttachmentVO, Collectors.toList())
                ));
    }

    private CustCustomer getCustomerOrThrow(Long id) {
        CustCustomer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }
        return customer;
    }

    private CustCustomerScore getRiskOrThrow(Long id) {
        CustCustomerScore score = customerScoreMapper.selectById(id);
        if (score == null) {
            throw new BusinessException("风险评估记录不存在");
        }
        CustCustomer customer = getCustomerOrThrow(score.getCustomerId());
        validateCustomerDataScope(customer);
        return score;
    }

    private CustCustomerDebt getDebtOrThrow(Long id) {
        CustCustomerDebt debt = customerDebtMapper.selectById(id);
        if (debt == null) {
            throw new BusinessException("负债登记记录不存在");
        }
        CustCustomer customer = getCustomerOrThrow(debt.getCustomerId());
        validateCustomerDataScope(customer);
        return debt;
    }

    private CustCustomerContract getContractOrThrow(Long id) {
        CustCustomerContract contract = customerContractMapper.selectById(id);
        if (contract == null) {
            throw new BusinessException("合同不存在");
        }
        CustCustomer customer = getCustomerOrThrow(contract.getCustomerId());
        validateCustomerDataScope(customer);
        return contract;
    }

    private CustCustomerTrade getTradeOrThrow(Long id) {
        CustCustomerTrade trade = customerTradeMapper.selectById(id);
        if (trade == null) {
            throw new BusinessException("交易记录不存在");
        }
        CustCustomer customer = getCustomerOrThrow(trade.getCustomerId());
        validateCustomerDataScope(customer);
        return trade;
    }

    private void validateCustomerNoUnique(String customerNo, Long excludeId) {
        if (!StringUtils.hasText(customerNo)) {
            return;
        }
        LambdaQueryWrapper<CustCustomer> wrapper = new LambdaQueryWrapper<CustCustomer>()
                .eq(CustCustomer::getCustomerNo, customerNo.trim());
        if (excludeId != null) {
            wrapper.ne(CustCustomer::getId, excludeId);
        }
        Long count = customerMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("客户编号已存在");
        }
    }

    private void fillCustomer(CustCustomer customer, CustomerProfileFields request) {
        customer.setCustomerName(request.customerName().trim());
        customer.setGender(request.gender());
        customer.setIdCard(request.idCard());
        customer.setBirthday(request.birthday());
        customer.setAge(calculateAge(request.birthday()));
        customer.setMobile(request.mobile());
        customer.setCompanyName(request.companyName());
        customer.setCreditCode(request.creditCode());
        customer.setEstablishedDate(request.establishedDate());
        customer.setIndustry(request.industry());
        customer.setBusinessAddress(request.businessAddress());
        customer.setBankName(request.bankName());
        customer.setBankAccount(request.bankAccount());
        customer.setRecommenderName(request.recommenderName());
        customer.setRecommenderRate(request.recommenderRate());
        customer.setServiceFee(request.serviceFee());
        customer.setBizStatus(StringUtils.hasText(request.bizStatus()) ? request.bizStatus() : "INIT");
        customer.setTaxRegistrationNormal(request.taxRegistrationNormal());
    }

    private void fillScore(CustCustomerScore score, CustomerScoreSaveRequest request, Long customerId) {
        score.setCustomerId(customerId);
        score.setTestDate(request.testDate());
        score.setTestLimit(request.testLimit());
        score.setTrafficValue(request.trafficValue());
        score.setCompositeScore(request.compositeScore());
        score.setThirdPartyScore(request.thirdPartyScore());
        score.setRemark(request.remark());
    }

    private void fillDebt(CustCustomerDebt debt, CustomerDebtSaveRequest request, Long customerId) {
        BigDecimal debtAmount = defaultDecimal(request.debtAmount());
        BigDecimal advancePaidAmount = defaultDecimal(request.advancePaidAmount());
        BigDecimal pendingAmount = request.pendingAmount() != null
                ? request.pendingAmount()
                : debtAmount.subtract(advancePaidAmount).max(BigDecimal.ZERO);
        debt.setCustomerId(customerId);
        debt.setDebtType(request.debtType());
        debt.setDebtAmount(request.debtAmount());
        debt.setTotalRepaymentAmount(request.totalRepaymentAmount());
        debt.setRepaidAmount(request.advancePaidAmount());
        debt.setPendingAmount(pendingAmount);
        debt.setInstallmentAmount(request.installmentAmount());
        debt.setRepaymentDay(request.repaymentDay());
        debt.setRemark(request.remark());
    }

    private void fillContract(CustCustomerContract contract, CustomerContractSaveRequest request, Long customerId) {
        contract.setCustomerId(customerId);
        contract.setCustomerName(request.customerName());
        contract.setCompanyName(request.companyName());
        contract.setCreditCode(request.creditCode());
        contract.setContractNo(request.contractNo());
        contract.setContractName(request.contractName());
        contract.setContractFileId(safeList(request.fileIds()).stream().filter(Objects::nonNull).findFirst().orElse(null));
        contract.setSignDate(request.signDate());
        contract.setRemark(request.remark());
    }

    private void fillTrade(CustCustomerTrade trade, CustomerTradeSaveRequest request, Long customerId) {
        trade.setCustomerId(customerId);
        trade.setTradeType(request.tradeType());
        trade.setAmount(request.amount());
        trade.setServiceFee(request.serviceFee());
        trade.setActualReceived(request.actualReceived());
        trade.setReturnedAmount(request.returnedAmount());
        trade.setBalanceAmount(request.balanceAmount());
        trade.setTradeDate(request.tradeDate());
        trade.setRemark(request.remark());
    }

    private CustomerVO toVO(CustCustomer customer, CustCustomerScore latestRisk) {
        return new CustomerVO(
                customer.getId(),
                customer.getCustomerNo(),
                customer.getCustomerName(),
                customer.getMobile(),
                customer.getCompanyName(),
                customer.getCreditCode(),
                customer.getIndustry(),
                customer.getAuditStatus(),
                customer.getBizStatus(),
                customer.getLoanStatus(),
                customer.getServiceFee(),
                customer.getRemark(),
                customer.getBirthday(),
                customer.getEstablishedDate(),
                customer.getBankName(),
                customer.getBankAccount(),
                customer.getRecommenderName(),
                customer.getRecommenderRate(),
                customer.getBusinessAddress(),
                customer.getTaxRegistrationNormal(),
                latestRisk == null ? null : latestRisk.getTestDate(),
                latestRisk == null ? null : latestRisk.getTestLimit(),
                latestRisk == null ? null : latestRisk.getTrafficValue(),
                latestRisk == null ? null : latestRisk.getCompositeScore(),
                latestRisk == null ? null : latestRisk.getThirdPartyScore(),
                customer.getCreatedAt(),
                customer.getUpdatedAt()
        );
    }

    private CustomerArchiveVO toArchiveVO(
            CustCustomer customer,
            List<CustomerAttachmentVO> archiveAttachments,
            List<CustomerRiskVO> riskRecords,
            List<CustomerDebtVO> debtRecords,
            List<CustomerContractVO> contractRecords
    ) {
        return new CustomerArchiveVO(
                customer.getId(),
                customer.getCustomerNo(),
                customer.getCustomerName(),
                customer.getGender(),
                customer.getIdCard(),
                customer.getBirthday(),
                customer.getAge(),
                customer.getMobile(),
                customer.getCompanyName(),
                customer.getCreditCode(),
                customer.getEstablishedDate(),
                customer.getIndustry(),
                customer.getBusinessAddress(),
                customer.getBankName(),
                customer.getBankAccount(),
                customer.getRecommenderName(),
                customer.getRecommenderRate(),
                customer.getServiceFee(),
                customer.getAuditStatus(),
                customer.getBizStatus(),
                customer.getLoanStatus(),
                customer.getTaxRegistrationNormal(),
                customer.getRemark(),
                archiveAttachments,
                riskRecords,
                debtRecords,
                contractRecords,
                customer.getCreatedAt(),
                customer.getUpdatedAt()
        );
    }

    private CustomerRiskVO toRiskVO(CustCustomerScore record, CustCustomer customer) {
        return new CustomerRiskVO(
                record.getId(),
                record.getCustomerId(),
                customer == null ? "" : customer.getCustomerNo(),
                customer == null ? "" : customer.getCustomerName(),
                customer == null ? "" : customer.getMobile(),
                customer == null ? "" : customer.getCompanyName(),
                customer == null ? "" : customer.getCreditCode(),
                customer == null ? "" : customer.getIndustry(),
                customer == null ? "" : customer.getBusinessAddress(),
                record.getTestDate(),
                record.getTestLimit(),
                record.getTrafficValue(),
                record.getCompositeScore(),
                record.getThirdPartyScore(),
                record.getRemark(),
                record.getCreatedAt(),
                record.getUpdatedAt()
        );
    }

    private CustomerDebtVO toDebtVO(CustCustomerDebt record, CustCustomer customer) {
        return new CustomerDebtVO(
                record.getId(),
                record.getCustomerId(),
                customer == null ? "" : customer.getCustomerNo(),
                customer == null ? "" : customer.getCustomerName(),
                customer == null ? "" : customer.getMobile(),
                customer == null ? "" : customer.getCompanyName(),
                customer == null ? "" : customer.getCreditCode(),
                record.getDebtType(),
                record.getDebtAmount(),
                record.getTotalRepaymentAmount(),
                record.getRepaidAmount(),
                record.getPendingAmount(),
                record.getInstallmentAmount(),
                record.getRepaymentDay(),
                record.getRemark(),
                record.getCreatedAt(),
                record.getUpdatedAt()
        );
    }

    private CustomerContractVO toContractVO(CustCustomerContract contract, List<CustomerAttachmentVO> attachments) {
        return new CustomerContractVO(
                contract.getId(),
                contract.getCustomerId(),
                contract.getCustomerName(),
                contract.getCompanyName(),
                contract.getCreditCode(),
                contract.getContractNo(),
                contract.getContractName(),
                attachments,
                contract.getSignDate(),
                contract.getRemark(),
                contract.getCreatedAt(),
                contract.getUpdatedAt()
        );
    }

    private CustomerTradeVO toTradeVO(CustCustomerTrade trade, CustCustomer customer) {
        return new CustomerTradeVO(
                trade.getId(),
                trade.getCustomerId(),
                customer == null ? "" : customer.getCustomerNo(),
                customer == null ? "" : customer.getCustomerName(),
                customer == null ? "" : customer.getMobile(),
                customer == null ? "" : customer.getCompanyName(),
                trade.getTradeType(),
                trade.getAmount(),
                trade.getServiceFee(),
                trade.getActualReceived(),
                trade.getReturnedAmount(),
                trade.getBalanceAmount(),
                trade.getTradeDate(),
                trade.getRemark(),
                trade.getCreatedAt(),
                trade.getUpdatedAt()
        );
    }

    private CustomerAttachmentVO toAttachmentVO(SysFile file) {
        return new CustomerAttachmentVO(file.getId(), file.getFileName());
    }

    private CustomerStatusLogVO toStatusLogVO(CustCustomerStatusLog log, String changedByName) {
        return new CustomerStatusLogVO(
                log.getId(),
                log.getCustomerId(),
                log.getStatusCode(),
                log.getStatusName(),
                log.getChangedAt(),
                log.getChangedBy(),
                changedByName,
                log.getRemark()
        );
    }

    private Map<Long, String> loadUserNameMap(List<Long> userIds) {
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(SysUser::getId, SysUser::getRealName));
    }

    private Integer calculateAge(LocalDate birthday) {
        if (birthday == null) {
            return null;
        }
        return Period.between(birthday, LocalDate.now()).getYears();
    }

    private Long requireCustomerId(Long customerId) {
        if (customerId == null) {
            throw new BusinessException("客户不能为空");
        }
        return customerId;
    }

    private BigDecimal defaultDecimal(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private <T> List<T> safeList(List<T> values) {
        return values == null ? List.of() : values.stream().filter(Objects::nonNull).toList();
    }

    private int normalizePageNum(Integer pageNum) {
        return pageNum == null || pageNum < 1 ? 1 : pageNum;
    }

    private int normalizePageSize(Integer pageSize) {
        return pageSize == null || pageSize < 1 ? 20 : Math.min(pageSize, 200);
    }

    private String generateCustomerNo() {
        return "KH" + CUSTOMER_NO_FORMATTER.format(java.time.LocalDateTime.now()) + UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 4)
                .toUpperCase();
    }
}
