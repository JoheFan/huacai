package com.huacai.auth.vo;

import java.util.List;
import java.util.Map;

public record CurrentUserInfoVO(
        Long id,
        String username,
        String realName,
        Long orgId,
        String orgName,
        String identityType,
        List<String> roles,
        String primaryRoleCode,
        String primaryRoleName,
        List<String> permissions,
        List<String> pagePermissions,
        List<String> buttonPermissions,
        Map<String, String> dataScopes,
        String permissionSummary
) {
}
