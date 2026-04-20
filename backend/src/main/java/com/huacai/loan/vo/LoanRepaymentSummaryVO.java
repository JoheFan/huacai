package com.huacai.loan.vo;

import java.math.BigDecimal;

public record LoanRepaymentSummaryVO(
        Long customerId,
        String customerName,
        Long loanOrderId,
        String capitalSourceType,
        long recordCount,
        BigDecimal repaymentAmountTotal,
        BigDecimal principalAmountTotal,
        BigDecimal interestAmountTotal
) {
}
