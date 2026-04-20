package com.huacai.workflow.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

@TableName("wf_task")
public class WfTask {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long processId;
    private String bizType;
    private Long bizId;
    private Integer roundNo;
    private String nodeKey;
    private String nodeName;
    private String taskStatus;
    private Long assigneeId;
    private String assigneeName;
    private String candidateRoleCode;
    private Long candidateOrgId;
    private String actionResult;
    private String actionOpinion;
    private LocalDateTime actionTime;
    private Integer sortNo;
    private Long createdBy;
    private LocalDateTime createdAt;
    private Long updatedBy;
    private LocalDateTime updatedAt;
    @TableLogic
    private Integer deletedFlag;
    private Integer version;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProcessId() { return processId; }
    public void setProcessId(Long processId) { this.processId = processId; }
    public String getBizType() { return bizType; }
    public void setBizType(String bizType) { this.bizType = bizType; }
    public Long getBizId() { return bizId; }
    public void setBizId(Long bizId) { this.bizId = bizId; }
    public Integer getRoundNo() { return roundNo; }
    public void setRoundNo(Integer roundNo) { this.roundNo = roundNo; }
    public String getNodeKey() { return nodeKey; }
    public void setNodeKey(String nodeKey) { this.nodeKey = nodeKey; }
    public String getNodeName() { return nodeName; }
    public void setNodeName(String nodeName) { this.nodeName = nodeName; }
    public String getTaskStatus() { return taskStatus; }
    public void setTaskStatus(String taskStatus) { this.taskStatus = taskStatus; }
    public Long getAssigneeId() { return assigneeId; }
    public void setAssigneeId(Long assigneeId) { this.assigneeId = assigneeId; }
    public String getAssigneeName() { return assigneeName; }
    public void setAssigneeName(String assigneeName) { this.assigneeName = assigneeName; }
    public String getCandidateRoleCode() { return candidateRoleCode; }
    public void setCandidateRoleCode(String candidateRoleCode) { this.candidateRoleCode = candidateRoleCode; }
    public Long getCandidateOrgId() { return candidateOrgId; }
    public void setCandidateOrgId(Long candidateOrgId) { this.candidateOrgId = candidateOrgId; }
    public String getActionResult() { return actionResult; }
    public void setActionResult(String actionResult) { this.actionResult = actionResult; }
    public String getActionOpinion() { return actionOpinion; }
    public void setActionOpinion(String actionOpinion) { this.actionOpinion = actionOpinion; }
    public LocalDateTime getActionTime() { return actionTime; }
    public void setActionTime(LocalDateTime actionTime) { this.actionTime = actionTime; }
    public Integer getSortNo() { return sortNo; }
    public void setSortNo(Integer sortNo) { this.sortNo = sortNo; }
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
