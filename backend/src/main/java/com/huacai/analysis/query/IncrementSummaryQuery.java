package com.huacai.analysis.query;

import com.huacai.common.model.PageQuery;
import java.math.BigDecimal;
import java.time.LocalDate;

public class IncrementSummaryQuery extends PageQuery {
    private String keyword;
    private LocalDate startDateFrom;
    private LocalDate startDateTo;

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public LocalDate getStartDateFrom() { return startDateFrom; }
    public void setStartDateFrom(LocalDate startDateFrom) { this.startDateFrom = startDateFrom; }
    public LocalDate getStartDateTo() { return startDateTo; }
    public void setStartDateTo(LocalDate startDateTo) { this.startDateTo = startDateTo; }
}
