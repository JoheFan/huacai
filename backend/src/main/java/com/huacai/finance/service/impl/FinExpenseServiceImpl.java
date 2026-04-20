package com.huacai.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huacai.common.exception.BusinessException;
import com.huacai.common.model.PageResponse;
import com.huacai.finance.dto.ExpenseSaveRequest;
import com.huacai.finance.entity.FinExpense;
import com.huacai.finance.entity.FinPayee;
import com.huacai.finance.mapper.FinExpenseMapper;
import com.huacai.finance.mapper.FinPayeeMapper;
import com.huacai.finance.service.FinExpenseService;
import com.huacai.finance.vo.ExpenseVO;
import com.huacai.security.AuthUser;
import com.huacai.security.CurrentUserProvider;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class FinExpenseServiceImpl implements FinExpenseService {

    private final FinExpenseMapper expenseMapper;
    private final FinPayeeMapper payeeMapper;
    private final CurrentUserProvider currentUserProvider;

    public FinExpenseServiceImpl(FinExpenseMapper expenseMapper, FinPayeeMapper payeeMapper, CurrentUserProvider currentUserProvider) {
        this.expenseMapper = expenseMapper;
        this.payeeMapper = payeeMapper;
        this.currentUserProvider = currentUserProvider;
    }

    private AuthUser getCurrentAuthUser() {
        return currentUserProvider.getCurrentUser().orElseThrow(() -> new BusinessException("未登录"));
    }

    @Override
    public PageResponse<ExpenseVO> pageExpenses(String keyword, String expenseType, int pageNum, int pageSize) {
        LambdaQueryWrapper<FinExpense> wrapper = Wrappers.lambdaQuery();
        wrapper.like(StringUtils.hasText(keyword), FinExpense::getExpenseName, keyword)
                .eq(StringUtils.hasText(expenseType), FinExpense::getExpenseType, expenseType)
                .orderByDesc(FinExpense::getId);
        Page<FinExpense> page = new Page<>(pageNum, pageSize);
        Page<FinExpense> result = expenseMapper.selectPage(page, wrapper);
        return new PageResponse<>(
                result.getRecords().stream().map(this::toVO).toList(),
                result.getTotal(),
                pageNum,
                pageSize
        );
    }

    @Override
    public ExpenseVO getExpense(Long id) {
        FinExpense expense = expenseMapper.selectById(id);
        if (expense == null) throw new BusinessException("支出记录不存在");
        return toVO(expense);
    }

    @Override
    public void createExpense(ExpenseSaveRequest request) {
        validateExpenseRequest(request);

        FinExpense expense = new FinExpense();
        expense.setExpenseName(request.expenseName());
        expense.setExpenseType(request.expenseType());
        expense.setBizDate(request.bizDate() != null ? request.bizDate() : LocalDate.now());
        expense.setAmount(request.amount() != null ? request.amount() : BigDecimal.ZERO);
        expense.setPayeeName(request.payeeName());
        expense.setPayeeId(request.payeeId());
        expense.setFileId(request.fileId());
        expense.setRemark(request.remark());
        AuthUser user = getCurrentAuthUser();
        expense.setCreatedBy(user.getUserId());
        expenseMapper.insert(expense);
    }

    @Override
    public void updateExpense(Long id, ExpenseSaveRequest request) {
        FinExpense expense = expenseMapper.selectById(id);
        if (expense == null) throw new BusinessException("支出记录不存在");
        if (StringUtils.hasText(request.expenseName())) expense.setExpenseName(request.expenseName());
        if (request.expenseType() != null) expense.setExpenseType(request.expenseType());
        if (request.bizDate() != null) expense.setBizDate(request.bizDate());
        if (request.amount() != null) expense.setAmount(request.amount());
        if (request.payeeName() != null) expense.setPayeeName(request.payeeName());
        if (request.payeeId() != null) expense.setPayeeId(request.payeeId());
        if (request.fileId() != null) expense.setFileId(request.fileId());
        if (request.remark() != null) expense.setRemark(request.remark());
        AuthUser user = getCurrentAuthUser();
        expense.setUpdatedBy(user.getUserId());
        expenseMapper.updateById(expense);
    }

    @Override
    public void deleteExpense(Long id) {
        FinExpense expense = expenseMapper.selectById(id);
        if (expense == null) throw new BusinessException("支出记录不存在");
        expenseMapper.deleteById(id);
    }

    @Override
    public List<ExpenseVO> listAllForSelect() {
        LambdaQueryWrapper<FinExpense> wrapper = Wrappers.lambdaQuery();
        wrapper.orderByDesc(FinExpense::getId).last("LIMIT 500");
        return expenseMapper.selectList(wrapper).stream().map(this::toVO).toList();
    }

    private void validateExpenseRequest(ExpenseSaveRequest request) {
        if (!StringUtils.hasText(request.expenseName())) {
            throw new BusinessException("支出名称不能为空");
        }
        if (!StringUtils.hasText(request.expenseType())) {
            throw new BusinessException("支出类型不能为空");
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
        if (request.payeeId() == null) {
            throw new BusinessException("收款方不能为空");
        }
        FinPayee payee = payeeMapper.selectById(request.payeeId());
        if (payee == null) {
            throw new BusinessException("收款方不存在");
        }
        if (!"ENABLE".equals(payee.getStatus())) {
            throw new BusinessException("收款方状态无效");
        }
    }

    private ExpenseVO toVO(FinExpense e) {
        return new ExpenseVO(
                e.getId(), e.getExpenseName(), e.getExpenseType(),
                e.getBizDate(), e.getAmount(), e.getPayeeName(),
                e.getPayeeId(), e.getFileId(), e.getRemark(), e.getCreatedAt()
        );
    }
}
