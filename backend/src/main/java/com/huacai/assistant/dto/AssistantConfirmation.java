package com.huacai.assistant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AssistantConfirmation(
        @NotBlank(message = "confirmationId不能为空")
        String confirmationId,
        @NotNull(message = "confirmed不能为空")
        Boolean confirmed,
        @NotBlank(message = "idempotencyKey不能为空")
        String idempotencyKey,
        String confirmedAt,
        String confirmationNote
) {
}
