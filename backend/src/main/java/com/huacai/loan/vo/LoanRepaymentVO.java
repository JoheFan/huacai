package com.huacai.loan.vo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record LoanRepaymentVO(
        Long id,
        Long loanOrderId,
        Long customerId,
        String customerName,
        String customerNo,
        String capitalSourceType,
        LocalDate repaymentDate,
        BigDecimal repaymentAmount,
        BigDecimal principalAmount,
        BigDecimal interestAmount,
        String repaymentChannel,
        String remark,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
