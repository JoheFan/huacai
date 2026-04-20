package com.huacai.customer.dto;

import java.time.LocalDate;

public record CustomerContractSaveRequest(
        Long id,
        Long customerId,
        String customerName,
        String companyName,
        String creditCode,
        String contractNo,
        String contractName,
        java.util.List<Long> fileIds,
        LocalDate signDate,
        String remark
) {
}
