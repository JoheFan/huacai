package com.huacai.customer.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CustomerDebtSaveRequest(
        Long id,
        Long customerId,
        @Size(max = 100, message = "负债类型长度不能超过100")
        String debtType,
        @DecimalMin(value = "0", message = "负债总额不能为负数")
        BigDecimal debtAmount,
        @DecimalMin(value = "0", message = "总需还金额不能为负数")
        BigDecimal totalRepaymentAmount,
        @DecimalMin(value = "0", message = "已垫还金额不能为负数")
        BigDecimal advancePaidAmount,
        @DecimalMin(value = "0", message = "待偿还金额不能为负数")
        BigDecimal pendingAmount,
        @DecimalMin(value = "0", message = "分期金额不能为负数")
        BigDecimal installmentAmount,
        @Min(value = 1, message = "还款日只能是1-31")
        @Max(value = 31, message = "还款日只能是1-31")
        Integer repaymentDay,
        @Size(max = 1000, message = "备注长度不能超过1000")
        String remark
) {
}
