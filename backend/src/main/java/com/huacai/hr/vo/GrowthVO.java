package com.huacai.hr.vo;

import java.time.LocalDate;

public record GrowthVO(
    Long id,
    Long employeeId,
    LocalDate startDate,
    LocalDate endDate,
    String workName
) {
}
