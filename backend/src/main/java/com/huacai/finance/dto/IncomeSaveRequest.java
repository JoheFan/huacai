package com.huacai.finance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record IncomeSaveRequest(
        String incomeName,
        String incomeType,
        LocalDate bizDate,
        BigDecimal amount,
        String payerName,
        Long payerId,
        Long fileId,
        String remark
) {
}
