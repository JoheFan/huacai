package com.huacai.customer.vo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record CustomerTradeVO(
        Long id,
        Long customerId,
        String customerNo,
        String customerName,
        String mobile,
        String companyName,
        String tradeType,
        BigDecimal amount,
        BigDecimal serviceFee,
        BigDecimal actualReceived,
        BigDecimal returnedAmount,
        BigDecimal balanceAmount,
        LocalDate tradeDate,
        String remark,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
