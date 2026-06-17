package com.huacai.loan.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huacai.common.exception.BusinessException;
import com.huacai.customer.entity.CustCustomer;
import com.huacai.customer.mapper.CustomerMapper;
import com.huacai.loan.dto.LoanOrderSaveRequest;
import com.huacai.loan.entity.LoanOrder;
import com.huacai.loan.entity.LoanRepayment;
import com.huacai.loan.mapper.LoanCustomerSummaryMapper;
import com.huacai.loan.mapper.LoanOrderMapper;
import com.huacai.loan.mapper.LoanRepaymentMapper;
import com.huacai.loan.query.LoanOrderPageQuery;
import com.huacai.loan.query.LoanRepaymentPageQuery;
import com.huacai.loan.vo.LoanOrderOverviewVO;
import com.huacai.loan.vo.LoanRepaymentSummaryVO;
import com.huacai.security.AuthUser;
import com.huacai.security.CurrentUserProvider;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoanManageServiceImplTest {

    @Mock
    private LoanOrderMapper loanOrderMapper;

    @Mock
    private LoanRepaymentMapper loanRepaymentMapper;

    @Mock
    private LoanCustomerSummaryMapper loanCustomerSummaryMapper;

    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @Test
    void pageOrderOverviewAggregatesRowsByCustomer() {
        LoanOrder orderA = new LoanOrder();
        orderA.setId(1L);
        orderA.setCustomerId(6L);
        orderA.setCapitalSourceType("SELF");
        orderA.setLoanAmount(new BigDecimal("100000"));
        orderA.setBalanceAmount(new BigDecimal("70000"));
        orderA.setMonthlyInterestAmount(new BigDecimal("3000"));
        orderA.setDepositGoldAmount(new BigDecimal("5000"));
        orderA.setCreditCardRepaymentAmount(new BigDecimal("20000"));
        orderA.setLoanCount(2);
        orderA.setLoanDate(LocalDate.of(2026, 4, 1));
        orderA.setRemark("首笔");
        orderA.setStatus("ACTIVE");

        LoanOrder orderB = new LoanOrder();
        orderB.setId(2L);
        orderB.setCustomerId(6L);
        orderB.setCapitalSourceType("SELF");
        orderB.setLoanAmount(new BigDecimal("50000"));
        orderB.setBalanceAmount(new BigDecimal("30000"));
        orderB.setMonthlyInterestAmount(new BigDecimal("1500"));
        orderB.setDepositGoldAmount(new BigDecimal("2000"));
        orderB.setCreditCardRepaymentAmount(new BigDecimal("10000"));
        orderB.setLoanCount(1);
        orderB.setLoanDate(LocalDate.of(2026, 4, 10));
        orderB.setRemark("二笔");
        orderB.setStatus("ACTIVE");

        LoanRepayment repayment = new LoanRepayment();
        repayment.setLoanOrderId(1L);
        repayment.setCustomerId(6L);
        repayment.setRepaymentAmount(new BigDecimal("35000"));
        repayment.setPrincipalAmount(new BigDecimal("30000"));
        repayment.setInterestAmount(new BigDecimal("5000"));

        CustCustomer customer = new CustCustomer();
        customer.setId(6L);
        customer.setCustomerNo("KH-001");
        customer.setCustomerName("张三");
        customer.setMobile("13800000000");
        customer.setCompanyName("华彩科技");

        when(loanOrderMapper.selectList(any())).thenReturn(List.of(orderA, orderB));
        when(loanRepaymentMapper.selectList(any())).thenReturn(List.of(repayment));
        when(customerMapper.selectBatchIds(any())).thenReturn(List.of(customer));

        LoanManageServiceImpl service = new LoanManageServiceImpl(
                loanOrderMapper,
                loanRepaymentMapper,
                loanCustomerSummaryMapper,
                customerMapper,
                currentUserProvider
        );

        LoanOrderPageQuery query = new LoanOrderPageQuery();
        query.setPageNum(1);
        query.setPageSize(10);
        query.setCapitalSourceType("SELF");

        LoanOrderOverviewVO row = service.pageOrderOverview(query).records().get(0);

        assertThat(row.customerNo()).isEqualTo("KH-001");
        assertThat(row.mobile()).isEqualTo("13800000000");
        assertThat(row.companyName()).isEqualTo("华彩科技");
        assertThat(row.totalLoanAmount()).isEqualByComparingTo("150000");
        assertThat(row.balanceAmount()).isEqualByComparingTo("100000");
        assertThat(row.repaidAmount()).isEqualByComparingTo("35000");
        assertThat(row.pendingAmount()).isEqualByComparingTo("100000");
        assertThat(row.firstLoanDate()).isEqualTo(LocalDate.of(2026, 4, 1));
    }

    @Test
    void repaymentSummaryAggregatesCurrentScope() {
        LoanRepayment repaymentA = new LoanRepayment();
        repaymentA.setLoanOrderId(1L);
        repaymentA.setCustomerId(6L);
        repaymentA.setCapitalSourceType("SELF");
        repaymentA.setRepaymentAmount(new BigDecimal("10000"));
        repaymentA.setPrincipalAmount(new BigDecimal("7500"));
        repaymentA.setInterestAmount(new BigDecimal("2500"));

        LoanRepayment repaymentB = new LoanRepayment();
        repaymentB.setLoanOrderId(1L);
        repaymentB.setCustomerId(6L);
        repaymentB.setCapitalSourceType("SELF");
        repaymentB.setRepaymentAmount(new BigDecimal("15000"));
        repaymentB.setPrincipalAmount(new BigDecimal("10000"));
        repaymentB.setInterestAmount(new BigDecimal("5000"));

        CustCustomer customer = new CustCustomer();
        customer.setId(6L);
        customer.setCustomerName("张三");

        when(loanRepaymentMapper.selectList(any())).thenReturn(List.of(repaymentA, repaymentB));
        when(customerMapper.selectById(6L)).thenReturn(customer);

        LoanManageServiceImpl service = new LoanManageServiceImpl(
                loanOrderMapper,
                loanRepaymentMapper,
                loanCustomerSummaryMapper,
                customerMapper,
                currentUserProvider
        );

        LoanRepaymentPageQuery query = new LoanRepaymentPageQuery();
        query.setCustomerId(6L);
        query.setCapitalSourceType("SELF");

        LoanRepaymentSummaryVO summary = service.getRepaymentSummary(query);

        assertThat(summary.customerName()).isEqualTo("张三");
        assertThat(summary.recordCount()).isEqualTo(2);
        assertThat(summary.repaymentAmountTotal()).isEqualByComparingTo("25000");
        assertThat(summary.principalAmountTotal()).isEqualByComparingTo("17500");
        assertThat(summary.interestAmountTotal()).isEqualByComparingTo("7500");
    }

    @Test
    void createOrderDeniedWhenUserLacksBankCapitalAccess() {
        AuthUser user = mock(AuthUser.class);
        when(user.isSuperAdmin()).thenReturn(false);
        when(user.getDataScopes()).thenReturn(Map.of("loan-orders-self", "SELF")); // 只有自营权限，无机构权限
        when(currentUserProvider.getCurrentUser()).thenReturn(Optional.of(user));
        CustCustomer customer = new CustCustomer();
        customer.setId(6L);
        when(customerMapper.selectById(6L)).thenReturn(customer);

        LoanManageServiceImpl service = new LoanManageServiceImpl(
                loanOrderMapper, loanRepaymentMapper, loanCustomerSummaryMapper, customerMapper, currentUserProvider);

        LoanOrderSaveRequest request = new LoanOrderSaveRequest(
                6L, "BANK", "某银行", LocalDate.of(2026, 4, 1),
                new BigDecimal("5000"), new BigDecimal("20000"), new BigDecimal("100000"),
                null, new BigDecimal("3000"), 2, "ACTIVE", "测试");

        assertThatThrownBy(() -> service.createOrder(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("无权访问");
        verify(loanOrderMapper, never()).insert(any(LoanOrder.class));
    }

    @Test
    void createOrderAllowedWhenUserHasMatchingCapitalAccess() {
        AuthUser user = mock(AuthUser.class);
        when(user.isSuperAdmin()).thenReturn(false);
        when(user.getDataScopes()).thenReturn(Map.of("loan-orders-self", "SELF"));
        when(currentUserProvider.getCurrentUser()).thenReturn(Optional.of(user));
        when(currentUserProvider.getCurrentUserId()).thenReturn(99L);
        CustCustomer customer = new CustCustomer();
        customer.setId(6L);
        when(customerMapper.selectById(6L)).thenReturn(customer);
        // 自营订单：有 self 权限应放行并落库

        LoanManageServiceImpl service = new LoanManageServiceImpl(
                loanOrderMapper, loanRepaymentMapper, loanCustomerSummaryMapper, customerMapper, currentUserProvider);

        LoanOrderSaveRequest request = new LoanOrderSaveRequest(
                6L, "SELF", null, LocalDate.of(2026, 4, 1),
                new BigDecimal("5000"), new BigDecimal("20000"), new BigDecimal("100000"),
                null, new BigDecimal("3000"), 2, "ACTIVE", "测试");

        service.createOrder(request);

        verify(loanOrderMapper).insert(any(LoanOrder.class));
    }
}
