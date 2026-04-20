package com.huacai.finance.service;

import com.huacai.common.model.PageResponse;
import com.huacai.finance.dto.ExpenseSaveRequest;
import com.huacai.finance.vo.ExpenseVO;

import java.util.List;

public interface FinExpenseService {

    PageResponse<ExpenseVO> pageExpenses(String keyword, String expenseType, int pageNum, int pageSize);

    ExpenseVO getExpense(Long id);

    void createExpense(ExpenseSaveRequest request);

    void updateExpense(Long id, ExpenseSaveRequest request);

    void deleteExpense(Long id);

    List<ExpenseVO> listAllForSelect();
}
