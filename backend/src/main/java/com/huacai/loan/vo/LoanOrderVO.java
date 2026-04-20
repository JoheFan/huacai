package com.huacai.loan.vo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record LoanOrderVO(
        Long id,
        Long customerId,
        String customerName,
        String customerNo,
        String capitalSourceType,
        String bankName,
        LocalDate loanDate,
        BigDecimal depositGoldAmount,
        BigDecimal creditCardRepaymentAmount,
        BigDecimal loanAmount,
        BigDecimal balanceAmount,
        BigDecimal monthlyInterestAmount,
        Integer loanCount,
        String status,
        String remark,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
