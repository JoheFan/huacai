package com.huacai.hr.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record GrowthSaveRequest(
    Long id,
    @NotNull(message = "员工ID不能为空")
    Long employeeId,
    LocalDate startDate,
    LocalDate endDate,
    String workName
) {
}
