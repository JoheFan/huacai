package com.huacai.assistant.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record AssistantFollowRecordUpdateDraftRequest(
        @Valid
        @NotNull(message = "requestContext不能为空")
        AssistantRequestContext requestContext,
        String requestText,
        @NotNull(message = "followRecordId不能为空")
        Long followRecordId,
        @Valid
        @NotNull(message = "patch不能为空")
        AssistantFollowRecordPatch patch
) {
}
