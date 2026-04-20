package com.huacai.common.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

class ApiResponseTest {

    @AfterEach
    void clearTraceId() {
        MDC.clear();
    }

    @Test
    void successUsesTraceIdFromMdc() {
        MDC.put("traceId", "trace-from-test");

        ApiResponse<String> response = ApiResponse.success("ok");

        assertThat(response.traceId()).isEqualTo("trace-from-test");
    }

    @Test
    void successFallsBackToGeneratedTraceIdWhenMdcMissing() {
        ApiResponse<String> response = ApiResponse.success("ok");

        assertThat(response.traceId()).isNotBlank();
        assertThat(response.traceId()).isNotEqualTo("todo-trace-id");
        assertThatCode(() -> UUID.fromString(response.traceId())).doesNotThrowAnyException();
    }

    private static org.assertj.core.api.AbstractThrowableAssert<?, ? extends Throwable> assertThatCode(
            org.assertj.core.api.ThrowableAssert.ThrowingCallable callable
    ) {
        return org.assertj.core.api.Assertions.assertThatCode(callable);
    }
}
