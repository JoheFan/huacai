package com.huacai.hr.query;

import com.huacai.common.model.PageQuery;

public class ManagementRecordPageQuery extends PageQuery {
    private Long employeeId;
    private String recordType;

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    public String getRecordType() { return recordType; }
    public void setRecordType(String recordType) { this.recordType = recordType; }
}
