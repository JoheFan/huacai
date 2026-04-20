package com.huacai.hr.vo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TransitionApplyVO(
    Long id,
    String applyNo,
    Long employeeId,
    String employeeCode,
    String employeeName,
    String phone,
    String department,
    Long orgId,
    String position,
    LocalDate joinDate,
    LocalDate expectedFormalDate,
    String applyReason,
    String selfEvaluation,
    String growth,
    String shortcomings,
    String developmentSuggestion,
    String draftOpinion,
    String hrOpinion,
    String companyOpinion,
    String adminOpinion,
    String processStatus,
    String currentNode,
    Long applicantId,
    String applicantName,
    LocalDateTime applyTime,
    LocalDateTime submitTime,
    LocalDateTime completeTime,
    LocalDateTime createdAt,
    Boolean canApprove
) {
}
