package com.huacai.system.service;

import com.huacai.common.model.PageResponse;
import com.huacai.system.dto.OrgCreateRequest;
import com.huacai.system.dto.OrgUpdateRequest;
import com.huacai.system.dto.RoleCreateRequest;
import com.huacai.system.dto.RolePermissionProfileUpdateRequest;
import com.huacai.system.dto.RoleUpdateRequest;
import com.huacai.system.dto.UserCreateRequest;
import com.huacai.system.dto.UserPermissionProfileUpdateRequest;
import com.huacai.system.dto.UserUpdateRequest;
import com.huacai.system.query.RolePageQuery;
import com.huacai.system.query.UserPageQuery;
import com.huacai.system.vo.OrgVO;
import com.huacai.system.vo.PermissionCatalogVO;
import com.huacai.system.vo.RolePermissionProfileVO;
import com.huacai.system.vo.RoleVO;
import com.huacai.system.vo.UserVO;
import com.huacai.system.vo.UserPermissionProfileVO;
import java.util.List;

public interface SystemManageService {

    // Organization
    List<OrgVO> getOrgTree();
    OrgVO getOrgDetail(Long id);
    void createOrg(OrgCreateRequest request);
    void updateOrg(Long id, OrgUpdateRequest request);
    void deleteOrg(Long id);
    void updateOrgStatus(Long id, String status);

    // User
    PageResponse<UserVO> pageUsers(UserPageQuery query);
    UserVO getUserDetail(Long id);
    void createUser(UserCreateRequest request);
    void updateUser(Long id, UserUpdateRequest request);
    void deleteUser(Long id);
    void updateUserStatus(Long id, String status);
    void resetPassword(Long id, String newPassword);
    void assignRoles(Long userId, List<Long> roleIds);
    List<Long> getUserRoleIds(Long userId);
    UserPermissionProfileVO getUserPermissionProfile(Long userId);
    void updateUserPermissionProfile(Long userId, UserPermissionProfileUpdateRequest request);

    // Role
    PageResponse<RoleVO> pageRoles(RolePageQuery query);
    RoleVO getRoleDetail(Long id);
    List<RoleVO> listAssignableRoles();
    void createRole(RoleCreateRequest request);
    void updateRole(Long id, RoleUpdateRequest request);
    void deleteRole(Long id);
    void updateRoleStatus(Long id, String status);
    RolePermissionProfileVO getRolePermissionProfile(Long roleId);
    void updateRolePermissionProfile(Long roleId, RolePermissionProfileUpdateRequest request);

    // Permission
    PermissionCatalogVO getPermissionCatalog();
}
