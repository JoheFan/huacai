package com.huacai.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerStatusLogSaveRequest(
        @NotBlank(message = "状态编码不能为空")
        @Size(max = 50, message = "状态编码长度不能超过50")
        String statusCode,
        @NotBlank(message = "状态名称不能为空")
        @Size(max = 100, message = "状态名称长度不能超过100")
        String statusName,
        String changedAt,
        @Size(max = 1000, message = "备注长度不能超过1000")
        String remark
) {
}
