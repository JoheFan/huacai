package com.huacai.opportunity.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huacai.customer.entity.CustCustomer;
import com.huacai.customer.mapper.CustomerMapper;
import com.huacai.opportunity.entity.OppFollowRecord;
import com.huacai.opportunity.entity.OppOpportunity;
import com.huacai.opportunity.mapper.FollowRecordMapper;
import com.huacai.opportunity.mapper.OpportunityMapper;
import com.huacai.opportunity.query.OpportunityPageQuery;
import com.huacai.opportunity.vo.OpportunityVO;
import com.huacai.security.CurrentUserProvider;
import com.huacai.system.mapper.UserMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OpportunityServiceImplTest {

    @Mock
    private OpportunityMapper opportunityMapper;

    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private FollowRecordMapper followRecordMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @Test
    void pageOpportunitiesIncludesLatestFollowSummaryAndCustomerFields() {
        OppOpportunity opportunity = new OppOpportunity();
        opportunity.setId(11L);
        opportunity.setCustomerId(6L);
        opportunity.setStatus("OPEN");

        Page<OppOpportunity> page = new Page<>(1, 10);
        page.setRecords(List.of(opportunity));
        page.setTotal(1);

        CustCustomer customer = new CustCustomer();
        customer.setId(6L);
        customer.setCustomerName("张三");
        customer.setCustomerNo("KH-001");
        customer.setMobile("13800000000");
        customer.setCompanyName("华彩科技");
        customer.setCreditCode("91310000123456789X");

        OppFollowRecord oldRecord = new OppFollowRecord();
        oldRecord.setOpportunityId(11L);
        oldRecord.setFollowTime(LocalDateTime.of(2026, 4, 14, 10, 0));
        oldRecord.setFollowerName("顾问A");
        oldRecord.setFollowContent("旧记录");

        OppFollowRecord latestRecord = new OppFollowRecord();
        latestRecord.setOpportunityId(11L);
        latestRecord.setFollowTime(LocalDateTime.of(2026, 4, 15, 9, 30));
        latestRecord.setFollowerName("顾问B");
        latestRecord.setFollowContent("最新跟进记录");

        when(opportunityMapper.selectPage(any(), any())).thenReturn(page);
        when(customerMapper.selectBatchIds(any())).thenReturn(List.of(customer));
        when(followRecordMapper.selectList(any())).thenReturn(List.of(oldRecord, latestRecord));

        OpportunityServiceImpl service = new OpportunityServiceImpl(
                opportunityMapper,
                customerMapper,
                followRecordMapper,
                userMapper,
                currentUserProvider
        );

        OpportunityVO record = service.pageOpportunities(new OpportunityPageQuery()).records().get(0);

        assertThat(record.mobile()).isEqualTo("13800000000");
        assertThat(record.companyName()).isEqualTo("华彩科技");
        assertThat(record.creditCode()).isEqualTo("91310000123456789X");
        assertThat(record.latestFollowTime()).isEqualTo(LocalDateTime.of(2026, 4, 15, 9, 30));
        assertThat(record.latestFollowerName()).isEqualTo("顾问B");
        assertThat(record.latestFollowContent()).isEqualTo("最新跟进记录");
    }
}
