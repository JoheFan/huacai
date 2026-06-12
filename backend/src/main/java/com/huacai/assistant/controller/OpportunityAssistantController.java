package com.huacai.assistant.controller;

import com.huacai.assistant.dto.AssistantFollowRecordCreateDraftRequest;
import com.huacai.assistant.dto.AssistantFollowRecordSearchRequest;
import com.huacai.assistant.dto.AssistantFollowRecordUpdateDraftRequest;
import com.huacai.assistant.dto.AssistantOpportunityCreateDraftRequest;
import com.huacai.assistant.dto.AssistantOpportunityDetailRequest;
import com.huacai.assistant.dto.AssistantOpportunitySearchRequest;
import com.huacai.assistant.dto.AssistantOpportunityUpdateDraftRequest;
import com.huacai.assistant.service.OpportunityAssistantService;
import com.huacai.assistant.vo.AssistantConfirmCardVO;
import com.huacai.common.model.ApiResponse;
import com.huacai.common.model.PageResponse;
import com.huacai.opportunity.vo.FollowRecordVO;
import com.huacai.opportunity.vo.OpportunityVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "飞书商机助手")
@RestController
@RequestMapping("/api/v1/assistant/opportunities")
public class OpportunityAssistantController {

    private final OpportunityAssistantService opportunityAssistantService;

    public OpportunityAssistantController(OpportunityAssistantService opportunityAssistantService) {
        this.opportunityAssistantService = opportunityAssistantService;
    }

    @PostMapping("/search")
    public ApiResponse<PageResponse<OpportunityVO>> search(@Valid @RequestBody AssistantOpportunitySearchRequest request) {
        return ApiResponse.success(opportunityAssistantService.searchOpportunities(request));
    }

    @PostMapping("/detail")
    public ApiResponse<OpportunityVO> detail(@Valid @RequestBody AssistantOpportunityDetailRequest request) {
        return ApiResponse.success(opportunityAssistantService.getOpportunityDetail(request));
    }

    @PostMapping("/follow-records/search")
    public ApiResponse<PageResponse<FollowRecordVO>> searchFollowRecords(
            @Valid @RequestBody AssistantFollowRecordSearchRequest request
    ) {
        return ApiResponse.success(opportunityAssistantService.searchFollowRecords(request));
    }

    @PostMapping("/create/draft")
    public ApiResponse<AssistantConfirmCardVO> draftCreate(@Valid @RequestBody AssistantOpportunityCreateDraftRequest request) {
        return ApiResponse.success(opportunityAssistantService.draftCreateOpportunity(request));
    }

    @PostMapping("/update-fields/draft")
    public ApiResponse<AssistantConfirmCardVO> draftUpdate(
            @Valid @RequestBody AssistantOpportunityUpdateDraftRequest request
    ) {
        return ApiResponse.success(opportunityAssistantService.draftUpdateOpportunity(request));
    }

    @PostMapping("/follow-records/create/draft")
    public ApiResponse<AssistantConfirmCardVO> draftCreateFollow(
            @Valid @RequestBody AssistantFollowRecordCreateDraftRequest request
    ) {
        return ApiResponse.success(opportunityAssistantService.draftCreateFollowRecord(request));
    }

    @PostMapping("/follow-records/update/draft")
    public ApiResponse<AssistantConfirmCardVO> draftUpdateFollow(
            @Valid @RequestBody AssistantFollowRecordUpdateDraftRequest request
    ) {
        return ApiResponse.success(opportunityAssistantService.draftUpdateFollowRecord(request));
    }
}
