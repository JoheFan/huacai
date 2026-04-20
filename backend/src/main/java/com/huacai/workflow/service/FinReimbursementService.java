package com.huacai.workflow.service;

import com.huacai.common.model.PageResponse;
import com.huacai.workflow.dto.ReimbursementCreateRequest;
import com.huacai.workflow.dto.ReimbursementSubmitRequest;
import com.huacai.workflow.dto.ReimbursementUpdateRequest;
import com.huacai.workflow.query.ReimbursementPageQuery;
import com.huacai.workflow.vo.ReimbursementVO;

public interface FinReimbursementService {

    PageResponse<ReimbursementVO> pageReimbursements(ReimbursementPageQuery query);

    ReimbursementVO getReimbursement(Long id);

    void createReimbursement(ReimbursementCreateRequest request);

    void updateReimbursement(Long id, ReimbursementUpdateRequest request);

    void submitReimbursement(Long id, ReimbursementSubmitRequest request);

    void withdrawReimbursement(Long id);

    void resubmitReimbursement(Long id, ReimbursementSubmitRequest request);
}
