package com.huacai.customer.vo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record CustomerRiskVO(
        Long id,
        Long customerId,
        String customerNo,
        String customerName,
        String mobile,
        String companyName,
        String creditCode,
        String industry,
        String businessAddress,
        LocalDate testDate,
        BigDecimal testLimit,
        BigDecimal trafficValue,
        BigDecimal compositeScore,
        BigDecimal thirdPartyScore,
        String remark,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
