package com.huacai.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huacai.common.exception.BusinessException;
import com.huacai.common.model.PageResponse;
import com.huacai.security.AuthUser;
import com.huacai.security.CurrentUserProvider;
import com.huacai.security.ModuleAccessRegistry;
import com.huacai.system.dto.OrgCreateRequest;
import com.huacai.system.dto.OrgUpdateRequest;
import com.huacai.system.dto.RoleCreateRequest;
import com.huacai.system.dto.RolePermissionProfileUpdateRequest;
import com.huacai.system.dto.RoleUpdateRequest;
import com.huacai.system.dto.UserCreateRequest;
import com.huacai.system.dto.UserPermissionProfileUpdateRequest;
import com.huacai.system.dto.UserUpdateRequest;
import com.huacai.system.entity.SysOrg;
import com.huacai.system.entity.SysPermissionItem;
import com.huacai.system.entity.SysRole;
import com.huacai.system.entity.SysRoleDataScope;
import com.huacai.system.entity.SysRolePermission;
import com.huacai.system.entity.SysUser;
import com.huacai.system.entity.SysUserModule;
import com.huacai.system.entity.SysUserDataScope;
import com.huacai.system.entity.SysUserPermission;
import com.huacai.system.entity.SysUserRole;
import com.huacai.system.mapper.OrgMapper;
import com.huacai.system.mapper.PermissionItemMapper;
import com.huacai.system.mapper.RoleMapper;
import com.huacai.system.mapper.RoleDataScopeMapper;
import com.huacai.system.mapper.RolePermissionMapper;
import com.huacai.system.mapper.UserMapper;
import com.huacai.system.mapper.UserDataScopeMapper;
import com.huacai.system.mapper.UserModuleMapper;
import com.huacai.system.mapper.UserPermissionMapper;
import com.huacai.system.mapper.UserRoleMapper;
import com.huacai.system.query.RolePageQuery;
import com.huacai.system.query.UserPageQuery;
import com.huacai.system.service.SystemManageService;
import com.huacai.system.vo.OrgVO;
import com.huacai.system.vo.PermissionCatalogVO;
import com.huacai.system.vo.PermissionItemVO;
import com.huacai.system.vo.RolePermissionProfileVO;
import com.huacai.system.vo.RoleVO;
import com.huacai.system.vo.UserVO;
import com.huacai.system.vo.UserPermissionProfileVO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class SystemManageServiceImpl implements SystemManageService {

    private final OrgMapper orgMapper;
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    private final UserModuleMapper userModuleMapper;
    private final PermissionItemMapper permissionItemMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final UserPermissionMapper userPermissionMapper;
    private final RoleDataScopeMapper roleDataScopeMapper;
    private final UserDataScopeMapper userDataScopeMapper;
    private final CurrentUserProvider currentUserProvider;
    private final PasswordEncoder passwordEncoder;
    private final ModuleAccessRegistry moduleAccessRegistry;

    public SystemManageServiceImpl(
            OrgMapper orgMapper,
            UserMapper userMapper,
            RoleMapper roleMapper,
            UserRoleMapper userRoleMapper,
            UserModuleMapper userModuleMapper,
            PermissionItemMapper permissionItemMapper,
            RolePermissionMapper rolePermissionMapper,
            UserPermissionMapper userPermissionMapper,
            RoleDataScopeMapper roleDataScopeMapper,
            UserDataScopeMapper userDataScopeMapper,
            CurrentUserProvider currentUserProvider,
            PasswordEncoder passwordEncoder,
            ModuleAccessRegistry moduleAccessRegistry
    ) {
        this.orgMapper = orgMapper;
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.userRoleMapper = userRoleMapper;
        this.userModuleMapper = userModuleMapper;
        this.permissionItemMapper = permissionItemMapper;
        this.rolePermissionMapper = rolePermissionMapper;
        this.userPermissionMapper = userPermissionMapper;
        this.roleDataScopeMapper = roleDataScopeMapper;
        this.userDataScopeMapper = userDataScopeMapper;
        this.currentUserProvider = currentUserProvider;
        this.passwordEncoder = passwordEncoder;
        this.moduleAccessRegistry = moduleAccessRegistry;
    }

    // ========== Organization ==========

    @Override
    public List<OrgVO> getOrgTree() {
        List<SysOrg> allOrgs = orgMapper.selectList(new LambdaQueryWrapper<SysOrg>()
                .orderByAsc(SysOrg::getSortNo)
                .orderByAsc(SysOrg::getId));
        Map<Long, String> orgNameMap = allOrgs.stream()
                .collect(Collectors.toMap(SysOrg::getId, SysOrg::getOrgName));
        return buildOrgTree(allOrgs, orgNameMap, 0L);
    }

    private List<OrgVO> buildOrgTree(List<SysOrg> allOrgs, Map<Long, String> orgNameMap, Long parentId) {
        List<OrgVO> result = new ArrayList<>();
        for (SysOrg org : allOrgs) {
            if (Objects.equals(org.getParentId(), parentId)) {
                List<OrgVO> children = buildOrgTree(allOrgs, orgNameMap, org.getId());
                result.add(new OrgVO(
                        org.getId(),
                        org.getParentId(),
                        resolveParentName(orgNameMap, org.getParentId()),
                        org.getOrgName(),
                        org.getOrgType(),
                        org.getSortNo(),
                        org.getStatus(),
                        org.getRemark(),
                        org.getCreatedAt(),
                        org.getUpdatedAt(),
                        children.isEmpty() ? null : children
                ));
            }
        }
        return result;
    }

    @Override
    public OrgVO getOrgDetail(Long id) {
        SysOrg org = getOrgOrThrow(id);
        SysOrg parentOrg = org.getParentId() == null || org.getParentId() == 0L
                ? null
                : orgMapper.selectById(org.getParentId());
        String parentName = parentOrg == null ? "" : parentOrg.getOrgName();
        return new OrgVO(
                org.getId(),
                org.getParentId(),
                parentName,
                org.getOrgName(),
                org.getOrgType(),
                org.getSortNo(),
                org.getStatus(),
                org.getRemark(),
                org.getCreatedAt(),
                org.getUpdatedAt(),
                null
        );
    }

    @Override
    @Transactional
    public void createOrg(OrgCreateRequest request) {
        SysOrg org = new SysOrg();
        org.setParentId(request.parentId() != null ? request.parentId() : 0L);
        org.setOrgName(request.orgName());
        org.setOrgType(request.orgType());
        org.setSortNo(request.sortNo() != null ? request.sortNo() : 0);
        org.setStatus(request.status());
        org.setRemark(request.remark());
        org.setCreatedBy(currentUserProvider.getCurrentUserId());
        org.setUpdatedBy(currentUserProvider.getCurrentUserId());
        orgMapper.insert(org);
    }

    @Override
    @Transactional
    public void updateOrg(Long id, OrgUpdateRequest request) {
        SysOrg org = getOrgOrThrow(id);
        if (request.parentId() != null) {
            if (Objects.equals(request.parentId(), id)) {
                throw new BusinessException("父组织不能是自己");
            }
            org.setParentId(request.parentId());
        }
        if (request.orgName() != null) {
            org.setOrgName(request.orgName());
        }
        if (request.orgType() != null) {
            org.setOrgType(request.orgType());
        }
        if (request.sortNo() != null) {
            org.setSortNo(request.sortNo());
        }
        if (request.remark() != null) {
            org.setRemark(request.remark());
        }
        org.setUpdatedBy(currentUserProvider.getCurrentUserId());
        orgMapper.updateById(org);
    }

    @Override
    @Transactional
    public void deleteOrg(Long id) {
        getOrgOrThrow(id);
        Long childCount = orgMapper.selectCount(new LambdaQueryWrapper<SysOrg>()
                .eq(SysOrg::getParentId, id));
        if (childCount > 0) {
            throw new BusinessException("存在子组织，无法删除");
        }
        Long userCount = userMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getOrgId, id));
        if (userCount > 0) {
            throw new BusinessException("组织下存在用户，无法删除");
        }
        orgMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void updateOrgStatus(Long id, String status) {
        SysOrg org = getOrgOrThrow(id);
        org.setStatus(status);
        org.setUpdatedBy(currentUserProvider.getCurrentUserId());
        orgMapper.updateById(org);
    }

    // ========== User ==========

    @Override
    public PageResponse<UserVO> pageUsers(UserPageQuery query) {
        AuthUser currentUser = getCurrentAuthUserOrThrow();
        Page<SysUser> page = new Page<>(normalizePageNum(query.getPageNum()), normalizePageSize(query.getPageSize()));
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>()
                .orderByDesc(SysUser::getCreatedAt);
        applyManageableUserBoundary(wrapper, currentUser);
        if (query.getOrgId() != null) {
            wrapper.eq(SysUser::getOrgId, query.getOrgId());
        }
        if (StringUtils.hasText(query.getAccountStatus())) {
            wrapper.eq(SysUser::getAccountStatus, query.getAccountStatus());
        }
        if (StringUtils.hasText(query.getEmploymentStatus())) {
            wrapper.eq(SysUser::getEmploymentStatus, query.getEmploymentStatus());
        }
        if (StringUtils.hasText(query.getKeyword())) {
            String keyword = query.getKeyword().trim();
            wrapper.and(w -> w
                    .like(SysUser::getUsername, keyword)
                    .or().like(SysUser::getRealName, keyword)
                    .or().like(SysUser::getPhone, keyword)
                    .or().like(SysUser::getEmail, keyword)
                    .or().like(SysUser::getEmployeeCode, keyword));
        }

        Page<SysUser> result = userMapper.selectPage(page, wrapper);
        List<Long> userIds = result.getRecords().stream()
                .map(SysUser::getId)
                .filter(Objects::nonNull)
                .toList();
        Map<Long, String> orgNameMap = loadOrgNameMap(result.getRecords().stream()
                .map(SysUser::getOrgId)
                .filter(Objects::nonNull)
                .toList());
        Map<Long, SysRole> primaryRoleMap = loadPrimaryRoleMap(userIds);
        Map<Long, Map<String, String>> userDataScopeMap = loadUserDataScopeMap(userIds);
        List<UserVO> records = result.getRecords().stream()
                .map(user -> toUserVO(
                        user,
                        orgNameMap.get(user.getOrgId()),
                        primaryRoleMap.get(user.getId()),
                        resolvePermissionSummary(primaryRoleMap.get(user.getId()), userDataScopeMap.get(user.getId()))
                ))
                .toList();
        return new PageResponse<>(records, result.getTotal(), (int) result.getCurrent(), (int) result.getSize());
    }

    private Map<Long, String> loadOrgNameMap(List<Long> orgIds) {
        if (orgIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return orgMapper.selectBatchIds(orgIds).stream()
                .collect(Collectors.toMap(SysOrg::getId, SysOrg::getOrgName));
    }

    @Override
    public UserVO getUserDetail(Long id) {
        SysUser user = getUserOrThrow(id);
        assertCanManageUser(user);
        String orgName = null;
        if (user.getOrgId() != null) {
            SysOrg org = orgMapper.selectById(user.getOrgId());
            orgName = org != null ? org.getOrgName() : null;
        }
        SysRole primaryRole = loadPrimaryRole(id);
        return toUserVO(user, orgName, primaryRole, resolvePermissionSummary(primaryRole, loadUserDataScopes(id)));
    }

    @Override
    @Transactional
    public void createUser(UserCreateRequest request) {
        AuthUser currentUser = getCurrentAuthUserOrThrow();
        validateUsernameUnique(request.username(), null);
        SysRole primaryRole = getEnabledRoleOrThrow(request.primaryRoleId());
        assertCanManageTarget(primaryRole.getIdentityType(), request.orgId(), currentUser);
        SysUser user = new SysUser();
        user.setUsername(request.username());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setEmployeeCode(request.employeeCode());
        user.setRealName(request.realName());
        user.setPhone(request.phone());
        user.setEmail(request.email());
        user.setOrgId(request.orgId());
        user.setIdentityType(primaryRole.getIdentityType());
        user.setJobTitle(request.jobTitle());
        user.setEmploymentStatus(request.employmentStatus() != null ? request.employmentStatus() : "ON_JOB");
        user.setAccountStatus(request.accountStatus() != null ? request.accountStatus() : "ENABLE");
        user.setRemark(request.remark());
        user.setCreatedBy(currentUserProvider.getCurrentUserId());
        user.setUpdatedBy(currentUserProvider.getCurrentUserId());
        userMapper.insert(user);
        assignRoles(user.getId(), List.of(primaryRole.getId()));
    }

    @Override
    @Transactional
    public void updateUser(Long id, UserUpdateRequest request) {
        SysUser user = getUserOrThrow(id);
        assertCanManageUser(user);
        SysRole currentRole = loadPrimaryRole(id);
        SysRole targetRole = request.primaryRoleId() != null ? getEnabledRoleOrThrow(request.primaryRoleId()) : currentRole;
        Long targetOrgId = request.orgId() != null ? request.orgId() : user.getOrgId();
        String targetIdentityType = targetRole != null && StringUtils.hasText(targetRole.getIdentityType())
                ? targetRole.getIdentityType()
                : user.getIdentityType();
        assertCanManageTarget(targetIdentityType, targetOrgId, getCurrentAuthUserOrThrow());
        if (request.username() != null && !request.username().equals(user.getUsername())) {
            validateUsernameUnique(request.username(), id);
            user.setUsername(request.username());
        }
        if (request.employeeCode() != null) {
            user.setEmployeeCode(request.employeeCode());
        }
        if (request.realName() != null) {
            user.setRealName(request.realName());
        }
        if (request.phone() != null) {
            user.setPhone(request.phone());
        }
        if (request.email() != null) {
            user.setEmail(request.email());
        }
        if (request.orgId() != null) {
            user.setOrgId(request.orgId());
        }
        if (targetRole != null) {
            user.setIdentityType(targetRole.getIdentityType());
        }
        if (request.jobTitle() != null) {
            user.setJobTitle(request.jobTitle());
        }
        if (request.employmentStatus() != null) {
            user.setEmploymentStatus(request.employmentStatus());
        }
        if (request.accountStatus() != null) {
            user.setAccountStatus(request.accountStatus());
        }
        if (request.remark() != null) {
            user.setRemark(request.remark());
        }
        user.setUpdatedBy(currentUserProvider.getCurrentUserId());
        userMapper.updateById(user);
        if (request.primaryRoleId() != null) {
            assignRoles(id, List.of(request.primaryRoleId()));
        }
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        SysUser user = getUserOrThrow(id);
        assertCanManageUser(user);
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, id));
        userModuleMapper.delete(new LambdaQueryWrapper<SysUserModule>()
                .eq(SysUserModule::getUserId, id));
        userPermissionMapper.delete(Wrappers.lambdaQuery(SysUserPermission.class)
                .eq(SysUserPermission::getUserId, id));
        userDataScopeMapper.delete(Wrappers.lambdaQuery(SysUserDataScope.class)
                .eq(SysUserDataScope::getUserId, id));
        userMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void updateUserStatus(Long id, String status) {
        SysUser user = getUserOrThrow(id);
        assertCanManageUser(user);
        user.setAccountStatus(status);
        user.setUpdatedBy(currentUserProvider.getCurrentUserId());
        userMapper.updateById(user);
    }

    @Override
    @Transactional
    public void resetPassword(Long id, String newPassword) {
        SysUser user = getUserOrThrow(id);
        assertCanManageUser(user);
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedBy(currentUserProvider.getCurrentUserId());
        userMapper.updateById(user);
    }

    @Override
    @Transactional
    public void assignRoles(Long userId, List<Long> roleIds) {
        SysUser user = getUserOrThrow(userId);
        assertCanManageUser(user);
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, userId));
        Long primaryRoleId = normalizePrimaryRoleId(roleIds);
        if (primaryRoleId != null) {
            SysRole role = getEnabledRoleOrThrow(primaryRoleId);
            assertCanManageTarget(role.getIdentityType(), user.getOrgId(), getCurrentAuthUserOrThrow());
            Long currentUserId = currentUserProvider.getCurrentUserId();
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(role.getId());
            userRole.setCreatedBy(currentUserId);
            userRole.setCreatedAt(LocalDateTime.now());
            userRoleMapper.insert(userRole);
            user.setIdentityType(role.getIdentityType());
            user.setUpdatedBy(currentUserId);
            userMapper.updateById(user);
        }
    }

    @Override
    public List<Long> getUserRoleIds(Long userId) {
        getUserOrThrow(userId);
        return userRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, userId))
                .stream()
                .map(SysUserRole::getRoleId)
                .toList();
    }

    @Override
    public UserPermissionProfileVO getUserPermissionProfile(Long userId) {
        SysUser user = getUserOrThrow(userId);
        assertCanManageUser(user);
        SysRole primaryRole = loadPrimaryRole(userId);
        List<String> rolePagePermissions = resolveRolePermissionCodes(primaryRole, "PAGE");
        List<String> roleButtonPermissions = resolveRolePermissionCodes(primaryRole, "BUTTON");
        Map<String, String> roleDataScopes = resolveRoleDataScopes(primaryRole, rolePagePermissions);
        List<String> effectivePagePermissions = applyUserPermissionOverrides(userId, rolePagePermissions, "PAGE");
        List<String> effectiveButtonPermissions = applyUserPermissionOverrides(userId, roleButtonPermissions, "BUTTON");
        Map<String, String> effectiveDataScopes = mergeUserDataScopes(userId, roleDataScopes, effectivePagePermissions);
        return new UserPermissionProfileVO(
                userId,
                user.getIdentityType(),
                primaryRole == null ? null : primaryRole.getId(),
                primaryRole == null ? "" : primaryRole.getRoleCode(),
                primaryRole == null ? "未设置" : primaryRole.getRoleName(),
                rolePagePermissions,
                roleButtonPermissions,
                roleDataScopes,
                effectivePagePermissions,
                effectiveButtonPermissions,
                effectiveDataScopes,
                moduleAccessRegistry.summarizeDataScopes(effectiveDataScopes)
        );
    }

    @Override
    @Transactional
    public void updateUserPermissionProfile(Long userId, UserPermissionProfileUpdateRequest request) {
        SysUser user = getUserOrThrow(userId);
        assertCanManageUser(user);
        SysRole primaryRole = loadPrimaryRole(userId);
        List<String> rolePagePermissions = resolveRolePermissionCodes(primaryRole, "PAGE");
        List<String> roleButtonPermissions = resolveRolePermissionCodes(primaryRole, "BUTTON");
        Map<String, String> roleDataScopes = resolveRoleDataScopes(primaryRole, rolePagePermissions);
        List<String> effectivePagePermissions = sanitizePermissionCodes(request.pagePermissions(), "PAGE");
        List<String> effectiveButtonPermissions = sanitizePermissionCodes(request.buttonPermissions(), "BUTTON");
        Map<String, String> effectiveDataScopes = sanitizeDataScopes(
                request.dataScopes(),
                effectivePagePermissions,
                primaryRole == null ? "SELF" : primaryRole.getDataScope()
        );

        rewriteUserPermissionOverrides(userId, rolePagePermissions, effectivePagePermissions, "PAGE");
        rewriteUserPermissionOverrides(userId, roleButtonPermissions, effectiveButtonPermissions, "BUTTON");
        rewriteUserDataScopeOverrides(userId, roleDataScopes, effectiveDataScopes);
    }

    // ========== Role ==========

    @Override
    public PageResponse<RoleVO> pageRoles(RolePageQuery query) {
        AuthUser currentUser = getCurrentAuthUserOrThrow();
        Page<SysRole> page = new Page<>(normalizePageNum(query.getPageNum()), normalizePageSize(query.getPageSize()));
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<SysRole>()
                .orderByDesc(SysRole::getCreatedAt);
        applyManageableRoleBoundary(wrapper, currentUser);
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.eq(SysRole::getStatus, query.getStatus());
        }
        if (StringUtils.hasText(query.getKeyword())) {
            String keyword = query.getKeyword().trim();
            wrapper.and(w -> w
                    .like(SysRole::getRoleCode, keyword)
                    .or().like(SysRole::getRoleName, keyword));
        }

        Page<SysRole> result = roleMapper.selectPage(page, wrapper);
        List<RoleVO> records = result.getRecords().stream()
                .filter(role -> canManageIdentity(currentUser, role.getIdentityType()))
                .map(this::toRoleVO)
                .toList();
        return new PageResponse<>(records, result.getTotal(), (int) result.getCurrent(), (int) result.getSize());
    }

    @Override
    public RoleVO getRoleDetail(Long id) {
        SysRole role = getRoleOrThrow(id);
        assertCanManageRole(role);
        return toRoleVO(role);
    }

    @Override
    public List<RoleVO> listAssignableRoles() {
        AuthUser currentUser = getCurrentAuthUserOrThrow();
        return roleMapper.selectList(Wrappers.lambdaQuery(SysRole.class)
                        .eq(SysRole::getStatus, "ENABLE")
                        .orderByAsc(SysRole::getId))
                .stream()
                .filter(role -> canManageIdentity(currentUser, role.getIdentityType()))
                .map(this::toRoleVO)
                .toList();
    }

    @Override
    @Transactional
    public void createRole(RoleCreateRequest request) {
        AuthUser currentUser = getCurrentAuthUserOrThrow();
        validateIdentityType(request.identityType());
        if (!canManageIdentity(currentUser, request.identityType())) {
            throw new BusinessException("没有管理权限");
        }
        validateRoleCodeUnique(request.roleCode(), null);
        validateRoleNameUnique(request.roleName(), null);
        SysRole role = new SysRole();
        role.setRoleCode(request.roleCode());
        role.setRoleName(request.roleName());
        role.setIdentityType(request.identityType());
        role.setDataScope(resolveDefaultRoleScope(request.identityType(), request.dataScope()));
        role.setStatus(request.status() != null ? request.status() : "ENABLE");
        role.setRemark(request.remark());
        role.setCreatedBy(currentUserProvider.getCurrentUserId());
        role.setUpdatedBy(currentUserProvider.getCurrentUserId());
        roleMapper.insert(role);
    }

    @Override
    @Transactional
    public void updateRole(Long id, RoleUpdateRequest request) {
        SysRole role = getRoleOrThrow(id);
        assertCanManageRole(role);
        if (request.roleCode() != null && !request.roleCode().equals(role.getRoleCode())) {
            validateRoleCodeUnique(request.roleCode(), id);
            role.setRoleCode(request.roleCode());
        }
        if (request.roleName() != null) {
            validateRoleNameUnique(request.roleName(), id);
            role.setRoleName(request.roleName());
        }
        if (request.identityType() != null && !Objects.equals(request.identityType(), role.getIdentityType())) {
            validateIdentityType(request.identityType());
            if (!canManageIdentity(getCurrentAuthUserOrThrow(), request.identityType())) {
                throw new BusinessException("没有管理权限");
            }
            role.setIdentityType(request.identityType());
        }
        if (request.dataScope() != null) {
            role.setDataScope(request.dataScope());
        }
        if (request.status() != null) {
            role.setStatus(request.status());
        }
        if (request.remark() != null) {
            role.setRemark(request.remark());
        }
        role.setUpdatedBy(currentUserProvider.getCurrentUserId());
        roleMapper.updateById(role);
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        SysRole role = getRoleOrThrow(id);
        assertCanManageRole(role);
        Long userCount = userRoleMapper.selectCount(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getRoleId, id));
        if (userCount > 0) {
            throw new BusinessException("角色已分配给用户，无法删除");
        }
        rolePermissionMapper.delete(Wrappers.lambdaQuery(SysRolePermission.class)
                .eq(SysRolePermission::getRoleId, id));
        roleDataScopeMapper.delete(Wrappers.lambdaQuery(SysRoleDataScope.class)
                .eq(SysRoleDataScope::getRoleId, id));
        roleMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void updateRoleStatus(Long id, String status) {
        SysRole role = getRoleOrThrow(id);
        assertCanManageRole(role);
        role.setStatus(status);
        role.setUpdatedBy(currentUserProvider.getCurrentUserId());
        roleMapper.updateById(role);
    }

    @Override
    public RolePermissionProfileVO getRolePermissionProfile(Long roleId) {
        SysRole role = getRoleOrThrow(roleId);
        assertCanManageRole(role);
        List<String> pagePermissions = resolveRolePermissionCodes(role, "PAGE");
        List<String> buttonPermissions = resolveRolePermissionCodes(role, "BUTTON");
        Map<String, String> dataScopes = resolveRoleDataScopes(role, pagePermissions);
        return new RolePermissionProfileVO(
                role.getId(),
                role.getRoleCode(),
                role.getRoleName(),
                role.getIdentityType(),
                pagePermissions,
                buttonPermissions,
                dataScopes,
                moduleAccessRegistry.summarizeDataScopes(dataScopes)
        );
    }

    @Override
    @Transactional
    public void updateRolePermissionProfile(Long roleId, RolePermissionProfileUpdateRequest request) {
        SysRole role = getRoleOrThrow(roleId);
        assertCanManageRole(role);
        List<String> pagePermissions = sanitizePermissionCodes(request.pagePermissions(), "PAGE");
        List<String> buttonPermissions = sanitizePermissionCodes(request.buttonPermissions(), "BUTTON");
        Map<String, String> dataScopes = sanitizeDataScopes(request.dataScopes(), pagePermissions, role.getDataScope());
        rewriteRolePermissions(roleId, pagePermissions, "PAGE");
        rewriteRolePermissions(roleId, buttonPermissions, "BUTTON");
        rewriteRoleDataScopes(roleId, dataScopes);
        role.setDataScope(resolveStoredRoleScope(dataScopes, role.getDataScope(), role.getIdentityType()));
        role.setUpdatedBy(currentUserProvider.getCurrentUserId());
        roleMapper.updateById(role);
    }

    @Override
    public PermissionCatalogVO getPermissionCatalog() {
        List<PermissionItemVO> items = permissionItemMapper.selectList(Wrappers.lambdaQuery(SysPermissionItem.class)
                        .eq(SysPermissionItem::getStatus, "ENABLE")
                        .orderByAsc(SysPermissionItem::getSortNo)
                        .orderByAsc(SysPermissionItem::getId))
                .stream()
                .map(this::toPermissionItemVO)
                .toList();
        return new PermissionCatalogVO(
                items.stream()
                        .filter(item -> "PAGE".equals(item.permissionType()))
                        .filter(item -> !"/welcome".equals(item.permissionCode()))
                        .toList(),
                items.stream()
                        .filter(item -> "BUTTON".equals(item.permissionType()))
                        .toList()
        );
    }

    // ========== Helper Methods ==========

    private AuthUser getCurrentAuthUserOrThrow() {
        return currentUserProvider.getCurrentUser()
                .orElseThrow(() -> new BusinessException("未获取到当前登录用户"));
    }

    private void applyManageableUserBoundary(LambdaQueryWrapper<SysUser> wrapper, AuthUser currentUser) {
        if (currentUser.isSuperAdmin()) {
            return;
        }
        if ("DEPT_ADMIN".equals(currentUser.getIdentityType())) {
            wrapper.eq(SysUser::getOrgId, currentUser.getOrgId())
                    .eq(SysUser::getIdentityType, "NORMAL_USER");
            return;
        }
        throw new BusinessException("没有访问权限");
    }

    private void assertCanManageUser(SysUser user) {
        assertCanManageTarget(user.getIdentityType(), user.getOrgId(), getCurrentAuthUserOrThrow());
    }

    private void assertCanManageTarget(String targetIdentityType, Long targetOrgId, AuthUser currentUser) {
        if (currentUser.isSuperAdmin()) {
            return;
        }
        if ("DEPT_ADMIN".equals(currentUser.getIdentityType())
                && "NORMAL_USER".equals(targetIdentityType)
                && Objects.equals(currentUser.getOrgId(), targetOrgId)) {
            return;
        }
        throw new BusinessException("没有管理权限");
    }

    private void applyManageableRoleBoundary(LambdaQueryWrapper<SysRole> wrapper, AuthUser currentUser) {
        if (currentUser.isSuperAdmin()) {
            return;
        }
        if ("DEPT_ADMIN".equals(currentUser.getIdentityType())) {
            wrapper.eq(SysRole::getIdentityType, "NORMAL_USER");
            return;
        }
        throw new BusinessException("没有访问权限");
    }

    private void assertCanManageRole(SysRole role) {
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        if (!canManageIdentity(getCurrentAuthUserOrThrow(), role.getIdentityType())) {
            throw new BusinessException("没有管理权限");
        }
    }

    private boolean canManageIdentity(AuthUser currentUser, String targetIdentityType) {
        if (currentUser.isSuperAdmin()) {
            return true;
        }
        return "DEPT_ADMIN".equals(currentUser.getIdentityType()) && "NORMAL_USER".equals(targetIdentityType);
    }

    private SysOrg getOrgOrThrow(Long id) {
        SysOrg org = orgMapper.selectById(id);
        if (org == null) {
            throw new BusinessException("组织不存在");
        }
        return org;
    }

    private SysUser getUserOrThrow(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    private SysRole getRoleOrThrow(Long id) {
        SysRole role = roleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        return role;
    }

    private SysRole getEnabledRoleOrThrow(Long id) {
        if (id == null) {
            throw new BusinessException("请选择主角色");
        }
        SysRole role = getRoleOrThrow(id);
        if (!"ENABLE".equals(role.getStatus())) {
            throw new BusinessException("主角色已停用");
        }
        return role;
    }

    private void validateUsernameUnique(String username, Long excludeId) {
        if (!StringUtils.hasText(username)) {
            return;
        }
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username.trim());
        if (excludeId != null) {
            wrapper.ne(SysUser::getId, excludeId);
        }
        Long count = userMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("用户名已存在");
        }
    }

    private void validateRoleCodeUnique(String roleCode, Long excludeId) {
        if (!StringUtils.hasText(roleCode)) {
            return;
        }
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleCode, roleCode.trim());
        if (excludeId != null) {
            wrapper.ne(SysRole::getId, excludeId);
        }
        Long count = roleMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("角色编码已存在");
        }
    }

    private void validateRoleNameUnique(String roleName, Long excludeId) {
        if (!StringUtils.hasText(roleName)) {
            return;
        }
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleName, roleName.trim());
        if (excludeId != null) {
            wrapper.ne(SysRole::getId, excludeId);
        }
        Long count = roleMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("角色名称已存在");
        }
    }

    private void validateIdentityType(String identityType) {
        if (!List.of("SUPER_ADMIN", "DEPT_ADMIN", "NORMAL_USER").contains(identityType)) {
            throw new BusinessException("角色身份不合法");
        }
    }

    private String resolveDefaultRoleScope(String identityType, String explicitScope) {
        if (StringUtils.hasText(explicitScope)) {
            return explicitScope;
        }
        return switch (identityType) {
            case "SUPER_ADMIN" -> "ALL";
            case "DEPT_ADMIN" -> "ORG";
            default -> "SELF";
        };
    }

    private UserVO toUserVO(SysUser user, String orgName, SysRole primaryRole, String permissionSummary) {
        return new UserVO(
                user.getId(),
                user.getUsername(),
                user.getEmployeeCode(),
                user.getRealName(),
                user.getPhone(),
                user.getEmail(),
                user.getOrgId(),
                orgName,
                user.getIdentityType(),
                primaryRole == null ? null : primaryRole.getId(),
                primaryRole == null ? "" : primaryRole.getRoleCode(),
                primaryRole == null ? "未设置" : primaryRole.getRoleName(),
                permissionSummary,
                user.getJobTitle(),
                user.getEmploymentStatus(),
                user.getAccountStatus(),
                user.getLastLoginAt(),
                user.getRemark(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    private Long normalizePrimaryRoleId(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return null;
        }
        return roleIds.get(0);
    }

    private Map<Long, SysRole> loadPrimaryRoleMap(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<SysUserRole> userRoles = userRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>()
                .in(SysUserRole::getUserId, userIds));
        if (userRoles.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Long, SysRole> roleMap = roleMapper.selectBatchIds(userRoles.stream()
                        .map(SysUserRole::getRoleId)
                        .filter(Objects::nonNull)
                        .distinct()
                        .toList())
                .stream()
                .filter(role -> role.getId() != null)
                .collect(Collectors.toMap(SysRole::getId, role -> role));

        return userRoles.stream()
                .collect(Collectors.toMap(
                        SysUserRole::getUserId,
                        userRole -> roleMap.get(userRole.getRoleId()),
                        (existing, ignored) -> existing
                ));
    }

    private SysRole loadPrimaryRole(Long userId) {
        return loadPrimaryRoleMap(List.of(userId)).get(userId);
    }

    private Map<Long, Map<String, String>> loadUserDataScopeMap(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return userDataScopeMapper.selectList(Wrappers.lambdaQuery(SysUserDataScope.class)
                        .in(SysUserDataScope::getUserId, userIds))
                .stream()
                .collect(Collectors.groupingBy(
                        SysUserDataScope::getUserId,
                        Collectors.toMap(
                                SysUserDataScope::getModuleCode,
                                SysUserDataScope::getScopeType,
                                (existing, ignored) -> existing,
                                LinkedHashMap::new
                        )
                ));
    }

    private Map<String, String> loadUserDataScopes(Long userId) {
        return loadUserDataScopeMap(List.of(userId)).getOrDefault(userId, Map.of());
    }

    private String resolvePermissionSummary(SysRole primaryRole, Map<String, String> userDataScopes) {
        if (userDataScopes != null && !userDataScopes.isEmpty()) {
            return moduleAccessRegistry.summarizeDataScopes(userDataScopes);
        }
        if (primaryRole != null && StringUtils.hasText(primaryRole.getDataScope())) {
            return moduleAccessRegistry.summarizeDataScopes(Map.of("customers", primaryRole.getDataScope()));
        }
        return "未配置数据权限";
    }

    private List<String> resolveRolePermissionCodes(SysRole primaryRole, String permissionType) {
        if (primaryRole == null) {
            return List.of();
        }
        List<Long> permissionIds = rolePermissionMapper.selectList(Wrappers.lambdaQuery(SysRolePermission.class)
                        .eq(SysRolePermission::getRoleId, primaryRole.getId()))
                .stream()
                .map(SysRolePermission::getPermissionItemId)
                .filter(Objects::nonNull)
                .toList();
        if (permissionIds.isEmpty()) {
            return List.of();
        }
        return permissionItemMapper.selectBatchIds(permissionIds).stream()
                .filter(item -> permissionType.equals(item.getPermissionType()))
                .filter(item -> "ENABLE".equals(item.getStatus()))
                .map(SysPermissionItem::getPermissionCode)
                .filter(StringUtils::hasText)
                .toList();
    }

    private List<String> applyUserPermissionOverrides(Long userId, List<String> rolePermissionCodes, String permissionType) {
        Set<String> effective = new LinkedHashSet<>(sanitizePermissionCodes(rolePermissionCodes, permissionType));
        List<SysUserPermission> overrides = userPermissionMapper.selectList(Wrappers.lambdaQuery(SysUserPermission.class)
                .eq(SysUserPermission::getUserId, userId));
        if (overrides.isEmpty()) {
            return "PAGE".equals(permissionType)
                    ? moduleAccessRegistry.sortPagePermissions(effective)
                    : List.copyOf(effective);
        }
        Map<Long, SysPermissionItem> permissionItemMap = permissionItemMapper.selectBatchIds(overrides.stream()
                        .map(SysUserPermission::getPermissionItemId)
                        .filter(Objects::nonNull)
                        .toList())
                .stream()
                .collect(Collectors.toMap(SysPermissionItem::getId, item -> item));
        overrides.forEach(item -> {
            SysPermissionItem permissionItem = permissionItemMap.get(item.getPermissionItemId());
            if (permissionItem == null || !permissionType.equals(permissionItem.getPermissionType())) {
                return;
            }
            if ("DENY".equals(item.getGrantMode())) {
                effective.remove(permissionItem.getPermissionCode());
            } else {
                effective.add(permissionItem.getPermissionCode());
            }
        });
        return "PAGE".equals(permissionType)
                ? moduleAccessRegistry.sortPagePermissions(effective)
                : List.copyOf(effective);
    }

    private Map<String, String> resolveRoleDataScopes(SysRole primaryRole, List<String> rolePagePermissions) {
        if (primaryRole == null) {
            return Map.of();
        }
        Map<String, String> roleDataScopes = roleDataScopeMapper.selectList(Wrappers.lambdaQuery(SysRoleDataScope.class)
                        .eq(SysRoleDataScope::getRoleId, primaryRole.getId()))
                .stream()
                .collect(Collectors.toMap(
                        SysRoleDataScope::getModuleCode,
                        SysRoleDataScope::getScopeType,
                        (existing, ignored) -> existing,
                        LinkedHashMap::new
                ));
        if (!roleDataScopes.isEmpty()) {
            return roleDataScopes;
        }
        String fallbackScope = StringUtils.hasText(primaryRole.getDataScope()) ? primaryRole.getDataScope() : "SELF";
        return moduleAccessRegistry.buildScopedModules(rolePagePermissions, fallbackScope);
    }

    private String resolveStoredRoleScope(Map<String, String> dataScopes, String existingScope, String identityType) {
        if (dataScopes == null || dataScopes.isEmpty()) {
            return resolveDefaultRoleScope(identityType, existingScope);
        }
        if (dataScopes.values().stream().anyMatch("ALL"::equals)) {
            return "ALL";
        }
        if (dataScopes.values().stream().anyMatch("ORG_AND_SUB"::equals)) {
            return "ORG_AND_SUB";
        }
        if (dataScopes.values().stream().anyMatch("ORG"::equals)) {
            return "ORG";
        }
        return "SELF";
    }

    private Map<String, String> mergeUserDataScopes(Long userId, Map<String, String> roleDataScopes, List<String> effectivePagePermissions) {
        Set<String> allowedModules = effectivePagePermissions.stream()
                .map(moduleAccessRegistry::resolveModuleCodeByPagePermission)
                .flatMap(Optional::stream)
                .filter(moduleCode -> !moduleCode.startsWith("system."))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        Map<String, String> effective = new LinkedHashMap<>();
        for (String moduleCode : allowedModules) {
            if (roleDataScopes.containsKey(moduleCode)) {
                effective.put(moduleCode, roleDataScopes.get(moduleCode));
            }
        }
        userDataScopeMapper.selectList(Wrappers.lambdaQuery(SysUserDataScope.class)
                        .eq(SysUserDataScope::getUserId, userId))
                .forEach(item -> {
                    if (allowedModules.contains(item.getModuleCode())) {
                        effective.put(item.getModuleCode(), item.getScopeType());
                    }
                });
        return effective;
    }

    private List<String> sanitizePermissionCodes(List<String> permissionCodes, String permissionType) {
        if (permissionCodes == null || permissionCodes.isEmpty()) {
            return List.of();
        }
        Set<String> requested = permissionCodes.stream()
                .filter(StringUtils::hasText)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        List<String> validCodes = permissionItemMapper.selectList(Wrappers.lambdaQuery(SysPermissionItem.class)
                        .eq(SysPermissionItem::getPermissionType, permissionType)
                        .eq(SysPermissionItem::getStatus, "ENABLE"))
                .stream()
                .map(SysPermissionItem::getPermissionCode)
                .filter(requested::contains)
                .toList();
        return "PAGE".equals(permissionType)
                ? moduleAccessRegistry.sortPagePermissions(validCodes)
                : validCodes;
    }

    private Map<String, String> sanitizeDataScopes(
            Map<String, String> requestedDataScopes,
            List<String> effectivePagePermissions,
            String defaultScope
    ) {
        Map<String, String> result = new LinkedHashMap<>();
        Map<String, String> fallbackScopes = moduleAccessRegistry.buildScopedModules(
                effectivePagePermissions,
                StringUtils.hasText(defaultScope) ? defaultScope : "SELF"
        );
        for (String moduleCode : fallbackScopes.keySet()) {
            String scopeType = requestedDataScopes == null ? null : requestedDataScopes.get(moduleCode);
            if (!isSupportedScopeType(scopeType)) {
                scopeType = fallbackScopes.get(moduleCode);
            }
            if (StringUtils.hasText(scopeType)) {
                result.put(moduleCode, scopeType);
            }
        }
        return result;
    }

    private boolean isSupportedScopeType(String scopeType) {
        return List.of("ALL", "ORG", "ORG_AND_SUB", "SELF").contains(scopeType);
    }

    private void rewriteUserPermissionOverrides(
            Long userId,
            List<String> rolePermissions,
            List<String> effectivePermissions,
            String permissionType
    ) {
        List<Long> permissionItemIds = permissionItemMapper.selectList(Wrappers.lambdaQuery(SysPermissionItem.class)
                        .eq(SysPermissionItem::getPermissionType, permissionType))
                .stream()
                .map(SysPermissionItem::getId)
                .toList();
        if (!permissionItemIds.isEmpty()) {
            userPermissionMapper.delete(Wrappers.lambdaQuery(SysUserPermission.class)
                    .eq(SysUserPermission::getUserId, userId)
                    .in(SysUserPermission::getPermissionItemId, permissionItemIds));
        }
        Set<String> roleSet = new LinkedHashSet<>(sanitizePermissionCodes(rolePermissions, permissionType));
        Set<String> effectiveSet = new LinkedHashSet<>(sanitizePermissionCodes(effectivePermissions, permissionType));
        Set<String> allowCodes = new LinkedHashSet<>(effectiveSet);
        allowCodes.removeAll(roleSet);
        Set<String> denyCodes = new LinkedHashSet<>(roleSet);
        denyCodes.removeAll(effectiveSet);
        insertUserPermissionRows(userId, allowCodes, "ALLOW");
        insertUserPermissionRows(userId, denyCodes, "DENY");
    }

    private void insertUserPermissionRows(Long userId, Set<String> permissionCodes, String grantMode) {
        if (permissionCodes.isEmpty()) {
            return;
        }
        Map<String, Long> permissionIdMap = permissionItemMapper.selectList(Wrappers.lambdaQuery(SysPermissionItem.class)
                        .in(SysPermissionItem::getPermissionCode, permissionCodes))
                .stream()
                .collect(Collectors.toMap(SysPermissionItem::getPermissionCode, SysPermissionItem::getId));
        Long currentUserId = currentUserProvider.getCurrentUserId();
        LocalDateTime now = LocalDateTime.now();
        for (String permissionCode : permissionCodes) {
            Long permissionId = permissionIdMap.get(permissionCode);
            if (permissionId == null) {
                continue;
            }
            SysUserPermission entity = new SysUserPermission();
            entity.setUserId(userId);
            entity.setPermissionItemId(permissionId);
            entity.setGrantMode(grantMode);
            entity.setCreatedBy(currentUserId);
            entity.setCreatedAt(now);
            userPermissionMapper.insert(entity);
        }
    }

    private void rewriteUserDataScopeOverrides(Long userId, Map<String, String> roleDataScopes, Map<String, String> effectiveDataScopes) {
        userDataScopeMapper.delete(Wrappers.lambdaQuery(SysUserDataScope.class)
                .eq(SysUserDataScope::getUserId, userId));
        Long currentUserId = currentUserProvider.getCurrentUserId();
        LocalDateTime now = LocalDateTime.now();
        for (Map.Entry<String, String> entry : effectiveDataScopes.entrySet()) {
            if (Objects.equals(roleDataScopes.get(entry.getKey()), entry.getValue())) {
                continue;
            }
            SysUserDataScope entity = new SysUserDataScope();
            entity.setUserId(userId);
            entity.setModuleCode(entry.getKey());
            entity.setScopeType(entry.getValue());
            entity.setCreatedBy(currentUserId);
            entity.setCreatedAt(now);
            userDataScopeMapper.insert(entity);
        }
    }

    private void rewriteRolePermissions(Long roleId, List<String> permissionCodes, String permissionType) {
        List<Long> permissionItemIds = permissionItemMapper.selectList(Wrappers.lambdaQuery(SysPermissionItem.class)
                        .eq(SysPermissionItem::getPermissionType, permissionType))
                .stream()
                .map(SysPermissionItem::getId)
                .toList();
        if (!permissionItemIds.isEmpty()) {
            rolePermissionMapper.delete(Wrappers.lambdaQuery(SysRolePermission.class)
                    .eq(SysRolePermission::getRoleId, roleId)
                    .in(SysRolePermission::getPermissionItemId, permissionItemIds));
        }
        Map<String, Long> permissionIdMap = permissionItemMapper.selectList(Wrappers.lambdaQuery(SysPermissionItem.class)
                        .in(SysPermissionItem::getPermissionCode, permissionCodes))
                .stream()
                .collect(Collectors.toMap(SysPermissionItem::getPermissionCode, SysPermissionItem::getId));
        Long currentUserId = currentUserProvider.getCurrentUserId();
        LocalDateTime now = LocalDateTime.now();
        for (String permissionCode : permissionCodes) {
            Long permissionId = permissionIdMap.get(permissionCode);
            if (permissionId == null) {
                continue;
            }
            SysRolePermission entity = new SysRolePermission();
            entity.setRoleId(roleId);
            entity.setPermissionItemId(permissionId);
            entity.setCreatedBy(currentUserId);
            entity.setCreatedAt(now);
            rolePermissionMapper.insert(entity);
        }
    }

    private void rewriteRoleDataScopes(Long roleId, Map<String, String> dataScopes) {
        roleDataScopeMapper.delete(Wrappers.lambdaQuery(SysRoleDataScope.class)
                .eq(SysRoleDataScope::getRoleId, roleId));
        Long currentUserId = currentUserProvider.getCurrentUserId();
        LocalDateTime now = LocalDateTime.now();
        for (Map.Entry<String, String> entry : dataScopes.entrySet()) {
            SysRoleDataScope entity = new SysRoleDataScope();
            entity.setRoleId(roleId);
            entity.setModuleCode(entry.getKey());
            entity.setScopeType(entry.getValue());
            entity.setCreatedBy(currentUserId);
            entity.setCreatedAt(now);
            roleDataScopeMapper.insert(entity);
        }
    }

    private String resolveParentName(Map<Long, String> orgNameMap, Long parentId) {
        if (parentId == null || parentId == 0L) {
            return "";
        }
        return orgNameMap.getOrDefault(parentId, "");
    }

    private RoleVO toRoleVO(SysRole role) {
        Map<String, String> roleDataScopes = resolveRoleDataScopes(role, resolveRolePermissionCodes(role, "PAGE"));
        return new RoleVO(
                role.getId(),
                role.getRoleCode(),
                role.getRoleName(),
                role.getIdentityType(),
                role.getDataScope(),
                moduleAccessRegistry.summarizeDataScopes(roleDataScopes),
                role.getStatus(),
                role.getRemark(),
                role.getCreatedAt(),
                role.getUpdatedAt()
        );
    }

    private PermissionItemVO toPermissionItemVO(SysPermissionItem item) {
        return new PermissionItemVO(
                item.getId(),
                item.getPermissionCode(),
                item.getPermissionName(),
                item.getPermissionType(),
                item.getModuleCode(),
                item.getRoutePath(),
                item.getButtonCode()
        );
    }

    private int normalizePageNum(Integer pageNum) {
        return pageNum == null || pageNum < 1 ? 1 : pageNum;
    }

    private int normalizePageSize(Integer pageSize) {
        return pageSize == null || pageSize < 1 ? 20 : Math.min(pageSize, 200);
    }
}
