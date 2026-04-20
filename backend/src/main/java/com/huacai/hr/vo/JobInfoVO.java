package com.huacai.hr.vo;

import java.time.LocalDate;

public record JobInfoVO(
    Long id,
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
