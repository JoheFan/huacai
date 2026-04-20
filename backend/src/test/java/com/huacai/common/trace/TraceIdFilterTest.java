package com.huacai.common.trace;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import jakarta.servlet.FilterChain;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class TraceIdFilterTest {

    private final TraceIdFilter filter = new TraceIdFilter();

    @AfterEach
    void clearTraceId() {
        TraceIdContext.clear();
    }

    @Test
    void reusesIncomingTraceIdAndClearsMdcAfterRequest() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/health");
        request.addHeader(TraceIdContext.TRACE_ID_HEADER, "trace-from-header");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicReference<String> traceIdInsideChain = new AtomicReference<>();

        FilterChain chain = (req, res) -> traceIdInsideChain.set(org.slf4j.MDC.get(TraceIdContext.TRACE_ID_KEY));

        filter.doFilter(request, response, chain);

        assertThat(traceIdInsideChain.get()).isEqualTo("trace-from-header");
        assertThat(response.getHeader(TraceIdContext.TRACE_ID_HEADER)).isEqualTo("trace-from-header");
        assertThat(org.slf4j.MDC.get(TraceIdContext.TRACE_ID_KEY)).isNull();
    }

    @Test
    void generatesTraceIdWhenRequestHeaderMissing() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/health");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, (req, res) -> {
        });

        String responseTraceId = response.getHeader(TraceIdContext.TRACE_ID_HEADER);
        assertThat(responseTraceId).isNotBlank();
        assertThatCode(() -> UUID.fromString(responseTraceId)).doesNotThrowAnyException();
    }
}
