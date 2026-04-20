package com.huacai.loan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.huacai.common.entity.AuditableEntity;
import java.math.BigDecimal;
import java.time.LocalDate;

@TableName("loan_order")
public class LoanOrder extends AuditableEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long customerId;
    private String capitalSourceType;
    private String bankName;
    private LocalDate loanDate;
    private BigDecimal depositGoldAmount;
    private BigDecimal creditCardRepaymentAmount;
    private BigDecimal loanAmount;
    private BigDecimal balanceAmount;
    private BigDecimal monthlyInterestAmount;
    private Integer loanCount;
    private String status;
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

    public String getCapitalSourceType() {
        return capitalSourceType;
    }

    public void setCapitalSourceType(String capitalSourceType) {
        this.capitalSourceType = capitalSourceType;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(LocalDate loanDate) {
        this.loanDate = loanDate;
    }

    public BigDecimal getDepositGoldAmount() {
        return depositGoldAmount;
    }

    public void setDepositGoldAmount(BigDecimal depositGoldAmount) {
        this.depositGoldAmount = depositGoldAmount;
    }

    public BigDecimal getCreditCardRepaymentAmount() {
        return creditCardRepaymentAmount;
    }

    public void setCreditCardRepaymentAmount(BigDecimal creditCardRepaymentAmount) {
        this.creditCardRepaymentAmount = creditCardRepaymentAmount;
    }

    public BigDecimal getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(BigDecimal loanAmount) {
        this.loanAmount = loanAmount;
    }

    public BigDecimal getBalanceAmount() {
        return balanceAmount;
    }

    public void setBalanceAmount(BigDecimal balanceAmount) {
        this.balanceAmount = balanceAmount;
    }

    public BigDecimal getMonthlyInterestAmount() {
        return monthlyInterestAmount;
    }

    public void setMonthlyInterestAmount(BigDecimal monthlyInterestAmount) {
        this.monthlyInterestAmount = monthlyInterestAmount;
    }

    public Integer getLoanCount() {
        return loanCount;
    }

    public void setLoanCount(Integer loanCount) {
        this.loanCount = loanCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
