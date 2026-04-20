package com.huacai.hr.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record JobInfoSaveRequest(
    @NotNull(message = "员工ID不能为空")
    Long employeeId,
    String employeeCode,
    LocalDate joinDate,
    LocalDate formalDate,
    String workUnit,
    String workMode,
    LocalDate borrowDispatchDate,
    String department,
    String rankLevel,
    String jobCategory,
    String position,
    Integer sortNo,
    Integer is编制
) {
}
