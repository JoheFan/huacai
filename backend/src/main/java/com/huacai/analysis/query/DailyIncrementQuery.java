package com.huacai.analysis.query;

import com.huacai.common.model.PageQuery;

public class DailyIncrementQuery extends PageQuery {
    private Long detailId;

    public Long getDetailId() { return detailId; }
    public void setDetailId(Long detailId) { this.detailId = detailId; }
}
