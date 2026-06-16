package com.huacai.customer.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CustomerTradeSaveRequest(
        @NotBlank(message = "交易类型不能为空")
        @Size(max = 100, message = "交易类型长度不能超过100")
        String tradeType,
        @DecimalMin(value = "0", message = "交易金额不能为负数")
        BigDecimal amount,
        @DecimalMin(value = "0", message = "服务费不能为负数")
        BigDecimal serviceFee,
        @DecimalMin(value = "0", message = "实收金额不能为负数")
        BigDecimal actualReceived,
        @DecimalMin(value = "0", message = "已退金额不能为负数")
        BigDecimal returnedAmount,
        BigDecimal balanceAmount,
        LocalDate tradeDate,
        @Size(max = 1000, message = "备注长度不能超过1000")
        String remark
) {
}
