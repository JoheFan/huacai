package com.huacai.assistant.vo;

public record AssistantWriteReceiptVO(
        boolean success,
        String traceId,
        String toolName,
        String targetType,
        Long targetId,
        String message
) {
}
