package com.huacai.assistant.service;

import com.huacai.assistant.dto.AssistantActionConfirmRequest;
import com.huacai.assistant.dto.AssistantFollowRecordCreateDraftRequest;
import com.huacai.assistant.dto.AssistantFollowRecordSearchRequest;
import com.huacai.assistant.dto.AssistantFollowRecordUpdateDraftRequest;
import com.huacai.assistant.dto.AssistantOpportunityCreateDraftRequest;
import com.huacai.assistant.dto.AssistantOpportunityDetailRequest;
import com.huacai.assistant.dto.AssistantOpportunitySearchRequest;
import com.huacai.assistant.dto.AssistantOpportunityUpdateDraftRequest;
import com.huacai.assistant.vo.AssistantConfirmCardVO;
import com.huacai.assistant.vo.AssistantWriteReceiptVO;
import com.huacai.common.model.PageResponse;
import com.huacai.opportunity.vo.FollowRecordVO;
import com.huacai.opportunity.vo.OpportunityVO;

public interface OpportunityAssistantService {

    PageResponse<OpportunityVO> searchOpportunities(AssistantOpportunitySearchRequest request);

    OpportunityVO getOpportunityDetail(AssistantOpportunityDetailRequest request);

    PageResponse<FollowRecordVO> searchFollowRecords(AssistantFollowRecordSearchRequest request);

    AssistantConfirmCardVO draftCreateOpportunity(AssistantOpportunityCreateDraftRequest request);

    AssistantConfirmCardVO draftUpdateOpportunity(AssistantOpportunityUpdateDraftRequest request);

    AssistantConfirmCardVO draftCreateFollowRecord(AssistantFollowRecordCreateDraftRequest request);

    AssistantConfirmCardVO draftUpdateFollowRecord(AssistantFollowRecordUpdateDraftRequest request);

    AssistantWriteReceiptVO confirmAction(AssistantActionConfirmRequest request);
}
