package com.huacai.hr.dto;

import java.math.BigDecimal;

public record SalaryAdjustApplyUpdateRequest(
    Long employeeId,
    BigDecimal adjustAmount,
    String applyReason,
    String draftOpinion,
    String deptOpinion,
    String hrOpinion,
    String leaderOpinion,
    String schoolLeaderOpinion,
    String financeOpinion
) {
}
