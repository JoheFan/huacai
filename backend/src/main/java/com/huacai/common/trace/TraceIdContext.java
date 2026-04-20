package com.huacai.common.trace;

import java.util.UUID;
import org.slf4j.MDC;

public final class TraceIdContext {

    public static final String TRACE_ID_KEY = "traceId";
    public static final String TRACE_ID_HEADER = "X-Trace-Id";

    private TraceIdContext() {
    }

    public static String getOrCreate() {
        String traceId = MDC.get(TRACE_ID_KEY);
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString();
            MDC.put(TRACE_ID_KEY, traceId);
        }
        return traceId;
    }

    public static void clear() {
        MDC.remove(TRACE_ID_KEY);
    }
}
