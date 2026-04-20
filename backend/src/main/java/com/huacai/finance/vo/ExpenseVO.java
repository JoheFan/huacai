package com.huacai.finance.vo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ExpenseVO(
    Long id,
    String expenseName,
    String expenseType,
    LocalDate bizDate,
    BigDecimal amount,
    String payeeName,
    Long payeeId,
    Long fileId,
    String remark,
    LocalDateTime createdAt
) {
}
