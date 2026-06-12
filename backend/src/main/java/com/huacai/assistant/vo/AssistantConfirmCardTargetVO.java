package com.huacai.assistant.vo;

public record AssistantConfirmCardTargetVO(
        Long customerId,
        String customerName,
        String customerNo,
        Long opportunityId,
        String opportunityStage,
        Long followRecordId
) {
}
