package com.huacai.hr.vo;

import java.time.LocalDateTime;

public record ApprovalRecordVO(
    Long id,
    String bizType,
    Long bizId,
    String nodeName,
    String nodeKey,
    Long operatorId,
    String operatorName,
    LocalDateTime operateTime,
    String opinion,
    String result
) {
}
