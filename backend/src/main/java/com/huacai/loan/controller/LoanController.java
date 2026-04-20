package com.huacai.loan.controller;

import com.huacai.common.model.ApiResponse;
import com.huacai.common.model.PageQuery;
import com.huacai.common.model.PageResponse;
import com.huacai.loan.dto.LoanCustomerSummarySaveRequest;
import com.huacai.loan.dto.LoanImportTaskCreateRequest;
import com.huacai.loan.dto.LoanOrderSaveRequest;
import com.huacai.loan.dto.LoanRepaymentSaveRequest;
import com.huacai.loan.query.LoanOrderPageQuery;
import com.huacai.loan.query.LoanRepaymentPageQuery;
import com.huacai.loan.service.LoanManageService;
import com.huacai.loan.vo.LoanCustomerSummaryVO;
import com.huacai.loan.vo.LoanOrderOverviewVO;
import com.huacai.loan.vo.LoanOrderVO;
import com.huacai.loan.vo.LoanRepaymentVO;
import com.huacai.loan.vo.LoanRepaymentSummaryVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "借贷与还款")
@RestController
public class LoanController {

    private final LoanManageService loanManageService;

    public LoanController(LoanManageService loanManageService) {
        this.loanManageService = loanManageService;
    }

    @GetMapping("/api/v1/loan-orders")
    public ApiResponse<PageResponse<LoanOrderVO>> page(@ModelAttribute LoanOrderPageQuery query) {
        return ApiResponse.success(loanManageService.pageOrders(query));
    }

    @GetMapping("/api/v1/loan-orders/overview")
    public ApiResponse<PageResponse<LoanOrderOverviewVO>> pageOverview(@ModelAttribute LoanOrderPageQuery query) {
        return ApiResponse.success(loanManageService.pageOrderOverview(query));
    }

    @GetMapping("/api/v1/loan-orders/{id}")
    public ApiResponse<LoanOrderVO> detail(@PathVariable Long id) {
        return ApiResponse.success(loanManageService.orderDetail(id));
    }

    @PostMapping("/api/v1/loan-orders")
    public ApiResponse<Void> create(@Valid @RequestBody LoanOrderSaveRequest request) {
        loanManageService.createOrder(request);
        return ApiResponse.success();
    }

    @PutMapping("/api/v1/loan-orders/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody LoanOrderSaveRequest request) {
        loanManageService.updateOrder(id, request);
        return ApiResponse.success();
    }

    @DeleteMapping("/api/v1/loan-orders/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        loanManageService.deleteOrder(id);
        return ApiResponse.success();
    }

    @GetMapping("/api/v1/loan-orders/export")
    public ApiResponse<Map<String, Object>> export() {
        throw new com.huacai.common.exception.BusinessException("借贷数据导出功能规划中，暂不支持");
    }

    @GetMapping("/api/v1/loan-orders/{loanOrderId}/repayments")
    public ApiResponse<PageResponse<LoanRepaymentVO>> repayments(@PathVariable Long loanOrderId,
                                                                 @ModelAttribute LoanRepaymentPageQuery query) {
        query.setLoanOrderId(loanOrderId);
        return ApiResponse.success(loanManageService.pageRepayments(query));
    }

    @GetMapping("/api/v1/loan-orders/{loanOrderId}/repayments/{id}")
    public ApiResponse<LoanRepaymentVO> repaymentDetail(@PathVariable Long loanOrderId, @PathVariable Long id) {
        return ApiResponse.success(loanManageService.repaymentDetail(loanOrderId, id));
    }

    @PostMapping("/api/v1/loan-orders/{loanOrderId}/repayments")
    public ApiResponse<Void> createRepayment(@PathVariable Long loanOrderId, @Valid @RequestBody LoanRepaymentSaveRequest request) {
        loanManageService.createRepayment(loanOrderId, request);
        return ApiResponse.success();
    }

    @PutMapping("/api/v1/loan-orders/{loanOrderId}/repayments/{id}")
    public ApiResponse<Void> updateRepayment(@PathVariable Long loanOrderId, @PathVariable Long id,
                                             @Valid @RequestBody LoanRepaymentSaveRequest request) {
        loanManageService.updateRepayment(loanOrderId, id, request);
        return ApiResponse.success();
    }

    @DeleteMapping("/api/v1/loan-orders/{loanOrderId}/repayments/{id}")
    public ApiResponse<Void> deleteRepayment(@PathVariable Long loanOrderId, @PathVariable Long id) {
        loanManageService.deleteRepayment(loanOrderId, id);
        return ApiResponse.success();
    }

    @GetMapping("/api/v1/repayments")
    public ApiResponse<PageResponse<LoanRepaymentVO>> repaymentPage(@ModelAttribute LoanRepaymentPageQuery query) {
        return ApiResponse.success(loanManageService.pageRepayments(query));
    }

    @GetMapping("/api/v1/repayments/summary")
    public ApiResponse<LoanRepaymentSummaryVO> repaymentSummary(@ModelAttribute LoanRepaymentPageQuery query) {
        return ApiResponse.success(loanManageService.getRepaymentSummary(query));
    }

    @GetMapping("/api/v1/repayments/{id}")
    public ApiResponse<LoanRepaymentVO> repaymentPageDetail(@PathVariable Long id) {
        return ApiResponse.success(loanManageService.repaymentDetail(null, id));
    }

    @PostMapping("/api/v1/repayments")
    public ApiResponse<Void> createRepayment(@Valid @RequestBody LoanRepaymentSaveRequest request) {
        loanManageService.createRepayment(null, request);
        return ApiResponse.success();
    }

    @PutMapping("/api/v1/repayments/{id}")
    public ApiResponse<Void> updateRepayment(@PathVariable Long id, @Valid @RequestBody LoanRepaymentSaveRequest request) {
        loanManageService.updateRepayment(null, id, request);
        return ApiResponse.success();
    }

    // ===================== 客户汇总台账（银行口径） =====================

    @GetMapping("/api/v1/loan-orders/customer-summary")
    public ApiResponse<LoanCustomerSummaryVO> getCustomerSummary(
            @RequestParam Long customerId,
            @RequestParam String capitalSourceType) {
        return ApiResponse.success(loanManageService.getCustomerSummary(customerId, capitalSourceType));
    }

    @PostMapping("/api/v1/loan-orders/customer-summary")
    public ApiResponse<Void> saveCustomerSummary(@Valid @RequestBody LoanCustomerSummarySaveRequest request) {
        loanManageService.saveCustomerSummary(request);
        return ApiResponse.success();
    }

    @DeleteMapping("/api/v1/loan-orders/customer-summary")
    public ApiResponse<Void> deleteCustomerSummary(
            @RequestParam Long customerId,
            @RequestParam String capitalSourceType) {
        loanManageService.deleteCustomerSummary(customerId, capitalSourceType);
        return ApiResponse.success();
    }

    @PostMapping("/api/v1/loan-import-tasks")
    public ApiResponse<Map<String, Object>> createImportTask(@RequestBody LoanImportTaskCreateRequest request) {
        throw new com.huacai.common.exception.BusinessException("借贷数据导入功能规划中，暂不支持");
    }

    @GetMapping("/api/v1/loan-import-tasks/{id}")
    public ApiResponse<Map<String, Object>> importTaskDetail(@PathVariable Long id) {
        throw new com.huacai.common.exception.BusinessException("借贷数据导入功能规划中，暂不支持");
    }

    @GetMapping("/api/v1/loan-import-tasks")
    public ApiResponse<PageResponse<Map<String, Object>>> importTasks(@ModelAttribute PageQuery query) {
        throw new com.huacai.common.exception.BusinessException("借贷数据导入功能规划中，暂不支持");
    }

    @GetMapping("/api/v1/loan-import-tasks/template")
    public ApiResponse<Map<String, Object>> importTemplate() {
        throw new com.huacai.common.exception.BusinessException("借贷数据导入功能规划中，暂不支持");
    }
}
