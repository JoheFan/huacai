package com.huacai.system.dto;

import java.util.List;
import java.util.Map;

public record UserPermissionProfileUpdateRequest(
        List<String> pagePermissions,
        List<String> buttonPermissions,
        Map<String, String> dataScopes
) {
}
