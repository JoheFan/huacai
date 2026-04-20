package com.huacai.finance.vo;

import java.time.LocalDateTime;

public record PayeeVO(
    Long id,
    String payeeName,
    String payeeType,
    String bankName,
    String bankAccount,
    String contactName,
    String contactPhone,
    String status,
    String remark,
    LocalDateTime createdAt
) {
}
