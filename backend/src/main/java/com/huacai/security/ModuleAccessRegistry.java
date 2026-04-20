package com.huacai.security;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ModuleAccessRegistry {

    private static final Map<String, String> BUSINESS_API_PREFIXES = new LinkedHashMap<>();
    private static final Map<String, String> API_REQUIRED_PAGE_PERMISSIONS = new LinkedHashMap<>();
    private static final Map<String, String> LEGACY_MODULE_PAGE_PERMISSIONS = new LinkedHashMap<>();
    private static final Map<String, String> PAGE_PERMISSION_MODULE_CODES = new LinkedHashMap<>();

    private static final List<String> ASSIGNABLE_MODULE_KEYS = List.of(
            "customers",
            "customer-risks",
            "customer-debts",
            "opportunities",
            "loan-orders-self",
            "loan-orders-bank",
            "increment-details",
            "increment-overview",
            "employee-performance",
            "repayments",
            "hr.employees",
            "hr.salaries",
            "workflow.transitions",
            "workflow.salary-adjusts",
            "finance.reimbursements",
            "workflow.my-approvals"
    );

    private static final List<String> ALL_PAGE_PERMISSIONS = List.of(
            "/dashboard",
            "/welcome",
            "/files",
            "/system/orgs",
            "/system/users",
            "/system/roles",
            "/system/logs",
            "/customers",
            "/customer-risks",
            "/customer-debts",
            "/opportunities",
            "/loan-orders-self",
            "/loan-orders-bank",
            "/increment-details",
            "/repayments",
            "/hr/employees",
            "/hr/my-profile",
            "/hr/salaries",
            "/workflow/transitions",
            "/workflow/salary-adjusts",
            "/workflow/my-approvals",
            "/finance/reimbursements",
            "/analysis/increment-overview",
            "/analysis/performance"
    );

    private static final List<String> ADMIN_BUTTON_PERMISSIONS = List.of(
            "system.orgs:create",
            "system.orgs:update",
            "system.orgs:delete",
            "system.orgs:sync",
            "system.users:create",
            "system.users:update",
            "system.users:delete",
            "system.users:status",
            "system.users:reset-password",
            "system.users:assign-permission",
            "system.roles:create",
            "system.roles:update",
            "system.roles:delete",
            "system.roles:assign-permission",
            "system.logs:query",
            "hr.employees:create",
            "hr.employees:update",
            "hr.employees:delete",
            "hr.employees:import",
            "hr.employees:export",
            "hr.salaries:create",
            "hr.salaries:update",
            "hr.salaries:delete",
            "workflow.transitions:create",
            "workflow.transitions:approve",
            "workflow.salary-adjusts:create",
            "workflow.salary-adjusts:approve",
            "finance.reimbursements:create",
            "finance.reimbursements:approve",
            "finance.reimbursements:view-all",
            "workflow.my-approvals:approve"
    );

    static {
        BUSINESS_API_PREFIXES.put("/api/v1/files", "files");
        BUSINESS_API_PREFIXES.put("/api/v1/customer-risks", "customer-risks");
        BUSINESS_API_PREFIXES.put("/api/v1/customer-debts", "customer-debts");
        BUSINESS_API_PREFIXES.put("/api/v1/customers", "customers");
        BUSINESS_API_PREFIXES.put("/api/v1/opportunities", "opportunities");
        BUSINESS_API_PREFIXES.put("/api/v1/loan-orders", "loan-orders-self");
        BUSINESS_API_PREFIXES.put("/api/v1/repayments", "repayments");
        BUSINESS_API_PREFIXES.put("/api/v1/hr/employees", "hr.employees");
        BUSINESS_API_PREFIXES.put("/api/v1/hr/salaries", "hr.salaries");
        BUSINESS_API_PREFIXES.put("/api/v1/hr/my-profile", "hr.my-profile");
        BUSINESS_API_PREFIXES.put("/api/v1/hr/management-records", "hr.management-records");
        BUSINESS_API_PREFIXES.put("/api/v1/workflow/transitions", "workflow.transitions");
        BUSINESS_API_PREFIXES.put("/api/v1/workflow/salary-adjusts", "workflow.salary-adjusts");
        BUSINESS_API_PREFIXES.put("/api/v1/workflow/my-approvals", "workflow.my-approvals");
        BUSINESS_API_PREFIXES.put("/api/v1/finance/reimbursements", "finance.reimbursements");
        BUSINESS_API_PREFIXES.put("/api/v1/finance/payees", "finance.payees");
        BUSINESS_API_PREFIXES.put("/api/v1/finance/expenses", "finance.expenses");
        BUSINESS_API_PREFIXES.put("/api/v1/finance/incomes", "finance.incomes");
        BUSINESS_API_PREFIXES.put("/api/v1/analysis/increment-summary", "increment-overview");
        BUSINESS_API_PREFIXES.put("/api/v1/analysis/increment-detail", "increment-details");
        BUSINESS_API_PREFIXES.put("/api/v1/analysis/daily-increment", "increment-details");
        BUSINESS_API_PREFIXES.put("/api/v1/analysis/employee-performance", "employee-performance");

        API_REQUIRED_PAGE_PERMISSIONS.put("/api/v1/files", "/files");
        API_REQUIRED_PAGE_PERMISSIONS.put("/api/v1/workbench", "/dashboard");
        API_REQUIRED_PAGE_PERMISSIONS.put("/api/v1/system/orgs", "/system/orgs");
        API_REQUIRED_PAGE_PERMISSIONS.put("/api/v1/system/users", "/system/users");
        API_REQUIRED_PAGE_PERMISSIONS.put("/api/v1/system/roles", "/system/roles");
        API_REQUIRED_PAGE_PERMISSIONS.put("/api/v1/system/operation-logs", "/system/logs");
        API_REQUIRED_PAGE_PERMISSIONS.put("/api/v1/customer-risks", "/customer-risks");
        API_REQUIRED_PAGE_PERMISSIONS.put("/api/v1/customer-debts", "/customer-debts");
        API_REQUIRED_PAGE_PERMISSIONS.put("/api/v1/customers", "/customers");
        API_REQUIRED_PAGE_PERMISSIONS.put("/api/v1/opportunities", "/opportunities");
        API_REQUIRED_PAGE_PERMISSIONS.put("/api/v1/loan-orders", "/loan-orders-self");
        API_REQUIRED_PAGE_PERMISSIONS.put("/api/v1/repayments", "/repayments");
        API_REQUIRED_PAGE_PERMISSIONS.put("/api/v1/hr/employees", "/hr/employees");
        API_REQUIRED_PAGE_PERMISSIONS.put("/api/v1/hr/salaries", "/hr/salaries");
        API_REQUIRED_PAGE_PERMISSIONS.put("/api/v1/hr/management-records", "/hr/management-records");
        API_REQUIRED_PAGE_PERMISSIONS.put("/api/v1/hr/my-profile", "/hr/my-profile");
        API_REQUIRED_PAGE_PERMISSIONS.put("/api/v1/workflow/transitions", "/workflow/transitions");
        API_REQUIRED_PAGE_PERMISSIONS.put("/api/v1/workflow/salary-adjusts", "/workflow/salary-adjusts");
        API_REQUIRED_PAGE_PERMISSIONS.put("/api/v1/workflow/my-approvals", "/workflow/my-approvals");
        API_REQUIRED_PAGE_PERMISSIONS.put("/api/v1/finance/reimbursements", "/finance/reimbursements");
        API_REQUIRED_PAGE_PERMISSIONS.put("/api/v1/finance/payees", "/finance/payees");
        API_REQUIRED_PAGE_PERMISSIONS.put("/api/v1/finance/expenses", "/finance/expenses");
        API_REQUIRED_PAGE_PERMISSIONS.put("/api/v1/finance/incomes", "/finance/incomes");
        API_REQUIRED_PAGE_PERMISSIONS.put("/api/v1/analysis/increment-summary", "/analysis/increment-overview");
        API_REQUIRED_PAGE_PERMISSIONS.put("/api/v1/analysis/increment-detail", "/increment-details");
        API_REQUIRED_PAGE_PERMISSIONS.put("/api/v1/analysis/daily-increment", "/increment-details");
        API_REQUIRED_PAGE_PERMISSIONS.put("/api/v1/analysis/employee-performance", "/analysis/performance");

        LEGACY_MODULE_PAGE_PERMISSIONS.put("customers", "/customers");
        LEGACY_MODULE_PAGE_PERMISSIONS.put("customer-risks", "/customer-risks");
        LEGACY_MODULE_PAGE_PERMISSIONS.put("customer-debts", "/customer-debts");
        LEGACY_MODULE_PAGE_PERMISSIONS.put("opportunities", "/opportunities");
        LEGACY_MODULE_PAGE_PERMISSIONS.put("loan-orders-self", "/loan-orders-self");
        LEGACY_MODULE_PAGE_PERMISSIONS.put("loan-orders-bank", "/loan-orders-bank");
        LEGACY_MODULE_PAGE_PERMISSIONS.put("increment-details", "/increment-details");
        LEGACY_MODULE_PAGE_PERMISSIONS.put("increment-overview", "/analysis/increment-overview");
        LEGACY_MODULE_PAGE_PERMISSIONS.put("employee-performance", "/analysis/performance");
        LEGACY_MODULE_PAGE_PERMISSIONS.put("repayments", "/repayments");
        LEGACY_MODULE_PAGE_PERMISSIONS.put("hr.employees", "/hr/employees");
        LEGACY_MODULE_PAGE_PERMISSIONS.put("hr.management-records", "/hr/management-records");
        LEGACY_MODULE_PAGE_PERMISSIONS.put("hr.salaries", "/hr/salaries");
        LEGACY_MODULE_PAGE_PERMISSIONS.put("workflow.transitions", "/workflow/transitions");
        LEGACY_MODULE_PAGE_PERMISSIONS.put("workflow.salary-adjusts", "/workflow/salary-adjusts");
        LEGACY_MODULE_PAGE_PERMISSIONS.put("workflow.my-approvals", "/workflow/my-approvals");
        LEGACY_MODULE_PAGE_PERMISSIONS.put("finance.reimbursements", "/finance/reimbursements");

        PAGE_PERMISSION_MODULE_CODES.put("/system/orgs", "system.orgs");
        PAGE_PERMISSION_MODULE_CODES.put("/system/users", "system.users");
        PAGE_PERMISSION_MODULE_CODES.put("/system/roles", "system.roles");
        PAGE_PERMISSION_MODULE_CODES.put("/system/logs", "system.logs");
        PAGE_PERMISSION_MODULE_CODES.put("/customers", "customers");
        PAGE_PERMISSION_MODULE_CODES.put("/customer-risks", "customer-risks");
        PAGE_PERMISSION_MODULE_CODES.put("/customer-debts", "customer-debts");
        PAGE_PERMISSION_MODULE_CODES.put("/opportunities", "opportunities");
        PAGE_PERMISSION_MODULE_CODES.put("/loan-orders-self", "loan-orders-self");
        PAGE_PERMISSION_MODULE_CODES.put("/loan-orders-bank", "loan-orders-bank");
        PAGE_PERMISSION_MODULE_CODES.put("/increment-details", "increment-details");
        PAGE_PERMISSION_MODULE_CODES.put("/repayments", "repayments");
        PAGE_PERMISSION_MODULE_CODES.put("/hr/employees", "hr.employees");
        PAGE_PERMISSION_MODULE_CODES.put("/hr/management-records", "hr.management-records");
        PAGE_PERMISSION_MODULE_CODES.put("/hr/my-profile", "hr.my-profile");
        PAGE_PERMISSION_MODULE_CODES.put("/hr/salaries", "hr.salaries");
        PAGE_PERMISSION_MODULE_CODES.put("/workflow/transitions", "workflow.transitions");
        PAGE_PERMISSION_MODULE_CODES.put("/workflow/salary-adjusts", "workflow.salary-adjusts");
        PAGE_PERMISSION_MODULE_CODES.put("/workflow/my-approvals", "workflow.my-approvals");
        PAGE_PERMISSION_MODULE_CODES.put("/finance/reimbursements", "finance.reimbursements");
        PAGE_PERMISSION_MODULE_CODES.put("/analysis/increment-overview", "increment-overview");
        PAGE_PERMISSION_MODULE_CODES.put("/analysis/performance", "employee-performance");
    }

    public List<String> normalizeVisibleModuleKeys(List<String> rawKeys) {
        if (rawKeys == null || rawKeys.isEmpty()) {
            return List.of();
        }

        Set<String> requested = rawKeys.stream()
                .filter(StringUtils::hasText)
                .map(String::trim)
                .collect(LinkedHashSet::new, Set::add, Set::addAll);

        return ASSIGNABLE_MODULE_KEYS.stream()
                .filter(requested::contains)
                .toList();
    }

    public Optional<String> resolveBusinessModuleKey(String requestUri) {
        if (!StringUtils.hasText(requestUri)) {
            return Optional.empty();
        }

        for (Map.Entry<String, String> entry : BUSINESS_API_PREFIXES.entrySet()) {
            if (requestUri.startsWith(entry.getKey())) {
                return Optional.of(entry.getValue());
            }
        }
        return Optional.empty();
    }

    public boolean isAdminOnlyPath(String requestUri) {
        return resolveRequiredPagePermission(requestUri)
                .map(this::isSystemPagePermission)
                .orElse(false);
    }

    public List<String> getAssignableModuleKeys() {
        return ASSIGNABLE_MODULE_KEYS;
    }

    public Map<String, String> getBusinessApiPrefixes() {
        return new LinkedHashMap<>(BUSINESS_API_PREFIXES);
    }

    public Optional<String> resolveRequiredPagePermission(String requestUri) {
        if (!StringUtils.hasText(requestUri)) {
            return Optional.empty();
        }

        for (Map.Entry<String, String> entry : API_REQUIRED_PAGE_PERMISSIONS.entrySet()) {
            if (requestUri.startsWith(entry.getKey())) {
                return Optional.of(entry.getValue());
            }
        }
        return Optional.empty();
    }

    public List<String> derivePagePermissionsFromLegacyModules(List<String> legacyModuleKeys) {
        Set<String> pagePermissions = new LinkedHashSet<>();
        pagePermissions.add("/welcome");
        for (String moduleKey : normalizeVisibleModuleKeys(legacyModuleKeys)) {
            String pagePermission = LEGACY_MODULE_PAGE_PERMISSIONS.get(moduleKey);
            if (StringUtils.hasText(pagePermission)) {
                pagePermissions.add(pagePermission);
            }
        }

        return ALL_PAGE_PERMISSIONS.stream()
                .filter(pagePermissions::contains)
                .toList();
    }

    public List<String> getAdminPagePermissions() {
        return ALL_PAGE_PERMISSIONS.stream()
                .filter(path -> !"/welcome".equals(path))
                .toList();
    }

    public List<String> getAdminButtonPermissions() {
        return ADMIN_BUTTON_PERMISSIONS;
    }

    public List<String> sortPagePermissions(Collection<String> pagePermissions) {
        if (pagePermissions == null || pagePermissions.isEmpty()) {
            return List.of();
        }
        Set<String> values = new LinkedHashSet<>(pagePermissions);
        List<String> ordered = ALL_PAGE_PERMISSIONS.stream()
                .filter(values::contains)
                .toList();
        Set<String> known = new LinkedHashSet<>(ordered);
        List<String> extras = values.stream()
                .filter(value -> !known.contains(value))
                .toList();
        if (extras.isEmpty()) {
            return ordered;
        }
        Set<String> merged = new LinkedHashSet<>(ordered);
        merged.addAll(extras);
        return List.copyOf(merged);
    }

    public Optional<String> resolveModuleCodeByPagePermission(String pagePermission) {
        if (!StringUtils.hasText(pagePermission)) {
            return Optional.empty();
        }
        return Optional.ofNullable(PAGE_PERMISSION_MODULE_CODES.get(pagePermission));
    }

    public Map<String, String> buildScopedModules(List<String> pagePermissions, String defaultScope) {
        if (!StringUtils.hasText(defaultScope)) {
            return Map.of();
        }
        Map<String, String> result = new LinkedHashMap<>();
        for (String pagePermission : pagePermissions) {
            resolveModuleCodeByPagePermission(pagePermission)
                    .filter(moduleCode -> !moduleCode.startsWith("system."))
                    .ifPresent(moduleCode -> result.put(moduleCode, defaultScope));
        }
        return result;
    }

    public String summarizeDataScopes(Map<String, String> dataScopes) {
        if (dataScopes == null || dataScopes.isEmpty()) {
            return "未配置数据权限";
        }
        if (dataScopes.values().stream().anyMatch("ALL"::equals)) {
            return "全部数据权限";
        }
        if (dataScopes.values().stream().anyMatch("ORG_AND_SUB"::equals)) {
            return "本部门及下级数据权限";
        }
        if (dataScopes.values().stream().anyMatch("ORG"::equals)) {
            return "本部门数据权限";
        }
        return "仅本人数据权限";
    }

    public boolean isSystemPagePermission(String pagePermission) {
        return StringUtils.hasText(pagePermission) && pagePermission.startsWith("/system");
    }
}
