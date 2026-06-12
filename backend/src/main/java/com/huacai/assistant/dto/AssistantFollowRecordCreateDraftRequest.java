package com.huacai.assistant.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record AssistantFollowRecordCreateDraftRequest(
        @Valid
        @NotNull(message = "requestContext不能为空")
        AssistantRequestContext requestContext,
        String requestText,
        @NotNull(message = "opportunityId不能为空")
        Long opportunityId,
        @NotNull(message = "followTime不能为空")
        LocalDateTime followTime,
        @Size(max = 100, message = "followerName长度不能超过100")
        String followerName,
        @NotBlank(message = "followContent不能为空")
        @Size(max = 2000, message = "followContent长度不能超过2000")
        String followContent,
        @Size(max = 500, message = "nextAction长度不能超过500")
        String nextAction,
        @Size(max = 500, message = "remark长度不能超过500")
        String remark
) {
}
