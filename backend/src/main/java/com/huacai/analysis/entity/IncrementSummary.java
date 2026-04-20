package com.huacai.analysis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.huacai.common.entity.AuditableEntity;
import java.math.BigDecimal;
import java.time.LocalDate;

@TableName("ana_increment_summary")
public class IncrementSummary extends AuditableEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String companyName;
    private String businessAddress;
    private String industry;
    private LocalDate startDate;
    private BigDecimal janAmount;
    private BigDecimal febAmount;
    private BigDecimal marAmount;
    private BigDecimal aprAmount;
    private BigDecimal mayAmount;
    private BigDecimal junAmount;
    private BigDecimal julAmount;
    private BigDecimal augAmount;
    private BigDecimal sepAmount;
    private BigDecimal octAmount;
    private BigDecimal novAmount;
    private BigDecimal decAmount;
    private BigDecimal totalAmount;
    private String remark;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getBusinessAddress() { return businessAddress; }
    public void setBusinessAddress(String businessAddress) { this.businessAddress = businessAddress; }
    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public BigDecimal getJanAmount() { return janAmount; }
    public void setJanAmount(BigDecimal janAmount) { this.janAmount = janAmount; }
    public BigDecimal getFebAmount() { return febAmount; }
    public void setFebAmount(BigDecimal febAmount) { this.febAmount = febAmount; }
    public BigDecimal getMarAmount() { return marAmount; }
    public void setMarAmount(BigDecimal marAmount) { this.marAmount = marAmount; }
    public BigDecimal getAprAmount() { return aprAmount; }
    public void setAprAmount(BigDecimal aprAmount) { this.aprAmount = aprAmount; }
    public BigDecimal getMayAmount() { return mayAmount; }
    public void setMayAmount(BigDecimal mayAmount) { this.mayAmount = mayAmount; }
    public BigDecimal getJunAmount() { return junAmount; }
    public void setJunAmount(BigDecimal junAmount) { this.junAmount = junAmount; }
    public BigDecimal getJulAmount() { return julAmount; }
    public void setJulAmount(BigDecimal julAmount) { this.julAmount = julAmount; }
    public BigDecimal getAugAmount() { return augAmount; }
    public void setAugAmount(BigDecimal augAmount) { this.augAmount = augAmount; }
    public BigDecimal getSepAmount() { return sepAmount; }
    public void setSepAmount(BigDecimal sepAmount) { this.sepAmount = sepAmount; }
    public BigDecimal getOctAmount() { return octAmount; }
    public void setOctAmount(BigDecimal octAmount) { this.octAmount = octAmount; }
    public BigDecimal getNovAmount() { return novAmount; }
    public void setNovAmount(BigDecimal novAmount) { this.novAmount = novAmount; }
    public BigDecimal getDecAmount() { return decAmount; }
    public void setDecAmount(BigDecimal decAmount) { this.decAmount = decAmount; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
