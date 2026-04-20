package com.huacai.workflow.vo;

import java.time.LocalDateTime;

public record MyApprovalTaskVO(
    Long taskId,
    Long processId,
    String bizType,
    Long bizId,
    String bizNo,
    String title,
    String nodeKey,
    String nodeName,
    String taskStatus,
    String applicantName,
    String applicantOrgName,
    LocalDateTime createdAt,
    LocalDateTime taskCreatedAt
) {
}
