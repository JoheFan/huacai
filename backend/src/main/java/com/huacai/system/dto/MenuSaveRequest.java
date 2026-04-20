package com.huacai.system.dto;

import jakarta.validation.constraints.NotBlank;

public record MenuSaveRequest(
        Long parentId,
        @NotBlank(message = "菜单名称不能为空")
        String menuName,
        @NotBlank(message = "菜单类型不能为空")
        String menuType,
        String routePath,
        String componentPath,
        String permissionCode,
        Integer sortNo,
        String status
) {
}
