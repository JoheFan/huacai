package com.huacai.workflow.dto;

import java.math.BigDecimal;

public record ReimbursementUpdateRequest(
    BigDecimal amount,
    String reason
) {
}
