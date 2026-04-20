package com.huacai.hr.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record EmployeeVO(
    Long id,
    String employeeCode,
    String realName,
    String gender,
    String idCardNo,
    LocalDate birthday,
    Integer age,
    String nation,
    String politicalStatus,
    String hometown,
    String maritalStatus,
    String phone,
    String email,
    String graduateSchool,
    String highestEducation,
    LocalDate workStartDate,
    String homeAddress,
    String emergencyContact,
    String emergencyContactPhone,
    String bankCardNo,
    String idPhotoUrl,
    String employmentStatus,
    String talentFlag,
    Integer createSystemAccount,
    String systemUsername,
    Long orgId,
    String orgName,
    String jobTitle,
    String remark,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
