package com.huacai.assistant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huacai.assistant.dto.AssistantActionConfirmRequest;
import com.huacai.assistant.dto.AssistantConfirmation;
import com.huacai.assistant.dto.AssistantFollowRecordCreateDraftRequest;
import com.huacai.assistant.dto.AssistantFollowRecordPatch;
import com.huacai.assistant.dto.AssistantFollowRecordSearchRequest;
import com.huacai.assistant.dto.AssistantFollowRecordUpdateDraftRequest;
import com.huacai.assistant.dto.AssistantOpportunityCreateDraftRequest;
import com.huacai.assistant.dto.AssistantOpportunityDetailRequest;
import com.huacai.assistant.dto.AssistantOpportunityPatch;
import com.huacai.assistant.dto.AssistantOpportunitySearchRequest;
import com.huacai.assistant.dto.AssistantOpportunityUpdateDraftRequest;
import com.huacai.assistant.dto.AssistantRequestContext;
import com.huacai.assistant.entity.AgentActionLog;
import com.huacai.assistant.mapper.AgentActionLogMapper;
import com.huacai.assistant.service.OpportunityAssistantService;
import com.huacai.assistant.vo.AssistantConfirmCardActionVO;
import com.huacai.assistant.vo.AssistantConfirmCardChangeVO;
import com.huacai.assistant.vo.AssistantConfirmCardTargetVO;
import com.huacai.assistant.vo.AssistantConfirmCardVO;
import com.huacai.assistant.vo.AssistantWriteReceiptVO;
import com.huacai.common.exception.BusinessException;
import com.huacai.common.model.PageResponse;
import com.huacai.common.trace.TraceIdContext;
import com.huacai.customer.entity.CustCustomer;
import com.huacai.customer.mapper.CustomerMapper;
import com.huacai.opportunity.dto.FollowRecordSaveRequest;
import com.huacai.opportunity.dto.OpportunitySaveRequest;
import com.huacai.opportunity.entity.OppFollowRecord;
import com.huacai.opportunity.entity.OppOpportunity;
import com.huacai.opportunity.mapper.FollowRecordMapper;
import com.huacai.opportunity.mapper.OpportunityMapper;
import com.huacai.opportunity.query.FollowRecordPageQuery;
import com.huacai.opportunity.query.OpportunityPageQuery;
import com.huacai.opportunity.service.OpportunityService;
import com.huacai.opportunity.vo.FollowRecordVO;
import com.huacai.opportunity.vo.OpportunityVO;
import com.huacai.security.AuthUser;
import com.huacai.security.CurrentUserProvider;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class OpportunityAssistantServiceImpl implements OpportunityAssistantService {

    private static final String SCENE_KEY = "OPPORTUNITY_ASSISTANT";
    private static final String STATUS_PENDING_CONFIRMATION = "PENDING_CONFIRMATION";
    private static final String STATUS_SUCCESS = "SUCCESS";
    private static final String STATUS_FAILED = "FAILED";
    private static final String STATUS_CANCELLED = "CANCELLED";
    private static final String TARGET_OPPORTUNITY = "opportunity";
    private static final String TARGET_FOLLOW_RECORD = "follow_record";
    private static final String RISK_LEVEL_MEDIUM = "MEDIUM";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final Map<String, String> STAGE_LABELS = Map.of(
            "NEW", "新建",
            "CONTACTED", "已联系",
            "QUALIFIED", "意向确认",
            "PROPOSAL", "方案报价",
            "NEGOTIATION", "商务谈判",
            "CLOSED_WON", "赢单",
            "CLOSED_LOST", "输单"
    );
    private static final Map<String, String> PRIORITY_LABELS = Map.of(
            "HIGH", "高",
            "MEDIUM", "中",
            "LOW", "低"
    );
    private static final Map<String, String> INTENT_LABELS = Map.of(
            "HIGH", "高",
            "MEDIUM", "中",
            "LOW", "低"
    );
    private static final Map<String, String> STATUS_LABELS = Map.of(
            "OPEN", "进行中",
            "CLOSED", "已关闭"
    );

    private final OpportunityService opportunityService;
    private final OpportunityMapper opportunityMapper;
    private final FollowRecordMapper followRecordMapper;
    private final CustomerMapper customerMapper;
    private final AgentActionLogMapper agentActionLogMapper;
    private final CurrentUserProvider currentUserProvider;
    private final ObjectMapper objectMapper;

    public OpportunityAssistantServiceImpl(
            OpportunityService opportunityService,
            OpportunityMapper opportunityMapper,
            FollowRecordMapper followRecordMapper,
            CustomerMapper customerMapper,
            AgentActionLogMapper agentActionLogMapper,
            CurrentUserProvider currentUserProvider,
            ObjectMapper objectMapper
    ) {
        this.opportunityService = opportunityService;
        this.opportunityMapper = opportunityMapper;
        this.followRecordMapper = followRecordMapper;
        this.customerMapper = customerMapper;
        this.agentActionLogMapper = agentActionLogMapper;
        this.currentUserProvider = currentUserProvider;
        this.objectMapper = objectMapper;
    }

    @Override
    public PageResponse<OpportunityVO> searchOpportunities(AssistantOpportunitySearchRequest request) {
        validateStageCode(request.stageCode());
        validateStatus(request.status());
        OpportunityPageQuery query = new OpportunityPageQuery();
        query.setKeyword(trimToNull(request.keyword()));
        query.setCustomerId(request.customerId());
        query.setStageCode(request.stageCode());
        query.setStatus(request.status());
        query.setPageNum(defaultPageNum(request.pageNum()));
        query.setPageSize(defaultPageSize(request.pageSize()));
        return opportunityService.pageOpportunities(query);
    }

    @Override
    public OpportunityVO getOpportunityDetail(AssistantOpportunityDetailRequest request) {
        return opportunityService.getOpportunityDetail(request.opportunityId());
    }

    @Override
    public PageResponse<FollowRecordVO> searchFollowRecords(AssistantFollowRecordSearchRequest request) {
        FollowRecordPageQuery query = new FollowRecordPageQuery();
        query.setOpportunityId(request.opportunityId());
        query.setPageNum(defaultPageNum(request.pageNum()));
        query.setPageSize(defaultPageSize(request.pageSize()));
        return opportunityService.pageFollowRecords(query);
    }

    @Override
    public AssistantConfirmCardVO draftCreateOpportunity(AssistantOpportunityCreateDraftRequest request) {
        validatePriorityLevel(request.priorityLevel());
        validateStageCode(request.stageCode());
        validateIntentLevel(request.intentLevel());
        validateStatus(request.status());

        CustCustomer customer = getCustomerOrThrow(request.customerId());
        OpportunitySaveRequest payload = new OpportunitySaveRequest(
                request.customerId(),
                defaultPriorityLevel(request.priorityLevel()),
                defaultStageCode(request.stageCode()),
                null,
                request.estimatedAmount(),
                defaultIntentLevel(request.intentLevel()),
                defaultStatus(request.status()),
                request.nextFollowTime(),
                trimToNull(request.remark())
        );
        List<AssistantConfirmCardChangeVO> changes = buildCreateOpportunityChanges(payload);
        AssistantConfirmCardTargetVO target = new AssistantConfirmCardTargetVO(
                customer.getId(),
                customer.getCustomerName(),
                customer.getCustomerNo(),
                null,
                null,
                null
        );
        AssistantConfirmCardVO card = buildCard(
                resolveTraceId(request.requestContext()),
                toolNameCreateOpportunity(),
                "请确认新建商机",
                "将为客户新建 1 条商机",
                target,
                changes,
                List.of("将直接写入华彩系统商机主表", "当前版本不支持在此流程中设置负责人")
        );
        persistDraftLog(
                request.requestContext(),
                request.requestText(),
                "CREATE_OPPORTUNITY",
                toolNameCreateOpportunity(),
                TARGET_OPPORTUNITY,
                customer.getId(),
                null,
                null,
                payload,
                payload,
                null,
                card,
                "/api/v1/opportunities",
                "POST"
        );
        return card;
    }

    @Override
    public AssistantConfirmCardVO draftUpdateOpportunity(AssistantOpportunityUpdateDraftRequest request) {
        if (request.patch().isEmpty()) {
            throw new BusinessException("未检测到有效变更");
        }
        validateOpportunityPatch(request.patch());

        OpportunityVO before = opportunityService.getOpportunityDetail(request.opportunityId());
        OpportunitySaveRequest payload = new OpportunitySaveRequest(
                null,
                request.patch().priorityLevel(),
                request.patch().stageCode(),
                null,
                request.patch().estimatedAmount(),
                request.patch().intentLevel(),
                request.patch().status(),
                request.patch().nextFollowTime(),
                trimToNull(request.patch().remark())
        );
        List<AssistantConfirmCardChangeVO> changes = buildUpdateOpportunityChanges(before, request.patch());
        if (changes.isEmpty()) {
            throw new BusinessException("未检测到有效变更");
        }
        AssistantConfirmCardTargetVO target = new AssistantConfirmCardTargetVO(
                before.customerId(),
                before.customerName(),
                before.customerNo(),
                before.id(),
                labelStage(before.stageCode()),
                null
        );
        AssistantConfirmCardVO card = buildCard(
                resolveTraceId(request.requestContext()),
                toolNameUpdateOpportunity(),
                "请确认商机更新",
                "将更新 1 个商机的 " + changes.size() + " 个字段",
                target,
                changes,
                List.of("写入后将直接更新华彩系统商机主表")
        );
        persistDraftLog(
                request.requestContext(),
                request.requestText(),
                "UPDATE_OPPORTUNITY",
                toolNameUpdateOpportunity(),
                TARGET_OPPORTUNITY,
                before.customerId(),
                before.id(),
                null,
                new OpportunityUpdateCommand(before.id(), payload),
                request.patch(),
                before,
                card,
                "/api/v1/opportunities/" + before.id(),
                "PUT"
        );
        return card;
    }

    @Override
    public AssistantConfirmCardVO draftCreateFollowRecord(AssistantFollowRecordCreateDraftRequest request) {
        OpportunityVO opportunity = opportunityService.getOpportunityDetail(request.opportunityId());
        String followerName = StringUtils.hasText(request.followerName())
                ? request.followerName().trim()
                : request.requestContext().feishuDisplayName();
        FollowRecordSaveRequest payload = new FollowRecordSaveRequest(
                request.opportunityId(),
                request.followTime(),
                followerName,
                request.followContent().trim(),
                trimToNull(request.nextAction()),
                trimToNull(request.remark())
        );
        List<AssistantConfirmCardChangeVO> changes = buildCreateFollowChanges(payload);
        AssistantConfirmCardTargetVO target = new AssistantConfirmCardTargetVO(
                opportunity.customerId(),
                opportunity.customerName(),
                opportunity.customerNo(),
                opportunity.id(),
                labelStage(opportunity.stageCode()),
                null
        );
        AssistantConfirmCardVO card = buildCard(
                resolveTraceId(request.requestContext()),
                toolNameCreateFollow(),
                "请确认新增跟进",
                "将为商机新增 1 条跟进记录",
                target,
                changes,
                List.of("当前流程只写入跟进记录，不会自动更新下次跟进时间")
        );
        persistDraftLog(
                request.requestContext(),
                request.requestText(),
                "CREATE_FOLLOW_RECORD",
                toolNameCreateFollow(),
                TARGET_FOLLOW_RECORD,
                opportunity.customerId(),
                opportunity.id(),
                null,
                payload,
                payload,
                null,
                card,
                "/api/v1/opportunities/follow-records",
                "POST"
        );
        return card;
    }

    @Override
    public AssistantConfirmCardVO draftUpdateFollowRecord(AssistantFollowRecordUpdateDraftRequest request) {
        if (request.patch().isEmpty()) {
            throw new BusinessException("未检测到有效变更");
        }
        FollowRecordVO before = opportunityService.getFollowRecordDetail(request.followRecordId());
        OpportunityVO opportunity = opportunityService.getOpportunityDetail(before.opportunityId());
        String followerName = request.patch().followerName() == null
                ? null
                : trimToNull(request.patch().followerName());
        FollowRecordSaveRequest payload = new FollowRecordSaveRequest(
                before.opportunityId(),
                request.patch().followTime(),
                followerName,
                trimToNull(request.patch().followContent()),
                trimToNull(request.patch().nextAction()),
                trimToNull(request.patch().remark())
        );
        List<AssistantConfirmCardChangeVO> changes = buildUpdateFollowChanges(before, request.patch());
        if (changes.isEmpty()) {
            throw new BusinessException("未检测到有效变更");
        }
        AssistantConfirmCardTargetVO target = new AssistantConfirmCardTargetVO(
                opportunity.customerId(),
                opportunity.customerName(),
                opportunity.customerNo(),
                opportunity.id(),
                labelStage(opportunity.stageCode()),
                before.id()
        );
        AssistantConfirmCardVO card = buildCard(
                resolveTraceId(request.requestContext()),
                toolNameUpdateFollow(),
                "请确认更新跟进",
                "将更新 1 条跟进记录",
                target,
                changes,
                List.of("写入后将直接更新华彩系统跟进记录")
        );
        persistDraftLog(
                request.requestContext(),
                request.requestText(),
                "UPDATE_FOLLOW_RECORD",
                toolNameUpdateFollow(),
                TARGET_FOLLOW_RECORD,
                opportunity.customerId(),
                opportunity.id(),
                before.id(),
                new FollowRecordUpdateCommand(before.id(), payload),
                request.patch(),
                before,
                card,
                "/api/v1/opportunities/follow-records/" + before.id(),
                "PUT"
        );
        return card;
    }

    @Override
    public AssistantWriteReceiptVO confirmAction(AssistantActionConfirmRequest request) {
        AssistantConfirmation confirmation = request.confirmation();
        AgentActionLog log = getLogByConfirmationId(confirmation.confirmationId());
        if (STATUS_SUCCESS.equals(log.getResultCode())) {
            return buildReceipt(log, true, log.getResultMessage());
        }
        if (STATUS_CANCELLED.equals(log.getResultCode())) {
            return buildReceipt(log, false, "该确认单已取消");
        }
        if (!STATUS_PENDING_CONFIRMATION.equals(log.getResultCode())) {
            throw new BusinessException("该确认单状态不可执行，请重新发起");
        }

        if (!Boolean.TRUE.equals(confirmation.confirmed())) {
            log.setConfirmedFlag(0);
            log.setResultCode(STATUS_CANCELLED);
            log.setResultMessage("用户取消执行");
            log.setErrorMessage(null);
            log.setUpdatedBy(currentUserProvider.getCurrentUserId());
            agentActionLogMapper.updateById(log);
            return buildReceipt(log, false, "已取消写入");
        }

        log.setConfirmedFlag(1);
        log.setConfirmedAt(LocalDateTime.now());
        log.setUpdatedBy(currentUserProvider.getCurrentUserId());
        agentActionLogMapper.updateById(log);

        try {
            AssistantWriteReceiptVO receipt = executePendingAction(log);
            log.setResultCode(STATUS_SUCCESS);
            log.setResultMessage(receipt.message());
            log.setErrorMessage(null);
            log.setUpdatedBy(currentUserProvider.getCurrentUserId());
            agentActionLogMapper.updateById(log);
            return receipt;
        } catch (BusinessException exception) {
            markFailed(log, exception.getMessage());
            throw exception;
        } catch (Exception exception) {
            markFailed(log, exception.getMessage());
            throw new BusinessException("系统异常，请稍后重试");
        }
    }

    private void persistDraftLog(
            AssistantRequestContext requestContext,
            String requestText,
            String intentKey,
            String toolName,
            String targetType,
            Long targetCustomerId,
            Long targetOpportunityId,
            Long targetFollowRecordId,
            Object toolInput,
            Object extractedPayload,
            Object beforeSnapshot,
            AssistantConfirmCardVO card,
            String requestUri,
            String requestMethod
    ) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        AgentActionLog log = new AgentActionLog();
        log.setTraceId(resolveTraceId(requestContext));
        log.setSessionId(requestContext.sessionId());
        log.setMessageId(trimToNull(requestContext.messageId()));
        log.setSceneKey(SCENE_KEY);
        log.setIntentKey(intentKey);
        log.setToolName(toolName);
        log.setFeishuUserId(requestContext.feishuUserId());
        log.setFeishuDisplayName(requestContext.feishuDisplayName());
        log.setChatId(trimToNull(requestContext.chatId()));
        log.setExecutorUserId(currentUserId);
        log.setExecutorUsername(currentUserProvider.getCurrentUser().map(AuthUser::getUsername).orElse(null));
        log.setTargetType(targetType);
        log.setTargetCustomerId(targetCustomerId);
        log.setTargetOpportunityId(targetOpportunityId);
        log.setTargetFollowRecordId(targetFollowRecordId);
        log.setRequestText(trimToNull(requestText));
        log.setRequestContextJson(toJson(requestContext));
        log.setToolInputJson(toJson(toolInput));
        log.setExtractedPayloadJson(toJson(extractedPayload));
        log.setBeforeSnapshotJson(toJson(beforeSnapshot));
        log.setConfirmationId(card.confirmationId());
        log.setConfirmationJson(toJson(card));
        log.setConfirmedFlag(0);
        log.setConfirmedAt(null);
        log.setAfterSnapshotJson(null);
        log.setRequestUri(requestUri);
        log.setRequestMethod(requestMethod);
        log.setRequestBodyJson(toJson(toolInput));
        log.setResultCode(STATUS_PENDING_CONFIRMATION);
        log.setResultMessage("待确认");
        log.setErrorMessage(null);
        log.setCreatedBy(currentUserId);
        log.setUpdatedBy(currentUserId);
        agentActionLogMapper.insert(log);
    }

    private AssistantWriteReceiptVO executePendingAction(AgentActionLog log) {
        return switch (log.getToolName()) {
            case "huacai.opportunity.create" -> executeCreateOpportunity(log);
            case "huacai.opportunity.update_fields" -> executeUpdateOpportunity(log);
            case "huacai.opportunity.follow.create" -> executeCreateFollow(log);
            case "huacai.opportunity.follow.update" -> executeUpdateFollow(log);
            default -> throw new BusinessException("不支持的确认工具");
        };
    }

    private AssistantWriteReceiptVO executeCreateOpportunity(AgentActionLog log) {
        OpportunitySaveRequest payload = fromJson(log.getToolInputJson(), OpportunitySaveRequest.class);
        opportunityService.createOpportunity(payload);
        OpportunityVO after = findNewestOpportunity(payload.customerId());
        log.setTargetOpportunityId(after == null ? null : after.id());
        log.setAfterSnapshotJson(toJson(after));
        return new AssistantWriteReceiptVO(
                true,
                log.getTraceId(),
                log.getToolName(),
                TARGET_OPPORTUNITY,
                after == null ? null : after.id(),
                "商机创建成功"
        );
    }

    private AssistantWriteReceiptVO executeUpdateOpportunity(AgentActionLog log) {
        OpportunityUpdateCommand command = fromJson(log.getToolInputJson(), OpportunityUpdateCommand.class);
        opportunityService.updateOpportunity(command.opportunityId(), command.payload());
        OpportunityVO after = opportunityService.getOpportunityDetail(command.opportunityId());
        log.setAfterSnapshotJson(toJson(after));
        return new AssistantWriteReceiptVO(
                true,
                log.getTraceId(),
                log.getToolName(),
                TARGET_OPPORTUNITY,
                after.id(),
                "商机更新成功"
        );
    }

    private AssistantWriteReceiptVO executeCreateFollow(AgentActionLog log) {
        FollowRecordSaveRequest payload = fromJson(log.getToolInputJson(), FollowRecordSaveRequest.class);
        opportunityService.createFollowRecord(payload);
        FollowRecordVO after = findNewestFollowRecord(payload.opportunityId());
        log.setTargetFollowRecordId(after == null ? null : after.id());
        log.setAfterSnapshotJson(toJson(after));
        return new AssistantWriteReceiptVO(
                true,
                log.getTraceId(),
                log.getToolName(),
                TARGET_FOLLOW_RECORD,
                after == null ? null : after.id(),
                "跟进记录创建成功"
        );
    }

    private AssistantWriteReceiptVO executeUpdateFollow(AgentActionLog log) {
        FollowRecordUpdateCommand command = fromJson(log.getToolInputJson(), FollowRecordUpdateCommand.class);
        opportunityService.updateFollowRecord(command.followRecordId(), command.payload());
        FollowRecordVO after = opportunityService.getFollowRecordDetail(command.followRecordId());
        log.setAfterSnapshotJson(toJson(after));
        return new AssistantWriteReceiptVO(
                true,
                log.getTraceId(),
                log.getToolName(),
                TARGET_FOLLOW_RECORD,
                after.id(),
                "跟进记录更新成功"
        );
    }

    private AgentActionLog getLogByConfirmationId(String confirmationId) {
        AgentActionLog log = agentActionLogMapper.selectOne(new LambdaQueryWrapper<AgentActionLog>()
                .eq(AgentActionLog::getConfirmationId, confirmationId)
                .last("limit 1"));
        if (log == null) {
            throw new BusinessException("确认单不存在");
        }
        return log;
    }

    private OpportunityVO findNewestOpportunity(Long customerId) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        OppOpportunity entity = opportunityMapper.selectOne(new LambdaQueryWrapper<OppOpportunity>()
                .eq(OppOpportunity::getCustomerId, customerId)
                .eq(currentUserId != null, OppOpportunity::getCreatedBy, currentUserId)
                .orderByDesc(OppOpportunity::getCreatedAt)
                .orderByDesc(OppOpportunity::getId)
                .last("limit 1"));
        return entity == null ? null : opportunityService.getOpportunityDetail(entity.getId());
    }

    private FollowRecordVO findNewestFollowRecord(Long opportunityId) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        OppFollowRecord entity = followRecordMapper.selectOne(new LambdaQueryWrapper<OppFollowRecord>()
                .eq(OppFollowRecord::getOpportunityId, opportunityId)
                .eq(currentUserId != null, OppFollowRecord::getCreatedBy, currentUserId)
                .orderByDesc(OppFollowRecord::getCreatedAt)
                .orderByDesc(OppFollowRecord::getId)
                .last("limit 1"));
        return entity == null ? null : opportunityService.getFollowRecordDetail(entity.getId());
    }

    private AssistantConfirmCardVO buildCard(
            String traceId,
            String toolName,
            String title,
            String summary,
            AssistantConfirmCardTargetVO target,
            List<AssistantConfirmCardChangeVO> changes,
            List<String> warnings
    ) {
        return new AssistantConfirmCardVO(
                "opportunity_write_confirmation",
                "confirm_" + UUID.randomUUID().toString().replace("-", ""),
                traceId,
                toolName,
                title,
                RISK_LEVEL_MEDIUM,
                summary,
                target,
                changes,
                warnings,
                List.of(
                        new AssistantConfirmCardActionVO("confirm", "确认写入"),
                        new AssistantConfirmCardActionVO("cancel", "取消")
                ),
                LocalDateTime.now().plusMinutes(10)
        );
    }

    private List<AssistantConfirmCardChangeVO> buildCreateOpportunityChanges(OpportunitySaveRequest payload) {
        List<AssistantConfirmCardChangeVO> changes = new ArrayList<>();
        changes.add(new AssistantConfirmCardChangeVO("stageCode", "商机阶段", null, labelStage(payload.stageCode())));
        changes.add(new AssistantConfirmCardChangeVO("priorityLevel", "优先级", null, labelPriority(payload.priorityLevel())));
        changes.add(new AssistantConfirmCardChangeVO("intentLevel", "意向等级", null, labelIntent(payload.intentLevel())));
        changes.add(new AssistantConfirmCardChangeVO("status", "状态", null, labelStatus(payload.status())));
        if (payload.estimatedAmount() != null) {
            changes.add(new AssistantConfirmCardChangeVO("estimatedAmount", "预计金额", null, formatAmount(payload.estimatedAmount())));
        }
        if (payload.nextFollowTime() != null) {
            changes.add(new AssistantConfirmCardChangeVO("nextFollowTime", "下次跟进时间", null, formatDateTime(payload.nextFollowTime())));
        }
        if (StringUtils.hasText(payload.remark())) {
            changes.add(new AssistantConfirmCardChangeVO("remark", "备注", null, payload.remark()));
        }
        return changes;
    }

    private List<AssistantConfirmCardChangeVO> buildUpdateOpportunityChanges(
            OpportunityVO before,
            AssistantOpportunityPatch patch
    ) {
        List<AssistantConfirmCardChangeVO> changes = new ArrayList<>();
        addChange(changes, "stageCode", "商机阶段", labelStage(before.stageCode()), labelStage(patch.stageCode()), patch.stageCode());
        addChange(changes, "priorityLevel", "优先级", labelPriority(before.priorityLevel()), labelPriority(patch.priorityLevel()), patch.priorityLevel());
        addChange(changes, "intentLevel", "意向等级", labelIntent(before.intentLevel()), labelIntent(patch.intentLevel()), patch.intentLevel());
        addChange(changes, "status", "状态", labelStatus(before.status()), labelStatus(patch.status()), patch.status());
        if (patch.estimatedAmount() != null && !Objects.equals(before.estimatedAmount(), patch.estimatedAmount())) {
            changes.add(new AssistantConfirmCardChangeVO(
                    "estimatedAmount",
                    "预计金额",
                    formatAmount(before.estimatedAmount()),
                    formatAmount(patch.estimatedAmount())
            ));
        }
        if (patch.nextFollowTime() != null && !Objects.equals(before.nextFollowTime(), patch.nextFollowTime())) {
            changes.add(new AssistantConfirmCardChangeVO(
                    "nextFollowTime",
                    "下次跟进时间",
                    formatDateTime(before.nextFollowTime()),
                    formatDateTime(patch.nextFollowTime())
            ));
        }
        if (patch.remark() != null && !Objects.equals(defaultString(before.remark()), defaultString(patch.remark()))) {
            changes.add(new AssistantConfirmCardChangeVO(
                    "remark",
                    "备注",
                    blankToDash(before.remark()),
                    blankToDash(patch.remark())
            ));
        }
        return changes;
    }

    private List<AssistantConfirmCardChangeVO> buildCreateFollowChanges(FollowRecordSaveRequest payload) {
        List<AssistantConfirmCardChangeVO> changes = new ArrayList<>();
        changes.add(new AssistantConfirmCardChangeVO("followTime", "跟进时间", null, formatDateTime(payload.followTime())));
        changes.add(new AssistantConfirmCardChangeVO("followerName", "跟进人", null, blankToDash(payload.followerName())));
        changes.add(new AssistantConfirmCardChangeVO("followContent", "跟进内容", null, payload.followContent()));
        if (StringUtils.hasText(payload.nextAction())) {
            changes.add(new AssistantConfirmCardChangeVO("nextAction", "下一步动作", null, payload.nextAction()));
        }
        if (StringUtils.hasText(payload.remark())) {
            changes.add(new AssistantConfirmCardChangeVO("remark", "备注", null, payload.remark()));
        }
        return changes;
    }

    private List<AssistantConfirmCardChangeVO> buildUpdateFollowChanges(
            FollowRecordVO before,
            AssistantFollowRecordPatch patch
    ) {
        List<AssistantConfirmCardChangeVO> changes = new ArrayList<>();
        if (patch.followTime() != null && !Objects.equals(before.followTime(), patch.followTime())) {
            changes.add(new AssistantConfirmCardChangeVO(
                    "followTime",
                    "跟进时间",
                    formatDateTime(before.followTime()),
                    formatDateTime(patch.followTime())
            ));
        }
        if (patch.followerName() != null && !Objects.equals(defaultString(before.followerName()), defaultString(patch.followerName()))) {
            changes.add(new AssistantConfirmCardChangeVO(
                    "followerName",
                    "跟进人",
                    blankToDash(before.followerName()),
                    blankToDash(patch.followerName())
            ));
        }
        if (patch.followContent() != null && !Objects.equals(defaultString(before.followContent()), defaultString(patch.followContent()))) {
            changes.add(new AssistantConfirmCardChangeVO(
                    "followContent",
                    "跟进内容",
                    blankToDash(before.followContent()),
                    blankToDash(patch.followContent())
            ));
        }
        if (patch.nextAction() != null && !Objects.equals(defaultString(before.nextAction()), defaultString(patch.nextAction()))) {
            changes.add(new AssistantConfirmCardChangeVO(
                    "nextAction",
                    "下一步动作",
                    blankToDash(before.nextAction()),
                    blankToDash(patch.nextAction())
            ));
        }
        if (patch.remark() != null && !Objects.equals(defaultString(before.remark()), defaultString(patch.remark()))) {
            changes.add(new AssistantConfirmCardChangeVO(
                    "remark",
                    "备注",
                    blankToDash(before.remark()),
                    blankToDash(patch.remark())
            ));
        }
        return changes;
    }

    private void addChange(
            List<AssistantConfirmCardChangeVO> changes,
            String fieldKey,
            String fieldLabel,
            String before,
            String after,
            String rawValue
    ) {
        if (rawValue == null || Objects.equals(before, after)) {
            return;
        }
        changes.add(new AssistantConfirmCardChangeVO(fieldKey, fieldLabel, before, after));
    }

    private void validateOpportunityPatch(AssistantOpportunityPatch patch) {
        validatePriorityLevel(patch.priorityLevel());
        validateStageCode(patch.stageCode());
        validateIntentLevel(patch.intentLevel());
        validateStatus(patch.status());
    }

    private void validatePriorityLevel(String priorityLevel) {
        validateValue("priorityLevel", priorityLevel, PRIORITY_LABELS);
    }

    private void validateStageCode(String stageCode) {
        validateValue("stageCode", stageCode, STAGE_LABELS);
    }

    private void validateIntentLevel(String intentLevel) {
        validateValue("intentLevel", intentLevel, INTENT_LABELS);
    }

    private void validateStatus(String status) {
        validateValue("status", status, STATUS_LABELS);
    }

    private void validateValue(String fieldName, String value, Map<String, String> allowedValues) {
        if (!StringUtils.hasText(value)) {
            return;
        }
        if (!allowedValues.containsKey(value.trim())) {
            throw new BusinessException(fieldName + "取值非法");
        }
    }

    private String defaultPriorityLevel(String priorityLevel) {
        return StringUtils.hasText(priorityLevel) ? priorityLevel.trim() : "MEDIUM";
    }

    private String defaultStageCode(String stageCode) {
        return StringUtils.hasText(stageCode) ? stageCode.trim() : "NEW";
    }

    private String defaultIntentLevel(String intentLevel) {
        return StringUtils.hasText(intentLevel) ? intentLevel.trim() : "MEDIUM";
    }

    private String defaultStatus(String status) {
        return StringUtils.hasText(status) ? status.trim() : "OPEN";
    }

    private CustCustomer getCustomerOrThrow(Long customerId) {
        CustCustomer customer = customerMapper.selectById(customerId);
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }
        return customer;
    }

    private void markFailed(AgentActionLog log, String message) {
        log.setResultCode(STATUS_FAILED);
        log.setResultMessage("执行失败");
        log.setErrorMessage(message);
        log.setUpdatedBy(currentUserProvider.getCurrentUserId());
        agentActionLogMapper.updateById(log);
    }

    private AssistantWriteReceiptVO buildReceipt(AgentActionLog log, boolean success, String message) {
        Long targetId = TARGET_FOLLOW_RECORD.equals(log.getTargetType())
                ? log.getTargetFollowRecordId()
                : log.getTargetOpportunityId();
        return new AssistantWriteReceiptVO(
                success,
                log.getTraceId(),
                log.getToolName(),
                log.getTargetType(),
                targetId,
                message
        );
    }

    private String resolveTraceId(AssistantRequestContext requestContext) {
        if (requestContext != null && StringUtils.hasText(requestContext.traceId())) {
            return requestContext.traceId().trim();
        }
        return TraceIdContext.getOrCreate();
    }

    private String toJson(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("JSON序列化失败", exception);
        }
    }

    private <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("JSON反序列化失败", exception);
        }
    }

    private int defaultPageNum(Integer pageNum) {
        return pageNum == null || pageNum < 1 ? 1 : pageNum;
    }

    private int defaultPageSize(Integer pageSize) {
        if (pageSize == null || pageSize < 1) {
            return 20;
        }
        return Math.min(pageSize, 50);
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private String labelStage(String value) {
        return label(value, STAGE_LABELS);
    }

    private String labelPriority(String value) {
        return label(value, PRIORITY_LABELS);
    }

    private String labelIntent(String value) {
        return label(value, INTENT_LABELS);
    }

    private String labelStatus(String value) {
        return label(value, STATUS_LABELS);
    }

    private String label(String value, Map<String, String> labels) {
        if (!StringUtils.hasText(value)) {
            return "-";
        }
        return labels.getOrDefault(value.trim(), value.trim());
    }

    private String formatAmount(BigDecimal amount) {
        return amount == null ? "-" : amount.stripTrailingZeros().toPlainString();
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime == null ? "-" : DATE_TIME_FORMATTER.format(dateTime);
    }

    private String blankToDash(String value) {
        return StringUtils.hasText(value) ? value : "-";
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }

    private String toolNameCreateOpportunity() {
        return "huacai.opportunity.create";
    }

    private String toolNameUpdateOpportunity() {
        return "huacai.opportunity.update_fields";
    }

    private String toolNameCreateFollow() {
        return "huacai.opportunity.follow.create";
    }

    private String toolNameUpdateFollow() {
        return "huacai.opportunity.follow.update";
    }

    private record OpportunityUpdateCommand(
            Long opportunityId,
            OpportunitySaveRequest payload
    ) {
    }

    private record FollowRecordUpdateCommand(
            Long followRecordId,
            FollowRecordSaveRequest payload
    ) {
    }
}
