package com.huacai.workflow.vo;

import java.time.LocalDateTime;

public record ProcessTimelineVO(
    Long processId,
    String bizType,
    Long bizId,
    String bizNo,
    String title,
    String overallStatus,
    String currentNodeName,
    LocalDateTime startedAt,
    LocalDateTime completedAt
) {
}
