package com.huacai.opportunity.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OpportunityVO(
    Long id,
    Long customerId,
    String customerName,
    String customerNo,
    String mobile,
    String companyName,
    String creditCode,
    String priorityLevel,
    String stageCode,
    Long ownerUserId,
    String ownerUserName,
    BigDecimal estimatedAmount,
    String intentLevel,
    String status,
    LocalDateTime nextFollowTime,
    LocalDateTime latestFollowTime,
    String latestFollowerName,
    String latestFollowContent,
    String remark,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
