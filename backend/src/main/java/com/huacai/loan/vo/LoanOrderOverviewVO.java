package com.huacai.loan.vo;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LoanOrderOverviewVO(
        Long customerId,
        String customerNo,
        String customerName,
        String mobile,
        String companyName,
        String capitalSourceType,
        BigDecimal depositGoldAmount,
        BigDecimal creditCardRepaymentAmount,
        BigDecimal balanceAmount,
        Integer loanCount,
        BigDecimal monthlyInterestAmount,
        BigDecimal totalLoanAmount,
        LocalDate firstLoanDate,
        BigDecimal repaidAmount,
        BigDecimal pendingAmount,
        String remark,
        String status,
        // 银行口径字段（持久化）
        BigDecimal totalIncrementAmount,
        Integer incrementCount,
        String yearsTerm,
        BigDecimal channelRate,
        BigDecimal channelFee,
        String referrer,
        BigDecimal selfTotalLoanAmount,
        BigDecimal bankTotalLoanAmount
) {
}
