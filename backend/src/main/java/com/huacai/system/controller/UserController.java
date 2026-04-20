package com.huacai.system.controller;

import com.huacai.common.model.ApiResponse;
import com.huacai.common.model.PageResponse;
import com.huacai.common.model.StatusUpdateRequest;
import com.huacai.system.dto.ResetPasswordRequest;
import com.huacai.system.dto.UserCreateRequest;
import com.huacai.system.dto.UserPermissionProfileUpdateRequest;
import com.huacai.system.dto.UserUpdateRequest;
import com.huacai.system.query.UserPageQuery;
import com.huacai.system.service.SystemManageService;
import com.huacai.system.vo.UserVO;
import com.huacai.system.vo.UserPermissionProfileVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/v1/system/users")
public class UserController {

    private final SystemManageService systemManageService;

    public UserController(SystemManageService systemManageService) {
        this.systemManageService = systemManageService;
    }

    @GetMapping
    public ApiResponse<PageResponse<UserVO>> page(@ModelAttribute UserPageQuery query) {
        return ApiResponse.success(systemManageService.pageUsers(query));
    }

    @GetMapping("/{id}")
    public ApiResponse<UserVO> detail(@PathVariable Long id) {
        return ApiResponse.success(systemManageService.getUserDetail(id));
    }

    @PostMapping
    public ApiResponse<Void> create(@Valid @RequestBody UserCreateRequest request) {
        systemManageService.createUser(request);
        return ApiResponse.success();
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        systemManageService.updateUser(id, request);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        systemManageService.deleteUser(id);
        return ApiResponse.success();
    }

    @PutMapping("/{id}/status")
    public ApiResponse<Void> updateStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdateRequest request) {
        systemManageService.updateUserStatus(id, request.status());
        return ApiResponse.success();
    }

    @PutMapping("/{id}/reset-password")
    public ApiResponse<Void> resetPassword(@PathVariable Long id, @Valid @RequestBody ResetPasswordRequest request) {
        systemManageService.resetPassword(id, request.newPassword());
        return ApiResponse.success();
    }

    @GetMapping("/{id}/roles")
    public ApiResponse<List<Long>> getUserRoles(@PathVariable Long id) {
        return ApiResponse.success(systemManageService.getUserRoleIds(id));
    }

    @PutMapping("/{id}/roles")
    public ApiResponse<Void> assignRoles(@PathVariable Long id, @RequestBody Map<String, List<Long>> request) {
        systemManageService.assignRoles(id, request.get("roleIds"));
        return ApiResponse.success();
    }

    @GetMapping("/{id}/permissions")
    public ApiResponse<UserPermissionProfileVO> permissionProfile(@PathVariable Long id) {
        return ApiResponse.success(systemManageService.getUserPermissionProfile(id));
    }

    @PutMapping("/{id}/permissions")
    public ApiResponse<Void> updatePermissionProfile(
            @PathVariable Long id,
            @RequestBody UserPermissionProfileUpdateRequest request
    ) {
        systemManageService.updateUserPermissionProfile(id, request);
        return ApiResponse.success();
    }
}
