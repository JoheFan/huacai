package com.huacai.system.vo;

import java.time.LocalDateTime;
public record UserVO(
    Long id,
    String username,
    String employeeCode,
    String realName,
    String phone,
    String email,
    Long orgId,
    String orgName,
    String identityType,
    Long primaryRoleId,
    String primaryRoleCode,
    String primaryRoleName,
    String permissionSummary,
    String jobTitle,
    String employmentStatus,
    String accountStatus,
    LocalDateTime lastLoginAt,
    String remark,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
