package com.huacai.opportunity.controller;

import com.huacai.common.model.ApiResponse;
import com.huacai.common.model.PageResponse;
import com.huacai.common.model.StatusUpdateRequest;
import com.huacai.opportunity.dto.FollowRecordSaveRequest;
import com.huacai.opportunity.dto.OpportunitySaveRequest;
import com.huacai.opportunity.query.FollowRecordPageQuery;
import com.huacai.opportunity.query.OpportunityPageQuery;
import com.huacai.opportunity.service.OpportunityService;
import com.huacai.opportunity.vo.FollowRecordVO;
import com.huacai.opportunity.vo.OpportunityVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "商机与跟进")
@RestController
@RequestMapping("/api/v1/opportunities")
public class OpportunityController {

    private final OpportunityService opportunityService;

    public OpportunityController(OpportunityService opportunityService) {
        this.opportunityService = opportunityService;
    }

    @GetMapping
    public ApiResponse<PageResponse<OpportunityVO>> page(@ModelAttribute OpportunityPageQuery query) {
        return ApiResponse.success(opportunityService.pageOpportunities(query));
    }

    @GetMapping("/{id}")
    public ApiResponse<OpportunityVO> detail(@PathVariable Long id) {
        return ApiResponse.success(opportunityService.getOpportunityDetail(id));
    }

    @PostMapping
    public ApiResponse<Void> create(@Valid @RequestBody OpportunitySaveRequest request) {
        opportunityService.createOpportunity(request);
        return ApiResponse.success();
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody OpportunitySaveRequest request) {
        opportunityService.updateOpportunity(id, request);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        opportunityService.deleteOpportunity(id);
        return ApiResponse.success();
    }

    @PutMapping("/{id}/status")
    public ApiResponse<Void> updateStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdateRequest request) {
        opportunityService.updateOpportunityStatus(id, request.status());
        return ApiResponse.success();
    }

    // ===================== 跟进记录 =====================

    @GetMapping("/follow-records")
    public ApiResponse<PageResponse<FollowRecordVO>> pageFollowRecords(@ModelAttribute FollowRecordPageQuery query) {
        return ApiResponse.success(opportunityService.pageFollowRecords(query));
    }

    @GetMapping("/follow-records/{id}")
    public ApiResponse<FollowRecordVO> getFollowRecord(@PathVariable Long id) {
        return ApiResponse.success(opportunityService.getFollowRecordDetail(id));
    }

    @PostMapping("/follow-records")
    public ApiResponse<Void> createFollowRecord(@Valid @RequestBody FollowRecordSaveRequest request) {
        opportunityService.createFollowRecord(request);
        return ApiResponse.success();
    }

    @PutMapping("/follow-records/{id}")
    public ApiResponse<Void> updateFollowRecord(@PathVariable Long id, @Valid @RequestBody FollowRecordSaveRequest request) {
        opportunityService.updateFollowRecord(id, request);
        return ApiResponse.success();
    }

    @DeleteMapping("/follow-records/{id}")
    public ApiResponse<Void> deleteFollowRecord(@PathVariable Long id) {
        opportunityService.deleteFollowRecord(id);
        return ApiResponse.success();
    }
}
