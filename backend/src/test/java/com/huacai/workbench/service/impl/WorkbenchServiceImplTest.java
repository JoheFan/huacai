package com.huacai.workbench.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.huacai.customer.entity.CustCustomer;
import com.huacai.customer.mapper.CustomerMapper;
import com.huacai.loan.entity.LoanOrder;
import com.huacai.loan.entity.LoanRepayment;
import com.huacai.loan.mapper.LoanOrderMapper;
import com.huacai.loan.mapper.LoanRepaymentMapper;
import com.huacai.workbench.query.WorkbenchQuery;
import com.huacai.workbench.vo.WorkbenchOverviewVO;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WorkbenchServiceImplTest {

    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private LoanOrderMapper loanOrderMapper;

    @Mock
    private LoanRepaymentMapper loanRepaymentMapper;

    @Test
    void overviewOrdersFocusRowsByLatestUpdateTime() {
        WorkbenchServiceImpl service = new WorkbenchServiceImpl(customerMapper, loanOrderMapper, loanRepaymentMapper);

        CustCustomer customer = new CustCustomer();
        customer.setId(2L);
        customer.setCustomerName("一期收口联调客户-A-更新");
        customer.setCompanyName("一期收口企业-A-更新");
        customer.setLoanStatus("RUNNING");
        customer.setCreditCode("91510000MA6CLOSEA9");
        customer.setMobile("13900000009");
        customer.setUpdatedAt(LocalDateTime.of(2026, 4, 6, 12, 1));
        customer.setCreatedAt(LocalDateTime.of(2026, 4, 6, 12, 0));

        LoanOrder loanOrder = new LoanOrder();
        loanOrder.setId(2L);
        loanOrder.setCustomerId(2L);
        loanOrder.setCapitalSourceType("SELF");
        loanOrder.setLoanDate(LocalDate.of(2026, 1, 15));
        loanOrder.setStatus("ACTIVE");
        loanOrder.setBalanceAmount(new BigDecimal("4000.00"));
        loanOrder.setUpdatedAt(LocalDateTime.of(2026, 4, 6, 12, 5));
        loanOrder.setCreatedAt(LocalDateTime.of(2026, 4, 6, 12, 4));

        LoanRepayment repayment = new LoanRepayment();
        repayment.setId(3L);
        repayment.setLoanOrderId(2L);
        repayment.setCustomerId(2L);
        repayment.setCapitalSourceType("SELF");
        repayment.setRepaymentDate(LocalDate.of(2026, 4, 6));
        repayment.setUpdatedAt(LocalDateTime.of(2026, 4, 6, 12, 3));
        repayment.setCreatedAt(LocalDateTime.of(2026, 4, 6, 12, 2));

        when(customerMapper.selectCount(any())).thenReturn(2L, 0L);
        when(loanOrderMapper.selectCount(any())).thenReturn(1L);
        when(loanRepaymentMapper.selectCount(any())).thenReturn(1L);
        when(customerMapper.selectList(any())).thenReturn(List.of(customer));
        when(loanOrderMapper.selectList(any())).thenReturn(List.of(loanOrder));
        when(loanRepaymentMapper.selectList(any())).thenReturn(List.of(repayment));
        when(customerMapper.selectById(2L)).thenReturn(customer);

        WorkbenchOverviewVO overview = service.overview(new WorkbenchQuery());

        assertThat(overview.focusRows()).hasSize(3);
        assertThat(overview.focusRows().getFirst().recordType()).isEqualTo("借贷主单");
        assertThat(overview.focusRows().getFirst().id()).isEqualTo(2L);
        assertThat(overview.focusRows().getFirst().actionDate()).isEqualTo("04-06");
    }
}
