package com.huacai.assistant.dto;

import jakarta.validation.constraints.NotBlank;

public record AssistantRequestContext(
        String traceId,
        @NotBlank(message = "sessionId不能为空")
        String sessionId,
        String messageId,
        @NotBlank(message = "feishuUserId不能为空")
        String feishuUserId,
        @NotBlank(message = "feishuDisplayName不能为空")
        String feishuDisplayName,
        String chatId,
        String requestAt,
        String locale
) {
}
