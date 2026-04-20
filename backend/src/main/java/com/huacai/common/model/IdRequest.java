package com.huacai.common.model;

import jakarta.validation.constraints.NotNull;

public record IdRequest(
        @NotNull(message = "id不能为空")
        Long id
) {
}
