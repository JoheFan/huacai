package com.huacai.workflow.dto;

import java.math.BigDecimal;

public record ReimbursementCreateRequest(
    BigDecimal amount,
    String reason
) {
}
