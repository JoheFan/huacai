package com.huacai.hr.vo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record LeaveRecordVO(
    Long id,
    Long employeeId,
    String leaveType,
    LocalDate startDate,
    LocalDate endDate,
    BigDecimal days,
    String reason,
    String status,
    Long applicantId,
    String applicantName,
    LocalDateTime applyTime
) {
}
