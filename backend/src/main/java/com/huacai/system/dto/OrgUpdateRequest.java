package com.huacai.system.dto;

import jakarta.validation.constraints.NotBlank;

public record OrgUpdateRequest(
        Long parentId,
        @NotBlank(message = "组织名称不能为空")
        String orgName,
        @NotBlank(message = "组织类型不能为空")
        String orgType,
        Integer sortNo,
        @NotBlank(message = "状态不能为空")
        String status,
        String remark
) {
}
