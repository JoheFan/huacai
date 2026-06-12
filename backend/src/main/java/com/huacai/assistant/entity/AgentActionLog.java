package com.huacai.assistant.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.huacai.common.entity.AuditableEntity;
import java.time.LocalDateTime;

@TableName("agent_action_log")
public class AgentActionLog extends AuditableEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String traceId;
    private String sessionId;
    private String messageId;
    private String sceneKey;
    private String intentKey;
    private String toolName;
    private String feishuUserId;
    private String feishuDisplayName;
    private String chatId;
    private Long executorUserId;
    private String executorUsername;
    private String targetType;
    private Long targetCustomerId;
    private Long targetOpportunityId;
    private Long targetFollowRecordId;
    private String requestText;
    private String requestContextJson;
    private String toolInputJson;
    private String extractedPayloadJson;
    private String beforeSnapshotJson;
    private String confirmationId;
    private String confirmationJson;
    private Integer confirmedFlag;
    private LocalDateTime confirmedAt;
    private String afterSnapshotJson;
    private String requestUri;
    private String requestMethod;
    private String requestBodyJson;
    private String resultCode;
    private String resultMessage;
    private String errorMessage;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSceneKey() {
        return sceneKey;
    }

    public void setSceneKey(String sceneKey) {
        this.sceneKey = sceneKey;
    }

    public String getIntentKey() {
        return intentKey;
    }

    public void setIntentKey(String intentKey) {
        this.intentKey = intentKey;
    }

    public String getToolName() {
        return toolName;
    }

    public void setToolName(String toolName) {
        this.toolName = toolName;
    }

    public String getFeishuUserId() {
        return feishuUserId;
    }

    public void setFeishuUserId(String feishuUserId) {
        this.feishuUserId = feishuUserId;
    }

    public String getFeishuDisplayName() {
        return feishuDisplayName;
    }

    public void setFeishuDisplayName(String feishuDisplayName) {
        this.feishuDisplayName = feishuDisplayName;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public Long getExecutorUserId() {
        return executorUserId;
    }

    public void setExecutorUserId(Long executorUserId) {
        this.executorUserId = executorUserId;
    }

    public String getExecutorUsername() {
        return executorUsername;
    }

    public void setExecutorUsername(String executorUsername) {
        this.executorUsername = executorUsername;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public Long getTargetCustomerId() {
        return targetCustomerId;
    }

    public void setTargetCustomerId(Long targetCustomerId) {
        this.targetCustomerId = targetCustomerId;
    }

    public Long getTargetOpportunityId() {
        return targetOpportunityId;
    }

    public void setTargetOpportunityId(Long targetOpportunityId) {
        this.targetOpportunityId = targetOpportunityId;
    }

    public Long getTargetFollowRecordId() {
        return targetFollowRecordId;
    }

    public void setTargetFollowRecordId(Long targetFollowRecordId) {
        this.targetFollowRecordId = targetFollowRecordId;
    }

    public String getRequestText() {
        return requestText;
    }

    public void setRequestText(String requestText) {
        this.requestText = requestText;
    }

    public String getRequestContextJson() {
        return requestContextJson;
    }

    public void setRequestContextJson(String requestContextJson) {
        this.requestContextJson = requestContextJson;
    }

    public String getToolInputJson() {
        return toolInputJson;
    }

    public void setToolInputJson(String toolInputJson) {
        this.toolInputJson = toolInputJson;
    }

    public String getExtractedPayloadJson() {
        return extractedPayloadJson;
    }

    public void setExtractedPayloadJson(String extractedPayloadJson) {
        this.extractedPayloadJson = extractedPayloadJson;
    }

    public String getBeforeSnapshotJson() {
        return beforeSnapshotJson;
    }

    public void setBeforeSnapshotJson(String beforeSnapshotJson) {
        this.beforeSnapshotJson = beforeSnapshotJson;
    }

    public String getConfirmationId() {
        return confirmationId;
    }

    public void setConfirmationId(String confirmationId) {
        this.confirmationId = confirmationId;
    }

    public String getConfirmationJson() {
        return confirmationJson;
    }

    public void setConfirmationJson(String confirmationJson) {
        this.confirmationJson = confirmationJson;
    }

    public Integer getConfirmedFlag() {
        return confirmedFlag;
    }

    public void setConfirmedFlag(Integer confirmedFlag) {
        this.confirmedFlag = confirmedFlag;
    }

    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(LocalDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public String getAfterSnapshotJson() {
        return afterSnapshotJson;
    }

    public void setAfterSnapshotJson(String afterSnapshotJson) {
        this.afterSnapshotJson = afterSnapshotJson;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getRequestBodyJson() {
        return requestBodyJson;
    }

    public void setRequestBodyJson(String requestBodyJson) {
        this.requestBodyJson = requestBodyJson;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
