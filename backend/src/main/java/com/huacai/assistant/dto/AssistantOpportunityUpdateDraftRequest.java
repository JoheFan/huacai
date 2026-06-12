package com.huacai.assistant.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record AssistantOpportunityUpdateDraftRequest(
        @Valid
        @NotNull(message = "requestContext不能为空")
        AssistantRequestContext requestContext,
        String requestText,
        @NotNull(message = "opportunityId不能为空")
        Long opportunityId,
        @Valid
        @NotNull(message = "patch不能为空")
        AssistantOpportunityPatch patch
) {
}
