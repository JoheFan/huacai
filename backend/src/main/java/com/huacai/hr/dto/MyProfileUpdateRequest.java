package com.huacai.hr.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record MyProfileUpdateRequest(
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
    String homeAddress,
    String emergencyContact,
    String emergencyContactPhone
) {
}
