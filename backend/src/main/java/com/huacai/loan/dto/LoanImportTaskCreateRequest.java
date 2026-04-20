package com.huacai.loan.dto;

public record LoanImportTaskCreateRequest(
        String capitalSourceType,
        Long fileId
) {
}
