package com.huacai.opportunity.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OpportunitySaveRequest(
        Long customerId,
        String priorityLevel,
        String stageCode,
        Long ownerUserId,
        BigDecimal estimatedAmount,
        String intentLevel,
        String status,
        LocalDateTime nextFollowTime,
        String remark
) {
}
