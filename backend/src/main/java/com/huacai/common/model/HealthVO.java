package com.huacai.common.model;

public record HealthVO(
        String status,
        String service,
        String version
) {
}
