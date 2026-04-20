package com.huacai.customer.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CustomerScoreSaveRequest(
        Long id,
        Long customerId,
        LocalDate testDate,
        BigDecimal testLimit,
        BigDecimal trafficValue,
        BigDecimal compositeScore,
        BigDecimal thirdPartyScore,
        String remark
) {
}
