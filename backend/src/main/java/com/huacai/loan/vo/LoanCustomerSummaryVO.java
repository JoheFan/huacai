package com.huacai.loan.vo;

import java.math.BigDecimal;

public record LoanCustomerSummaryVO(
    Long id,
    Long customerId,
    String customerNo,
    String customerName,
    String mobile,
    String companyName,
    String capitalSourceType,
    BigDecimal totalIncrementAmount,
    Integer incrementCount,
    String yearsTerm,
    BigDecimal channelRate,
    BigDecimal channelFee,
    String referrer,
    BigDecimal selfTotalLoanAmount,
    BigDecimal bankTotalLoanAmount,
    String remark
) {
}
