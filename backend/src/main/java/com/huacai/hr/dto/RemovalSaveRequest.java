package com.huacai.hr.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record RemovalSaveRequest(
    @NotNull(message = "员工ID不能为空")
    Long employeeId,
    @NotNull(message = "调离类型不能为空")
    String removalType,
    LocalDate removalDate,
    LocalDate expectedRetireDate,
    String reason
) {
}
