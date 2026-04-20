package com.huacai.finance.query;

import com.huacai.common.model.PageQuery;

public class FinExpensePageQuery extends PageQuery {
    private String expenseType;

    public String getExpenseType() { return expenseType; }
    public void setExpenseType(String expenseType) { this.expenseType = expenseType; }
}
