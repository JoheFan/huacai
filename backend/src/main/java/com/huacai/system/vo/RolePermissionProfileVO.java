package com.huacai.system.vo;

import java.util.List;
import java.util.Map;

public record RolePermissionProfileVO(
        Long roleId,
        String roleCode,
        String roleName,
        String identityType,
        List<String> pagePermissions,
        List<String> buttonPermissions,
        Map<String, String> dataScopes,
        String permissionSummary
) {
}
