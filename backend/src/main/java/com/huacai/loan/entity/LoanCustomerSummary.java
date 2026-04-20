package com.huacai.loan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.huacai.common.entity.AuditableEntity;
import java.math.BigDecimal;

@TableName("loan_customer_summary")
public class LoanCustomerSummary extends AuditableEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long customerId;
    private String capitalSourceType;
    private BigDecimal totalIncrementAmount;
    private Integer incrementCount;
    private String yearsTerm;
    private BigDecimal channelRate;
    private BigDecimal channelFee;
    private String referrer;
    private BigDecimal selfTotalLoanAmount;
    private BigDecimal bankTotalLoanAmount;
    private String remark;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public String getCapitalSourceType() { return capitalSourceType; }
    public void setCapitalSourceType(String capitalSourceType) { this.capitalSourceType = capitalSourceType; }
    public BigDecimal getTotalIncrementAmount() { return totalIncrementAmount; }
    public void setTotalIncrementAmount(BigDecimal totalIncrementAmount) { this.totalIncrementAmount = totalIncrementAmount; }
    public Integer getIncrementCount() { return incrementCount; }
    public void setIncrementCount(Integer incrementCount) { this.incrementCount = incrementCount; }
    public String getYearsTerm() { return yearsTerm; }
    public void setYearsTerm(String yearsTerm) { this.yearsTerm = yearsTerm; }
    public BigDecimal getChannelRate() { return channelRate; }
    public void setChannelRate(BigDecimal channelRate) { this.channelRate = channelRate; }
    public BigDecimal getChannelFee() { return channelFee; }
    public void setChannelFee(BigDecimal channelFee) { this.channelFee = channelFee; }
    public String getReferrer() { return referrer; }
    public void setReferrer(String referrer) { this.referrer = referrer; }
    public BigDecimal getSelfTotalLoanAmount() { return selfTotalLoanAmount; }
    public void setSelfTotalLoanAmount(BigDecimal selfTotalLoanAmount) { this.selfTotalLoanAmount = selfTotalLoanAmount; }
    public BigDecimal getBankTotalLoanAmount() { return bankTotalLoanAmount; }
    public void setBankTotalLoanAmount(BigDecimal bankTotalLoanAmount) { this.bankTotalLoanAmount = bankTotalLoanAmount; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
