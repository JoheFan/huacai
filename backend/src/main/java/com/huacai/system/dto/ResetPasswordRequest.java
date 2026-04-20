package com.huacai.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotBlank(message = "新密码不能为空")
        @Size(min = 6, max = 50, message = "新密码长度必须在6-50位之间")
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z]).+$", message = "新密码必须包含字母和数字")
        String newPassword
) {
}
