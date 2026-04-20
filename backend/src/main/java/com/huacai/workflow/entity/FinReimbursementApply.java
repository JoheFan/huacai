package com.huacai.workflow.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName("fin_reimbursement_apply")
public class FinReimbursementApply {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String applyNo;
    private Long applicantId;
    private String applicantName;
    private Long applicantOrgId;
    private String applicantOrgName;
    private Long reimbursementUserId;
    private String reimbursementUserName;
    private BigDecimal amount;
    private String reason;
    private String processStatus;
    private String currentNodeKey;
    private String currentNodeName;
    private Integer currentRound;
    private Integer resubmitCount;
    private LocalDateTime submitTime;
    private LocalDateTime completeTime;
    private LocalDateTime withdrawTime;
    private Long createdBy;
    private LocalDateTime createdAt;
    private Long updatedBy;
    private LocalDateTime updatedAt;
    @TableLogic
    private Integer deletedFlag;
    private Integer version;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getApplyNo() { return applyNo; }
    public void setApplyNo(String applyNo) { this.applyNo = applyNo; }
    public Long getApplicantId() { return applicantId; }
    public void setApplicantId(Long applicantId) { this.applicantId = applicantId; }
    public String getApplicantName() { return applicantName; }
    public void setApplicantName(String applicantName) { this.applicantName = applicantName; }
    public Long getApplicantOrgId() { return applicantOrgId; }
    public void setApplicantOrgId(Long applicantOrgId) { this.applicantOrgId = applicantOrgId; }
    public String getApplicantOrgName() { return applicantOrgName; }
    public void setApplicantOrgName(String applicantOrgName) { this.applicantOrgName = applicantOrgName; }
    public Long getReimbursementUserId() { return reimbursementUserId; }
    public void setReimbursementUserId(Long reimbursementUserId) { this.reimbursementUserId = reimbursementUserId; }
    public String getReimbursementUserName() { return reimbursementUserName; }
    public void setReimbursementUserName(String reimbursementUserName) { this.reimbursementUserName = reimbursementUserName; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getProcessStatus() { return processStatus; }
    public void setProcessStatus(String processStatus) { this.processStatus = processStatus; }
    public String getCurrentNodeKey() { return currentNodeKey; }
    public void setCurrentNodeKey(String currentNodeKey) { this.currentNodeKey = currentNodeKey; }
    public String getCurrentNodeName() { return currentNodeName; }
    public void setCurrentNodeName(String currentNodeName) { this.currentNodeName = currentNodeName; }
    public Integer getCurrentRound() { return currentRound; }
    public void setCurrentRound(Integer currentRound) { this.currentRound = currentRound; }
    public Integer getResubmitCount() { return resubmitCount; }
    public void setResubmitCount(Integer resubmitCount) { this.resubmitCount = resubmitCount; }
    public LocalDateTime getSubmitTime() { return submitTime; }
    public void setSubmitTime(LocalDateTime submitTime) { this.submitTime = submitTime; }
    public LocalDateTime getCompleteTime() { return completeTime; }
    public void setCompleteTime(LocalDateTime completeTime) { this.completeTime = completeTime; }
    public LocalDateTime getWithdrawTime() { return withdrawTime; }
    public void setWithdrawTime(LocalDateTime withdrawTime) { this.withdrawTime = withdrawTime; }
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Long getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(Long updatedBy) { this.updatedBy = updatedBy; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public Integer getDeletedFlag() { return deletedFlag; }
    public void setDeletedFlag(Integer deletedFlag) { this.deletedFlag = deletedFlag; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
}
