package com.huacai.customer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.huacai.common.entity.AuditableEntity;
import java.math.BigDecimal;
import java.time.LocalDate;

@TableName("cust_customer_score")
public class CustCustomerScore extends AuditableEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long customerId;
    private LocalDate testDate;
    private BigDecimal testLimit;
    private BigDecimal trafficValue;
    private BigDecimal compositeScore;
    private BigDecimal thirdPartyScore;
    private String scoreResult;
    private String remark;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public LocalDate getTestDate() {
        return testDate;
    }

    public void setTestDate(LocalDate testDate) {
        this.testDate = testDate;
    }

    public BigDecimal getTestLimit() {
        return testLimit;
    }

    public void setTestLimit(BigDecimal testLimit) {
        this.testLimit = testLimit;
    }

    public BigDecimal getTrafficValue() {
        return trafficValue;
    }

    public void setTrafficValue(BigDecimal trafficValue) {
        this.trafficValue = trafficValue;
    }

    public BigDecimal getCompositeScore() {
        return compositeScore;
    }

    public void setCompositeScore(BigDecimal compositeScore) {
        this.compositeScore = compositeScore;
    }

    public BigDecimal getThirdPartyScore() {
        return thirdPartyScore;
    }

    public void setThirdPartyScore(BigDecimal thirdPartyScore) {
        this.thirdPartyScore = thirdPartyScore;
    }

    public String getScoreResult() {
        return scoreResult;
    }

    public void setScoreResult(String scoreResult) {
        this.scoreResult = scoreResult;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
