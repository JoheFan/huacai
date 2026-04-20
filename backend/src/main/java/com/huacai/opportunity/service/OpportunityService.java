package com.huacai.opportunity.service;

import com.huacai.common.model.PageResponse;
import com.huacai.opportunity.dto.FollowRecordSaveRequest;
import com.huacai.opportunity.dto.OpportunitySaveRequest;
import com.huacai.opportunity.query.FollowRecordPageQuery;
import com.huacai.opportunity.query.OpportunityPageQuery;
import com.huacai.opportunity.vo.FollowRecordVO;
import com.huacai.opportunity.vo.OpportunityVO;

public interface OpportunityService {

    PageResponse<OpportunityVO> pageOpportunities(OpportunityPageQuery query);

    OpportunityVO getOpportunityDetail(Long id);

    void createOpportunity(OpportunitySaveRequest request);

    void updateOpportunity(Long id, OpportunitySaveRequest request);

    void deleteOpportunity(Long id);

    void updateOpportunityStatus(Long id, String status);

    // ===================== 跟进记录 =====================

    PageResponse<FollowRecordVO> pageFollowRecords(FollowRecordPageQuery query);

    FollowRecordVO getFollowRecordDetail(Long id);

    void createFollowRecord(FollowRecordSaveRequest request);

    void updateFollowRecord(Long id, FollowRecordSaveRequest request);

    void deleteFollowRecord(Long id);
}
