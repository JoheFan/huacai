package com.huacai.customer.dto;

public record CustomerStatusLogSaveRequest(
        String statusCode,
        String statusName,
        String changedAt,
        String remark
) {
}
