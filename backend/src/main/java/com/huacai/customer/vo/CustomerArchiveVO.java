package com.huacai.customer.vo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record CustomerArchiveVO(
        Long id,
        String customerNo,
        String customerName,
        String gender,
        String idCard,
        LocalDate birthday,
        Integer age,
        String mobile,
        String companyName,
        String creditCode,
        LocalDate establishedDate,
        String industry,
        String businessAddress,
        String bankName,
        String bankAccount,
        String recommenderName,
        BigDecimal recommenderRate,
        BigDecimal serviceFee,
        String auditStatus,
        String bizStatus,
        String loanStatus,
        Boolean taxRegistrationNormal,
        String remark,
        List<CustomerAttachmentVO> archiveAttachments,
        List<CustomerRiskVO> riskRecords,
        List<CustomerDebtVO> debtRecords,
        List<CustomerContractVO> contractRecords,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
