package com.huacai.assistant.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record AssistantFollowRecordSearchRequest(
        @Valid
        @NotNull(message = "requestContext不能为空")
        AssistantRequestContext requestContext,
        @NotNull(message = "opportunityId不能为空")
        Long opportunityId,
        Integer pageNum,
        Integer pageSize
) {
}
