package com.huacai.customer.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CustomerArchiveSaveRequest(
        String customerNo,
        @NotBlank(message = "客户姓名不能为空")
        String customerName,
        String gender,
        String idCard,
        LocalDate birthday,
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
        String bizStatus,
        Boolean taxRegistrationNormal,
        List<Long> archiveFileIds,
        @Valid List<CustomerScoreSaveRequest> riskRecords,
        @Valid List<CustomerDebtSaveRequest> debtRecords,
        @Valid List<CustomerContractSaveRequest> contractRecords
) implements CustomerProfileFields {
}
