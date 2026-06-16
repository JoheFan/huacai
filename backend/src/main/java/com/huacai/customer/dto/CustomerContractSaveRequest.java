package com.huacai.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record CustomerContractSaveRequest(
        Long id,
        Long customerId,
        @Size(max = 100, message = "客户姓名长度不能超过100")
        String customerName,
        @Size(max = 200, message = "公司名称长度不能超过200")
        String companyName,
        @Size(max = 18, message = "统一信用代码长度不能超过18")
        String creditCode,
        @Size(max = 100, message = "合同编号长度不能超过100")
        String contractNo,
        @NotBlank(message = "合同名称不能为空")
        @Size(max = 200, message = "合同名称长度不能超过200")
        String contractName,
        java.util.List<Long> fileIds,
        LocalDate signDate,
        @Size(max = 1000, message = "备注长度不能超过1000")
        String remark
) {
}
