package com.huacai.analysis.query;

import com.huacai.common.model.PageQuery;

public class EmployeePerformanceQuery extends PageQuery {
    private String keyword;
    private Long employeeId;
    private String periodDate;

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    public String getPeriodDate() { return periodDate; }
    public void setPeriodDate(String periodDate) { this.periodDate = periodDate; }
}
