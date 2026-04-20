package com.huacai.security;

import java.util.List;
import java.util.Map;

public class AuthUser {

    private final Long userId;
    private final String username;
    private final String realName;
    private final Long orgId;
    private final String orgName;
    private final String identityType;
    private final String primaryRoleCode;
    private final String primaryRoleName;
    private final List<String> roles;
    private final List<String> pagePermissions;
    private final List<String> buttonPermissions;
    private final Map<String, String> dataScopes;
    private final String permissionSummary;

    public AuthUser(
            Long userId,
            String username,
            String realName,
            Long orgId,
            String orgName,
            String identityType,
            String primaryRoleCode,
            String primaryRoleName,
            List<String> roles,
            List<String> pagePermissions,
            List<String> buttonPermissions,
            Map<String, String> dataScopes,
            String permissionSummary
    ) {
        this.userId = userId;
        this.username = username;
        this.realName = realName;
        this.orgId = orgId;
        this.orgName = orgName;
        this.identityType = identityType;
        this.primaryRoleCode = primaryRoleCode;
        this.primaryRoleName = primaryRoleName;
        this.roles = roles;
        this.pagePermissions = pagePermissions;
        this.buttonPermissions = buttonPermissions;
        this.dataScopes = dataScopes;
        this.permissionSummary = permissionSummary;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getRealName() {
        return realName;
    }

    public Long getOrgId() {
        return orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public String getIdentityType() {
        return identityType;
    }

    public String getPrimaryRoleCode() {
        return primaryRoleCode;
    }

    public String getPrimaryRoleName() {
        return primaryRoleName;
    }

    public List<String> getRoles() {
        return roles;
    }

    public List<String> getPagePermissions() {
        return pagePermissions;
    }

    public List<String> getButtonPermissions() {
        return buttonPermissions;
    }

    public Map<String, String> getDataScopes() {
        return dataScopes;
    }

    public String getPermissionSummary() {
        return permissionSummary;
    }

    public boolean isSuperAdmin() {
        return "SUPER_ADMIN".equals(identityType) || roles.contains("ADMIN");
    }
}
