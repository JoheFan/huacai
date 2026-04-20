package com.huacai.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CustomerSaveRequest(
        @Size(max = 50, message = "客户编号长度不能超过50")
        String customerNo,
        @NotBlank(message = "客户姓名不能为空")
        @Size(min = 2, max = 100, message = "客户姓名长度必须在2-100位之间")
        String customerName,
        @Pattern(regexp = "^(MALE|FEMALE)?$", message = "性别只能是 MALE 或 FEMALE")
        String gender,
        @Size(max = 18, message = "身份证号长度不能超过18")
        String idCard,
        LocalDate birthday,
        @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
        String mobile,
        @Size(max = 200, message = "公司名称长度不能超过200")
        String companyName,
        @Size(max = 18, message = "统一信用代码长度不能超过18")
        String creditCode,
        LocalDate establishedDate,
        @Size(max = 100, message = "行业长度不能超过100")
        String industry,
        @Size(max = 500, message = "经营地址长度不能超过500")
        String businessAddress,
        @Size(max = 100, message = "开户行长度不能超过100")
        String bankName,
        @Size(max = 50, message = "银行账号长度不能超过50")
        String bankAccount,
        @Size(max = 100, message = "推荐人姓名长度不能超过100")
        String recommenderName,
        BigDecimal recommenderRate,
        BigDecimal serviceFee,
        @Pattern(regexp = "^(INIT|PENDING|APPROVED|REJECTED)?$", message = "业务状态只能是 INIT/PENDING/APPROVED/REJECTED")
        String bizStatus,
        Boolean taxRegistrationNormal,
        @Size(max = 1000, message = "备注长度不能超过1000")
        String remark
) {
}
