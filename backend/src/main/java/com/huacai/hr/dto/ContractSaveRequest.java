package com.huacai.hr.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ContractSaveRequest(
    Long id,
    @NotNull(message = "员工ID不能为空")
    Long employeeId,
    @NotNull(message = "合同名称不能为空")
    String contractName,
    String contractNo,
    LocalDate contractStartDate,
    LocalDate contractEndDate,
    String contractFileUrl,
    String remark
) {
}
