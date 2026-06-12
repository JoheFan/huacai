package com.huacai.customer.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huacai.customer.dto.CustomerArchiveSaveRequest;
import com.huacai.customer.dto.CustomerContractSaveRequest;
import com.huacai.customer.dto.CustomerDebtSaveRequest;
import com.huacai.customer.dto.CustomerScoreSaveRequest;
import com.huacai.customer.entity.CustCustomer;
import com.huacai.customer.entity.CustCustomerDebt;
import com.huacai.customer.entity.CustCustomerScore;
import com.huacai.customer.mapper.CustomerContractMapper;
import com.huacai.customer.mapper.CustomerDebtMapper;
import com.huacai.customer.mapper.CustomerMapper;
import com.huacai.customer.mapper.CustomerScoreMapper;
import com.huacai.customer.mapper.CustomerStatusLogMapper;
import com.huacai.customer.mapper.CustomerTradeMapper;
import com.huacai.customer.query.CustomerDebtPageQuery;
import com.huacai.customer.query.CustomerPageQuery;
import com.huacai.customer.query.CustomerRiskPageQuery;
import com.huacai.file.entity.SysFile;
import com.huacai.file.mapper.FileMapper;
import com.huacai.security.CurrentUserProvider;
import com.huacai.system.mapper.UserMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private CustomerScoreMapper customerScoreMapper;

    @Mock
    private CustomerDebtMapper customerDebtMapper;

    @Mock
    private CustomerContractMapper customerContractMapper;

    @Mock
    private CustomerStatusLogMapper customerStatusLogMapper;

    @Mock
    private CustomerTradeMapper customerTradeMapper;

    @Mock
    private FileMapper fileMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @Test
    void createArchivePersistsCustomerAndChildRecords() {
        when(currentUserProvider.getCurrentUserId()).thenReturn(99L);
        doAnswer(invocation -> {
            CustCustomer customer = invocation.getArgument(0);
            customer.setId(8L);
            return 1;
        }).when(customerMapper).insert(any(CustCustomer.class));
        when(fileMapper.selectBatchIds(List.of(31L, 32L))).thenReturn(List.of(
                buildFile(31L, "身份证正面.png"),
                buildFile(32L, "营业执照.pdf")
        ));
        when(fileMapper.selectBatchIds(List.of(41L, 42L))).thenReturn(List.of(
                buildFile(41L, "合同首页.pdf"),
                buildFile(42L, "补充协议.pdf")
        ));

        CustomerServiceImpl service = new CustomerServiceImpl(
                customerMapper,
                customerScoreMapper,
                customerDebtMapper,
                customerContractMapper,
                customerStatusLogMapper,
                customerTradeMapper,
                fileMapper,
                userMapper,
                currentUserProvider
        );

        service.createArchive(new CustomerArchiveSaveRequest(
                "KH-001",
                "张三",
                "MALE",
                "440100199001011234",
                LocalDate.of(1990, 1, 1),
                "13800000000",
                "华彩科技",
                "91440101123456789X",
                LocalDate.of(2020, 1, 1),
                "零售",
                "广州",
                "建设银行",
                "6222000000001",
                "李四",
                new BigDecimal("8.50"),
                new BigDecimal("1280.00"),
                "INIT",
                true,
                List.of(31L, 32L),
                List.of(new CustomerScoreSaveRequest(
                        null,
                        8L,
                        LocalDate.of(2026, 4, 15),
                        new BigDecimal("90"),
                        new BigDecimal("1950000"),
                        new BigDecimal("118"),
                        new BigDecimal("113"),
                        "评分通过"
                )),
                List.of(new CustomerDebtSaveRequest(
                        null,
                        8L,
                        "信用卡",
                        new BigDecimal("100000"),
                        new BigDecimal("100000"),
                        new BigDecimal("22000"),
                        new BigDecimal("78000"),
                        new BigDecimal("3000"),
                        15,
                        "正常"
                )),
                List.of(new CustomerContractSaveRequest(
                        null,
                        8L,
                        "张三",
                        "华彩科技",
                        "91440101123456789X",
                        "HT-001",
                        "授信合同",
                        List.of(41L, 42L),
                        null,
                        "已签署"
                ))
        ));

        verify(customerMapper).insert(any(CustCustomer.class));
        verify(customerScoreMapper).insert(any(CustCustomerScore.class));
        verify(customerDebtMapper).insert(any(CustCustomerDebt.class));
        verify(customerContractMapper).insert(any(com.huacai.customer.entity.CustCustomerContract.class));
        verify(fileMapper).selectBatchIds(List.of(31L, 32L));
    }

    @Test
    void pageReturnsLatestRiskSummaryOnCustomerRows() {
        CustCustomer customer = new CustCustomer();
        customer.setId(8L);
        customer.setCustomerNo("KH-001");
        customer.setCustomerName("张三");
        customer.setMobile("13800000000");
        customer.setCompanyName("华彩科技");
        customer.setCreditCode("91440101123456789X");
        customer.setIndustry("零售");
        customer.setBusinessAddress("广州");

        Page<CustCustomer> page = new Page<>(1, 10);
        page.setRecords(List.of(customer));
        page.setTotal(1);

        CustCustomerScore latestRisk = new CustCustomerScore();
        latestRisk.setId(11L);
        latestRisk.setCustomerId(8L);
        latestRisk.setTestDate(LocalDate.of(2026, 4, 15));
        latestRisk.setTestLimit(new BigDecimal("90"));
        latestRisk.setTrafficValue(new BigDecimal("1950000"));
        latestRisk.setCompositeScore(new BigDecimal("118"));
        latestRisk.setThirdPartyScore(new BigDecimal("113"));
        latestRisk.setUpdatedAt(LocalDateTime.of(2026, 4, 15, 12, 0));

        when(customerMapper.selectPage(any(), any())).thenReturn(page);
        when(customerScoreMapper.selectList(any())).thenReturn(List.of(latestRisk));

        CustomerServiceImpl service = new CustomerServiceImpl(
                customerMapper,
                customerScoreMapper,
                customerDebtMapper,
                customerContractMapper,
                customerStatusLogMapper,
                customerTradeMapper,
                fileMapper,
                userMapper,
                currentUserProvider
        );

        CustomerPageQuery query = new CustomerPageQuery();
        query.setPageNum(1);
        query.setPageSize(10);

        var row = service.page(query).records().get(0);

        assertThat(row.testDate()).isEqualTo(LocalDate.of(2026, 4, 15));
        assertThat(row.testLimit()).isEqualByComparingTo("90");
        assertThat(row.trafficValue()).isEqualByComparingTo("1950000");
        assertThat(row.compositeScore()).isEqualByComparingTo("118");
        assertThat(row.thirdPartyScore()).isEqualByComparingTo("113");
    }

    @Test
    void riskAndDebtPagesReturnRecordRowsWithCustomerFields() {
        CustCustomer customer = new CustCustomer();
        customer.setId(8L);
        customer.setCustomerNo("KH-001");
        customer.setCustomerName("张三");
        customer.setMobile("13800000000");
        customer.setCompanyName("华彩科技");
        customer.setCreditCode("91440101123456789X");
        customer.setIndustry("零售");
        customer.setBusinessAddress("广州");

        CustCustomerScore risk = new CustCustomerScore();
        risk.setId(11L);
        risk.setCustomerId(8L);
        risk.setTestDate(LocalDate.of(2026, 4, 15));
        risk.setCompositeScore(new BigDecimal("118"));

        CustCustomerDebt debt = new CustCustomerDebt();
        debt.setId(21L);
        debt.setCustomerId(8L);
        debt.setDebtType("信用卡");
        debt.setDebtAmount(new BigDecimal("100000"));
        debt.setRepaidAmount(new BigDecimal("22000"));
        debt.setPendingAmount(new BigDecimal("78000"));

        Page<CustCustomerScore> riskPage = new Page<>(1, 10);
        riskPage.setRecords(List.of(risk));
        riskPage.setTotal(1);

        Page<CustCustomerDebt> debtPage = new Page<>(1, 10);
        debtPage.setRecords(List.of(debt));
        debtPage.setTotal(1);

        when(customerScoreMapper.selectPage(any(), any())).thenReturn(riskPage);
        when(customerDebtMapper.selectPage(any(), any())).thenReturn(debtPage);
        when(customerMapper.selectBatchIds(any())).thenReturn(List.of(customer));

        CustomerServiceImpl service = new CustomerServiceImpl(
                customerMapper,
                customerScoreMapper,
                customerDebtMapper,
                customerContractMapper,
                customerStatusLogMapper,
                customerTradeMapper,
                fileMapper,
                userMapper,
                currentUserProvider
        );

        var riskRow = service.pageRisks(new CustomerRiskPageQuery()).records().get(0);
        var debtRow = service.pageDebts(new CustomerDebtPageQuery()).records().get(0);

        assertThat(riskRow.customerName()).isEqualTo("张三");
        assertThat(debtRow.customerName()).isEqualTo("张三");
        assertThat(debtRow.totalRepaymentAmount()).isEqualByComparingTo("100000");
        assertThat(debtRow.advancePaidAmount()).isEqualByComparingTo("22000");
        assertThat(debtRow.pendingAmount()).isEqualByComparingTo("78000");
    }

    private SysFile buildFile(Long id, String fileName) {
        SysFile file = new SysFile();
        file.setId(id);
        file.setFileName(fileName);
        file.setFilePath("/tmp/" + fileName);
        file.setUploaderId(99L);
        return file;
    }
}
