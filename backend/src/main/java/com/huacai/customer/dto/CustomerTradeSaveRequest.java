package com.huacai.customer.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CustomerTradeSaveRequest(
        String tradeType,
        BigDecimal amount,
        BigDecimal serviceFee,
        BigDecimal actualReceived,
        BigDecimal returnedAmount,
        BigDecimal balanceAmount,
        LocalDate tradeDate,
        String remark
) {
}
