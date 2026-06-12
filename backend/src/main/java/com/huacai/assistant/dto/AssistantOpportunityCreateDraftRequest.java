package com.huacai.assistant.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AssistantOpportunityCreateDraftRequest(
        @Valid
        @NotNull(message = "requestContext不能为空")
        AssistantRequestContext requestContext,
        String requestText,
        @NotNull(message = "customerId不能为空")
        Long customerId,
        String priorityLevel,
        String stageCode,
        BigDecimal estimatedAmount,
        String intentLevel,
        String status,
        LocalDateTime nextFollowTime,
        @Size(max = 500, message = "remark长度不能超过500")
        String remark
) {
}
