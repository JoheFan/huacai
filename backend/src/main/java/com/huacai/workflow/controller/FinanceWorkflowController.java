package com.huacai.workflow.controller;

import com.huacai.common.model.ApiResponse;
import com.huacai.common.model.PageResponse;
import com.huacai.workflow.dto.ReimbursementCreateRequest;
import com.huacai.workflow.dto.ReimbursementSubmitRequest;
import com.huacai.workflow.dto.ReimbursementUpdateRequest;
import com.huacai.workflow.query.ReimbursementPageQuery;
import com.huacai.workflow.service.FinReimbursementService;
import com.huacai.workflow.vo.ReimbursementVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/finance")
public class FinanceWorkflowController {

    private final FinReimbursementService reimbursementService;

    public FinanceWorkflowController(FinReimbursementService reimbursementService) {
        this.reimbursementService = reimbursementService;
    }

    @GetMapping("/reimbursements")
    public ApiResponse<PageResponse<ReimbursementVO>> pageReimbursements(ReimbursementPageQuery query) {
        return ApiResponse.success(reimbursementService.pageReimbursements(query));
    }

    @GetMapping("/reimbursements/{id}")
    public ApiResponse<ReimbursementVO> getReimbursement(@PathVariable Long id) {
        return ApiResponse.success(reimbursementService.getReimbursement(id));
    }

    @PostMapping("/reimbursements")
    public ApiResponse<Void> createReimbursement(@Valid @RequestBody ReimbursementCreateRequest request) {
        reimbursementService.createReimbursement(request);
        return ApiResponse.success();
    }

    @PutMapping("/reimbursements/{id}")
    public ApiResponse<Void> updateReimbursement(@PathVariable Long id, @RequestBody ReimbursementUpdateRequest request) {
        reimbursementService.updateReimbursement(id, request);
        return ApiResponse.success();
    }

    @PostMapping("/reimbursements/{id}/submit")
    public ApiResponse<Void> submitReimbursement(@PathVariable Long id, @Valid @RequestBody ReimbursementSubmitRequest request) {
        reimbursementService.submitReimbursement(id, request);
        return ApiResponse.success();
    }

    @PostMapping("/reimbursements/{id}/withdraw")
    public ApiResponse<Void> withdrawReimbursement(@PathVariable Long id) {
        reimbursementService.withdrawReimbursement(id);
        return ApiResponse.success();
    }

    @PostMapping("/reimbursements/{id}/resubmit")
    public ApiResponse<Void> resubmitReimbursement(@PathVariable Long id, @Valid @RequestBody ReimbursementSubmitRequest request) {
        reimbursementService.resubmitReimbursement(id, request);
        return ApiResponse.success();
    }
}
