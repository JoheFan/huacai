package com.huacai.opportunity.query;

import com.huacai.common.model.PageQuery;

public class FollowRecordPageQuery extends PageQuery {
    private Long opportunityId;
    private String keyword;

    public Long getOpportunityId() { return opportunityId; }
    public void setOpportunityId(Long opportunityId) { this.opportunityId = opportunityId; }
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
}
