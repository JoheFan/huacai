package com.huacai.finance.service;

import com.huacai.common.model.PageResponse;
import com.huacai.finance.dto.IncomeSaveRequest;
import com.huacai.finance.vo.IncomeVO;

public interface FinIncomeService {

    PageResponse<IncomeVO> pageIncomes(String keyword, String incomeType, int pageNum, int pageSize);

    IncomeVO getIncome(Long id);

    void createIncome(IncomeSaveRequest request);

    void updateIncome(Long id, IncomeSaveRequest request);

    void deleteIncome(Long id);
}
