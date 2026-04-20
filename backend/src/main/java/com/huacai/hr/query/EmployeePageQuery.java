package com.huacai.hr.query;

import com.huacai.common.model.PageQuery;

public class EmployeePageQuery extends PageQuery {
    private String keyword;
    private String employmentStatus;
    private Long orgId;

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public String getEmploymentStatus() { return employmentStatus; }
    public void setEmploymentStatus(String employmentStatus) { this.employmentStatus = employmentStatus; }
    public Long getOrgId() { return orgId; }
    public void setOrgId(Long orgId) { this.orgId = orgId; }
}
