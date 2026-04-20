package com.huacai.hr.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SalaryStandardUpdateRequest(
    String salaryName,
    BigDecimal amount,
    String jobTitle,
    Long orgId,
    String orgName,
    String description,
    Integer sortNo,
    String status,
    LocalDate effectiveDate,
    LocalDate expireDate,
    String versionNo,
    String applicableScope
) {
}
