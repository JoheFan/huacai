# 华彩系统后端接口分组与 DTO 草案

## 1. 目标

本草案用于指导后端 `controller / dto / vo / service` 的第一轮落地，确保接口按模块清晰分组，并提前统一请求与响应模型。

适用范围：

- 一期 MVP
- Spring Boot 3
- 模块化单体架构


## 2. 模块分组

建议后端接口按以下分组组织：

- `auth` 认证与登录
- `system` 系统管理
- `file` 文件管理
- `customer` 客户管理
- `opportunity` 商机与跟进
- `loan` 借贷与还款
- `finance` 财务管理


## 3. 通用 DTO/VO

## 3.1 通用分页查询 DTO

类名建议：

- `PageQuery`

字段建议：

- `pageNum`
- `pageSize`
- `keyword`
- `sortField`
- `sortOrder`

## 3.2 通用 ID DTO

类名建议：

- `IdRequest`

字段建议：

- `id`

## 3.3 通用状态更新 DTO

类名建议：

- `StatusUpdateRequest`

字段建议：

- `status`
- `remark`

## 3.4 通用分页返回 VO

类名建议：

- `PageResponse<T>`

字段建议：

- `records`
- `total`
- `pageNum`
- `pageSize`

## 3.5 通用接口返回 VO

类名建议：

- `ApiResponse<T>`

字段建议：

- `code`
- `message`
- `data`
- `traceId`


## 4. Auth 模块

Controller 建议：

- `AuthController`

### 4.1 LoginRequest

字段：

- `username`
- `password`

### 4.2 LoginResponse

字段：

- `token`
- `tokenType`
- `expiresIn`
- `userInfo`

### 4.3 CurrentUserInfoVO

字段：

- `id`
- `username`
- `realName`
- `orgId`
- `orgName`
- `roles`
- `permissions`

### 4.4 ChangePasswordRequest

字段：

- `oldPassword`
- `newPassword`


## 5. System 模块

建议拆分 Controller：

- `OrgController`
- `UserController`
- `RoleController`
- `MenuController`
- `DictController`
- `OperationLogController`

### 5.1 组织管理 DTO

#### OrgCreateRequest

- `parentId`
- `orgName`
- `orgType`
- `sortNo`
- `status`
- `remark`

#### OrgUpdateRequest

- `orgName`
- `orgType`
- `sortNo`
- `status`
- `remark`

#### OrgTreeNodeVO

- `id`
- `parentId`
- `orgName`
- `orgType`
- `status`
- `sortNo`
- `children`

### 5.2 用户管理 DTO

#### UserQueryRequest

- `pageNum`
- `pageSize`
- `keyword`
- `orgId`
- `accountStatus`
- `employmentStatus`

#### UserCreateRequest

- `username`
- `password`
- `employeeCode`
- `realName`
- `phone`
- `email`
- `orgId`
- `jobTitle`
- `employmentStatus`
- `accountStatus`
- `roleIds`
- `remark`

#### UserUpdateRequest

- `realName`
- `phone`
- `email`
- `orgId`
- `jobTitle`
- `employmentStatus`
- `accountStatus`
- `roleIds`
- `remark`

#### UserVO

- `id`
- `username`
- `employeeCode`
- `realName`
- `phone`
- `email`
- `orgId`
- `orgName`
- `jobTitle`
- `employmentStatus`
- `accountStatus`
- `lastLoginAt`
- `roleList`

#### ResetPasswordRequest

- `newPassword`

### 5.3 角色管理 DTO

#### RoleQueryRequest

- `pageNum`
- `pageSize`
- `keyword`
- `status`

#### RoleCreateRequest

- `roleCode`
- `roleName`
- `dataScope`
- `status`
- `remark`

#### RoleUpdateRequest

- `roleName`
- `dataScope`
- `status`
- `remark`

#### RoleMenuAssignRequest

- `menuIds`

#### RoleVO

- `id`
- `roleCode`
- `roleName`
- `dataScope`
- `status`
- `remark`

### 5.4 菜单管理 DTO

#### MenuCreateRequest

- `parentId`
- `menuName`
- `menuType`
- `routePath`
- `componentPath`
- `permissionCode`
- `icon`
- `sortNo`
- `status`
- `remark`

#### MenuUpdateRequest

- `menuName`
- `menuType`
- `routePath`
- `componentPath`
- `permissionCode`
- `icon`
- `sortNo`
- `status`
- `remark`

#### MenuTreeNodeVO

- `id`
- `parentId`
- `menuName`
- `menuType`
- `routePath`
- `componentPath`
- `permissionCode`
- `icon`
- `sortNo`
- `status`
- `children`

#### RouteVO

- `path`
- `name`
- `component`
- `redirect`
- `meta`
- `children`

### 5.5 字典 DTO

#### DictTypeCreateRequest

- `dictCode`
- `dictName`
- `status`
- `remark`

#### DictItemCreateRequest

- `dictTypeId`
- `itemCode`
- `itemName`
- `itemValue`
- `sortNo`
- `status`
- `remark`

#### DictItemVO

- `id`
- `dictTypeId`
- `itemCode`
- `itemName`
- `itemValue`
- `sortNo`
- `status`

### 5.6 日志 VO

#### OperationLogQueryRequest

- `pageNum`
- `pageSize`
- `moduleCode`
- `bizType`
- `userId`
- `startTime`
- `endTime`

#### OperationLogVO

- `id`
- `userId`
- `userName`
- `moduleCode`
- `bizType`
- `requestUri`
- `requestMethod`
- `resultCode`
- `resultMsg`
- `ip`
- `createdAt`


## 6. File 模块

Controller 建议：

- `FileController`

### 6.1 FileUploadRequest

表单字段：

- `file`
- `bizType`
- `bizId`

### 6.2 FileVO

字段：

- `id`
- `bizType`
- `bizId`
- `fileName`
- `filePath`
- `fileExt`
- `fileSize`
- `storageType`
- `uploaderId`
- `createdAt`


## 7. Customer 模块

建议拆分 Controller：

- `CustomerController`
- `CustomerScoreController`
- `CustomerDebtController`
- `CustomerContractController`
- `CustomerTradeController`
- `CustomerStatusLogController`

### 7.1 客户主表 DTO

#### CustomerQueryRequest

- `pageNum`
- `pageSize`
- `keyword`
- `auditStatus`
- `bizStatus`
- `loanStatus`
- `companyName`
- `creditCode`

#### CustomerCreateRequest

- `customerNo`
- `customerName`
- `gender`
- `idCard`
- `birthday`
- `mobile`
- `companyName`
- `creditCode`
- `establishedDate`
- `industry`
- `businessAddress`
- `bankName`
- `bankAccount`
- `recommenderName`
- `recommenderRate`
- `serviceFee`
- `auditStatus`
- `bizStatus`
- `loanStatus`
- `remark`

#### CustomerUpdateRequest

- `customerName`
- `gender`
- `idCard`
- `birthday`
- `mobile`
- `companyName`
- `creditCode`
- `establishedDate`
- `industry`
- `businessAddress`
- `bankName`
- `bankAccount`
- `recommenderName`
- `recommenderRate`
- `serviceFee`
- `auditStatus`
- `bizStatus`
- `loanStatus`
- `remark`

#### CustomerStatusUpdateRequest

- `auditStatus`
- `bizStatus`
- `loanStatus`
- `remark`

#### CustomerVO

- `id`
- `customerNo`
- `customerName`
- `gender`
- `mobile`
- `companyName`
- `creditCode`
- `industry`
- `businessAddress`
- `auditStatus`
- `bizStatus`
- `loanStatus`
- `recommenderName`
- `recommenderRate`
- `serviceFee`
- `createdAt`

#### CustomerDetailVO

- `baseInfo`
- `scoreList`
- `debtList`
- `contractList`
- `tradeList`
- `statusLogList`

### 7.2 客户评分 DTO

#### CustomerScoreCreateRequest

- `testDate`
- `testLimit`
- `trafficValue`
- `compositeScore`
- `thirdPartyScore`
- `scoreResult`
- `remark`

#### CustomerScoreVO

- `id`
- `customerId`
- `testDate`
- `testLimit`
- `trafficValue`
- `compositeScore`
- `thirdPartyScore`
- `scoreResult`
- `remark`

### 7.3 客户负债 DTO

#### CustomerDebtCreateRequest

- `debtType`
- `debtAmount`
- `repaidAmount`
- `pendingAmount`
- `installmentAmount`
- `repaymentDay`
- `remark`

#### CustomerDebtVO

- `id`
- `customerId`
- `debtType`
- `debtAmount`
- `repaidAmount`
- `pendingAmount`
- `installmentAmount`
- `repaymentDay`
- `remark`

### 7.4 客户合同 DTO

#### CustomerContractCreateRequest

- `contractNo`
- `contractName`
- `contractFileId`
- `signDate`
- `remark`

#### CustomerContractVO

- `id`
- `customerId`
- `contractNo`
- `contractName`
- `contractFileId`
- `signDate`
- `remark`

### 7.5 客户交易 DTO

#### CustomerTradeCreateRequest

- `tradeType`
- `amount`
- `serviceFee`
- `actualReceived`
- `returnedAmount`
- `balanceAmount`
- `tradeDate`
- `remark`

#### CustomerTradeVO

- `id`
- `customerId`
- `tradeType`
- `amount`
- `serviceFee`
- `actualReceived`
- `returnedAmount`
- `balanceAmount`
- `tradeDate`
- `remark`

### 7.6 客户状态日志 DTO

#### CustomerStatusLogCreateRequest

- `statusCode`
- `statusName`
- `changedAt`
- `remark`

#### CustomerStatusLogVO

- `id`
- `customerId`
- `statusCode`
- `statusName`
- `changedAt`
- `changedBy`
- `remark`


## 8. Opportunity 模块

建议拆分 Controller：

- `OpportunityController`
- `FollowRecordController`

### 8.1 商机 DTO

#### OpportunityQueryRequest

- `pageNum`
- `pageSize`
- `keyword`
- `customerId`
- `priorityLevel`
- `stageCode`
- `ownerUserId`
- `status`

#### OpportunityCreateRequest

- `customerId`
- `priorityLevel`
- `stageCode`
- `ownerUserId`
- `estimatedAmount`
- `intentLevel`
- `status`
- `nextFollowTime`
- `remark`

#### OpportunityVO

- `id`
- `customerId`
- `customerName`
- `priorityLevel`
- `stageCode`
- `ownerUserId`
- `ownerUserName`
- `estimatedAmount`
- `intentLevel`
- `status`
- `nextFollowTime`
- `remark`

### 8.2 跟进记录 DTO

#### FollowRecordCreateRequest

- `followTime`
- `followerUserId`
- `followContent`
- `nextAction`
- `remark`

#### FollowRecordVO

- `id`
- `opportunityId`
- `customerId`
- `followTime`
- `followerUserId`
- `followerUserName`
- `followContent`
- `nextAction`
- `remark`


## 9. Loan 模块

建议拆分 Controller：

- `LoanOrderController`
- `LoanRepaymentController`
- `LoanImportTaskController`

### 9.1 借贷主单 DTO

#### LoanOrderQueryRequest

- `pageNum`
- `pageSize`
- `keyword`
- `customerId`
- `capitalSourceType`
- `status`
- `loanDateStart`
- `loanDateEnd`

#### LoanOrderCreateRequest

- `customerId`
- `capitalSourceType`
- `bankName`
- `loanDate`
- `depositGoldAmount`
- `creditCardRepaymentAmount`
- `loanAmount`
- `balanceAmount`
- `monthlyInterestAmount`
- `loanCount`
- `status`
- `remark`

#### LoanOrderVO

- `id`
- `customerId`
- `customerName`
- `capitalSourceType`
- `bankName`
- `loanDate`
- `depositGoldAmount`
- `creditCardRepaymentAmount`
- `loanAmount`
- `balanceAmount`
- `monthlyInterestAmount`
- `loanCount`
- `status`
- `remark`

### 9.2 还款 DTO

#### LoanRepaymentCreateRequest

- `repaymentDate`
- `repaymentAmount`
- `principalAmount`
- `interestAmount`
- `repaymentChannel`
- `remark`

#### LoanRepaymentVO

- `id`
- `loanOrderId`
- `customerId`
- `customerName`
- `capitalSourceType`
- `repaymentDate`
- `repaymentAmount`
- `principalAmount`
- `interestAmount`
- `repaymentChannel`
- `remark`

### 9.3 导入任务 DTO

#### LoanImportTaskCreateRequest

- `capitalSourceType`
- `fileId`

#### LoanImportTaskVO

- `id`
- `capitalSourceType`
- `fileId`
- `taskStatus`
- `totalCount`
- `successCount`
- `failCount`
- `resultMessage`
- `createdBy`
- `createdAt`


## 10. Finance 模块

建议拆分 Controller：

- `PayeeController`
- `IncomeController`
- `ExpenseController`

### 10.1 收款方 DTO

#### PayeeQueryRequest

- `pageNum`
- `pageSize`
- `keyword`
- `payeeType`
- `status`

#### PayeeCreateRequest

- `payeeName`
- `payeeType`
- `bankName`
- `bankAccount`
- `contactName`
- `contactPhone`
- `status`
- `remark`

#### PayeeVO

- `id`
- `payeeName`
- `payeeType`
- `bankName`
- `bankAccount`
- `contactName`
- `contactPhone`
- `status`
- `remark`

### 10.2 收入 DTO

#### IncomeQueryRequest

- `pageNum`
- `pageSize`
- `keyword`
- `incomeType`
- `bizDateStart`
- `bizDateEnd`

#### IncomeCreateRequest

- `incomeName`
- `incomeType`
- `bizDate`
- `amount`
- `payerName`
- `payerId`
- `fileId`
- `remark`

#### IncomeVO

- `id`
- `incomeName`
- `incomeType`
- `bizDate`
- `amount`
- `payerName`
- `payerId`
- `fileId`
- `remark`

### 10.3 支出 DTO

#### ExpenseQueryRequest

- `pageNum`
- `pageSize`
- `keyword`
- `expenseType`
- `bizDateStart`
- `bizDateEnd`

#### ExpenseCreateRequest

- `expenseName`
- `expenseType`
- `bizDate`
- `amount`
- `payeeName`
- `payeeId`
- `fileId`
- `remark`

#### ExpenseVO

- `id`
- `expenseName`
- `expenseType`
- `bizDate`
- `amount`
- `payeeName`
- `payeeId`
- `fileId`
- `remark`


## 11. Java 包命名建议

后端建议统一采用：

- `com.huacai.auth.controller`
- `com.huacai.auth.dto`
- `com.huacai.auth.vo`
- `com.huacai.system.controller`
- `com.huacai.system.dto`
- `com.huacai.system.vo`
- `com.huacai.customer.controller`
- `com.huacai.customer.dto`
- `com.huacai.customer.vo`
- `com.huacai.opportunity.controller`
- `com.huacai.opportunity.dto`
- `com.huacai.opportunity.vo`
- `com.huacai.loan.controller`
- `com.huacai.loan.dto`
- `com.huacai.loan.vo`
- `com.huacai.finance.controller`
- `com.huacai.finance.dto`
- `com.huacai.finance.vo`
- `com.huacai.file.controller`
- `com.huacai.file.dto`
- `com.huacai.file.vo`


## 12. 第一批建议先落地的 Java DTO

建议最先创建：

- `LoginRequest`
- `LoginResponse`
- `PageQuery`
- `ApiResponse`
- `CustomerQueryRequest`
- `CustomerCreateRequest`
- `CustomerVO`
- `OpportunityQueryRequest`
- `OpportunityCreateRequest`
- `LoanOrderQueryRequest`
- `LoanOrderCreateRequest`
- `LoanRepaymentCreateRequest`
- `IncomeCreateRequest`
- `ExpenseCreateRequest`
- `PayeeCreateRequest`

这样可以优先把登录、客户、商机、借贷、财务五条主线的 controller 骨架先搭起来。
