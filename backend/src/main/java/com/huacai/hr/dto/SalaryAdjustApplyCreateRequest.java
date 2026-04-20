package com.huacai.hr.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record SalaryAdjustApplyCreateRequest(
    @NotNull(message = "调薪员工ID不能为空")
    Long employeeId,
    @NotNull(message = "申请调薪幅度不能为空")
    BigDecimal adjustAmount,
    String applyReason,
    String draftOpinion
) {
}
