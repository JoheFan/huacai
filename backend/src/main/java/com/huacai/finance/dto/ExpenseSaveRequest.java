package com.huacai.finance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseSaveRequest(
        String expenseName,
        String expenseType,
        LocalDate bizDate,
        BigDecimal amount,
        String payeeName,
        Long payeeId,
        Long fileId,
        String remark
) {
}
