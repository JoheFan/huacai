package com.huacai.hr.dto;

import java.time.LocalDate;

public record EmployeeUpdateRequest(
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
    String systemPasswordPlain,
    Long orgId,
    String jobTitle,
    String remark
) {
}
