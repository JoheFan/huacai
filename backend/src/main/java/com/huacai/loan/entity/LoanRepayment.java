package com.huacai.loan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.huacai.common.entity.AuditableEntity;
import java.math.BigDecimal;
import java.time.LocalDate;

@TableName("loan_repayment")
public class LoanRepayment extends AuditableEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long loanOrderId;
    private Long customerId;
    private String capitalSourceType;
    private LocalDate repaymentDate;
    private BigDecimal repaymentAmount;
    private BigDecimal principalAmount;
    private BigDecimal interestAmount;
    private String repaymentChannel;
    private String remark;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLoanOrderId() {
        return loanOrderId;
    }

    public void setLoanOrderId(Long loanOrderId) {
        this.loanOrderId = loanOrderId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCapitalSourceType() {
        return capitalSourceType;
    }

    public void setCapitalSourceType(String capitalSourceType) {
        this.capitalSourceType = capitalSourceType;
    }

    public LocalDate getRepaymentDate() {
        return repaymentDate;
    }

    public void setRepaymentDate(LocalDate repaymentDate) {
        this.repaymentDate = repaymentDate;
    }

    public BigDecimal getRepaymentAmount() {
        return repaymentAmount;
    }

    public void setRepaymentAmount(BigDecimal repaymentAmount) {
        this.repaymentAmount = repaymentAmount;
    }

    public BigDecimal getPrincipalAmount() {
        return principalAmount;
    }

    public void setPrincipalAmount(BigDecimal principalAmount) {
        this.principalAmount = principalAmount;
    }

    public BigDecimal getInterestAmount() {
        return interestAmount;
    }

    public void setInterestAmount(BigDecimal interestAmount) {
        this.interestAmount = interestAmount;
    }

    public String getRepaymentChannel() {
        return repaymentChannel;
    }

    public void setRepaymentChannel(String repaymentChannel) {
        this.repaymentChannel = repaymentChannel;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
