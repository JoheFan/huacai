package com.huacai.system.dto;

import jakarta.validation.constraints.NotBlank;

public record DictTypeSaveRequest(
        @NotBlank(message = "字典编码不能为空")
        String dictCode,
        @NotBlank(message = "字典名称不能为空")
        String dictName,
        String status,
        String remark
) {
}
