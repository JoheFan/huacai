package com.huacai.system.vo;

import java.util.List;
import java.util.Map;

public record UserPermissionProfileVO(
        Long userId,
        String identityType,
        Long primaryRoleId,
        String primaryRoleCode,
        String primaryRoleName,
        List<String> rolePagePermissions,
        List<String> roleButtonPermissions,
        Map<String, String> roleDataScopes,
        List<String> effectivePagePermissions,
        List<String> effectiveButtonPermissions,
        Map<String, String> dataScopes,
        String permissionSummary
) {
}
