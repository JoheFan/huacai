package com.huacai.hr.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.huacai.common.entity.AuditableEntity;
import java.time.LocalDate;

@TableName("hr_employee_change")
public class HrEmployeeChange extends AuditableEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long employeeId;
    private String currentDepartment;
    private String currentPosition;
    private String currentRankLevel;
    private String originalDepartment;
    private String originalPosition;
    private String originalRankLevel;
    private LocalDate reportDate;
    private String changeType;
    private String changeReason;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    public String getCurrentDepartment() { return currentDepartment; }
    public void setCurrentDepartment(String currentDepartment) { this.currentDepartment = currentDepartment; }
    public String getCurrentPosition() { return currentPosition; }
    public void setCurrentPosition(String currentPosition) { this.currentPosition = currentPosition; }
    public String getCurrentRankLevel() { return currentRankLevel; }
    public void setCurrentRankLevel(String currentRankLevel) { this.currentRankLevel = currentRankLevel; }
    public String getOriginalDepartment() { return originalDepartment; }
    public void setOriginalDepartment(String originalDepartment) { this.originalDepartment = originalDepartment; }
    public String getOriginalPosition() { return originalPosition; }
    public void setOriginalPosition(String originalPosition) { this.originalPosition = originalPosition; }
    public String getOriginalRankLevel() { return originalRankLevel; }
    public void setOriginalRankLevel(String originalRankLevel) { this.originalRankLevel = originalRankLevel; }
    public LocalDate getReportDate() { return reportDate; }
    public void setReportDate(LocalDate reportDate) { this.reportDate = reportDate; }
    public String getChangeType() { return changeType; }
    public void setChangeType(String changeType) { this.changeType = changeType; }
    public String getChangeReason() { return changeReason; }
    public void setChangeReason(String changeReason) { this.changeReason = changeReason; }
}
