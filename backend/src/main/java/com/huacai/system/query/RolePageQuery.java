package com.huacai.system.query;

import com.huacai.common.model.PageQuery;

public class RolePageQuery extends PageQuery {
    private String keyword;
    private String status;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
