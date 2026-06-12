package com.huacai.assistant.dto;

import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AssistantOpportunityPatch(
        String stageCode,
        String priorityLevel,
        BigDecimal estimatedAmount,
        String intentLevel,
        String status,
        LocalDateTime nextFollowTime,
        @Size(max = 500, message = "remark长度不能超过500")
        String remark
) {

    public boolean isEmpty() {
        return stageCode == null
                && priorityLevel == null
                && estimatedAmount == null
                && intentLevel == null
                && status == null
                && nextFollowTime == null
                && remark == null;
    }
}
