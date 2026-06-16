package com.huacai.customer.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CustomerScoreSaveRequest(
        Long id,
        Long customerId,
        LocalDate testDate,
        @DecimalMin(value = "0", message = "测试额度不能为负数")
        BigDecimal testLimit,
        @DecimalMin(value = "0", message = "流量不能为负数")
        BigDecimal trafficValue,
        @DecimalMin(value = "0", message = "综合评分不能为负数")
        BigDecimal compositeScore,
        @DecimalMin(value = "0", message = "第三方评分不能为负数")
        BigDecimal thirdPartyScore,
        @Size(max = 1000, message = "备注长度不能超过1000")
        String remark
) {
}
