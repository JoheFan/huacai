package com.huacai.assistant.vo;

import java.time.LocalDateTime;
import java.util.List;

public record AssistantConfirmCardVO(
        String cardType,
        String confirmationId,
        String traceId,
        String toolName,
        String title,
        String riskLevel,
        String summary,
        AssistantConfirmCardTargetVO target,
        List<AssistantConfirmCardChangeVO> changes,
        List<String> warnings,
        List<AssistantConfirmCardActionVO> actions,
        LocalDateTime expiresAt
) {
}
