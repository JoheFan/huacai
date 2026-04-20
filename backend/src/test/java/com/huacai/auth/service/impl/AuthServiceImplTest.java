package com.huacai.auth.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huacai.auth.dto.LoginRequest;
import com.huacai.auth.vo.LoginResponse;
import com.huacai.security.CurrentUserProvider;
import com.huacai.security.JwtTokenService;
import com.huacai.security.ModuleAccessRegistry;
import com.huacai.system.entity.SysOrg;
import com.huacai.system.entity.SysPermissionItem;
import com.huacai.system.entity.SysRole;
import com.huacai.system.entity.SysRolePermission;
import com.huacai.system.entity.SysUser;
import com.huacai.system.mapper.PermissionItemMapper;
import com.huacai.system.mapper.RoleDataScopeMapper;
import com.huacai.system.mapper.RolePermissionMapper;
import com.huacai.system.mapper.SysOrgMapper;
import com.huacai.system.mapper.SysRoleMapper;
import com.huacai.system.mapper.SysUserMapper;
import com.huacai.system.mapper.UserDataScopeMapper;
import com.huacai.system.mapper.UserModuleMapper;
import com.huacai.system.mapper.UserPermissionMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private SysUserMapper sysUserMapper;

    @Mock
    private SysOrgMapper sysOrgMapper;

    @Mock
    private SysRoleMapper sysRoleMapper;

    @Mock
    private UserModuleMapper userModuleMapper;

    @Mock
    private RolePermissionMapper rolePermissionMapper;

    @Mock
    private UserPermissionMapper userPermissionMapper;

    @Mock
    private RoleDataScopeMapper roleDataScopeMapper;

    @Mock
    private UserDataScopeMapper userDataScopeMapper;

    @Mock
    private PermissionItemMapper permissionItemMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenService jwtTokenService;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @Test
    void loginReturnsExpandedPermissionStateWithLegacyFallback() {
        SysUser user = new SysUser();
        user.setId(7L);
        user.setUsername("zhangyiyi");
        user.setRealName("张一一");
        user.setOrgId(2L);
        user.setAccountStatus("ENABLE");
        user.setPasswordHash("hashed");
        user.setIdentityType("NORMAL_USER");

        SysOrg financeOrg = new SysOrg();
        financeOrg.setId(2L);
        financeOrg.setOrgName("财务");

        SysRole role = new SysRole();
        role.setId(3L);
        role.setRoleCode("STAFF");
        role.setRoleName("普通用户");
        role.setIdentityType("NORMAL_USER");
        role.setDataScope("SELF");

        SysRolePermission rolePermission = new SysRolePermission();
        rolePermission.setRoleId(3L);
        rolePermission.setPermissionItemId(10L);

        SysPermissionItem permissionItem = new SysPermissionItem();
        permissionItem.setId(10L);
        permissionItem.setPermissionCode("/customers");
        permissionItem.setPermissionType("PAGE");

        when(sysUserMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(user);
        when(sysOrgMapper.selectById(2L)).thenReturn(financeOrg);
        when(sysRoleMapper.selectEnabledRolesByUserId(7L)).thenReturn(List.of(role));
        when(rolePermissionMapper.selectList(any())).thenReturn(List.of(rolePermission));
        when(permissionItemMapper.selectBatchIds(List.of(10L))).thenReturn(List.of(permissionItem));
        when(userPermissionMapper.selectList(any())).thenReturn(List.of());
        when(roleDataScopeMapper.selectList(any())).thenReturn(List.of());
        when(userDataScopeMapper.selectList(any())).thenReturn(List.of());
        when(userModuleMapper.selectModuleKeysByUserId(7L)).thenReturn(List.of("repayments"));
        when(passwordEncoder.matches("123456", "hashed")).thenReturn(true);
        when(jwtTokenService.generateToken(any())).thenReturn("token");

        AuthServiceImpl service = new AuthServiceImpl(
                sysUserMapper,
                sysOrgMapper,
                sysRoleMapper,
                userModuleMapper,
                rolePermissionMapper,
                userPermissionMapper,
                roleDataScopeMapper,
                userDataScopeMapper,
                permissionItemMapper,
                passwordEncoder,
                jwtTokenService,
                currentUserProvider,
                new ModuleAccessRegistry()
        );

        LoginResponse response = service.login(new LoginRequest("zhangyiyi", "123456"));

        assertThat(response.userInfo().identityType()).isEqualTo("NORMAL_USER");
        assertThat(response.userInfo().primaryRoleCode()).isEqualTo("STAFF");
        assertThat(response.userInfo().primaryRoleName()).isEqualTo("普通用户");
        assertThat(response.userInfo().pagePermissions()).containsExactly("/welcome", "/customers", "/repayments");
        assertThat(response.userInfo().buttonPermissions()).isEmpty();
        assertThat(response.userInfo().dataScopes()).containsEntry("customers", "SELF");
        assertThat(response.userInfo().dataScopes()).containsEntry("repayments", "SELF");
        assertThat(response.userInfo().permissionSummary()).isEqualTo("仅本人数据权限");
    }
}
