package com.huacai.hr.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record EmployeeCreateRequest(
    @NotBlank(message = "员工编号不能为空")
    String employeeCode,
    @NotBlank(message = "姓名不能为空")
    String realName,
    String gender,
    String idCardNo,
    LocalDate birthday,
    Integer age,
    String nation,
    String politicalStatus,
    String hometown,
    String maritalStatus,
    @NotBlank(message = "手机号不能为空")
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
    @NotBlank(message = "人员状态不能为空")
    String employmentStatus,
    String talentFlag,
    @NotNull(message = "是否开通系统账号不能为空")
    Integer createSystemAccount,
    String systemUsername,
    String systemPasswordPlain,
    Long orgId,
    String jobTitle,
    String remark
) {
}
