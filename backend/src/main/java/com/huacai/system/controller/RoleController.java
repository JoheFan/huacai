package com.huacai.system.controller;

import com.huacai.common.model.ApiResponse;
import com.huacai.common.model.PageResponse;
import com.huacai.common.model.StatusUpdateRequest;
import com.huacai.system.dto.RoleCreateRequest;
import com.huacai.system.dto.RolePermissionProfileUpdateRequest;
import com.huacai.system.dto.RoleUpdateRequest;
import com.huacai.system.query.RolePageQuery;
import com.huacai.system.service.SystemManageService;
import com.huacai.system.vo.RolePermissionProfileVO;
import com.huacai.system.vo.RoleVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "角色管理")
@RestController
@RequestMapping("/api/v1/system/roles")
public class RoleController {

    private final SystemManageService systemManageService;

    public RoleController(SystemManageService systemManageService) {
        this.systemManageService = systemManageService;
    }

    @GetMapping
    public ApiResponse<PageResponse<RoleVO>> page(@ModelAttribute RolePageQuery query) {
        return ApiResponse.success(systemManageService.pageRoles(query));
    }

    @GetMapping("/options")
    public ApiResponse<List<RoleVO>> options() {
        return ApiResponse.success(systemManageService.listAssignableRoles());
    }

    @GetMapping("/{id}")
    public ApiResponse<RoleVO> detail(@PathVariable Long id) {
        return ApiResponse.success(systemManageService.getRoleDetail(id));
    }

    @PostMapping
    public ApiResponse<Void> create(@Valid @RequestBody RoleCreateRequest request) {
        systemManageService.createRole(request);
        return ApiResponse.success();
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody RoleUpdateRequest request) {
        systemManageService.updateRole(id, request);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        systemManageService.deleteRole(id);
        return ApiResponse.success();
    }

    @PutMapping("/{id}/status")
    public ApiResponse<Void> updateStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdateRequest request) {
        systemManageService.updateRoleStatus(id, request.status());
        return ApiResponse.success();
    }

    @GetMapping("/{id}/permissions")
    public ApiResponse<RolePermissionProfileVO> getPermissionProfile(@PathVariable Long id) {
        return ApiResponse.success(systemManageService.getRolePermissionProfile(id));
    }

    @PutMapping("/{id}/permissions")
    public ApiResponse<Void> updatePermissionProfile(
            @PathVariable Long id,
            @RequestBody RolePermissionProfileUpdateRequest request
    ) {
        systemManageService.updateRolePermissionProfile(id, request);
        return ApiResponse.success();
    }
}
