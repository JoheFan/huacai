package com.huacai.hr.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.huacai.common.entity.AuditableEntity;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName("hr_salary_adjust_apply")
public class HrSalaryAdjustApply extends AuditableEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String applyNo;
    private Long employeeId;
    private String employeeName;
    private String department;
    private Long orgId;
    private BigDecimal currentSalary;
    private BigDecimal adjustAmount;
    private BigDecimal newSalary;
    private String applyReason;
    private String deptOpinion;
    private String hrOpinion;
    private String leaderOpinion;
    private String schoolLeaderOpinion;
    private String financeOpinion;
    private String draftOpinion;
    private String processStatus;
    private String currentNode;
    private Long applicantId;
    private LocalDateTime applyTime;
    private LocalDateTime submitTime;
    private LocalDate effectiveDate;
    private LocalDateTime completeTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getApplyNo() { return applyNo; }
    public void setApplyNo(String applyNo) { this.applyNo = applyNo; }
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public Long getOrgId() { return orgId; }
    public void setOrgId(Long orgId) { this.orgId = orgId; }
    public BigDecimal getCurrentSalary() { return currentSalary; }
    public void setCurrentSalary(BigDecimal currentSalary) { this.currentSalary = currentSalary; }
    public BigDecimal getAdjustAmount() { return adjustAmount; }
    public void setAdjustAmount(BigDecimal adjustAmount) { this.adjustAmount = adjustAmount; }
    public BigDecimal getNewSalary() { return newSalary; }
    public void setNewSalary(BigDecimal newSalary) { this.newSalary = newSalary; }
    public String getApplyReason() { return applyReason; }
    public void setApplyReason(String applyReason) { this.applyReason = applyReason; }
    public String getDeptOpinion() { return deptOpinion; }
    public void setDeptOpinion(String deptOpinion) { this.deptOpinion = deptOpinion; }
    public String getHrOpinion() { return hrOpinion; }
    public void setHrOpinion(String hrOpinion) { this.hrOpinion = hrOpinion; }
    public String getLeaderOpinion() { return leaderOpinion; }
    public void setLeaderOpinion(String leaderOpinion) { this.leaderOpinion = leaderOpinion; }
    public String getSchoolLeaderOpinion() { return schoolLeaderOpinion; }
    public void setSchoolLeaderOpinion(String schoolLeaderOpinion) { this.schoolLeaderOpinion = schoolLeaderOpinion; }
    public String getFinanceOpinion() { return financeOpinion; }
    public void setFinanceOpinion(String financeOpinion) { this.financeOpinion = financeOpinion; }
    public String getDraftOpinion() { return draftOpinion; }
    public void setDraftOpinion(String draftOpinion) { this.draftOpinion = draftOpinion; }
    public String getProcessStatus() { return processStatus; }
    public void setProcessStatus(String processStatus) { this.processStatus = processStatus; }
    public String getCurrentNode() { return currentNode; }
    public void setCurrentNode(String currentNode) { this.currentNode = currentNode; }
    public Long getApplicantId() { return applicantId; }
    public void setApplicantId(Long applicantId) { this.applicantId = applicantId; }
    public LocalDateTime getApplyTime() { return applyTime; }
    public void setApplyTime(LocalDateTime applyTime) { this.applyTime = applyTime; }
    public LocalDateTime getSubmitTime() { return submitTime; }
    public void setSubmitTime(LocalDateTime submitTime) { this.submitTime = submitTime; }
    public LocalDate getEffectiveDate() { return effectiveDate; }
    public void setEffectiveDate(LocalDate effectiveDate) { this.effectiveDate = effectiveDate; }
    public LocalDateTime getCompleteTime() { return completeTime; }
    public void setCompleteTime(LocalDateTime completeTime) { this.completeTime = completeTime; }
}
