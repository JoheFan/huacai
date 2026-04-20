package com.huacai.customer.dto;

import java.math.BigDecimal;

public record CustomerDebtSaveRequest(
        Long id,
        Long customerId,
        String debtType,
        BigDecimal debtAmount,
        BigDecimal totalRepaymentAmount,
        BigDecimal advancePaidAmount,
        BigDecimal pendingAmount,
        BigDecimal installmentAmount,
        Integer repaymentDay,
        String remark
) {
}
