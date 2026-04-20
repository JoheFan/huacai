package com.huacai.hr.vo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record SalaryStandardVO(
    Long id,
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
    String applicableScope,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
