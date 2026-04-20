package com.huacai.opportunity.vo;

import java.time.LocalDateTime;

public record FollowRecordVO(
    Long id,
    Long opportunityId,
    Long customerId,
    String customerName,
    LocalDateTime followTime,
    String followerName,
    String followContent,
    String nextAction,
    String remark,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
