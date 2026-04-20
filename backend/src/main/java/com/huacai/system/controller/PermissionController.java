package com.huacai.system.controller;

import com.huacai.common.model.ApiResponse;
import com.huacai.system.service.SystemManageService;
import com.huacai.system.vo.PermissionCatalogVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "权限项")
@RestController
@RequestMapping("/api/v1/system/permissions")
public class PermissionController {

    private final SystemManageService systemManageService;

    public PermissionController(SystemManageService systemManageService) {
        this.systemManageService = systemManageService;
    }

    @GetMapping("/catalog")
    public ApiResponse<PermissionCatalogVO> catalog() {
        return ApiResponse.success(systemManageService.getPermissionCatalog());
    }
}
