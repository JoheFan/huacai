package com.huacai.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.huacai.auth.dto.ChangePasswordRequest;
import com.huacai.auth.dto.LoginRequest;
import com.huacai.auth.service.AuthService;
import com.huacai.auth.vo.CurrentUserInfoVO;
import com.huacai.auth.vo.LoginResponse;
import com.huacai.common.exception.BusinessException;
import com.huacai.security.AuthUser;
import com.huacai.security.CurrentUserProvider;
import com.huacai.security.JwtTokenService;
import com.huacai.security.ModuleAccessRegistry;
import com.huacai.system.entity.SysOrg;
import com.huacai.system.entity.SysPermissionItem;
import com.huacai.system.entity.SysRole;
import com.huacai.system.entity.SysRoleDataScope;
import com.huacai.system.entity.SysRolePermission;
import com.huacai.system.entity.SysUser;
import com.huacai.system.entity.SysUserDataScope;
import com.huacai.system.entity.SysUserPermission;
import com.huacai.system.mapper.PermissionItemMapper;
import com.huacai.system.mapper.RoleDataScopeMapper;
import com.huacai.system.mapper.RolePermissionMapper;
import com.huacai.system.mapper.SysOrgMapper;
import com.huacai.system.mapper.SysRoleMapper;
import com.huacai.system.mapper.SysUserMapper;
import com.huacai.system.mapper.UserDataScopeMapper;
import com.huacai.system.mapper.UserModuleMapper;
import com.huacai.system.mapper.UserPermissionMapper;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper sysUserMapper;
    private final SysOrgMapper sysOrgMapper;
    private final SysRoleMapper sysRoleMapper;
    private final UserModuleMapper userModuleMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final UserPermissionMapper userPermissionMapper;
    private final RoleDataScopeMapper roleDataScopeMapper;
    private final UserDataScopeMapper userDataScopeMapper;
    private final PermissionItemMapper permissionItemMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final CurrentUserProvider currentUserProvider;
    private final ModuleAccessRegistry moduleAccessRegistry;

    public AuthServiceImpl(
            SysUserMapper sysUserMapper,
            SysOrgMapper sysOrgMapper,
            SysRoleMapper sysRoleMapper,
            UserModuleMapper userModuleMapper,
            RolePermissionMapper rolePermissionMapper,
            UserPermissionMapper userPermissionMapper,
            RoleDataScopeMapper roleDataScopeMapper,
            UserDataScopeMapper userDataScopeMapper,
            PermissionItemMapper permissionItemMapper,
            PasswordEncoder passwordEncoder,
            JwtTokenService jwtTokenService,
            CurrentUserProvider currentUserProvider,
            ModuleAccessRegistry moduleAccessRegistry
    ) {
        this.sysUserMapper = sysUserMapper;
        this.sysOrgMapper = sysOrgMapper;
        this.sysRoleMapper = sysRoleMapper;
        this.userModuleMapper = userModuleMapper;
        this.rolePermissionMapper = rolePermissionMapper;
        this.userPermissionMapper = userPermissionMapper;
        this.roleDataScopeMapper = roleDataScopeMapper;
        this.userDataScopeMapper = userDataScopeMapper;
        this.permissionItemMapper = permissionItemMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
        this.currentUserProvider = currentUserProvider;
        this.moduleAccessRegistry = moduleAccessRegistry;
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        SysUser user = sysUserMapper.selectOne(Wrappers.lambdaQuery(SysUser.class)
                .eq(SysUser::getUsername, request.username())
                .last("LIMIT 1"));
        if (user == null || !"ENABLE".equals(user.getAccountStatus())) {
            throw new BusinessException("账号不存在或已停用");
        }
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BusinessException("账号或密码错误");
        }

        user.setLastLoginAt(LocalDateTime.now());
        sysUserMapper.updateById(user);

        AuthUser authUser = buildAuthUser(user);
        String token = jwtTokenService.generateToken(authUser);
        return new LoginResponse(token, "Bearer", jwtTokenService.getExpireSeconds(), toCurrentUserInfo(authUser));
    }

    @Override
    public CurrentUserInfoVO currentUser() {
        AuthUser authUser = currentUserProvider.getCurrentUser()
                .orElseThrow(() -> new BusinessException("未获取到当前登录用户"));
        return toCurrentUserInfo(authUser);
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        AuthUser authUser = currentUserProvider.getCurrentUser()
                .orElseThrow(() -> new BusinessException("未获取到当前登录用户"));
        SysUser user = sysUserMapper.selectById(authUser.getUserId());
        if (user == null) {
            throw new BusinessException("当前用户不存在");
        }
        if (!passwordEncoder.matches(request.oldPassword(), user.getPasswordHash())) {
            throw new BusinessException("旧密码不正确");
        }
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        user.setUpdatedBy(authUser.getUserId());
        sysUserMapper.updateById(user);
    }

    @Override
    public AuthUser loadAuthUser(Long userId) {
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getId, userId)
                .eq(SysUser::getAccountStatus, "ENABLE")
                .last("LIMIT 1"));
        if (user == null) {
            throw new BusinessException("登录状态已失效");
        }
        return buildAuthUser(user);
    }

    private AuthUser buildAuthUser(SysUser user) {
        SysOrg org = user.getOrgId() == null ? null : sysOrgMapper.selectById(user.getOrgId());
        List<SysRole> enabledRoles = sysRoleMapper.selectEnabledRolesByUserId(user.getId());
        List<String> roles = enabledRoles.stream()
                .map(SysRole::getRoleCode)
                .filter(StringUtils::hasText)
                .toList();
        if (roles.isEmpty()) {
            roles = List.of("STAFF");
        }
        SysRole primaryRole = enabledRoles.isEmpty() ? null : enabledRoles.get(0);
        String identityType = resolveIdentityType(user, primaryRole, roles);
        List<String> legacyModuleKeys = isSuperAdmin(identityType, roles)
                ? moduleAccessRegistry.getAssignableModuleKeys()
                : userModuleMapper.selectModuleKeysByUserId(user.getId());
        List<String> pagePermissions = resolvePagePermissions(user.getId(), identityType, primaryRole, legacyModuleKeys);
        List<String> buttonPermissions = resolveButtonPermissions(user.getId(), identityType, primaryRole);
        Map<String, String> dataScopes = resolveDataScopes(user.getId(), identityType, primaryRole, pagePermissions);
        return new AuthUser(
                user.getId(),
                user.getUsername(),
                user.getRealName(),
                user.getOrgId(),
                org == null ? "" : org.getOrgName(),
                identityType,
                resolvePrimaryRoleCode(primaryRole, roles),
                resolvePrimaryRoleName(primaryRole, identityType),
                roles,
                pagePermissions,
                buttonPermissions,
                dataScopes,
                moduleAccessRegistry.summarizeDataScopes(dataScopes)
        );
    }

    private CurrentUserInfoVO toCurrentUserInfo(AuthUser authUser) {
        List<String> permissions = authUser.isSuperAdmin() ? List.of("*:*:*") : List.of();
        return new CurrentUserInfoVO(
                authUser.getUserId(),
                authUser.getUsername(),
                authUser.getRealName(),
                authUser.getOrgId(),
                authUser.getOrgName(),
                authUser.getIdentityType(),
                authUser.getRoles(),
                authUser.getPrimaryRoleCode(),
                authUser.getPrimaryRoleName(),
                permissions,
                authUser.getPagePermissions(),
                authUser.getButtonPermissions(),
                authUser.getDataScopes(),
                authUser.getPermissionSummary()
        );
    }

    private List<String> resolvePagePermissions(
            Long userId,
            String identityType,
            SysRole primaryRole,
            List<String> legacyModuleKeys
    ) {
        if (isSuperAdmin(identityType, List.of())) {
            return moduleAccessRegistry.getAdminPagePermissions();
        }

        Set<String> pagePermissions = new LinkedHashSet<>(moduleAccessRegistry.derivePagePermissionsFromLegacyModules(legacyModuleKeys));
        pagePermissions.addAll(loadRolePermissionCodes(primaryRole == null ? null : primaryRole.getId(), "PAGE"));
        applyUserPermissionOverrides(userId, "PAGE", pagePermissions);
        if (pagePermissions.isEmpty()) {
            pagePermissions.add("/welcome");
        }
        return moduleAccessRegistry.sortPagePermissions(pagePermissions);
    }

    private List<String> resolveButtonPermissions(Long userId, String identityType, SysRole primaryRole) {
        if (isSuperAdmin(identityType, List.of())) {
            return moduleAccessRegistry.getAdminButtonPermissions();
        }

        Set<String> buttonPermissions = new LinkedHashSet<>(loadRolePermissionCodes(primaryRole == null ? null : primaryRole.getId(), "BUTTON"));
        applyUserPermissionOverrides(userId, "BUTTON", buttonPermissions);
        return List.copyOf(buttonPermissions);
    }

    private Map<String, String> resolveDataScopes(
            Long userId,
            String identityType,
            SysRole primaryRole,
            List<String> pagePermissions
    ) {
        if (isSuperAdmin(identityType, List.of())) {
            return moduleAccessRegistry.buildScopedModules(pagePermissions, "ALL");
        }

        Map<String, String> dataScopes = new LinkedHashMap<>();
        if (primaryRole != null) {
            roleDataScopeMapper.selectList(Wrappers.lambdaQuery(SysRoleDataScope.class)
                            .eq(SysRoleDataScope::getRoleId, primaryRole.getId()))
                    .forEach(item -> dataScopes.put(item.getModuleCode(), item.getScopeType()));
        }

        if (dataScopes.isEmpty()) {
            String fallbackScope = primaryRole != null && StringUtils.hasText(primaryRole.getDataScope())
                    ? primaryRole.getDataScope()
                    : "SELF";
            dataScopes.putAll(moduleAccessRegistry.buildScopedModules(pagePermissions, fallbackScope));
        }

        userDataScopeMapper.selectList(Wrappers.lambdaQuery(SysUserDataScope.class)
                        .eq(SysUserDataScope::getUserId, userId))
                .forEach(item -> dataScopes.put(item.getModuleCode(), item.getScopeType()));
        return dataScopes;
    }

    private Set<String> loadRolePermissionCodes(Long roleId, String permissionType) {
        if (roleId == null) {
            return Set.of();
        }
        List<Long> permissionItemIds = rolePermissionMapper.selectList(Wrappers.lambdaQuery(SysRolePermission.class)
                        .eq(SysRolePermission::getRoleId, roleId))
                .stream()
                .map(SysRolePermission::getPermissionItemId)
                .filter(Objects::nonNull)
                .toList();
        if (permissionItemIds.isEmpty()) {
            return Set.of();
        }
        return permissionItemMapper.selectBatchIds(permissionItemIds).stream()
                .filter(item -> permissionType.equals(item.getPermissionType()))
                .map(SysPermissionItem::getPermissionCode)
                .filter(StringUtils::hasText)
                .collect(LinkedHashSet::new, Set::add, Set::addAll);
    }

    private void applyUserPermissionOverrides(Long userId, String permissionType, Set<String> permissions) {
        List<SysUserPermission> overrides = userPermissionMapper.selectList(Wrappers.lambdaQuery(SysUserPermission.class)
                .eq(SysUserPermission::getUserId, userId));
        if (overrides.isEmpty()) {
            return;
        }

        Map<Long, SysPermissionItem> itemMap = permissionItemMapper.selectBatchIds(overrides.stream()
                        .map(SysUserPermission::getPermissionItemId)
                        .filter(Objects::nonNull)
                        .toList())
                .stream()
                .collect(LinkedHashMap::new, (map, item) -> map.put(item.getId(), item), Map::putAll);

        for (SysUserPermission override : overrides) {
            SysPermissionItem item = itemMap.get(override.getPermissionItemId());
            if (item == null || !permissionType.equals(item.getPermissionType()) || !StringUtils.hasText(item.getPermissionCode())) {
                continue;
            }
            if ("DENY".equalsIgnoreCase(override.getGrantMode())) {
                permissions.remove(item.getPermissionCode());
                continue;
            }
            permissions.add(item.getPermissionCode());
        }
    }

    private String resolveIdentityType(SysUser user, SysRole primaryRole, List<String> roles) {
        if (StringUtils.hasText(user.getIdentityType())) {
            return user.getIdentityType();
        }
        if (primaryRole != null && StringUtils.hasText(primaryRole.getIdentityType())) {
            return primaryRole.getIdentityType();
        }
        if (roles.contains("ADMIN")) {
            return "SUPER_ADMIN";
        }
        if (roles.stream().anyMatch(code -> "DEPT_ADMIN".equals(code) || "MANAGER".equals(code))) {
            return "DEPT_ADMIN";
        }
        return "NORMAL_USER";
    }

    private boolean isSuperAdmin(String identityType, List<String> roles) {
        return "SUPER_ADMIN".equals(identityType) || roles.contains("ADMIN");
    }

    private String resolvePrimaryRoleCode(SysRole primaryRole, List<String> roles) {
        if (primaryRole != null && StringUtils.hasText(primaryRole.getRoleCode())) {
            return primaryRole.getRoleCode();
        }
        return roles.isEmpty() ? "" : roles.get(0);
    }

    private String resolvePrimaryRoleName(SysRole primaryRole, String identityType) {
        if (primaryRole != null && StringUtils.hasText(primaryRole.getRoleName())) {
            return primaryRole.getRoleName();
        }
        return switch (identityType) {
            case "SUPER_ADMIN" -> "超级管理员";
            case "DEPT_ADMIN" -> "管理员";
            default -> "普通用户";
        };
    }
}
