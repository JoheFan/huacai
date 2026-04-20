package com.huacai.loan.query;

import com.huacai.common.model.PageQuery;

public class LoanOrderPageQuery extends PageQuery {

    private Long customerId;
    private String capitalSourceType;
    private String status;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
