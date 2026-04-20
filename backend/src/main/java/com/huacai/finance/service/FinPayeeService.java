package com.huacai.finance.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.huacai.common.model.PageResponse;
import com.huacai.finance.dto.PayeeSaveRequest;
import com.huacai.finance.vo.PayeeVO;

public interface FinPayeeService {

    PageResponse<PayeeVO> pagePayees(String keyword, String status, int pageNum, int pageSize);

    PayeeVO getPayee(Long id);

    void createPayee(PayeeSaveRequest request);

    void updatePayee(Long id, PayeeSaveRequest request);

    void deletePayee(Long id);

    void updatePayeeStatus(Long id, String status);
}
