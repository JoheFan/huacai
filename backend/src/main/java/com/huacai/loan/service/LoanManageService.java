package com.huacai.loan.service;

import com.huacai.common.model.PageResponse;
import com.huacai.loan.dto.LoanCustomerSummarySaveRequest;
import com.huacai.loan.dto.LoanOrderSaveRequest;
import com.huacai.loan.dto.LoanRepaymentSaveRequest;
import com.huacai.loan.query.LoanOrderPageQuery;
import com.huacai.loan.query.LoanRepaymentPageQuery;
import com.huacai.loan.vo.LoanCustomerSummaryVO;
import com.huacai.loan.vo.LoanOrderVO;
import com.huacai.loan.vo.LoanOrderOverviewVO;
import com.huacai.loan.vo.LoanRepaymentVO;
import com.huacai.loan.vo.LoanRepaymentSummaryVO;

public interface LoanManageService {

    PageResponse<LoanOrderVO> pageOrders(LoanOrderPageQuery query);

    PageResponse<LoanOrderOverviewVO> pageOrderOverview(LoanOrderPageQuery query);

    LoanOrderVO orderDetail(Long id);

    void createOrder(LoanOrderSaveRequest request);

    void updateOrder(Long id, LoanOrderSaveRequest request);

    void deleteOrder(Long id);

    PageResponse<LoanRepaymentVO> pageRepayments(LoanRepaymentPageQuery query);

    LoanRepaymentSummaryVO getRepaymentSummary(LoanRepaymentPageQuery query);

    LoanRepaymentVO repaymentDetail(Long pathLoanOrderId, Long id);

    void createRepayment(Long pathLoanOrderId, LoanRepaymentSaveRequest request);

    void updateRepayment(Long pathLoanOrderId, Long id, LoanRepaymentSaveRequest request);

    void deleteRepayment(Long pathLoanOrderId, Long id);

    // ===================== 客户汇总台账 =====================

    LoanCustomerSummaryVO getCustomerSummary(Long customerId, String capitalSourceType);

    void saveCustomerSummary(LoanCustomerSummarySaveRequest request);

    void deleteCustomerSummary(Long customerId, String capitalSourceType);
}
