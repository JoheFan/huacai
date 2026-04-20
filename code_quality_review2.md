# 华彩系统二期 - 代码质量报告 (Update)

在上一期的重构与治理中，我们已经成功修复了多个高风险技术债（如 `traceId` 动态化、JWT 密钥环境变量注入、前端 401 拦截等）。

针对当前阶段的代码库状态，以下是最新的代码质量评估与改进建议：

## 🔴 高优先级改进项（架构隐患）

### 1. 前端“巨型组件”现象严重

当前前端部分页面组件职责过多，将列表展示、表单录入、状态管理、校验逻辑甚至多个子表格（负债、合同、风险等）全部糅合在一个 `.vue` 文件中，导致代码极其臃肿难维护。

- `CustomerArchiveView.vue` 高达 **875 行**
- `LoanOrderListView.vue` 高达 **645 行**
- `UserManageView.vue` 高达 **602 行**

**风险**：
1. 随着需求增加，组件将很快突破 1500+ 行，陷入完全无法阅读和维护的境地（"千行代码警告"）。
2. Vue 的响应式追踪负担加重，页面性能可能受到影响。
3. 表单片段（如联系人录入、附件上传）无法在其他页面复用。

**建议**：
按功能模块强制拆分子组件。例如把 `CustomerArchiveView.vue` 拆分为：
- `CustomerBasicForm.vue`
- `CustomerRiskTable.vue`
- `CustomerDebtTable.vue`
- `CustomerContractTable.vue`，用 `Provide/Inject` 或统一的表单 Store 维护状态。

---

### 2. 后端 DTO 严重缺乏请求校验（Bean Validation）

虽然 Controller 层加上了 `@Valid`，但实际的 DTO（如 `CustomerSaveRequest`, `CustomerArchiveSaveRequest`）中几乎没有应用 Jakarta Validation。

```java
// 仅 customerName 做了校验，其余重要字段处于“裸奔”状态
public record CustomerSaveRequest(
        String customerNo,
        @NotBlank(message = "客户姓名不能为空")
        String customerName,
        String gender, // 枚举值不合法怎么办？
        String idCard, // 身份证格式不校验？
        String mobile, // 手机号格式不校验？
        // ...
)
```

**风险**：脏数据极易在未校验的情况下落库，导致空指针异常、业务逻辑错误或超长字符引起的数据库截断报错。

**建议**：
全面排查所有 `*Request` 文件，补充 `@NotNull`, `@Size`, `@Pattern(regexp = "手机号/身份证正则")` 等注解。

---

## 🟡 中优先级改进项（代码坏味道）

### 3. 被遗弃的映射库，大量手写 Get/Set 转换

通过分析 `CustomerServiceImpl.java` 发现，系统依然在大量手撕 Entity 到 DTO 以及 DTO 到 Entity 的转换。动辄几百行的 `fillCustomer()`、`fillDebt()`。

```java
private void fillCustomer(CustCustomer customer, CustomerSaveRequest request) {
    customer.setCustomerName(request.customerName().trim());
    customer.setGender(request.gender());
    customer.setIdCard(request.idCard());
    customer.setBirthday(request.birthday());
    // ... 大量冗余胶水代码
}
```

**建议**：引入 `MapStruct` 或 `ModelMapper`，仅需声明一个 `CustomerMapper` 接口，自动在编译期生成转换代码，解放双手并减少人为漏设字段的 Bug。

### 4. MyBatis-Plus “魔法字符串”混用

大部分代码使用了类型安全的 `LambdaQueryWrapper`（如 `CustCustomer::getCustomerName`），但依然能看到散落的魔法字符串拼接查询：

```java
// CustomerServiceImpl.java
.eq("biz_type", bizType)
.in("biz_id", ids)
.orderByDesc("created_at")
```

**风险**：如果后续重构修改了数据库字段（如 `created_at` 变为 `create_time`），这些硬编码的字符串编译器不会报错，直到运行时才会崩盘。

**建议**：全部统一替换为 `SFunction` 风格的 Lambda 语法：`.eq(SysFile::getBizType, bizType)`。

---

## 🟢 低优先级 / 长期改进项

### 5. 缺失的全局常量与枚举定义

代码中大量散落着硬编码的业务状态，如：
- `"INIT", "FOLLOWING", "SIGNED"` (客户业务状态)
- `"PENDING", "APPROVED", "REJECTED"` (审核状态)
- `"CUSTOMER_ARCHIVE", "CUSTOMER_CONTRACT"` (业务附件类型)

这极易导致拼写错误。建议在 `com.huacai.common.enums` 包下统一定义枚举类（Enum）或者全局常量类（Consts），由枚举来约束和维护所有的魔法值。

### 6. 日志占位与调试信息不足

Controller 和 Service 层对关键业务动作缺乏日志记录（如“用户A更新了客户B的档案”）。仅凭 `OperationLog` 拦截器对于复杂链路的排查通常不够，建议在业务 Service 内引入 `log.info()` 进行关键节点的埋点。
