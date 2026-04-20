package com.huacai.hr.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record SalaryStandardCreateRequest(
    @NotBlank(message = "工资名称不能为空")
    String salaryName,
    @NotNull(message = "工资金额不能为空")
    BigDecimal amount,
    String jobTitle,
    Long orgId,
    String orgName,
    String description,
    Integer sortNo,
    @NotBlank(message = "状态不能为空")
    String status,
    LocalDate effectiveDate,
    LocalDate expireDate,
    String versionNo,
    String applicableScope
) {
}
