package com.huacai.assistant.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record AssistantActionConfirmRequest(
        @Valid
        @NotNull(message = "requestContext不能为空")
        AssistantRequestContext requestContext,
        @Valid
        @NotNull(message = "confirmation不能为空")
        AssistantConfirmation confirmation
) {
}
