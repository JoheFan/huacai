package com.huacai.hr.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.huacai.common.entity.AuditableEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName("hr_transition_apply")
public class HrTransitionApply extends AuditableEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String applyNo;
    private Long employeeId;
    private String employeeCode;
    private String employeeName;
    private String phone;
    private String department;
    private Long orgId;
    private String position;
    private LocalDate joinDate;
    private LocalDate expectedFormalDate;
    private String applyReason;
    private String selfEvaluation;
    private String growth;
    private String shortcomings;
    private String developmentSuggestion;
    private String draftOpinion;
    private String hrOpinion;
    private String companyOpinion;
    private String adminOpinion;
    private String processStatus;
    private String currentNode;
    private Long applicantId;
    private LocalDateTime applyTime;
    private LocalDateTime submitTime;
    private LocalDateTime completeTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getApplyNo() { return applyNo; }
    public void setApplyNo(String applyNo) { this.applyNo = applyNo; }
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public Long getOrgId() { return orgId; }
    public void setOrgId(Long orgId) { this.orgId = orgId; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public LocalDate getJoinDate() { return joinDate; }
    public void setJoinDate(LocalDate joinDate) { this.joinDate = joinDate; }
    public LocalDate getExpectedFormalDate() { return expectedFormalDate; }
    public void setExpectedFormalDate(LocalDate expectedFormalDate) { this.expectedFormalDate = expectedFormalDate; }
    public String getApplyReason() { return applyReason; }
    public void setApplyReason(String applyReason) { this.applyReason = applyReason; }
    public String getSelfEvaluation() { return selfEvaluation; }
    public void setSelfEvaluation(String selfEvaluation) { this.selfEvaluation = selfEvaluation; }
    public String getGrowth() { return growth; }
    public void setGrowth(String growth) { this.growth = growth; }
    public String getShortcomings() { return shortcomings; }
    public void setShortcomings(String shortcomings) { this.shortcomings = shortcomings; }
    public String getDevelopmentSuggestion() { return developmentSuggestion; }
    public void setDevelopmentSuggestion(String developmentSuggestion) { this.developmentSuggestion = developmentSuggestion; }
    public String getDraftOpinion() { return draftOpinion; }
    public void setDraftOpinion(String draftOpinion) { this.draftOpinion = draftOpinion; }
    public String getHrOpinion() { return hrOpinion; }
    public void setHrOpinion(String hrOpinion) { this.hrOpinion = hrOpinion; }
    public String getCompanyOpinion() { return companyOpinion; }
    public void setCompanyOpinion(String companyOpinion) { this.companyOpinion = companyOpinion; }
    public String getAdminOpinion() { return adminOpinion; }
    public void setAdminOpinion(String adminOpinion) { this.adminOpinion = adminOpinion; }
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
    public LocalDateTime getCompleteTime() { return completeTime; }
    public void setCompleteTime(LocalDateTime completeTime) { this.completeTime = completeTime; }
}
