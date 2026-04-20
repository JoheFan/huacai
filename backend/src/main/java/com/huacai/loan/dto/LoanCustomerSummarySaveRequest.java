package com.huacai.loan.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record LoanCustomerSummarySaveRequest(
    @NotNull(message = "客户ID不能为空")
    Long customerId,
    @NotNull(message = "资金来源不能为空")
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
