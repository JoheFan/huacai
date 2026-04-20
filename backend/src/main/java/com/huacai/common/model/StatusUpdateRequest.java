package com.huacai.common.model;

import jakarta.validation.constraints.NotBlank;

public record StatusUpdateRequest(
        @NotBlank(message = "status不能为空")
        String status,
        String statusName,
        String remark
) {
}
