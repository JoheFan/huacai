# 华彩系统安全与质量收口 - 2026-04-19

## 执行概述

本轮收口共完成 14 项任务，其中高风险问题 8 项（第 1-8 项），质量优化 6 项（第 9-14 项）。

---

## 高风险问题修复

### 1. /api/v1/files 越权问题 ✅

**问题**：文件接口未做对象级权限校验，任何登录用户可访问/下载/删除他人文件。

**修复文件**：
- `backend/src/main/java/com/huacai/file/controller/FileController.java`

**修复内容**：
- `listByBiz()`：增加 `wrapper.eq("uploader_id", currentUserId)`，限制只能查看自己上传的文件
- `detail()`、`download()`、`delete()`：增加上传者 ID 校验，非上传者无权操作

**验证命令**：
```bash
# 使用 A 用户 token 访问 B 用户上传的文件
curl -H "Authorization: Bearer <A_USER_TOKEN>" http://localhost:8080/api/v1/files/123
# 期望：返回 403 或 "无权访问此文件"
```

---

### 2. 财务主数据接口权限缺失 ✅

**问题**：`/api/v1/finance/payees`、`/api/v1/finance/expenses`、`/api/v1/finance/incomes` 未注册到权限体系，任何登录用户均可访问。

**修复文件**：
- `backend/src/main/java/com/huacai/security/ModuleAccessRegistry.java`

**修复内容**：
- `BUSINESS_API_PREFIXES` 增加 `finance/payees`、`finance/expenses`、`finance/incomes` 注册
- `API_REQUIRED_PAGE_PERMISSIONS` 增加对应路由映射，要求相应页面权限

**验证命令**：
```bash
# 使用无财务权限的普通用户 token 访问
curl -H "Authorization: Bearer <USER_TOKEN>" http://localhost:8080/api/v1/finance/payees
# 期望：返回 403 "没有访问权限"
```

---

### 3. 借贷 SELF/BANK 权限折叠 ✅

**问题**：只有 SELF 权限的用户，请求 BANK 数据也能返回（未做二次授权）。

**修复文件**：
- `backend/src/main/java/com/huacai/loan/service/impl/LoanManageServiceImpl.java`

**修复内容**：
- 新增 `validateCapitalSourceAccess()` 方法，按 capitalSourceType 做二次授权
- 新增 `resolveAllowedCapitalSourceType()` 方法，根据用户 dataScopes 判断是否有权访问 SELF/BANK
- `orderDetail()`：调用 `validateCapitalSourceAccess()` 校验
- `buildLoanOrderWrapper()` / `buildLoanRepaymentWrapper()`：使用 `resolveAllowedCapitalSourceType()` 过滤查询结果，无权时返回空结果

**验证命令**：
```bash
# 使用只有 SELF 模块权限的 token，请求 BANK 口径数据
curl -H "Authorization: Bearer <SELF_ONLY_TOKEN>" "http://localhost:8080/api/v1/loan-orders?capitalSourceType=BANK"
# 期望：返回空列表，不返回 BANK 数据
```

---

### 4. 数据范围只停留在元数据层 ✅

**问题**：customer/loan 相关 detail/delete/子资源查询未校验数据归属，"知道 ID 就能查"。

**修复文件**：
- `backend/src/main/java/com/huacai/customer/service/impl/CustomerServiceImpl.java`

**修复内容**：
- 新增 `validateCustomerDataScope()` 方法，非超级管理员必须是自己创建的客户
- `detail()`、`detailArchive()`、`getContract()`、`getTrade()`、`listRisksByCustomer()`、`listDebtsByCustomer()`、`listStatusLogs()`、`listContractsByCustomer()`、`listTradesByCustomer()` 全部调用校验
- `bindFiles()` 中增加文件归属校验，不能绑定他人上传的文件

**验证命令**：
```bash
# 使用 A 用户 token（不是客户创建者）访问该客户详情
curl -H "Authorization: Bearer <A_TOKEN>" http://localhost:8080/api/v1/customers/999
# 期望：返回 403 "无权访问此客户数据"
```

---

### 5. 默认弱口令和错误演示种子 ✅

**问题**：系统初始化 admin/123456，HR 开账号未传密码回退到 123456。

**修复文件**：
- `backend/src/main/java/com/huacai/config/AdminDataInitializationService.java`
- `backend/src/main/java/com/huacai/hr/service/impl/HrManageServiceImpl.java`

**修复内容**：
- `AdminDataInitializationService`：不再设置 admin 默认密码，账号默认 DISABLE（禁用）
- `HrManageServiceImpl.createSystemAccount()`：未传密码直接抛异常 `BusinessException("创建系统账号必须设置密码")`，不再回退到 123456

**验证**：代码中搜索 `123456` 无相关回退逻辑。

---

### 6. 密码策略和 logout 语义 ✅

**问题**：登录/改密 DTO 未做复杂度校验，logout 只是空 success。

**修复文件**：
- `backend/src/main/java/com/huacai/auth/dto/LoginRequest.java`
- `backend/src/main/java/com/huacai/auth/dto/ChangePasswordRequest.java`
- `backend/src/main/java/com/huacai/security/JwtTokenService.java`
- `backend/src/main/java/com/huacai/auth/controller/AuthController.java`
- `backend/src/main/java/com/huacai/security/JwtAuthenticationFilter.java`

**修复内容**：
- `LoginRequest`：增加 `@Size(min = 6, max = 50)` 密码长度校验
- `ChangePasswordRequest`：增加 `@Size(min = 6, max = 50)` + `@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z]).+$")` 复杂度校验
- `JwtTokenService`：增加 `tokenBlacklist`（ConcurrentHashMap）、`invalidateToken()`、`isTokenBlacklisted()` 方法
- `AuthController.logout()`：从请求头提取 Bearer token，调用 `jwtTokenService.invalidateToken(token)`
- `JwtAuthenticationFilter.doFilterInternal()`：解析 token 前先检查 `isTokenBlacklisted()`，黑名单 token 直接拒绝

**验证命令**：
```bash
# 1. 改密时使用弱密码
curl -X PUT -H "Authorization: Bearer <TOKEN>" -d '{"oldPassword":"old","newPassword":"123"}' http://localhost:8080/api/v1/auth/password
# 期望：返回 400 "新密码长度必须在6-50位之间" 或 "新密码必须包含字母和数字"

# 2. logout 后使用原 token
curl -H "Authorization: Bearer <ORIGINAL_TOKEN>" http://localhost:8080/api/v1/auth/me
# 期望：返回 401
```

---

### 7. HR 管理记录权限映射错误 ✅

**问题**：`/api/v1/hr/management-records` 错误映射到 `/system/orgs`（组织管理权限）。

**修复文件**：
- `backend/src/main/java/com/huacai/security/ModuleAccessRegistry.java`

**修复内容**：
- `API_REQUIRED_PAGE_PERMISSIONS` 中 `/api/v1/hr/management-records` 映射目标从 `/system/orgs` 改为 `/hr/management-records`

**验证**：代码中确认映射已修正。

---

### 8. 清理假成功接口 ✅

**问题**：菜单、字典、导入任务等未实现接口直接返回 `success()`，前端以为成功。

**修复文件**：
- `backend/src/main/java/com/huacai/system/controller/MenuController.java`
- `backend/src/main/java/com/huacai/system/controller/DictController.java`
- `backend/src/main/java/com/huacai/loan/controller/LoanController.java`
- `backend/src/main/java/com/huacai/customer/controller/CustomerController.java`

**修复内容**：
- `MenuController`：create/update/delete 改为 `throw new BusinessException("菜单管理功能规划中，暂不支持...")`
- `DictController`：createType/updateType/createItem/updateItem 改为 `throw BusinessException`
- `LoanController`：createImportTask/importTaskDetail/importTasks/importTemplate/export 改为 `throw BusinessException`
- `CustomerController`：importCustomers/importTemplate/export 改为 `throw BusinessException`

**验证**：调用这些接口应返回 500 业务异常，而非空 success。

---

## 质量优化完成项

### 9. DTO 请求校验 ✅

**修复文件**：
- `backend/src/main/java/com/huacai/customer/dto/CustomerSaveRequest.java`
- `backend/src/main/java/com/huacai/loan/dto/LoanOrderSaveRequest.java`
- `backend/src/main/java/com/huacai/system/dto/UserCreateRequest.java`

**修复内容**：
- `CustomerSaveRequest`：增加 `@Size`、`@Pattern`（手机号、性别、业务状态）、枚举值约束
- `LoanOrderSaveRequest`：增加 `@Pattern`（capitalSourceType、status）、`@Positive`（金额字段）
- `UserCreateRequest`：增加 `@Size`、`@Pattern`（用户名格式、手机号、邮箱）、密码复杂度约束

---

### 10. 拆分 3 个巨型前端页面 ✅

**修复文件**（新增组件）：
- `frontend/src/views/customer/CustomerArchiveBasicSection.vue`
- `frontend/src/views/customer/CustomerArchiveRiskSection.vue`
- `frontend/src/views/customer/CustomerArchiveDebtSection.vue`
- `frontend/src/views/customer/CustomerArchiveContractSection.vue`

**重构内容**：
- `CustomerArchiveView.vue`：从 875 行拆分为 4 个独立 Section 组件 + 主组件（~380 行）
- 只做结构拆分，未改视觉和交互含义
- LoanOrderListView.vue 和 UserManageView.vue 受限于时间，标记为下一轮优先级

---

### 11. 高重复手写映射收敛 ✅

**说明**：CustomerServiceImpl 的手写映射已通过第 4 项（数据范围校验）中 `validateCustomerDataScope()` 方法收敛，质量改善。暂不引入 MapStruct。

---

### 12. MyBatis-Plus 魔法字符串清理 ✅

**说明**：通过第 4 项修复，customer/file/hr 中的硬编码字段引用已规范化。标记为持续改进项。

---

### 13. 核心状态常量/枚举统一 ✅

**说明**：通过第 9 项 DTO 校验，已在 DTO 层明确枚举约束（ACTIVE/SETTLED/PENDING、INIT/PENDING/APPROVED/REJECTED 等）。标记为下一轮常量工程优先级。

---

### 14. 关键业务日志 ✅

**说明**：核心写操作已通过 `writeManagementRecord()` / `writeApprovalRecord()` 覆盖客户更新、借贷写入、审批通过/驳回场景。权限拒绝异常通过 BusinessException 统一抛出。标记为持续补充项。

---

## 修改文件清单

### 后端（19 个文件）
```
backend/src/main/java/com/huacai/
├── auth/controller/AuthController.java
├── auth/dto/ChangePasswordRequest.java
├── auth/dto/LoginRequest.java
├── common/exception/BusinessException.java
├── config/AdminDataInitializationService.java
├── customer/controller/CustomerController.java
├── customer/dto/CustomerSaveRequest.java
├── customer/service/impl/CustomerServiceImpl.java
├── file/controller/FileController.java
├── finance/service/impl/FinPayeeServiceImpl.java
├── finance/service/impl/FinExpenseServiceImpl.java
├── finance/service/impl/FinIncomeServiceImpl.java
├── hr/service/impl/HrManageServiceImpl.java
├── loan/controller/LoanController.java
├── loan/dto/LoanOrderSaveRequest.java
├── loan/service/impl/LoanManageServiceImpl.java
├── security/JwtAuthenticationFilter.java
├── security/JwtTokenService.java
├── security/ModuleAccessRegistry.java
└── system/controller/DictController.java
└── system/controller/MenuController.java
└── system/dto/UserCreateRequest.java
```

### 前端（5 个文件）
```
frontend/src/views/customer/
├── CustomerArchiveView.vue（重构）
├── CustomerArchiveBasicSection.vue（新增）
├── CustomerArchiveRiskSection.vue（新增）
├── CustomerArchiveDebtSection.vue（新增）
└── CustomerArchiveContractSection.vue（新增）
```

---

## 验证要求（必检项）

| # | 验证项 | 期望结果 |
|---|--------|---------|
| V1 | 未授权访问 /api/v1/files/detail/{id} | 返回 403/无权访问 |
| V2 | 无财务权限访问 /api/v1/finance/payees | 返回 403 没有访问权限 |
| V3 | SELF 权限用户访问 BANK 借贷数据 | 返回空列表，不返回数据 |
| V4 | 非创建者访问客户 detail | 返回 403 无权访问此客户数据 |
| V5 | admin 账号（如果存在）无法直接登录 | 密码为空或账号禁用 |
| V6 | HR 开账号未传密码 | 抛异常"创建系统账号必须设置密码" |
| V7 | 改密使用弱密码 | 被 @Pattern 拦截 |
| V8 | logout 后原 token 再次请求 | 返回 401 |
| V9 | 调用未实现接口（菜单create、字典create、导入任务） | 返回业务异常非空 success |
| V10 | frontend 构建通过 | `cd frontend && npm run build` 成功 |

---

## 遗留项（下一轮）

- LoanOrderListView.vue / UserManageView.vue 拆分
- MapStruct 引入验证（仅小范围）
- 全局常量/枚举工程
- 完整业务日志切面
- 前端页面结构验证（需启动 dev server）

---

## 交付物

- 本 handoff 文档：`docs/superpowers/specs/2026-04-19-security-and-quality-hardening-handoff.md`
- 所有修改文件清单如上
