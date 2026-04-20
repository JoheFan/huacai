package com.huacai.hr.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record AssessmentSaveRequest(
    Long id,
    @NotNull(message = "员工ID不能为空")
    Long employeeId,
    LocalDate assessmentMonth,
    BigDecimal assessmentScore,
    String assessmentGrade,
    String remark
) {
}
