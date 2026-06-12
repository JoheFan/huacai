package com.huacai.workflow.controller;

import com.huacai.common.model.ApiResponse;
import com.huacai.common.model.PageResponse;
import com.huacai.hr.dto.*;
import com.huacai.hr.query.*;
import com.huacai.hr.service.HrManageService;
import com.huacai.hr.vo.*;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workflow")
public class WorkflowController {

    private final HrManageService hrManageService;

    public WorkflowController(HrManageService hrManageService) {
        this.hrManageService = hrManageService;
    }

    @GetMapping("/transitions")
    public ApiResponse<PageResponse<TransitionApplyVO>> pageTransitionApplies(TransitionApplyPageQuery query) {
        return ApiResponse.success(hrManageService.pageTransitionApplies(query));
    }

    @GetMapping("/transitions/{id}")
    public ApiResponse<TransitionApplyVO> getTransitionApply(@PathVariable Long id) {
        return ApiResponse.success(hrManageService.getTransitionApply(id));
    }

    @PostMapping("/transitions")
    public ApiResponse<Void> createTransitionApply(@Valid @RequestBody TransitionApplyCreateRequest request) {
        hrManageService.createTransitionApply(request);
        return ApiResponse.success();
    }

    @PutMapping("/transitions/{id}")
    public ApiResponse<Void> updateTransitionApply(@PathVariable Long id, @RequestBody TransitionApplyUpdateRequest request) {
        hrManageService.updateTransitionApply(id, request);
        return ApiResponse.success();
    }

    @PostMapping("/transitions/{id}/submit")
    public ApiResponse<Void> submitTransitionApply(@PathVariable Long id) {
        hrManageService.submitTransitionApply(id);
        return ApiResponse.success();
    }

    @PostMapping("/transitions/{id}/approve")
    public ApiResponse<Void> approveTransitionApply(@PathVariable Long id, @Valid @RequestBody ApprovalOpinionRequest request) {
        hrManageService.approveTransitionApply(id, request);
        return ApiResponse.success();
    }

    @PostMapping("/transitions/{id}/reject")
    public ApiResponse<Void> rejectTransitionApply(@PathVariable Long id, @Valid @RequestBody ApprovalOpinionRequest request) {
        hrManageService.rejectTransitionApply(id, request);
        return ApiResponse.success();
    }

    @GetMapping("/salary-adjusts")
    public ApiResponse<PageResponse<SalaryAdjustApplyVO>> pageSalaryAdjustApplies(SalaryAdjustApplyPageQuery query) {
        return ApiResponse.success(hrManageService.pageSalaryAdjustApplies(query));
    }

    @GetMapping("/salary-adjusts/{id}")
    public ApiResponse<SalaryAdjustApplyVO> getSalaryAdjustApply(@PathVariable Long id) {
        return ApiResponse.success(hrManageService.getSalaryAdjustApply(id));
    }

    @PostMapping("/salary-adjusts")
    public ApiResponse<Void> createSalaryAdjustApply(@Valid @RequestBody SalaryAdjustApplyCreateRequest request) {
        hrManageService.createSalaryAdjustApply(request);
        return ApiResponse.success();
    }

    @PutMapping("/salary-adjusts/{id}")
    public ApiResponse<Void> updateSalaryAdjustApply(@PathVariable Long id, @RequestBody SalaryAdjustApplyUpdateRequest request) {
        hrManageService.updateSalaryAdjustApply(id, request);
        return ApiResponse.success();
    }

    @PostMapping("/salary-adjusts/{id}/submit")
    public ApiResponse<Void> submitSalaryAdjustApply(@PathVariable Long id) {
        hrManageService.submitSalaryAdjustApply(id);
        return ApiResponse.success();
    }

    @PostMapping("/salary-adjusts/{id}/approve")
    public ApiResponse<Void> approveSalaryAdjustApply(@PathVariable Long id, @Valid @RequestBody ApprovalOpinionRequest request) {
        hrManageService.approveSalaryAdjustApply(id, request);
        return ApiResponse.success();
    }

    @PostMapping("/salary-adjusts/{id}/reject")
    public ApiResponse<Void> rejectSalaryAdjustApply(@PathVariable Long id, @Valid @RequestBody ApprovalOpinionRequest request) {
        hrManageService.rejectSalaryAdjustApply(id, request);
        return ApiResponse.success();
    }

    @GetMapping("/approval-records")
    public ApiResponse<List<ApprovalRecordVO>> getApprovalRecords(
            @RequestParam String bizType,
            @RequestParam Long bizId
    ) {
        return ApiResponse.success(hrManageService.getApprovalRecords(bizType, bizId));
    }
}
