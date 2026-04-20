package com.huacai.system.vo;

public record PermissionItemVO(
        Long id,
        String permissionCode,
        String permissionName,
        String permissionType,
        String moduleCode,
        String routePath,
        String buttonCode
) {
}
