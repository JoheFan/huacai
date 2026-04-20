package com.huacai.workflow.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReimbursementVO(
    Long id,
    String applyNo,
    Long applicantId,
    String applicantName,
    Long applicantOrgId,
    String applicantOrgName,
    Long reimbursementUserId,
    String reimbursementUserName,
    BigDecimal amount,
    String reason,
    String processStatus,
    String currentNodeKey,
    String currentNodeName,
    Integer currentRound,
    Integer resubmitCount,
    LocalDateTime submitTime,
    LocalDateTime completeTime,
    LocalDateTime withdrawTime,
    LocalDateTime createdAt,
    Boolean canApprove,
    Boolean canWithdraw,
    Boolean canEdit,
    Boolean canResubmit
) {
}
