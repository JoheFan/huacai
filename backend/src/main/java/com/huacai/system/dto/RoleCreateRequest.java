package com.huacai.system.dto;

import jakarta.validation.constraints.NotBlank;

public record RoleCreateRequest(
    @NotBlank(message = "角色编码不能为空")
    String roleCode,
    @NotBlank(message = "角色名称不能为空")
    String roleName,
    @NotBlank(message = "角色身份不能为空")
    String identityType,
    String dataScope,
    String status,
    String remark
) {
}
