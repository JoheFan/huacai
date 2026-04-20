package com.huacai.system.dto;

public record RoleUpdateRequest(
    String roleCode,
    String roleName,
    String identityType,
    String dataScope,
    String status,
    String remark
) {
}
