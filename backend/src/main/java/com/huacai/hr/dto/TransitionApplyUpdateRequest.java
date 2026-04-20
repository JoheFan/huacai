package com.huacai.hr.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransitionApplyUpdateRequest(
    Long employeeId,
    LocalDate expectedFormalDate,
    String applyReason,
    String selfEvaluation,
    String growth,
    String shortcomings,
    String developmentSuggestion,
    String draftOpinion,
    String hrOpinion,
    String companyOpinion,
    String adminOpinion
) {
}
