# 第四批 UI 改造继续修复

你是一个资深 Vue 3 + Element Plus 前端工程师，必须按 UI/UX 设计标准完成本次修复。

**本轮目标**：完成客户档案详情、资金来源分流页与商机页面的收口。

> 注意：第四批没有通过 review，原因是执行范围不完整 + 遗留调试文件。必须严格按范围执行，不要扩散。

---

## 第一步：立即删除调试残留文件

```bash
rm frontend/src/test.txt
```

该文件内容是调试样式（`.test-class { color: red; }`），不属于改造范围，必须先删除。

---

## 第二步：阅读已有样式基础

先阅读以下文件，理解第三/四批已建立的样式体系，再动手：

- `docs/superpowers/specs/2026-04-23-ui-fourth-batch-detail-opportunity-pages-prompt.md`
- `UI preview/design-agent-optimized.md`
- `frontend/src/style.css` ← 已有 `form-section`、`record-section`、`inline-table-card`、`empty-tip`、`detail-header` 等
- `frontend/src/views/customer/CustomerArchiveView.vue`
- `frontend/src/views/loan/LoanOrderListView.vue`
- `frontend/src/views/loan/RepaymentListView.vue`
- `frontend/src/views/customer/CustomerListView.vue`

启用 `frontend-design` 和 `ui-ux-pro-max` skill。**不要另起一套审美**。

---

## 允许修改的文件

| 类别 | 文件 |
|------|------|
| 已有样式 | `frontend/src/style.css` |
| 客户档案 Section | `CustomerArchiveBasicSection.vue`、`CustomerArchiveRiskSection.vue`、`CustomerArchiveDebtSection.vue`、`CustomerArchiveContractSection.vue` |
| 借贷分流列表 | `LoanOrderSelfListView.vue`、`LoanOrderBankListView.vue` |
| 借贷详情页 | `LoanOrderSelfDetailView.vue`、`LoanOrderBankDetailView.vue` |
| 商机页 | `OpportunityListView.vue`、`OpportunityFollowView.vue` |
| 已完成可保留 | `CustomerArchiveView.vue`（除非发现明确问题，否则不动） |

**必须删除**：`frontend/src/test.txt`

---

## 不允许修改

- 后端文件、API 请求函数、Pinia store、router 配置、权限/菜单逻辑
- `AppLayout.vue`、`LoginView.vue`、`WorkbenchView.vue`、`UI preview/*`
- 第三批已通过的客户列表、风险列表、负债列表、借贷汇总、还款列表（除非全局样式兼容需要极小调整）
- 不新增依赖、不引入新 UI 框架、不改业务字段/接口参数/响应结构/payload/路由

---

## 收口任务详情

### 1. 客户档案四个 Section 统一

- 对齐 `CustomerArchiveView.vue` 中已有的 `form-section`、`record-section`、`inline-table-card`、`empty-tip` 等全局样式
- 基本信息、风险评估、负债登记、合同管理四个 section 视觉一致
- 表单 label 宽度、输入框宽度、动态表格、上传区统一
- **不删除字段、不改 v-model、不改 props/emit 合约**
- 动态记录操作（新增/删除/上传）清晰但不抢主操作

### 2. 我方/银行借贷分流页统一

- 对齐第三批 `LoanOrderListView.vue` 的业务列表样板
- 使用统一的 `page-intro`、`list-toolbar`、`row-actions`、表格、分页、抽屉表单
- **保留** `capitalSourceType: 'SELF'` 和 `'BANK'` 的差异，不合并两个页面
- **保留** `fetchLoanOrderOverviewPage`、`fetchLoanOrderPage`、`createLoanOrder`、`updateLoanOrder`、`openOrderDetail`、`openCreate`、`openEdit`、`handleSubmit` 等逻辑
- 金额/日期/状态字段保持扫描效率
- 明细抽屉和编辑抽屉与 Element Plus 全局覆盖一致

### 3. 借贷详情页轻量统一

- 若页面较简单，只做轻量统一
- 对齐 `detail-shell`、`detail-header`、`form-section` 体系
- 保留现有加载、返回、保存、字段展示逻辑，**不做结构性重构**

### 4. 商机页面统一

- `OpportunityListView.vue`：顶部 intro、筛选区、表格、分页、抽屉表单与第三批业务列表对齐
- 阶段/优先级/意向等级/状态使用柔和 Tag 便于快速识别
- 最新跟进摘要保持可读，长文本不撑破表格
- 操作列主次明确，不挤压行高
- `OpportunityFollowView.vue`：轻量详情/记录页，跟进记录、表单输入、时间/备注层级清楚
- **保留** `fetchOpportunityPage`、`createOpportunity`、`updateOpportunity`、`deleteOpportunity`、`updateOpportunityStatus`、`fetchCustomerPage`、`formatLatestFollowSummary` 及所有现有业务逻辑

---

## 可复用的全局样式

已在 `frontend/src/style.css` 定义：

- `detail-header`、`form-section`、`record-section`、`archive-nav`、`timeline-panel`、`timeline-item`、`empty-tip`

如需补充（少量）：`detail-shell`、`detail-meta`、`inline-table-card`、`drawer-form`、`summary-strip`、`status-tag`

**不要重复造与已有样式含义相近的新 class**。

---

## 禁止事项

- 不要新增临时测试文件或 `.test-class`、`.debug-*`、`.temp-*` 调试样式
- 不要使用运行时 `template` 字符串组件
- 不要在 AppLayout 子页面新增 `min-height: 100vh`、`position: fixed` 背景、`100vw` 外壳
- 不要把详情页改成新路由或新业务流程
- 不要删除任何业务字段和操作按钮
- 不要改测试预期

---

## 验收

完成后必须执行：

```bash
cd frontend && npm run build
cd frontend && npm run test:unit
```

仓库已知 3 个既有失败（与本次修改无关）：
- `customer-menu-structure.test.ts`
- `hr-finance-entry-binding.test.ts`
- `module-access-routing.test.ts`

如果测试仅失败这 3 个，说明通过。**如果出现新的相关失败，必须修复**。

---

## 最终汇报必须包含

1. ✅ 是否已删除 `frontend/src/test.txt`
2. ✅ 实际修改了哪些第四批目标文件
3. ✅ 客户档案 section 做了哪些统一
4. ✅ 借贷分流页/详情页做了哪些统一
5. ✅ 商机列表/跟进页做了哪些统一
6. ✅ 是否保留所有字段、操作、接口、路由和权限逻辑
7. ✅ `npm run build` 结果
8. ✅ `npm run test:unit` 结果（仅既有 3 个失败？）
9. ✅ 是否有未完成的第四批范围
