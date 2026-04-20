package com.huacai.system.vo;

import java.time.LocalDateTime;

public record OperationLogVO(
        Long id,
        Long userId,
        String userName,
        String moduleCode,
        String bizType,
        String actionCode,
        String actionDesc,
        String targetType,
        Long targetId,
        String requestUri,
        String requestMethod,
        String resultCode,
        String resultMsg,
        String ip,
        LocalDateTime createdAt
) {
}