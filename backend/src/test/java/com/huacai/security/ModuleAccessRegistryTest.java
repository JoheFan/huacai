package com.huacai.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class ModuleAccessRegistryTest {

    private final ModuleAccessRegistry registry = new ModuleAccessRegistry();

    @Test
    void derivePagePermissionsFromLegacyModulesIncludesWelcomeAndGrantedPages() {
        List<String> permissions = registry.derivePagePermissionsFromLegacyModules(List.of(
                "repayments",
                "customers",
                "customers",
                "unknown"
        ));

        assertThat(permissions).containsExactly("/welcome", "/customers", "/repayments");
    }

    @Test
    void resolveRequiredPagePermissionMatchesConfiguredApiPrefix() {
        assertThat(registry.resolveRequiredPagePermission("/api/v1/customers")).contains("/customers");
        assertThat(registry.resolveRequiredPagePermission("/api/v1/loan-orders/18")).contains("/loan-orders-self");
        assertThat(registry.resolveRequiredPagePermission("/api/v1/system/users")).contains("/system/users");
    }

    @Test
    void adminPagePermissionsContainSystemManagementAndWorkbench() {
        assertThat(registry.getAdminPagePermissions()).contains("/dashboard", "/system/users", "/system/roles", "/system/orgs");
        assertThat(registry.getAdminPagePermissions()).doesNotContain("/welcome");
    }
}
