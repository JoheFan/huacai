package com.huacai.system.vo;

import java.time.LocalDateTime;

public record RoleVO(
    Long id,
    String roleCode,
    String roleName,
    String identityType,
    String dataScope,
    String permissionSummary,
    String status,
    String remark,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
