package com.huacai.finance.controller;

import com.huacai.common.model.ApiResponse;
import com.huacai.common.model.PageResponse;
import com.huacai.common.model.StatusUpdateRequest;
import com.huacai.finance.dto.ExpenseSaveRequest;
import com.huacai.finance.dto.IncomeSaveRequest;
import com.huacai.finance.dto.PayeeSaveRequest;
import com.huacai.finance.query.FinExpensePageQuery;
import com.huacai.finance.query.FinIncomePageQuery;
import com.huacai.finance.query.FinPayeePageQuery;
import com.huacai.finance.service.FinExpenseService;
import com.huacai.finance.service.FinIncomeService;
import com.huacai.finance.service.FinPayeeService;
import com.huacai.finance.vo.ExpenseVO;
import com.huacai.finance.vo.IncomeVO;
import com.huacai.finance.vo.PayeeVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "财务管理")
@RestController
@RequestMapping("/api/v1/finance")
public class FinanceController {

    private final FinPayeeService finPayeeService;
    private final FinExpenseService finExpenseService;
    private final FinIncomeService finIncomeService;

    public FinanceController(
            FinPayeeService finPayeeService,
            FinExpenseService finExpenseService,
            FinIncomeService finIncomeService
    ) {
        this.finPayeeService = finPayeeService;
        this.finExpenseService = finExpenseService;
        this.finIncomeService = finIncomeService;
    }

    // ==================== 收款方管理 ====================
    @GetMapping("/payees")
    public ApiResponse<PageResponse<PayeeVO>> payees(@ModelAttribute FinPayeePageQuery query) {
        return ApiResponse.success(finPayeeService.pagePayees(
                query.getKeyword(),
                query.getStatus(),
                query.getPageNum() != null ? query.getPageNum() : 1,
                query.getPageSize() != null ? query.getPageSize() : 20
        ));
    }

    @GetMapping("/payees/{id}")
    public ApiResponse<PayeeVO> payeeDetail(@PathVariable Long id) {
        return ApiResponse.success(finPayeeService.getPayee(id));
    }

    @PostMapping("/payees")
    public ApiResponse<Void> createPayee(@RequestBody PayeeSaveRequest request) {
        finPayeeService.createPayee(request);
        return ApiResponse.success();
    }

    @PutMapping("/payees/{id}")
    public ApiResponse<Void> updatePayee(@PathVariable Long id, @RequestBody PayeeSaveRequest request) {
        finPayeeService.updatePayee(id, request);
        return ApiResponse.success();
    }

    @DeleteMapping("/payees/{id}")
    public ApiResponse<Void> deletePayee(@PathVariable Long id) {
        finPayeeService.deletePayee(id);
        return ApiResponse.success();
    }

    @PutMapping("/payees/{id}/status")
    public ApiResponse<Void> updatePayeeStatus(@PathVariable Long id, @RequestBody StatusUpdateRequest request) {
        finPayeeService.updatePayeeStatus(id, request.status());
        return ApiResponse.success();
    }

    // ==================== 支出管理 ====================
    @GetMapping("/expenses")
    public ApiResponse<PageResponse<ExpenseVO>> expenses(@ModelAttribute FinExpensePageQuery query) {
        return ApiResponse.success(finExpenseService.pageExpenses(
                query.getKeyword(),
                query.getExpenseType(),
                query.getPageNum() != null ? query.getPageNum() : 1,
                query.getPageSize() != null ? query.getPageSize() : 20
        ));
    }

    @GetMapping("/expenses/{id}")
    public ApiResponse<ExpenseVO> expenseDetail(@PathVariable Long id) {
        return ApiResponse.success(finExpenseService.getExpense(id));
    }

    @PostMapping("/expenses")
    public ApiResponse<Void> createExpense(@RequestBody ExpenseSaveRequest request) {
        finExpenseService.createExpense(request);
        return ApiResponse.success();
    }

    @PutMapping("/expenses/{id}")
    public ApiResponse<Void> updateExpense(@PathVariable Long id, @RequestBody ExpenseSaveRequest request) {
        finExpenseService.updateExpense(id, request);
        return ApiResponse.success();
    }

    @DeleteMapping("/expenses/{id}")
    public ApiResponse<Void> deleteExpense(@PathVariable Long id) {
        finExpenseService.deleteExpense(id);
        return ApiResponse.success();
    }

    // ==================== 收入管理 ====================
    @GetMapping("/incomes")
    public ApiResponse<PageResponse<IncomeVO>> incomes(@ModelAttribute FinIncomePageQuery query) {
        return ApiResponse.success(finIncomeService.pageIncomes(
                query.getKeyword(),
                query.getIncomeType(),
                query.getPageNum() != null ? query.getPageNum() : 1,
                query.getPageSize() != null ? query.getPageSize() : 20
        ));
    }

    @GetMapping("/incomes/{id}")
    public ApiResponse<IncomeVO> incomeDetail(@PathVariable Long id) {
        return ApiResponse.success(finIncomeService.getIncome(id));
    }

    @PostMapping("/incomes")
    public ApiResponse<Void> createIncome(@RequestBody IncomeSaveRequest request) {
        finIncomeService.createIncome(request);
        return ApiResponse.success();
    }

    @PutMapping("/incomes/{id}")
    public ApiResponse<Void> updateIncome(@PathVariable Long id, @RequestBody IncomeSaveRequest request) {
        finIncomeService.updateIncome(id, request);
        return ApiResponse.success();
    }

    @DeleteMapping("/incomes/{id}")
    public ApiResponse<Void> deleteIncome(@PathVariable Long id) {
        finIncomeService.deleteIncome(id);
        return ApiResponse.success();
    }
}
