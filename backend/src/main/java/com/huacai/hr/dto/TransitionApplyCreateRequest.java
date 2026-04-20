package com.huacai.hr.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record TransitionApplyCreateRequest(
    @NotNull(message = "转正员工ID不能为空")
    Long employeeId,
    @NotNull(message = "预计转正日期不能为空")
    LocalDate expectedFormalDate,
    String applyReason,
    String selfEvaluation,
    String growth,
    String shortcomings,
    String developmentSuggestion,
    String draftOpinion
) {
}
