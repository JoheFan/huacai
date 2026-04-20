package com.huacai.workflow.dto;

import java.math.BigDecimal;

public record ReimbursementSubmitRequest(
    BigDecimal amount,
    String reason
) {
}
