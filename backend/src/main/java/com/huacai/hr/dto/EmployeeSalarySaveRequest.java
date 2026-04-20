package com.huacai.hr.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record EmployeeSalarySaveRequest(
    @NotNull(message = "员工ID不能为空")
    Long employeeId,
    Long salaryStandardId,
    String salaryName,
    @NotNull(message = "工资金额不能为空")
    BigDecimal amount,
    LocalDate effectiveDate,
    LocalDate expireDate,
    String changeReason
) {
}
