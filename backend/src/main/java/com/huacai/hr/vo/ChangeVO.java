package com.huacai.hr.vo;

import java.time.LocalDate;

public record ChangeVO(
    Long id,
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
