package com.huacai.workflow.controller;

import com.huacai.common.model.ApiResponse;
import com.huacai.common.model.PageResponse;
import com.huacai.workflow.dto.ApprovalActionRequest;
import com.huacai.workflow.query.MyApprovalPageQuery;
import com.huacai.workflow.service.MyApprovalService;
import com.huacai.workflow.vo.ActionLogVO;
import com.huacai.workflow.vo.MyApprovalTaskVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workflow")
public class MyWorkflowController {

    private final MyApprovalService myApprovalService;

    public MyWorkflowController(MyApprovalService myApprovalService) {
        this.myApprovalService = myApprovalService;
    }

    @GetMapping("/my-approvals/todos")
    public PageResponse<MyApprovalTaskVO> pageTodos(MyApprovalPageQuery query) {
        return myApprovalService.pageTodos(query);
    }

    @GetMapping("/my-approvals/initiated")
    public PageResponse<MyApprovalTaskVO> pageInitiated(MyApprovalPageQuery query) {
        return myApprovalService.pageInitiated(query);
    }

    @GetMapping("/my-approvals/processed")
    public PageResponse<MyApprovalTaskVO> pageProcessed(MyApprovalPageQuery query) {
        return myApprovalService.pageProcessed(query);
    }

    @PostMapping("/tasks/{taskId}/approve")
    public ApiResponse<Void> approveTask(@PathVariable Long taskId, @RequestBody ApprovalActionRequest request) {
        myApprovalService.approveTask(taskId, request.opinion());
        return ApiResponse.success();
    }

    @PostMapping("/tasks/{taskId}/reject")
    public ApiResponse<Void> rejectTask(@PathVariable Long taskId, @RequestBody ApprovalActionRequest request) {
        myApprovalService.rejectTask(taskId, request.opinion());
        return ApiResponse.success();
    }

    @GetMapping("/processes/{bizType}/{bizId}/timeline")
    public ApiResponse<List<ActionLogVO>> getProcessTimeline(
            @PathVariable String bizType,
            @PathVariable Long bizId
    ) {
        return ApiResponse.success(myApprovalService.getProcessTimeline(bizType, bizId));
    }
}
