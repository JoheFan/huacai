# 第四批 UI 改造严格继续修复

你是一个资深 Vue 3 + Element Plus 前端工程师，必须按 UI/UX 设计标准完成本次修复。

> **背景**：第四批已失败两次，问题相同——只改了 `style.css` 和 `CustomerArchiveView.vue`，遗漏了核心目标文件，并遗留了调试文件。本轮必须全部覆盖。

---

## 执行顺序：先检入，再改文件

### 第一步：删除调试残留（必须先完成）

```bash
rm -f frontend/src/test.txt
test -e frontend/src/test.txt && echo "ERROR" || echo "OK: deleted"
```

如果输出不是 `OK`，立即停止并报告。不得跳过此步。

---

### 第二步：阅读样式基础

先读以下文件，理解已建立的浅蓝企业 SaaS 样式体系，**不要另起一套**：

- `docs/superpowers/specs/2026-04-23-ui-fourth-batch-detail-opportunity-pages-prompt.md`
- `docs/superpowers/specs/2026-04-23-ui-fourth-batch-continue-fix-prompt.md`
- `UI preview/design-agent-optimized.md`
- `frontend/src/style.css` ← 已有 `form-section`、`record-section`、`detail-header`、`empty-tip` 等
- `frontend/src/views/customer/CustomerArchiveView.vue`
- `frontend/src/views/loan/LoanOrderListView.vue`
- `frontend/src/views/customer/CustomerListView.vue`

**已有样式体系**：第一批全局 token + Element Plus 覆盖 → 第二批登录/工作台 → 第三批客户/风险/负债/借贷/还款列表 → 第四批初稿 `detail-header`/`form-section`。

启用 `frontend-design` 和 `ui-ux-pro-max` skill。

---

## 允许修改的文件

| 类别 | 文件 |
|------|------|
| 样式 | `frontend/src/style.css` |
| 客户档案 Section | `CustomerArchiveBasicSection.vue`、`CustomerArchiveRiskSection.vue`、`CustomerArchiveDebtSection.vue`、`CustomerArchiveContractSection.vue` |
| 借贷分流列表 | `LoanOrderSelfListView.vue`、`LoanOrderBankListView.vue` |
| 借贷详情页 | `LoanOrderSelfDetailView.vue`、`LoanOrderBankDetailView.vue` |
| 商机页 | `OpportunityListView.vue`、`OpportunityFollowView.vue` |
| 已完成可保留 | `CustomerArchiveView.vue`（除非发现明确问题否则不动） |

**必须删除**：`frontend/src/test.txt`

---

## 不允许修改

- 后端文件、API 请求函数、Pinia store、router 配置、权限/菜单逻辑
- `AppLayout.vue`、`LoginView.vue`、`WorkbenchView.vue`、`UI preview/*`
- 测试文件、package 依赖
- 不新增依赖、不引入新 UI 框架、不改业务字段/接口/payload/权限/路由

---

## 收口任务详情

### 1. 客户档案四个 Section

- 对齐 `CustomerArchiveView.vue` 的 `form-section`、`record-section`、`inline-table-card`、`empty-tip`
- 四个 section（基本信息/风险评估/负债登记/合同管理）视觉一致
- 表单 label 宽度、输入框宽度、动态表格、上传区统一
- **保持**：所有 `v-model`、`props`、`emits`、新增/删除/上传逻辑、所有字段
- **不改**：payload、提交逻辑

### 2. 我方/银行借贷分流列表

- 对齐第三批 `LoanOrderListView.vue` 的 `page-intro`、`list-toolbar`、`row-actions`、表格、分页、抽屉表单
- **保留** `capitalSourceType: 'SELF'` 和 `'BANK'` 差异，不合并
- **保留** `fetchLoanOrderPage`、`createLoanOrder`、`updateLoanOrder`、`openOrderDetail`、`openCreate`、`openEdit`、`handleSubmit`
- 金额/日期/状态字段保持扫描效率
- 明细抽屉和编辑抽屉与 Element Plus 全局覆盖一致

### 3. 商机页面

- `OpportunityListView.vue`：顶部 intro、筛选区、表格、分页、抽屉表单与第三批业务列表对齐
- 阶段/优先级/意向等级/状态使用柔和 Tag
- 最新跟进摘要可读，长文本不撑破表格
- 操作列主次明确，不挤压行高
- `OpportunityFollowView.vue`：轻量详情/记录页，`detail-header`/`timeline-panel`/`form-section` 体系
- **保留** `fetchOpportunityPage`、`createOpportunity`、`updateOpportunity`、`deleteOpportunity`、`updateOpportunityStatus`、`fetchCustomerPage`、`formatLatestFollowSummary`

### 4. 借贷详情页（轻量）

- 对齐 `detail-shell`、`detail-header`、`form-section`
- 保留现有加载、返回、保存、字段展示逻辑
- **不做结构性重构**

---

## 可复用全局样式

`frontend/src/style.css` 已有：`.page-intro`、`.list-toolbar`、`.row-actions`、`.detail-header`、`.form-section`、`.record-section`、`.archive-nav`、`.timeline-panel`、`.timeline-item`、`.empty-tip`

如需补充（少量）：`.detail-shell`、`.detail-meta`、`.inline-table-card`、`.drawer-form`、`.summary-strip`、`.status-tag`

**不要重复造近义 class**。

---

## 禁止事项

- 禁止保留 `frontend/src/test.txt` 或任何调试文件
- 禁止 `.test-class`、`.debug-*`、`.temp-*` 调试样式
- 禁止运行时 `template` 字符串组件
- 禁止在 AppLayout 子页面新增 `min-height: 100vh`、`position: fixed`、`100vw` 外壳
- 禁止删除业务字段和操作按钮
- 禁止改接口/store/router/权限/菜单/测试预期

---

## 自检（完成后必须执行）

```bash
# 1. 验证 test.txt 已删除
test -e frontend/src/test.txt && echo "ERROR" || echo "OK: test.txt deleted"

# 2. 验证 git diff 覆盖至少 6 个第四批目标文件
git diff --name-only -- \
  frontend/src/style.css \
  frontend/src/views/customer/CustomerArchiveView.vue \
  frontend/src/views/customer/CustomerArchiveBasicSection.vue \
  frontend/src/views/customer/CustomerArchiveRiskSection.vue \
  frontend/src/views/customer/CustomerArchiveDebtSection.vue \
  frontend/src/views/customer/CustomerArchiveContractSection.vue \
  frontend/src/views/loan/LoanOrderSelfListView.vue \
  frontend/src/views/loan/LoanOrderBankListView.vue \
  frontend/src/views/loan/LoanOrderSelfDetailView.vue \
  frontend/src/views/loan/LoanOrderBankDetailView.vue \
  frontend/src/views/opportunity/OpportunityListView.vue \
  frontend/src/views/opportunity/OpportunityFollowView.vue

# 3. 搜索调试残留
rg -n 'test-class|debug-|temp-|template:|min-height: 100vh|position: fixed|100vw|workbench-root__bg' \
  frontend/src/views/customer frontend/src/views/loan frontend/src/views/opportunity frontend/src/style.css

# 4. 构建
cd frontend && npm run build

# 5. 测试
cd frontend && npm run test:unit
```

**验收标准**：

| 检查项 | 标准 |
|--------|------|
| `test.txt` | 必须不存在 |
| `git diff --name-only` | 必须覆盖至少 **6 个**第四批目标文件 |
| `rg` 自检 | 不能搜到调试样式或运行时 template |
| `npm run build` | 必须通过 |
| `npm run test:unit` | 失败只允许是既有 3 个（`customer-menu-structure`、`hr-finance-entry-binding`、`module-access-routing`） |

**如果出现本次修改相关的新失败，必须修复后才能结束**。

---

## 最低完成标准

如果无法一次完成全部，至少先完成这 7 项后才能汇报：

1. ✅ 删除 `frontend/src/test.txt`
2. ✅ 客户档案四个 section（`BasicSection`、`RiskSection`、`DebtSection`、`ContractSection`）
3. ✅ `LoanOrderSelfListView.vue`
4. ✅ `LoanOrderBankListView.vue`
5. ✅ `OpportunityListView.vue`
6. ✅ `OpportunityFollowView.vue`
7. ✅ `npm run build` 通过

---

## 最终汇报必须包含

1. ✅ `frontend/src/test.txt` 删除验证输出
2. ✅ `git diff --name-only` 结果
3. ✅ 实际覆盖了几个第四批目标文件，列出文件名
4. ✅ 客户档案四个 section 分别做了什么统一
5. ✅ 借贷分流页做了什么统一
6. ✅ 商机列表/跟进页做了什么统一
7. ✅ 借贷详情页是否处理（如未处理说明原因）
8. ✅ 是否保留所有字段、操作、接口、路由、权限逻辑
9. ✅ `rg` 自检结果
10. ✅ `npm run build` 结果
11. ✅ `npm run test:unit` 结果（仅既有 3 个失败？）

---

## 强制结束条件

以下任一情况均不得结束：

- `frontend/src/test.txt` 仍存在
- `git diff --name-only` 覆盖少于 6 个第四批目标文件
- 只改了 `style.css` 和 `CustomerArchiveView.vue` 就想结束
