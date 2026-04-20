package com.huacai.system.dto;

public record UserUpdateRequest(
    String username,
    String employeeCode,
    String realName,
    String phone,
    String email,
    Long orgId,
    String jobTitle,
    String employmentStatus,
    String accountStatus,
    String remark,
    Long primaryRoleId
) {
}
