package com.huacai.hr.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.huacai.common.entity.AuditableEntity;
import java.math.BigDecimal;
import java.time.LocalDate;

@TableName("hr_employee_assessment")
public class HrEmployeeAssessment extends AuditableEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long employeeId;
    private LocalDate assessmentMonth;
    private BigDecimal assessmentScore;
    private String assessmentGrade;
    private String remark;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    public LocalDate getAssessmentMonth() { return assessmentMonth; }
    public void setAssessmentMonth(LocalDate assessmentMonth) { this.assessmentMonth = assessmentMonth; }
    public BigDecimal getAssessmentScore() { return assessmentScore; }
    public void setAssessmentScore(BigDecimal assessmentScore) { this.assessmentScore = assessmentScore; }
    public String getAssessmentGrade() { return assessmentGrade; }
    public void setAssessmentGrade(String assessmentGrade) { this.assessmentGrade = assessmentGrade; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
