package com.huacai.finance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PayeeSaveRequest(
        @NotBlank(message = "收款方名称不能为空")
        @Size(max = 100, message = "收款方名称长度不能超过100")
        String payeeName,
        @Size(max = 32, message = "收款方类型长度不能超过32")
        String payeeType,
        @Size(max = 100, message = "开户行长度不能超过100")
        String bankName,
        @Size(max = 64, message = "银行账号长度不能超过64")
        String bankAccount,
        @Size(max = 50, message = "联系人长度不能超过50")
        String contactName,
        @Size(max = 20, message = "联系电话长度不能超过20")
        String contactPhone,
        @Size(max = 16, message = "状态长度不能超过16")
        String status,
        @Size(max = 500, message = "备注长度不能超过500")
        String remark
) {
}
