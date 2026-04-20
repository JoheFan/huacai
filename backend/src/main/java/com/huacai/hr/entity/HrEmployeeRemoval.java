package com.huacai.hr.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.huacai.common.entity.AuditableEntity;
import java.time.LocalDate;

@TableName("hr_employee_removal")
public class HrEmployeeRemoval extends AuditableEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long employeeId;
    private String removalType;
    private LocalDate removalDate;
    private LocalDate expectedRetireDate;
    private String reason;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    public String getRemovalType() { return removalType; }
    public void setRemovalType(String removalType) { this.removalType = removalType; }
    public LocalDate getRemovalDate() { return removalDate; }
    public void setRemovalDate(LocalDate removalDate) { this.removalDate = removalDate; }
    public LocalDate getExpectedRetireDate() { return expectedRetireDate; }
    public void setExpectedRetireDate(LocalDate expectedRetireDate) { this.expectedRetireDate = expectedRetireDate; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
