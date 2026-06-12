package com.huacai.assistant.vo;

public record AssistantConfirmCardChangeVO(
        String fieldKey,
        String fieldLabel,
        String before,
        String after
) {
}
