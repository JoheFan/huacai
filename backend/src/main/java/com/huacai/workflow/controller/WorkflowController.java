package com.huacai.workflow.controller;

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
    public PageResponse<TransitionApplyVO> pageTransitionApplies(TransitionApplyPageQuery query) {
        return hrManageService.pageTransitionApplies(query);
    }

    @GetMapping("/transitions/{id}")
    public TransitionApplyVO getTransitionApply(@PathVariable Long id) {
        return hrManageService.getTransitionApply(id);
    }

    @PostMapping("/transitions")
    public void createTransitionApply(@Valid @RequestBody TransitionApplyCreateRequest request) {
        hrManageService.createTransitionApply(request);
    }

    @PutMapping("/transitions/{id}")
    public void updateTransitionApply(@PathVariable Long id, @RequestBody TransitionApplyUpdateRequest request) {
        hrManageService.updateTransitionApply(id, request);
    }

    @PostMapping("/transitions/{id}/submit")
    public void submitTransitionApply(@PathVariable Long id) {
        hrManageService.submitTransitionApply(id);
    }

    @PostMapping("/transitions/{id}/approve")
    public void approveTransitionApply(@PathVariable Long id, @Valid @RequestBody ApprovalOpinionRequest request) {
        hrManageService.approveTransitionApply(id, request);
    }

    @PostMapping("/transitions/{id}/reject")
    public void rejectTransitionApply(@PathVariable Long id, @Valid @RequestBody ApprovalOpinionRequest request) {
        hrManageService.rejectTransitionApply(id, request);
    }

    @GetMapping("/salary-adjusts")
    public PageResponse<SalaryAdjustApplyVO> pageSalaryAdjustApplies(SalaryAdjustApplyPageQuery query) {
        return hrManageService.pageSalaryAdjustApplies(query);
    }

    @GetMapping("/salary-adjusts/{id}")
    public SalaryAdjustApplyVO getSalaryAdjustApply(@PathVariable Long id) {
        return hrManageService.getSalaryAdjustApply(id);
    }

    @PostMapping("/salary-adjusts")
    public void createSalaryAdjustApply(@Valid @RequestBody SalaryAdjustApplyCreateRequest request) {
        hrManageService.createSalaryAdjustApply(request);
    }

    @PutMapping("/salary-adjusts/{id}")
    public void updateSalaryAdjustApply(@PathVariable Long id, @RequestBody SalaryAdjustApplyUpdateRequest request) {
        hrManageService.updateSalaryAdjustApply(id, request);
    }

    @PostMapping("/salary-adjusts/{id}/submit")
    public void submitSalaryAdjustApply(@PathVariable Long id) {
        hrManageService.submitSalaryAdjustApply(id);
    }

    @PostMapping("/salary-adjusts/{id}/approve")
    public void approveSalaryAdjustApply(@PathVariable Long id, @Valid @RequestBody ApprovalOpinionRequest request) {
        hrManageService.approveSalaryAdjustApply(id, request);
    }

    @PostMapping("/salary-adjusts/{id}/reject")
    public void rejectSalaryAdjustApply(@PathVariable Long id, @Valid @RequestBody ApprovalOpinionRequest request) {
        hrManageService.rejectSalaryAdjustApply(id, request);
    }

    @GetMapping("/approval-records")
    public List<ApprovalRecordVO> getApprovalRecords(
            @RequestParam String bizType,
            @RequestParam Long bizId
    ) {
        return hrManageService.getApprovalRecords(bizType, bizId);
    }
}
