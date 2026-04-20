package com.huacai.customer.vo;

import java.time.LocalDateTime;

public record CustomerStatusLogVO(
        Long id,
        Long customerId,
        String statusCode,
        String statusName,
        LocalDateTime changedAt,
        Long changedBy,
        String changedByName,
        String remark
) {
}