package com.huacai.hr.query;

import com.huacai.common.model.PageQuery;

public class SalaryStandardPageQuery extends PageQuery {
    private String keyword;
    private String status;
    private Long orgId;

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getOrgId() { return orgId; }
    public void setOrgId(Long orgId) { this.orgId = orgId; }
}
