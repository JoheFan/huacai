package com.huacai.hr.vo;

import java.time.LocalDate;

public record RemovalVO(
    Long id,
    Long employeeId,
    String removalType,
    LocalDate removalDate,
    LocalDate expectedRetireDate,
    String reason
) {
}
