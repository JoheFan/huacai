package com.huacai.assistant.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huacai.assistant.dto.AssistantActionConfirmRequest;
import com.huacai.assistant.dto.AssistantConfirmation;
import com.huacai.assistant.dto.AssistantOpportunityPatch;
import com.huacai.assistant.dto.AssistantOpportunityUpdateDraftRequest;
import com.huacai.assistant.dto.AssistantRequestContext;
import com.huacai.assistant.entity.AgentActionLog;
import com.huacai.assistant.mapper.AgentActionLogMapper;
import com.huacai.assistant.vo.AssistantWriteReceiptVO;
import com.huacai.customer.mapper.CustomerMapper;
import com.huacai.opportunity.mapper.FollowRecordMapper;
import com.huacai.opportunity.mapper.OpportunityMapper;
import com.huacai.opportunity.service.OpportunityService;
import com.huacai.opportunity.vo.OpportunityVO;
import com.huacai.security.CurrentUserProvider;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OpportunityAssistantServiceImplTest {

    @Mock
    private OpportunityService opportunityService;

    @Mock
    private OpportunityMapper opportunityMapper;

    @Mock
    private FollowRecordMapper followRecordMapper;

    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private AgentActionLogMapper agentActionLogMapper;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @Test
    void draftUpdateOpportunityCreatesConfirmationCardAndPendingLog() {
        OpportunityVO before = buildOpportunity(
                11L,
                "张三",
                "KH-001",
                "QUALIFIED",
                "MEDIUM",
                new BigDecimal("500000"),
                "MEDIUM",
                "OPEN",
                LocalDateTime.of(2026, 5, 16, 10, 0),
                "旧备注"
        );
        when(opportunityService.getOpportunityDetail(11L)).thenReturn(before);
        when(currentUserProvider.getCurrentUserId()).thenReturn(9001L);

        OpportunityAssistantServiceImpl service = new OpportunityAssistantServiceImpl(
                opportunityService,
                opportunityMapper,
                followRecordMapper,
                customerMapper,
                agentActionLogMapper,
                currentUserProvider,
                new ObjectMapper().findAndRegisterModules()
        );

        var card = service.draftUpdateOpportunity(new AssistantOpportunityUpdateDraftRequest(
                new AssistantRequestContext("trace-1", "session-1", "msg-1", "ou_1", "李四", "chat-1", null, "zh-CN"),
                "把这个商机推进到商务谈判，下次周二跟进",
                11L,
                new AssistantOpportunityPatch(
                        "NEGOTIATION",
                        null,
                        null,
                        null,
                        null,
                        LocalDateTime.of(2026, 5, 20, 14, 0),
                        null
                )
        ));

        assertThat(card.toolName()).isEqualTo("huacai.opportunity.update_fields");
        assertThat(card.changes()).hasSize(2);
        assertThat(card.summary()).contains("2 个字段");

        ArgumentCaptor<AgentActionLog> captor = ArgumentCaptor.forClass(AgentActionLog.class);
        verify(agentActionLogMapper).insert(captor.capture());
        AgentActionLog savedLog = captor.getValue();
        assertThat(savedLog.getResultCode()).isEqualTo("PENDING_CONFIRMATION");
        assertThat(savedLog.getTargetOpportunityId()).isEqualTo(11L);
        assertThat(savedLog.getConfirmationId()).isEqualTo(card.confirmationId());
        assertThat(savedLog.getToolName()).isEqualTo("huacai.opportunity.update_fields");
    }

    @Test
    void confirmActionSupportsCancellation() {
        AgentActionLog log = new AgentActionLog();
        log.setId(1L);
        log.setConfirmationId("confirm-1");
        log.setTraceId("trace-1");
        log.setToolName("huacai.opportunity.follow.create");
        log.setTargetType("follow_record");
        log.setTargetFollowRecordId(88L);
        log.setResultCode("PENDING_CONFIRMATION");
        when(agentActionLogMapper.selectOne(any())).thenReturn(log);

        OpportunityAssistantServiceImpl service = new OpportunityAssistantServiceImpl(
                opportunityService,
                opportunityMapper,
                followRecordMapper,
                customerMapper,
                agentActionLogMapper,
                currentUserProvider,
                new ObjectMapper().findAndRegisterModules()
        );

        AssistantWriteReceiptVO receipt = service.confirmAction(new AssistantActionConfirmRequest(
                new AssistantRequestContext("trace-1", "session-1", "msg-1", "ou_1", "李四", "chat-1", null, "zh-CN"),
                new AssistantConfirmation("confirm-1", false, "idem-1", null, null)
        ));

        assertThat(receipt.success()).isFalse();
        assertThat(receipt.message()).isEqualTo("已取消写入");
        verify(agentActionLogMapper).updateById(log);
        assertThat(log.getResultCode()).isEqualTo("CANCELLED");
    }

    @Test
    void confirmActionExecutesOpportunityUpdateAndReturnsReceipt() {
        AgentActionLog log = new AgentActionLog();
        log.setId(2L);
        log.setConfirmationId("confirm-2");
        log.setTraceId("trace-2");
        log.setToolName("huacai.opportunity.update_fields");
        log.setTargetType("opportunity");
        log.setTargetOpportunityId(11L);
        log.setResultCode("PENDING_CONFIRMATION");
        log.setToolInputJson("""
                {"opportunityId":11,"payload":{"customerId":null,"priorityLevel":"HIGH","stageCode":"NEGOTIATION","ownerUserId":null,"estimatedAmount":600000,"intentLevel":"HIGH","status":"OPEN","nextFollowTime":"2026-05-20T14:00:00","remark":"客户意向增强"}}
                """);
        when(agentActionLogMapper.selectOne(any())).thenReturn(log);
        when(opportunityService.getOpportunityDetail(11L)).thenReturn(buildOpportunity(
                11L,
                "张三",
                "KH-001",
                "NEGOTIATION",
                "HIGH",
                new BigDecimal("600000"),
                "HIGH",
                "OPEN",
                LocalDateTime.of(2026, 5, 20, 14, 0),
                "客户意向增强"
        ));

        OpportunityAssistantServiceImpl service = new OpportunityAssistantServiceImpl(
                opportunityService,
                opportunityMapper,
                followRecordMapper,
                customerMapper,
                agentActionLogMapper,
                currentUserProvider,
                new ObjectMapper().findAndRegisterModules()
        );

        AssistantWriteReceiptVO receipt = service.confirmAction(new AssistantActionConfirmRequest(
                new AssistantRequestContext("trace-2", "session-2", "msg-2", "ou_2", "王五", "chat-2", null, "zh-CN"),
                new AssistantConfirmation("confirm-2", true, "idem-2", null, null)
        ));

        assertThat(receipt.success()).isTrue();
        assertThat(receipt.targetId()).isEqualTo(11L);
        assertThat(receipt.message()).isEqualTo("商机更新成功");
        verify(opportunityService).updateOpportunity(anyLong(), any());
        assertThat(log.getResultCode()).isEqualTo("SUCCESS");
        assertThat(log.getAfterSnapshotJson()).contains("NEGOTIATION");
    }

    private OpportunityVO buildOpportunity(
            Long id,
            String customerName,
            String customerNo,
            String stageCode,
            String priorityLevel,
            BigDecimal estimatedAmount,
            String intentLevel,
            String status,
            LocalDateTime nextFollowTime,
            String remark
    ) {
        return new OpportunityVO(
                id,
                6L,
                customerName,
                customerNo,
                "13800000000",
                "华彩科技",
                "91310000123456789X",
                priorityLevel,
                stageCode,
                null,
                "",
                estimatedAmount,
                intentLevel,
                status,
                nextFollowTime,
                LocalDateTime.of(2026, 5, 14, 10, 0),
                "顾问A",
                "最近跟进",
                remark,
                LocalDateTime.of(2026, 5, 1, 9, 0),
                LocalDateTime.of(2026, 5, 14, 10, 0)
        );
    }
}
