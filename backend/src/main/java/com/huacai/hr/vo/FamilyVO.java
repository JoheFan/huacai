package com.huacai.hr.vo;

import java.time.LocalDate;

public record FamilyVO(
    Long id,
    Long employeeId,
    String relation,
    String name,
    LocalDate birthday,
    String idCardNo,
    String politicalStatus,
    String workUnitPosition
) {
}
