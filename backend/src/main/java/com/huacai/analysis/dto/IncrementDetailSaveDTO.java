package com.huacai.analysis.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class IncrementDetailSaveDTO {
    private Long id;
    private Long customerId;
    @NotBlank(message = "客户名称不能为空")
    private String customerName;
    @NotNull(message = "增量日期不能为空")
    private LocalDate incrementDate;
    private BigDecimal totalAmount;
    private String businessAddress;
    private String industry;
    private Integer dailyCount;
    private String remark;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public LocalDate getIncrementDate() { return incrementDate; }
    public void setIncrementDate(LocalDate incrementDate) { this.incrementDate = incrementDate; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getBusinessAddress() { return businessAddress; }
    public void setBusinessAddress(String businessAddress) { this.businessAddress = businessAddress; }
    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }
    public Integer getDailyCount() { return dailyCount; }
    public void setDailyCount(Integer dailyCount) { this.dailyCount = dailyCount; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
