package com.huacai.hr.vo;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AssessmentVO(
    Long id,
    Long employeeId,
    LocalDate assessmentMonth,
    BigDecimal assessmentScore,
    String assessmentGrade,
    String remark
) {
}
