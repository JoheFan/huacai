package com.huacai.workflow.vo;

import java.time.LocalDateTime;

public record ActionLogVO(
    Long id,
    Long processId,
    String bizType,
    Long bizId,
    Integer roundNo,
    String actionCode,
    String nodeKey,
    String nodeName,
    Long operatorId,
    String operatorName,
    String actionOpinion,
    String actionResult,
    LocalDateTime actionTime
) {
}
