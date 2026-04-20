package com.huacai.hr.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record FamilySaveRequest(
    Long id,
    @NotNull(message = "员工ID不能为空")
    Long employeeId,
    String relation,
    String name,
    LocalDate birthday,
    String idCardNo,
    String politicalStatus,
    String workUnitPosition
) {
}
