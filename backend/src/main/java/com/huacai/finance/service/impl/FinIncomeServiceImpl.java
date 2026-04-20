package com.huacai.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huacai.common.exception.BusinessException;
import com.huacai.common.model.PageResponse;
import com.huacai.finance.dto.IncomeSaveRequest;
import com.huacai.finance.entity.FinIncome;
import com.huacai.finance.mapper.FinIncomeMapper;
import com.huacai.finance.service.FinIncomeService;
import com.huacai.finance.vo.IncomeVO;
import com.huacai.security.AuthUser;
import com.huacai.security.CurrentUserProvider;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class FinIncomeServiceImpl implements FinIncomeService {

    private final FinIncomeMapper incomeMapper;
    private final CurrentUserProvider currentUserProvider;

    public FinIncomeServiceImpl(FinIncomeMapper incomeMapper, CurrentUserProvider currentUserProvider) {
        this.incomeMapper = incomeMapper;
        this.currentUserProvider = currentUserProvider;
    }

    private AuthUser getCurrentAuthUser() {
        return currentUserProvider.getCurrentUser().orElseThrow(() -> new BusinessException("未登录"));
    }

    @Override
    public PageResponse<IncomeVO> pageIncomes(String keyword, String incomeType, int pageNum, int pageSize) {
        LambdaQueryWrapper<FinIncome> wrapper = Wrappers.lambdaQuery();
        wrapper.like(StringUtils.hasText(keyword), FinIncome::getIncomeName, keyword)
                .eq(StringUtils.hasText(incomeType), FinIncome::getIncomeType, incomeType)
                .orderByDesc(FinIncome::getId);
        Page<FinIncome> page = new Page<>(pageNum, pageSize);
        Page<FinIncome> result = incomeMapper.selectPage(page, wrapper);
        return new PageResponse<>(
                result.getRecords().stream().map(this::toVO).toList(),
                result.getTotal(),
                pageNum,
                pageSize
        );
    }

    @Override
    public IncomeVO getIncome(Long id) {
        FinIncome income = incomeMapper.selectById(id);
        if (income == null) throw new BusinessException("收入记录不存在");
        return toVO(income);
    }

    @Override
    public void createIncome(IncomeSaveRequest request) {
        validateIncomeRequest(request);
        FinIncome income = new FinIncome();
        income.setIncomeName(request.incomeName());
        income.setIncomeType(request.incomeType());
        income.setBizDate(request.bizDate() != null ? request.bizDate() : LocalDate.now());
        income.setAmount(request.amount() != null ? request.amount() : BigDecimal.ZERO);
        income.setPayerName(request.payerName());
        income.setPayerId(request.payerId());
        income.setFileId(request.fileId());
        income.setRemark(request.remark());
        AuthUser user = getCurrentAuthUser();
        income.setCreatedBy(user.getUserId());
        incomeMapper.insert(income);
    }

    @Override
    public void updateIncome(Long id, IncomeSaveRequest request) {
        FinIncome income = incomeMapper.selectById(id);
        if (income == null) throw new BusinessException("收入记录不存在");
        if (StringUtils.hasText(request.incomeName())) income.setIncomeName(request.incomeName());
        if (request.incomeType() != null) income.setIncomeType(request.incomeType());
        if (request.bizDate() != null) income.setBizDate(request.bizDate());
        if (request.amount() != null) income.setAmount(request.amount());
        if (request.payerName() != null) income.setPayerName(request.payerName());
        if (request.payerId() != null) income.setPayerId(request.payerId());
        if (request.fileId() != null) income.setFileId(request.fileId());
        if (request.remark() != null) income.setRemark(request.remark());
        AuthUser user = getCurrentAuthUser();
        income.setUpdatedBy(user.getUserId());
        incomeMapper.updateById(income);
    }

    @Override
    public void deleteIncome(Long id) {
        FinIncome income = incomeMapper.selectById(id);
        if (income == null) throw new BusinessException("收入记录不存在");
        incomeMapper.deleteById(id);
    }

    private void validateIncomeRequest(IncomeSaveRequest request) {
        if (!StringUtils.hasText(request.incomeName())) {
            throw new BusinessException("收入名称不能为空");
        }
        if (!StringUtils.hasText(request.incomeType())) {
            throw new BusinessException("收入类型不能为空");
        }
        if (request.bizDate() == null) {
            throw new BusinessException("日期不能为空");
        }
        if (request.amount() == null || request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("金额必须大于0");
        }
        if (request.amount().scale() > 2) {
            throw new BusinessException("金额最多保留两位小数");
        }
    }

    private IncomeVO toVO(FinIncome i) {
        return new IncomeVO(
                i.getId(), i.getIncomeName(), i.getIncomeType(),
                i.getBizDate(), i.getAmount(), i.getPayerName(),
                i.getPayerId(), i.getFileId(), i.getRemark(), i.getCreatedAt()
        );
    }
}
