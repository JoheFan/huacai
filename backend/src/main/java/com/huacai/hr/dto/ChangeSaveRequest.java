package com.huacai.hr.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ChangeSaveRequest(
    Long id,
    @NotNull(message = "员工ID不能为空")
    Long employeeId,
    String currentDepartment,
    String currentPosition,
    String currentRankLevel,
    String originalDepartment,
    String originalPosition,
    String originalRankLevel,
    LocalDate reportDate,
    String changeType,
    String changeReason
) {
}
