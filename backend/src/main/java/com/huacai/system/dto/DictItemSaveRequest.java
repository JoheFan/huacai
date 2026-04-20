package com.huacai.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DictItemSaveRequest(
        @NotNull(message = "字典类型不能为空")
        Long dictTypeId,
        @NotBlank(message = "条目编码不能为空")
        String itemCode,
        @NotBlank(message = "条目名称不能为空")
        String itemName,
        @NotBlank(message = "条目值不能为空")
        String itemValue,
        Integer sortNo,
        String status
) {
}
