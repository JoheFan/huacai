package com.huacai.hr.vo;

import java.time.LocalDate;

public record ContractVO(
    Long id,
    Long employeeId,
    String contractName,
    String contractNo,
    LocalDate contractStartDate,
    LocalDate contractEndDate,
    String contractFileUrl,
    String remark
) {
}
