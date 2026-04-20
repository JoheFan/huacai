package com.huacai.hr.query;

import com.huacai.common.model.PageQuery;

public class TransitionApplyPageQuery extends PageQuery {
    private String keyword;
    private String processStatus;
    private String scope;
    private Long employeeId;

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public String getProcessStatus() { return processStatus; }
    public void setProcessStatus(String processStatus) { this.processStatus = processStatus; }
    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
}
