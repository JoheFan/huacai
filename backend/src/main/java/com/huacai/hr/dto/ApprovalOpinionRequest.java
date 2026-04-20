package com.huacai.hr.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ApprovalOpinionRequest(
    @NotBlank(message = "审批意见不能为空")
    String opinion,
    @NotNull(message = "处理结果不能为空")
    String result
) {
}
