# 飞书商机助手 API 规格文档

## 1. 文档范围

本文档对应当前代码库中新增的“飞书商机助手”后端接口，目标是为 `openclaw + 飞书机器人` 提供一组可控、可审计的商机问答与写入能力。

当前实现范围：

- 商机查询
- 商机详情查询
- 商机跟进记录查询
- 新建商机 draft
- 更新商机字段 draft
- 新增跟进记录 draft
- 更新跟进记录 draft
- 确认执行写入

当前不支持：

- 删除商机
- 删除跟进记录
- 修改负责人 `ownerUserId`
- 审批
- 附件处理

## 2. 实现位置

- Controller
  - [OpportunityAssistantController.java](/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/assistant/controller/OpportunityAssistantController.java)
  - [AssistantActionController.java](/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/assistant/controller/AssistantActionController.java)
- Service
  - [OpportunityAssistantService.java](/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/assistant/service/OpportunityAssistantService.java)
  - [OpportunityAssistantServiceImpl.java](/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/assistant/service/impl/OpportunityAssistantServiceImpl.java)
- 审计表与迁移
  - [AgentActionLog.java](/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/assistant/entity/AgentActionLog.java)
  - [V2_120__agent_action_log.sql](/Users/edy/Documents/tob /华彩系统1025/sql/migration/V2_120__agent_action_log.sql)
- 权限映射
  - [ModuleAccessRegistry.java](/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/security/ModuleAccessRegistry.java)

## 3. 设计原则

- 读接口直接返回结果，不进入确认流程。
- 写接口先生成 `draft`，由 openclaw 渲染确认卡片。
- 用户确认后，再通过 `confirm` 接口执行底层写入。
- 所有写流程都写入 `agent_action_log`。
- assistant 层复用现有 `OpportunityService`，不复制商机业务逻辑。

## 4. 鉴权与权限

- 鉴权方式：沿用当前系统 JWT。
- 页面权限：assistant 接口复用 `/opportunities` 页面权限。
- 适用路径：
  - `/api/v1/assistant/opportunities/**`
  - `/api/v1/assistant/actions/**`

## 5. 统一响应

所有接口统一返回：

```json
{
  "code": 0,
  "message": "success",
  "data": {},
  "traceId": "trace-id"
}
```

失败时：

```json
{
  "code": -1,
  "message": "业务错误信息",
  "data": null,
  "traceId": "trace-id"
}
```

## 6. 通用对象

### 6.1 `AssistantRequestContext`

```json
{
  "traceId": "trace-001",
  "sessionId": "session-001",
  "messageId": "om_001",
  "feishuUserId": "ou_xxx",
  "feishuDisplayName": "张三",
  "chatId": "oc_xxx",
  "requestAt": "2026-05-14T10:00:00+08:00",
  "locale": "zh-CN"
}
```

### 6.2 `AssistantConfirmation`

```json
{
  "confirmationId": "confirm_xxx",
  "confirmed": true,
  "idempotencyKey": "idem_xxx",
  "confirmedAt": "2026-05-14T10:01:00+08:00",
  "confirmationNote": "用户确认"
}
```

## 7. 查询接口

### 7.1 商机查询

`POST /api/v1/assistant/opportunities/search`

请求：

```json
{
  "requestContext": {
    "traceId": "trace-search-1",
    "sessionId": "session-1",
    "messageId": "msg-1",
    "feishuUserId": "ou_1",
    "feishuDisplayName": "李四",
    "chatId": "chat-1",
    "locale": "zh-CN"
  },
  "keyword": "张三",
  "stageCode": "NEGOTIATION",
  "status": "OPEN",
  "pageNum": 1,
  "pageSize": 10
}
```

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 11,
        "customerId": 6,
        "customerName": "张三",
        "customerNo": "KH-001",
        "mobile": "13800000000",
        "companyName": "华彩科技",
        "creditCode": "91310000123456789X",
        "priorityLevel": "HIGH",
        "stageCode": "NEGOTIATION",
        "ownerUserId": null,
        "ownerUserName": "",
        "estimatedAmount": 600000,
        "intentLevel": "HIGH",
        "status": "OPEN",
        "nextFollowTime": "2026-05-20T14:00:00",
        "latestFollowTime": "2026-05-14T15:00:00",
        "latestFollowerName": "李四",
        "latestFollowContent": "电话沟通，客户下周补材料",
        "remark": "客户意向增强",
        "createdAt": "2026-05-01T09:00:00",
        "updatedAt": "2026-05-14T15:00:00"
      }
    ],
    "total": 1,
    "pageNum": 1,
    "pageSize": 10
  },
  "traceId": "trace-search-1"
}
```

说明：

- `keyword` 当前实际命中客户姓名、客户编号、公司名、手机号。
- `ownerUserName` 当前代码返回空串，不能作为稳定字段使用。

### 7.2 商机详情

`POST /api/v1/assistant/opportunities/detail`

请求：

```json
{
  "requestContext": {
    "traceId": "trace-detail-1",
    "sessionId": "session-1",
    "messageId": "msg-2",
    "feishuUserId": "ou_1",
    "feishuDisplayName": "李四",
    "chatId": "chat-1",
    "locale": "zh-CN"
  },
  "opportunityId": 11
}
```

### 7.3 跟进记录查询

`POST /api/v1/assistant/opportunities/follow-records/search`

请求：

```json
{
  "requestContext": {
    "traceId": "trace-follow-list-1",
    "sessionId": "session-1",
    "messageId": "msg-3",
    "feishuUserId": "ou_1",
    "feishuDisplayName": "李四",
    "chatId": "chat-1",
    "locale": "zh-CN"
  },
  "opportunityId": 11,
  "pageNum": 1,
  "pageSize": 20
}
```

响应中的 `records` 元素：

```json
{
  "id": 101,
  "opportunityId": 11,
  "customerId": 6,
  "customerName": "",
  "followTime": "2026-05-14T15:00:00",
  "followerName": "李四",
  "followContent": "电话沟通，客户下周补材料",
  "nextAction": "下周二继续跟进",
  "remark": "来源：飞书",
  "createdAt": "2026-05-14T15:00:00",
  "updatedAt": "2026-05-14T15:00:00"
}
```

说明：

- 当前后端未实现 `follow-records` 的 `keyword` 检索。
- `customerName` 字段当前 VO 存在，但现有实现可能为空。

## 8. Draft 接口

Draft 接口不会直接写业务表，只会：

1. 校验字段
2. 查询写前对象
3. 生成确认卡片
4. 写入 `agent_action_log`
5. 返回确认卡 payload

### 8.1 新建商机 Draft

`POST /api/v1/assistant/opportunities/create/draft`

请求：

```json
{
  "requestContext": {
    "traceId": "trace-create-1",
    "sessionId": "session-2",
    "messageId": "msg-10",
    "feishuUserId": "ou_2",
    "feishuDisplayName": "王五",
    "chatId": "chat-2",
    "locale": "zh-CN"
  },
  "requestText": "给张三新建一个高优先级商机，阶段先放已联系",
  "customerId": 6,
  "priorityLevel": "HIGH",
  "stageCode": "CONTACTED",
  "estimatedAmount": 500000,
  "intentLevel": "HIGH",
  "status": "OPEN",
  "nextFollowTime": "2026-05-16T10:00:00",
  "remark": "飞书创建"
}
```

### 8.2 更新商机字段 Draft

`POST /api/v1/assistant/opportunities/update-fields/draft`

请求：

```json
{
  "requestContext": {
    "traceId": "trace-update-1",
    "sessionId": "session-3",
    "messageId": "msg-11",
    "feishuUserId": "ou_3",
    "feishuDisplayName": "赵六",
    "chatId": "chat-3",
    "locale": "zh-CN"
  },
  "requestText": "把这个商机推进到商务谈判，下次周二跟进",
  "opportunityId": 11,
  "patch": {
    "stageCode": "NEGOTIATION",
    "nextFollowTime": "2026-05-20T14:00:00"
  }
}
```

### 8.3 新增跟进 Draft

`POST /api/v1/assistant/opportunities/follow-records/create/draft`

请求：

```json
{
  "requestContext": {
    "traceId": "trace-follow-create-1",
    "sessionId": "session-4",
    "messageId": "msg-12",
    "feishuUserId": "ou_4",
    "feishuDisplayName": "李四",
    "chatId": "chat-4",
    "locale": "zh-CN"
  },
  "requestText": "补一条跟进，客户说下周补材料",
  "opportunityId": 11,
  "followTime": "2026-05-14T15:00:00",
  "followContent": "电话沟通，客户说下周补材料",
  "nextAction": "下周二继续跟进",
  "remark": "来源：飞书"
}
```

说明：

- 若未传 `followerName`，当前实现默认写入 `requestContext.feishuDisplayName`。

### 8.4 更新跟进 Draft

`POST /api/v1/assistant/opportunities/follow-records/update/draft`

请求：

```json
{
  "requestContext": {
    "traceId": "trace-follow-update-1",
    "sessionId": "session-5",
    "messageId": "msg-13",
    "feishuUserId": "ou_5",
    "feishuDisplayName": "李四",
    "chatId": "chat-5",
    "locale": "zh-CN"
  },
  "requestText": "把刚才那条跟进改成客户周三给材料",
  "followRecordId": 101,
  "patch": {
    "followContent": "客户承诺周三前给材料",
    "nextAction": "周三上午回访"
  }
}
```

## 9. 确认卡返回结构

Draft 接口统一返回：

```json
{
  "cardType": "opportunity_write_confirmation",
  "confirmationId": "confirm_xxx",
  "traceId": "trace-update-1",
  "toolName": "huacai.opportunity.update_fields",
  "title": "请确认商机更新",
  "riskLevel": "MEDIUM",
  "summary": "将更新 1 个商机的 2 个字段",
  "target": {
    "customerId": 6,
    "customerName": "张三",
    "customerNo": "KH-001",
    "opportunityId": 11,
    "opportunityStage": "意向确认",
    "followRecordId": null
  },
  "changes": [
    {
      "fieldKey": "stageCode",
      "fieldLabel": "商机阶段",
      "before": "意向确认",
      "after": "商务谈判"
    },
    {
      "fieldKey": "nextFollowTime",
      "fieldLabel": "下次跟进时间",
      "before": "2026-05-16 10:00",
      "after": "2026-05-20 14:00"
    }
  ],
  "warnings": [
    "写入后将直接更新华彩系统商机主表"
  ],
  "actions": [
    { "action": "confirm", "label": "确认写入" },
    { "action": "cancel", "label": "取消" }
  ],
  "expiresAt": "2026-05-14T10:10:00"
}
```

## 10. 确认执行接口

`POST /api/v1/assistant/actions/confirm`

请求：

```json
{
  "requestContext": {
    "traceId": "trace-update-1",
    "sessionId": "session-3",
    "messageId": "msg-14",
    "feishuUserId": "ou_3",
    "feishuDisplayName": "赵六",
    "chatId": "chat-3",
    "locale": "zh-CN"
  },
  "confirmation": {
    "confirmationId": "confirm_xxx",
    "confirmed": true,
    "idempotencyKey": "idem-001",
    "confirmedAt": "2026-05-14T10:01:00+08:00",
    "confirmationNote": "确认执行"
  }
}
```

成功响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "success": true,
    "traceId": "trace-update-1",
    "toolName": "huacai.opportunity.update_fields",
    "targetType": "opportunity",
    "targetId": 11,
    "message": "商机更新成功"
  },
  "traceId": "trace-update-1"
}
```

取消响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "success": false,
    "traceId": "trace-update-1",
    "toolName": "huacai.opportunity.update_fields",
    "targetType": "opportunity",
    "targetId": 11,
    "message": "已取消写入"
  },
  "traceId": "trace-update-1"
}
```

## 11. toolName 对照表

| toolName | 说明 | 下游 REST |
| --- | --- | --- |
| `huacai.opportunity.create` | 新建商机 | `POST /api/v1/opportunities` |
| `huacai.opportunity.update_fields` | 更新商机字段 | `PUT /api/v1/opportunities/{id}` |
| `huacai.opportunity.follow.create` | 新增跟进记录 | `POST /api/v1/opportunities/follow-records` |
| `huacai.opportunity.follow.update` | 更新跟进记录 | `PUT /api/v1/opportunities/follow-records/{id}` |

## 12. 字段取值约束

### 12.1 `stageCode`

- `NEW`
- `CONTACTED`
- `QUALIFIED`
- `PROPOSAL`
- `NEGOTIATION`
- `CLOSED_WON`
- `CLOSED_LOST`

### 12.2 `priorityLevel`

- `HIGH`
- `MEDIUM`
- `LOW`

### 12.3 `intentLevel`

- `HIGH`
- `MEDIUM`
- `LOW`

### 12.4 `status`

- `OPEN`
- `CLOSED`

## 13. 错误码与错误信息

当前实现延续全局异常风格，不单独定义数字业务码，统一使用：

- HTTP `400`
  - `客户不存在`
  - `确认单不存在`
  - `未检测到有效变更`
  - `stageCode取值非法`
  - `priorityLevel取值非法`
  - `intentLevel取值非法`
  - `status取值非法`
  - `该确认单状态不可执行，请重新发起`
- HTTP `403`
  - `没有访问权限`
- HTTP `500`
  - `系统异常，请稍后重试`

## 14. 审计落库说明

所有 draft / confirm 写流程都会写 `agent_action_log`。

关键字段：

- `trace_id`
- `scene_key`
- `intent_key`
- `tool_name`
- `feishu_user_id`
- `feishu_display_name`
- `target_customer_id`
- `target_opportunity_id`
- `target_follow_record_id`
- `request_text`
- `tool_input_json`
- `before_snapshot_json`
- `confirmation_id`
- `confirmation_json`
- `confirmed_flag`
- `after_snapshot_json`
- `result_code`
- `result_message`
- `error_message`

## 15. 当前已知限制

- 商机详情中的 `ownerUserName` 当前未补真实值。
- 跟进查询当前不支持 `keyword` 检索。
- 新建商机、新增跟进的底层 REST 不直接返回新建 ID，assistant 层通过“查最新一条”补齐，适合作为 P0 方案，但不是强一致 ID 回传。
- 当前确认执行接口未使用 `idempotencyKey` 做二次防重校验，P0 只保留该字段并依赖 `confirmationId + 状态` 做幂等保护。

## 16. 推荐调用顺序

### 16.1 只读问答

1. `POST /api/v1/assistant/opportunities/search`
2. `POST /api/v1/assistant/opportunities/detail`
3. `POST /api/v1/assistant/opportunities/follow-records/search`

### 16.2 写商机字段

1. `POST /api/v1/assistant/opportunities/update-fields/draft`
2. 飞书展示确认卡
3. `POST /api/v1/assistant/actions/confirm`

### 16.3 写跟进记录

1. `POST /api/v1/assistant/opportunities/follow-records/create/draft`
2. 飞书展示确认卡
3. `POST /api/v1/assistant/actions/confirm`

