package com.huacai.loan.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record LoanRepaymentSaveRequest(
        Long loanOrderId,
        @NotNull(message = "还款日期不能为空")
        LocalDate repaymentDate,
        @NotNull(message = "还款金额不能为空")
        BigDecimal repaymentAmount,
        BigDecimal principalAmount,
        BigDecimal interestAmount,
        String repaymentChannel,
        String remark
) {
}
