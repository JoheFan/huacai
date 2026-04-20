package com.huacai.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huacai.system.entity.SysOrg;
import com.huacai.system.entity.SysRole;
import com.huacai.system.entity.SysUser;
import com.huacai.system.entity.SysUserRole;
import com.huacai.system.mapper.SysOrgMapper;
import com.huacai.system.mapper.SysRoleMapper;
import com.huacai.system.mapper.SysUserMapper;
import com.huacai.system.mapper.SysUserRoleMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminDataInitializationService {

    private final SysOrgMapper sysOrgMapper;
    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final PasswordEncoder passwordEncoder;

    public AdminDataInitializationService(
            SysOrgMapper sysOrgMapper,
            SysUserMapper sysUserMapper,
            SysRoleMapper sysRoleMapper,
            SysUserRoleMapper sysUserRoleMapper,
            PasswordEncoder passwordEncoder
    ) {
        this.sysOrgMapper = sysOrgMapper;
        this.sysUserMapper = sysUserMapper;
        this.sysRoleMapper = sysRoleMapper;
        this.sysUserRoleMapper = sysUserRoleMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void initializeAdminData() {
        SysOrg org = sysOrgMapper.selectOne(new LambdaQueryWrapper<SysOrg>()
                .eq(SysOrg::getOrgName, "总部")
                .last("LIMIT 1"));
        if (org == null) {
            org = new SysOrg();
            org.setParentId(0L);
            org.setOrgName("总部");
            org.setOrgType("HQ");
            org.setSortNo(1);
            org.setStatus("ENABLE");
            org.setRemark("系统初始化组织");
            sysOrgMapper.insert(org);
        }

        SysRole role = sysRoleMapper.selectOne(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleCode, "ADMIN")
                .last("LIMIT 1"));
        if (role == null) {
            role = new SysRole();
            role.setRoleCode("ADMIN");
            role.setRoleName("系统管理员");
            role.setDataScope("ALL");
            role.setStatus("ENABLE");
            role.setRemark("系统初始化角色");
            sysRoleMapper.insert(role);
        }

        SysUser admin = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, "admin")
                .last("LIMIT 1"));
        if (admin == null) {
            admin = new SysUser();
            admin.setUsername("admin");
            admin.setPasswordHash(passwordEncoder.encode("huacai123"));
            admin.setRealName("管理员");
            admin.setOrgId(org.getId());
            admin.setJobTitle("系统管理员");
            admin.setEmploymentStatus("ON_JOB");
            admin.setAccountStatus("ENABLE");
            admin.setRemark("系统初始化账号");
            sysUserMapper.insert(admin);
        }

        Long userId = admin.getId();
        Long roleId = role.getId();
        SysUserRole userRole = sysUserRoleMapper.selectOne(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, userId)
                .eq(SysUserRole::getRoleId, roleId)
                .last("LIMIT 1"));
        if (userRole == null) {
            userRole = new SysUserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRole.setCreatedBy(userId);
            sysUserRoleMapper.insert(userRole);
        }
    }
}
