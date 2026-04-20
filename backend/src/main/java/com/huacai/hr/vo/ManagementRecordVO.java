package com.huacai.hr.vo;

import java.time.LocalDateTime;

public record ManagementRecordVO(
    Long id,
    Long employeeId,
    String employeeName,
    String recordType,
    String content,
    Long operatorId,
    String operatorName,
    LocalDateTime operatedAt,
    String remark
) {
}
