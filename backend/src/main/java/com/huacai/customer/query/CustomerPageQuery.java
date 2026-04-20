package com.huacai.customer.query;

import com.huacai.common.model.PageQuery;

public class CustomerPageQuery extends PageQuery {

    private String auditStatus;
    private String loanStatus;

    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    public String getLoanStatus() {
        return loanStatus;
    }

    public void setLoanStatus(String loanStatus) {
        this.loanStatus = loanStatus;
    }
}
