package com.huacai.finance.dto;

public record PayeeSaveRequest(
        String payeeName,
        String payeeType,
        String bankName,
        String bankAccount,
        String contactName,
        String contactPhone,
        String status,
        String remark
) {
}
