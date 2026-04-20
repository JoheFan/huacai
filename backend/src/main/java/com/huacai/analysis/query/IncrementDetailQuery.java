package com.huacai.analysis.query;

import com.huacai.common.model.PageQuery;
import java.time.LocalDate;

public class IncrementDetailQuery extends PageQuery {
    private String keyword;
    private Long customerId;
    private LocalDate incrementDateFrom;
    private LocalDate incrementDateTo;

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public LocalDate getIncrementDateFrom() { return incrementDateFrom; }
    public void setIncrementDateFrom(LocalDate incrementDateFrom) { this.incrementDateFrom = incrementDateFrom; }
    public LocalDate getIncrementDateTo() { return incrementDateTo; }
    public void setIncrementDateTo(LocalDate incrementDateTo) { this.incrementDateTo = incrementDateTo; }
}
