package com.huacai.workflow.query;

public class ReimbursementPageQuery {
    private Integer pageNum = 1;
    private Integer pageSize = 20;
    private String keyword;
    private String processStatus;
    private String scope;

    public Integer getPageNum() { return pageNum; }
    public void setPageNum(Integer pageNum) { this.pageNum = pageNum; }
    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public String getProcessStatus() { return processStatus; }
    public void setProcessStatus(String processStatus) { this.processStatus = processStatus; }
    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }
}
