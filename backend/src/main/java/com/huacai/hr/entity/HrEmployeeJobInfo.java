package com.huacai.hr.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.huacai.common.entity.AuditableEntity;
import java.time.LocalDate;

@TableName("hr_employee_job_info")
public class HrEmployeeJobInfo extends AuditableEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long employeeId;
    private String employeeCode;
    private LocalDate joinDate;
    private LocalDate formalDate;
    private String workUnit;
    private String workMode;
    private LocalDate borrowDispatchDate;
    private String department;
    private String rankLevel;
    private String jobCategory;
    private String position;
    private Integer sortNo;
    private Integer is编制;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }
    public LocalDate getJoinDate() { return joinDate; }
    public void setJoinDate(LocalDate joinDate) { this.joinDate = joinDate; }
    public LocalDate getFormalDate() { return formalDate; }
    public void setFormalDate(LocalDate formalDate) { this.formalDate = formalDate; }
    public String getWorkUnit() { return workUnit; }
    public void setWorkUnit(String workUnit) { this.workUnit = workUnit; }
    public String getWorkMode() { return workMode; }
    public void setWorkMode(String workMode) { this.workMode = workMode; }
    public LocalDate getBorrowDispatchDate() { return borrowDispatchDate; }
    public void setBorrowDispatchDate(LocalDate borrowDispatchDate) { this.borrowDispatchDate = borrowDispatchDate; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getRankLevel() { return rankLevel; }
    public void setRankLevel(String rankLevel) { this.rankLevel = rankLevel; }
    public String getJobCategory() { return jobCategory; }
    public void setJobCategory(String jobCategory) { this.jobCategory = jobCategory; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public Integer getSortNo() { return sortNo; }
    public void setSortNo(Integer sortNo) { this.sortNo = sortNo; }
    public Integer getIs编制() { return is编制; }
    public void setIs编制(Integer is编制) { this.is编制 = is编制; }
}
