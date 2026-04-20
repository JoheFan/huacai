package com.huacai.hr.vo;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EmployeeSalaryVO(
    Long id,
    Long employeeId,
    Long salaryStandardId,
    String salaryName,
    BigDecimal amount,
    LocalDate effectiveDate,
    LocalDate expireDate,
    String changeReason
) {
}
