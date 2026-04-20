package com.huacai.hr.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.huacai.common.entity.AuditableEntity;
import java.math.BigDecimal;
import java.time.LocalDate;

@TableName("hr_employee_salary")
public class HrEmployeeSalary extends AuditableEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long employeeId;
    private Long salaryStandardId;
    private String salaryName;
    private BigDecimal amount;
    private LocalDate effectiveDate;
    private LocalDate expireDate;
    private String changeReason;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    public Long getSalaryStandardId() { return salaryStandardId; }
    public void setSalaryStandardId(Long salaryStandardId) { this.salaryStandardId = salaryStandardId; }
    public String getSalaryName() { return salaryName; }
    public void setSalaryName(String salaryName) { this.salaryName = salaryName; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public LocalDate getEffectiveDate() { return effectiveDate; }
    public void setEffectiveDate(LocalDate effectiveDate) { this.effectiveDate = effectiveDate; }
    public LocalDate getExpireDate() { return expireDate; }
    public void setExpireDate(LocalDate expireDate) { this.expireDate = expireDate; }
    public String getChangeReason() { return changeReason; }
    public void setChangeReason(String changeReason) { this.changeReason = changeReason; }
}
