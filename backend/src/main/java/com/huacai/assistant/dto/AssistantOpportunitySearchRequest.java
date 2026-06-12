package com.huacai.assistant.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record AssistantOpportunitySearchRequest(
        @Valid
        @NotNull(message = "requestContext不能为空")
        AssistantRequestContext requestContext,
        String keyword,
        Long customerId,
        String stageCode,
        String status,
        Integer pageNum,
        Integer pageSize
) {
}
