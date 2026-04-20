package com.huacai.finance.vo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record IncomeVO(
    Long id,
    String incomeName,
    String incomeType,
    LocalDate bizDate,
    BigDecimal amount,
    String payerName,
    Long payerId,
    Long fileId,
    String remark,
    LocalDateTime createdAt
) {
}
