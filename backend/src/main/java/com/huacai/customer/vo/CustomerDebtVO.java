package com.huacai.customer.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CustomerDebtVO(
        Long id,
        Long customerId,
        String customerNo,
        String customerName,
        String mobile,
        String companyName,
        String creditCode,
        String debtType,
        BigDecimal debtAmount,
        BigDecimal totalRepaymentAmount,
        BigDecimal advancePaidAmount,
        BigDecimal pendingAmount,
        BigDecimal installmentAmount,
        Integer repaymentDay,
        String remark,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
