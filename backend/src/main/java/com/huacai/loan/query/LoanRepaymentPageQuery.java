package com.huacai.loan.query;

import com.huacai.common.model.PageQuery;

public class LoanRepaymentPageQuery extends PageQuery {

    private Long loanOrderId;
    private Long customerId;
    private String capitalSourceType;

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
}
