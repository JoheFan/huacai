package com.huacai.common.model;

import com.huacai.common.trace.TraceIdContext;

public record ApiResponse<T>(
        int code,
        String message,
        T data,
        String traceId
) {

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(0, "success", data, TraceIdContext.getOrCreate());
    }

    public static ApiResponse<Void> success() {
        return new ApiResponse<>(0, "success", null, TraceIdContext.getOrCreate());
    }

    public static ApiResponse<Void> fail(String message) {
        return new ApiResponse<>(-1, message, null, TraceIdContext.getOrCreate());
    }
}
