package com.huacai.customer.query;

import com.huacai.common.model.PageQuery;

public class CustomerDebtPageQuery extends PageQuery {

    private Long customerId;
    private String keyword;

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
