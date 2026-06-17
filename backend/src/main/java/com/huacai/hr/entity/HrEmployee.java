package com.huacai.hr.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.huacai.common.entity.AuditableEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName("hr_employee")
public class HrEmployee extends AuditableEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String employeeCode;
    private String realName;
    private String gender;
    private String idCardNo;
    private LocalDate birthday;
    private Integer age;
    private String nation;
    private String politicalStatus;
    private String hometown;
    private String maritalStatus;
    private String phone;
    private String email;
    private String graduateSchool;
    private String highestEducation;
    private LocalDate workStartDate;
    private String homeAddress;
    private String emergencyContact;
    private String emergencyContactPhone;
    private String bankCardNo;
    private String idPhotoUrl;
    private String employmentStatus;
    private String talentFlag;
    private Integer createSystemAccount;
    private String systemUsername;
    private Long orgId;
    private String jobTitle;
    private String remark;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }
    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getIdCardNo() { return idCardNo; }
    public void setIdCardNo(String idCardNo) { this.idCardNo = idCardNo; }
    public LocalDate getBirthday() { return birthday; }
    public void setBirthday(LocalDate birthday) { this.birthday = birthday; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getNation() { return nation; }
    public void setNation(String nation) { this.nation = nation; }
    public String getPoliticalStatus() { return politicalStatus; }
    public void setPoliticalStatus(String politicalStatus) { this.politicalStatus = politicalStatus; }
    public String getHometown() { return hometown; }
    public void setHometown(String hometown) { this.hometown = hometown; }
    public String getMaritalStatus() { return maritalStatus; }
    public void setMaritalStatus(String maritalStatus) { this.maritalStatus = maritalStatus; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getGraduateSchool() { return graduateSchool; }
    public void setGraduateSchool(String graduateSchool) { this.graduateSchool = graduateSchool; }
    public String getHighestEducation() { return highestEducation; }
    public void setHighestEducation(String highestEducation) { this.highestEducation = highestEducation; }
    public LocalDate getWorkStartDate() { return workStartDate; }
    public void setWorkStartDate(LocalDate workStartDate) { this.workStartDate = workStartDate; }
    public String getHomeAddress() { return homeAddress; }
    public void setHomeAddress(String homeAddress) { this.homeAddress = homeAddress; }
    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }
    public String getEmergencyContactPhone() { return emergencyContactPhone; }
    public void setEmergencyContactPhone(String emergencyContactPhone) { this.emergencyContactPhone = emergencyContactPhone; }
    public String getBankCardNo() { return bankCardNo; }
    public void setBankCardNo(String bankCardNo) { this.bankCardNo = bankCardNo; }
    public String getIdPhotoUrl() { return idPhotoUrl; }
    public void setIdPhotoUrl(String idPhotoUrl) { this.idPhotoUrl = idPhotoUrl; }
    public String getEmploymentStatus() { return employmentStatus; }
    public void setEmploymentStatus(String employmentStatus) { this.employmentStatus = employmentStatus; }
    public String getTalentFlag() { return talentFlag; }
    public void setTalentFlag(String talentFlag) { this.talentFlag = talentFlag; }
    public Integer getCreateSystemAccount() { return createSystemAccount; }
    public void setCreateSystemAccount(Integer createSystemAccount) { this.createSystemAccount = createSystemAccount; }
    public String getSystemUsername() { return systemUsername; }
    public void setSystemUsername(String systemUsername) { this.systemUsername = systemUsername; }
    public Long getOrgId() { return orgId; }
    public void setOrgId(Long orgId) { this.orgId = orgId; }
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
