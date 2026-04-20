package com.huacai.loan.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public record LoanOrderSaveRequest(
        @NotNull(message = "客户ID不能为空")
        Long customerId,
        @Pattern(regexp = "^(SELF|BANK)?$", message = "资金来源类型只能是 SELF 或 BANK")
        String capitalSourceType,
        @Pattern(regexp = "^.{0,100}$", message = "银行名称长度不能超过100")
        String bankName,
        LocalDate loanDate,
        @Positive(message = "保证金金额必须大于0")
        BigDecimal depositGoldAmount,
        BigDecimal creditCardRepaymentAmount,
        @NotNull(message = "借款金额不能为空")
        @Positive(message = "借款金额必须大于0")
        BigDecimal loanAmount,
        BigDecimal balanceAmount,
        @Positive(message = "月利息金额必须大于0")
        BigDecimal monthlyInterestAmount,
        @Positive(message = "借款期数必须大于0")
        Integer loanCount,
        @Pattern(regexp = "^(ACTIVE|SETTLED|PENDING)?$", message = "状态只能是 ACTIVE/SETTLED/PENDING")
        String status,
        @Pattern(regexp = "^.{0,1000}$", message = "备注长度不能超过1000")
        String remark
) {
}
