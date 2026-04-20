package com.huacai.hr.vo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record SalaryAdjustApplyVO(
    Long id,
    String applyNo,
    Long employeeId,
    String employeeName,
    String department,
    Long orgId,
    BigDecimal currentSalary,
    BigDecimal adjustAmount,
    BigDecimal newSalary,
    String applyReason,
    String draftOpinion,
    String deptOpinion,
    String hrOpinion,
    String leaderOpinion,
    String schoolLeaderOpinion,
    String financeOpinion,
    String processStatus,
    String currentNode,
    Long applicantId,
    String applicantName,
    LocalDateTime applyTime,
    LocalDateTime submitTime,
    LocalDate effectiveDate,
    LocalDateTime completeTime,
    LocalDateTime createdAt,
    Boolean canApprove
) {
}
