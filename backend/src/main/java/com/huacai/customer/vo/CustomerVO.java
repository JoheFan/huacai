package com.huacai.customer.vo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record CustomerVO(
        Long id,
        String customerNo,
        String customerName,
        String mobile,
        String companyName,
        String creditCode,
        String industry,
        String auditStatus,
        String bizStatus,
        String loanStatus,
        BigDecimal serviceFee,
        String remark,
        LocalDate birthday,
        LocalDate establishedDate,
        String bankName,
        String bankAccount,
        String recommenderName,
        BigDecimal recommenderRate,
        String businessAddress,
        Boolean taxRegistrationNormal,
        LocalDate testDate,
        BigDecimal testLimit,
        BigDecimal trafficValue,
        BigDecimal compositeScore,
        BigDecimal thirdPartyScore,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
