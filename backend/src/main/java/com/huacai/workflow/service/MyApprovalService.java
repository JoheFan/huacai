package com.huacai.workflow.service;

import com.huacai.common.model.PageResponse;
import com.huacai.workflow.query.MyApprovalPageQuery;
import com.huacai.workflow.vo.ActionLogVO;
import com.huacai.workflow.vo.MyApprovalTaskVO;

import java.util.List;

public interface MyApprovalService {

    PageResponse<MyApprovalTaskVO> pageTodos(MyApprovalPageQuery query);

    PageResponse<MyApprovalTaskVO> pageInitiated(MyApprovalPageQuery query);

    PageResponse<MyApprovalTaskVO> pageProcessed(MyApprovalPageQuery query);

    void approveTask(Long taskId, String opinion);

    void rejectTask(Long taskId, String opinion);

    List<ActionLogVO> getProcessTimeline(String bizType, Long bizId);
}
