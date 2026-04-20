package com.huacai.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

public record UserCreateRequest(
    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 50, message = "用户名长度必须在4-50位之间")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_]*$", message = "用户名必须以字母开头，只能包含字母、数字和下划线")
    String username,
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 50, message = "密码长度必须在6-50位之间")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z]).+$", message = "密码必须包含字母和数字")
    String password,
    String employeeCode,
    @NotBlank(message = "真实姓名不能为空")
    @Size(min = 2, max = 50, message = "真实姓名长度必须在2-50位之间")
    String realName,
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    String phone,
    @Pattern(regexp = "^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$", message = "邮箱格式不正确")
    String email,
    Long orgId,
    @Size(max = 50, message = "职位长度不能超过50")
    String jobTitle,
    @Pattern(regexp = "^(ON_JOB|OFF_JOB)?$", message = "在职状态只能是 ON_JOB 或 OFF_JOB")
    String employmentStatus,
    @Pattern(regexp = "^(ENABLE|DISABLE)?$", message = "账号状态只能是 ENABLE 或 DISABLE")
    String accountStatus,
    @Size(max = 500, message = "备注长度不能超过500")
    String remark,
    Long primaryRoleId
) {
}
