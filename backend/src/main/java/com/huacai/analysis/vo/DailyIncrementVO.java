package com.huacai.analysis.vo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DailyIncrementVO {
    private Long id;
    private Long detailId;
    private String customerName;
    private LocalDate incrementDate;
    private BigDecimal incrementAmount;
    private BigDecimal channelRate;
    private BigDecimal channelFee;
    private Integer seqNo;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getDetailId() { return detailId; }
    public void setDetailId(Long detailId) { this.detailId = detailId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public LocalDate getIncrementDate() { return incrementDate; }
    public void setIncrementDate(LocalDate incrementDate) { this.incrementDate = incrementDate; }
    public BigDecimal getIncrementAmount() { return incrementAmount; }
    public void setIncrementAmount(BigDecimal incrementAmount) { this.incrementAmount = incrementAmount; }
    public BigDecimal getChannelRate() { return channelRate; }
    public void setChannelRate(BigDecimal channelRate) { this.channelRate = channelRate; }
    public BigDecimal getChannelFee() { return channelFee; }
    public void setChannelFee(BigDecimal channelFee) { this.channelFee = channelFee; }
    public Integer getSeqNo() { return seqNo; }
    public void setSeqNo(Integer seqNo) { this.seqNo = seqNo; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
