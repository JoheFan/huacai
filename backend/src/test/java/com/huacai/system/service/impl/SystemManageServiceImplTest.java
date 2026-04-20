package com.huacai.system.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huacai.security.AuthUser;
import com.huacai.security.CurrentUserProvider;
import com.huacai.security.ModuleAccessRegistry;
import com.huacai.system.dto.RoleCreateRequest;
import com.huacai.system.dto.RolePermissionProfileUpdateRequest;
import com.huacai.system.dto.UserCreateRequest;
import com.huacai.system.dto.UserPermissionProfileUpdateRequest;
import com.huacai.system.entity.SysOrg;
import com.huacai.system.entity.SysPermissionItem;
import com.huacai.system.entity.SysRole;
import com.huacai.system.entity.SysRoleDataScope;
import com.huacai.system.entity.SysRolePermission;
import com.huacai.system.entity.SysUser;
import com.huacai.system.entity.SysUserDataScope;
import com.huacai.system.entity.SysUserPermission;
import com.huacai.system.entity.SysUserRole;
import com.huacai.system.mapper.OrgMapper;
import com.huacai.system.mapper.PermissionItemMapper;
import com.huacai.system.mapper.RoleDataScopeMapper;
import com.huacai.system.mapper.RoleMapper;
import com.huacai.system.mapper.RolePermissionMapper;
import com.huacai.system.mapper.UserDataScopeMapper;
import com.huacai.system.mapper.UserMapper;
import com.huacai.system.mapper.UserModuleMapper;
import com.huacai.system.mapper.UserPermissionMapper;
import com.huacai.system.mapper.UserRoleMapper;
import com.huacai.system.vo.RoleVO;
import com.huacai.system.vo.UserVO;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class SystemManageServiceImplTest {

    @Mock
    private OrgMapper orgMapper;
    @Mock
    private UserMapper userMapper;
    @Mock
    private RoleMapper roleMapper;
    @Mock
    private UserRoleMapper userRoleMapper;
    @Mock
    private UserModuleMapper userModuleMapper;
    @Mock
    private PermissionItemMapper permissionItemMapper;
    @Mock
    private RolePermissionMapper rolePermissionMapper;
    @Mock
    private UserPermissionMapper userPermissionMapper;
    @Mock
    private RoleDataScopeMapper roleDataScopeMapper;
    @Mock
    private UserDataScopeMapper userDataScopeMapper;
    @Mock
    private CurrentUserProvider currentUserProvider;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void createRolePersistsIdentityType() {
        when(currentUserProvider.getCurrentUser()).thenReturn(Optional.of(superAdmin()));
        when(currentUserProvider.getCurrentUserId()).thenReturn(99L);

        SystemManageServiceImpl service = createService();

        service.createRole(new RoleCreateRequest(
                "CUSTOMER_MANAGER",
                "客户经理",
                "NORMAL_USER",
                "ORG",
                "ENABLE",
                null
        ));

        ArgumentCaptor<SysRole> roleCaptor = ArgumentCaptor.forClass(SysRole.class);
        verify(roleMapper).insert(roleCaptor.capture());
        assertThat(roleCaptor.getValue().getIdentityType()).isEqualTo("NORMAL_USER");
        assertThat(roleCaptor.getValue().getDataScope()).isEqualTo("ORG");
    }

    @Test
    void createUserPersistsSinglePrimaryRoleAndDerivedIdentity() {
        when(currentUserProvider.getCurrentUser()).thenReturn(Optional.of(superAdmin()));
        when(currentUserProvider.getCurrentUserId()).thenReturn(99L);
        when(passwordEncoder.encode("123456")).thenReturn("encoded-password");
        when(roleMapper.selectById(3L)).thenReturn(enabledRole(3L, "STAFF", "普通用户", "NORMAL_USER", "SELF"));
        doAnswer(invocation -> {
            SysUser user = invocation.getArgument(0);
            user.setId(7L);
            return 1;
        }).when(userMapper).insert(any(SysUser.class));
        when(userMapper.selectById(7L)).thenReturn(user(7L, "zhangyiyi", 5L, "NORMAL_USER"));

        SystemManageServiceImpl service = createService();

        service.createUser(new UserCreateRequest(
                "zhangyiyi",
                "123456",
                "A-1001",
                "张一一",
                "18888888888",
                null,
                5L,
                "财务",
                "ON_JOB",
                "ENABLE",
                null,
                3L
        ));

        ArgumentCaptor<SysUser> userCaptor = ArgumentCaptor.forClass(SysUser.class);
        ArgumentCaptor<SysUserRole> userRoleCaptor = ArgumentCaptor.forClass(SysUserRole.class);
        verify(userMapper).insert(userCaptor.capture());
        verify(userRoleMapper).insert(userRoleCaptor.capture());
        assertThat(userCaptor.getValue().getIdentityType()).isEqualTo("NORMAL_USER");
        assertThat(userRoleCaptor.getValue().getRoleId()).isEqualTo(3L);
    }

    @Test
    void pageRolesOnlyReturnsManageableIdentityRange() {
        when(currentUserProvider.getCurrentUser()).thenReturn(Optional.of(deptAdmin()));
        doAnswer(invocation -> {
            Page<SysRole> page = invocation.getArgument(0);
            page.setRecords(List.of(
                    enabledRole(1L, "ADMIN", "系统管理员", "SUPER_ADMIN", "ALL"),
                    enabledRole(3L, "STAFF", "普通用户", "NORMAL_USER", "SELF")
            ));
            page.setTotal(2L);
            return page;
        }).when(roleMapper).selectPage(any(), any());

        SystemManageServiceImpl service = createService();

        List<RoleVO> records = service.pageRoles(new com.huacai.system.query.RolePageQuery()).records();

        assertThat(records).extracting(RoleVO::roleCode).containsExactly("STAFF");
    }

    @Test
    void getUserDetailReturnsPrimaryRoleAndPermissionSummary() {
        SysUser user = user(7L, "zhangyiyi", 5L, "NORMAL_USER");
        user.setRealName("张一一");
        SysOrg org = new SysOrg();
        org.setId(5L);
        org.setOrgName("财务");
        SysRole role = enabledRole(3L, "STAFF", "普通用户", "NORMAL_USER", "SELF");
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(7L);
        userRole.setRoleId(3L);

        when(currentUserProvider.getCurrentUser()).thenReturn(Optional.of(superAdmin()));
        when(userMapper.selectById(7L)).thenReturn(user);
        when(orgMapper.selectById(5L)).thenReturn(org);
        when(userRoleMapper.selectList(any())).thenReturn(List.of(userRole));
        when(roleMapper.selectBatchIds(any())).thenReturn(List.of(role));
        when(userDataScopeMapper.selectList(any())).thenReturn(List.of());

        SystemManageServiceImpl service = createService();

        UserVO detail = service.getUserDetail(7L);

        assertThat(detail.primaryRoleCode()).isEqualTo("STAFF");
        assertThat(detail.primaryRoleName()).isEqualTo("普通用户");
        assertThat(detail.identityType()).isEqualTo("NORMAL_USER");
        assertThat(detail.permissionSummary()).isEqualTo("仅本人数据权限");
    }

    @Test
    void getRolePermissionProfileReturnsTemplatePermissionsAndDataScopes() {
        SysRole role = enabledRole(3L, "STAFF", "普通用户", "NORMAL_USER", "SELF");
        SysPermissionItem customerPage = permissionItem(21L, "/customers", "PAGE", "customers");
        SysPermissionItem roleAssignButton = permissionItem(22L, "system.roles:assign-permission", "BUTTON", "system.roles");
        SysRolePermission pageRolePermission = new SysRolePermission();
        pageRolePermission.setRoleId(3L);
        pageRolePermission.setPermissionItemId(21L);
        SysRolePermission buttonRolePermission = new SysRolePermission();
        buttonRolePermission.setRoleId(3L);
        buttonRolePermission.setPermissionItemId(22L);
        SysRoleDataScope roleDataScope = new SysRoleDataScope();
        roleDataScope.setRoleId(3L);
        roleDataScope.setModuleCode("customers");
        roleDataScope.setScopeType("ORG");

        when(currentUserProvider.getCurrentUser()).thenReturn(Optional.of(superAdmin()));
        when(roleMapper.selectById(3L)).thenReturn(role);
        when(permissionItemMapper.selectBatchIds(any())).thenReturn(List.of(customerPage, roleAssignButton));
        when(rolePermissionMapper.selectList(any())).thenReturn(List.of(pageRolePermission, buttonRolePermission));
        when(roleDataScopeMapper.selectList(any())).thenReturn(List.of(roleDataScope));

        SystemManageServiceImpl service = createService();

        var profile = service.getRolePermissionProfile(3L);

        assertThat(profile.roleCode()).isEqualTo("STAFF");
        assertThat(profile.pagePermissions()).containsExactly("/customers");
        assertThat(profile.buttonPermissions()).containsExactly("system.roles:assign-permission");
        assertThat(profile.dataScopes()).containsEntry("customers", "ORG");
        assertThat(profile.permissionSummary()).isEqualTo("本部门数据权限");
    }

    @Test
    void updateRolePermissionProfileRewritesTemplatePermissionsAndDataScopes() {
        SysRole role = enabledRole(3L, "STAFF", "普通用户", "NORMAL_USER", "SELF");
        SysPermissionItem customerPage = permissionItem(21L, "/customers", "PAGE", "customers");
        SysPermissionItem loanPage = permissionItem(23L, "/loan-orders", "PAGE", "loan-orders");
        SysPermissionItem roleAssignButton = permissionItem(22L, "system.roles:assign-permission", "BUTTON", "system.roles");

        when(currentUserProvider.getCurrentUser()).thenReturn(Optional.of(superAdmin()));
        when(currentUserProvider.getCurrentUserId()).thenReturn(99L);
        when(roleMapper.selectById(3L)).thenReturn(role);
        when(permissionItemMapper.selectList(any())).thenReturn(List.of(customerPage, roleAssignButton, loanPage));

        SystemManageServiceImpl service = createService();

        service.updateRolePermissionProfile(3L, new RolePermissionProfileUpdateRequest(
                List.of("/customers", "/loan-orders"),
                List.of("system.roles:assign-permission"),
                Map.of("customers", "ORG", "loan-orders", "SELF")
        ));

        ArgumentCaptor<SysRoleDataScope> dataScopeCaptor = ArgumentCaptor.forClass(SysRoleDataScope.class);
        verify(rolePermissionMapper, org.mockito.Mockito.times(2)).delete(any());
        verify(roleDataScopeMapper).delete(any());
        verify(roleDataScopeMapper, org.mockito.Mockito.times(2)).insert(dataScopeCaptor.capture());
        assertThat(dataScopeCaptor.getAllValues())
                .extracting(SysRoleDataScope::getModuleCode, SysRoleDataScope::getScopeType)
                .containsExactlyInAnyOrder(
                        tuple("customers", "ORG"),
                        tuple("loan-orders", "SELF")
                );
        assertThat(role.getDataScope()).isEqualTo("ORG");
    }

    @Test
    void updateUserPermissionProfileStoresAllowAndDataScopeOverrides() {
        SysUser user = user(7L, "zhangyiyi", 5L, "NORMAL_USER");
        SysRole role = enabledRole(3L, "STAFF", "普通用户", "NORMAL_USER", "SELF");
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(7L);
        userRole.setRoleId(3L);
        SysPermissionItem customerPage = permissionItem(21L, "/customers", "PAGE", "customers");
        SysPermissionItem userCreate = permissionItem(22L, "system.users:create", "BUTTON", "system.users");
        SysRolePermission pageRolePermission = new SysRolePermission();
        pageRolePermission.setRoleId(3L);
        pageRolePermission.setPermissionItemId(21L);
        SysRoleDataScope roleDataScope = new SysRoleDataScope();
        roleDataScope.setRoleId(3L);
        roleDataScope.setModuleCode("customers");
        roleDataScope.setScopeType("SELF");

        when(currentUserProvider.getCurrentUser()).thenReturn(Optional.of(superAdmin()));
        when(currentUserProvider.getCurrentUserId()).thenReturn(99L);
        when(userMapper.selectById(7L)).thenReturn(user);
        when(userRoleMapper.selectList(any())).thenReturn(List.of(userRole));
        when(roleMapper.selectBatchIds(any())).thenReturn(List.of(role));
        when(permissionItemMapper.selectBatchIds(any())).thenReturn(List.of(customerPage, userCreate));
        when(permissionItemMapper.selectList(any())).thenReturn(List.of(customerPage, userCreate));
        when(rolePermissionMapper.selectList(any())).thenReturn(List.of(pageRolePermission));
        when(roleDataScopeMapper.selectList(any())).thenReturn(List.of(roleDataScope));

        SystemManageServiceImpl service = createService();

        service.updateUserPermissionProfile(7L, new UserPermissionProfileUpdateRequest(
                List.of("/customers"),
                List.of("system.users:create"),
                Map.of("customers", "ORG")
        ));

        ArgumentCaptor<SysUserDataScope> dataScopeCaptor = ArgumentCaptor.forClass(SysUserDataScope.class);
        verify(userPermissionMapper, org.mockito.Mockito.atLeastOnce()).delete(any());
        verify(userDataScopeMapper).insert(dataScopeCaptor.capture());
        assertThat(dataScopeCaptor.getValue().getModuleCode()).isEqualTo("customers");
        assertThat(dataScopeCaptor.getValue().getScopeType()).isEqualTo("ORG");
    }

    @Test
    void listAssignableRolesFiltersToManageableIdentityRange() {
        when(currentUserProvider.getCurrentUser()).thenReturn(Optional.of(deptAdmin()));
        when(roleMapper.selectList(any())).thenReturn(List.of(
                enabledRole(1L, "ADMIN", "系统管理员", "SUPER_ADMIN", "ALL"),
                enabledRole(2L, "DEPT_ADMIN", "部门管理员", "DEPT_ADMIN", "ORG"),
                enabledRole(3L, "STAFF", "普通用户", "NORMAL_USER", "SELF")
        ));

        SystemManageServiceImpl service = createService();

        assertThat(service.listAssignableRoles()).extracting("roleCode").containsExactly("STAFF");
    }

    private SystemManageServiceImpl createService() {
        return new SystemManageServiceImpl(
                orgMapper,
                userMapper,
                roleMapper,
                userRoleMapper,
                userModuleMapper,
                permissionItemMapper,
                rolePermissionMapper,
                userPermissionMapper,
                roleDataScopeMapper,
                userDataScopeMapper,
                currentUserProvider,
                passwordEncoder,
                new ModuleAccessRegistry()
        );
    }

    private static AuthUser superAdmin() {
        return new AuthUser(
                99L,
                "admin",
                "管理员",
                1L,
                "总部",
                "SUPER_ADMIN",
                "ADMIN",
                "系统管理员",
                List.of("ADMIN"),
                List.of("/system/users"),
                List.of(),
                Map.of("customers", "ALL"),
                "全部数据权限"
        );
    }

    private static AuthUser deptAdmin() {
        return new AuthUser(
                88L,
                "dept_admin",
                "部门管理员",
                5L,
                "财务",
                "DEPT_ADMIN",
                "DEPT_ADMIN",
                "部门管理员",
                List.of("DEPT_ADMIN"),
                List.of("/system/users"),
                List.of(),
                Map.of("customers", "ORG"),
                "本部门数据权限"
        );
    }

    private static SysUser user(Long id, String username, Long orgId, String identityType) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setUsername(username);
        user.setOrgId(orgId);
        user.setIdentityType(identityType);
        return user;
    }

    private static SysRole enabledRole(Long id, String roleCode, String roleName, String identityType, String dataScope) {
        SysRole role = new SysRole();
        role.setId(id);
        role.setRoleCode(roleCode);
        role.setRoleName(roleName);
        role.setIdentityType(identityType);
        role.setDataScope(dataScope);
        role.setStatus("ENABLE");
        return role;
    }

    private static SysPermissionItem permissionItem(Long id, String permissionCode, String permissionType, String moduleCode) {
        SysPermissionItem item = new SysPermissionItem();
        item.setId(id);
        item.setPermissionCode(permissionCode);
        item.setPermissionType(permissionType);
        item.setModuleCode(moduleCode);
        item.setStatus("ENABLE");
        return item;
    }
}
