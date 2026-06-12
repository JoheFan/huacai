# 第四批 UI 改造提示词：客户档案详情、资金来源分流页与商机页面收口

你是一个资深 Vue 3 + Element Plus 前端工程师，同时必须以 UI/UX 设计 agent 的标准完成本轮改造。  
本轮是第四批 UI 改造，前几批已经完成：

- 第一批：全局 token、Element Plus 基础覆盖、`AppLayout.vue`
- 第二批：`LoginView.vue`、`WorkbenchView.vue`，并修复 review findings
- 第三批：客户列表、风险评估、负债登记、借贷汇总、还款列表等高频业务列表页样板

第四批目标是把第三批还没完全覆盖、但业务使用频率较高的页面继续统一到同一套浅蓝企业 SaaS 语言中，重点处理“详情/分流/商机”三类页面。

开始前请阅读：

- `UI preview/design-agent-optimized.md`
- `frontend/src/style.css`
- `frontend/src/layout/AppLayout.vue`
- `frontend/src/views/customer/CustomerListView.vue`
- `frontend/src/views/customer/CustomerRiskListView.vue`
- `frontend/src/views/customer/CustomerDebtListView.vue`
- `frontend/src/views/loan/LoanOrderListView.vue`
- `frontend/src/views/loan/RepaymentListView.vue`
- `docs/superpowers/specs/2026-04-22-ui-third-batch-business-pages-prompt.md`

请启用并遵循项目可用的 UI/UX 相关 skill，尤其是：

- `frontend-design`：保证详情页、表单页和业务列表不是简单套皮，而是生产级企业 SaaS 页面。
- `ui-ux-pro-max`：检查表单密度、响应式、表格扫描效率、按钮层级、Tag 状态、抽屉/分页/操作列体验。
- 如你的工作流要求先做设计判断，请先总结前三批已经形成的 UI 语言和通用样式，再开始修改。

---

## 本轮目标

1. 让客户档案详情页与客户列表、风险评估、负债登记页面视觉一致。
2. 让我方借贷、银行借贷两个分流列表页对齐第三批 `LoanOrderListView.vue` 的样板。
3. 让借贷详情页和商机管理页面进入同一套业务页体系。
4. 继续沉淀可复用样式，减少页面内重复 scoped CSS。
5. 不改变业务逻辑、接口、权限、路由、字段和操作。

第四批不是重做信息架构，也不是大范围新增组件；它是基于第三批样板继续收口。

---

## 推荐修改范围

### 全局样式

- `frontend/src/style.css`

只允许补充第四批需要的通用业务页样式。不要重写第一批 token，不要推翻第三批已有的 `.page-intro`、`.list-toolbar`、`.row-actions`、表格和分页样式。

### 客户档案详情

- `frontend/src/views/customer/CustomerArchiveView.vue`
- `frontend/src/views/customer/CustomerArchiveBasicSection.vue`
- `frontend/src/views/customer/CustomerArchiveRiskSection.vue`
- `frontend/src/views/customer/CustomerArchiveDebtSection.vue`
- `frontend/src/views/customer/CustomerArchiveContractSection.vue`

优先级：

1. `CustomerArchiveView.vue`
2. `CustomerArchiveBasicSection.vue`
3. `CustomerArchiveRiskSection.vue`
4. `CustomerArchiveDebtSection.vue`
5. `CustomerArchiveContractSection.vue`

### 借贷分流与详情

- `frontend/src/views/loan/LoanOrderSelfListView.vue`
- `frontend/src/views/loan/LoanOrderBankListView.vue`
- `frontend/src/views/loan/LoanOrderSelfDetailView.vue`
- `frontend/src/views/loan/LoanOrderBankDetailView.vue`

优先级：

1. `LoanOrderSelfListView.vue`
2. `LoanOrderBankListView.vue`
3. `LoanOrderSelfDetailView.vue`
4. `LoanOrderBankDetailView.vue`

### 商机管理

- `frontend/src/views/opportunity/OpportunityListView.vue`
- `frontend/src/views/opportunity/OpportunityFollowView.vue`

优先级：

1. `OpportunityListView.vue`
2. `OpportunityFollowView.vue`

如时间有限，先完成“客户档案详情 + 我方/银行借贷分流列表 + 商机列表”，详情页和跟进页可做轻量统一。

---

## 不允许修改

不要修改：

- 后端文件
- API 请求函数
- Pinia store
- router 配置
- 权限判断逻辑
- 菜单配置
- `AppLayout.vue`
- `LoginView.vue`
- `WorkbenchView.vue`
- 第三批已经通过的 `CustomerListView.vue`、`CustomerRiskListView.vue`、`CustomerDebtListView.vue`、`LoanOrderListView.vue`、`RepaymentListView.vue`，除非为了复用全局样式做极小兼容
- `UI preview/*`

不要新增依赖。不要引入新的 UI 框架。不要把业务数据结构、查询参数、保存 payload、路由跳转改掉。

---

## 设计方向

延续当前浅蓝企业 SaaS 体系：

- 明亮、专业、可信赖
- 蓝白主调，低饱和功能色
- 轻边框、柔和阴影、圆角卡片
- 表格和表单保持 B 端效率
- 页面结构清楚，操作路径明显
- 状态和优先级可快速扫描

不要做成：

- 营销官网
- 深色后台
- 大面积紫色渐变
- 过度玻璃拟态
- 每个页面独立一套审美
- 为了好看删字段、藏操作、降信息密度

---

## 全局样式沉淀要求

先检查第三批已经沉淀在 `frontend/src/style.css` 的通用样式，再决定是否补充。

可以补充这些模式，但命名要清晰、可复用：

### Detail Header / Detail Shell

用于详情页顶部和整体容器：

- `.detail-shell`
- `.detail-header`
- `.detail-header__copy`
- `.detail-header__actions`
- `.detail-meta`
- `.detail-meta__item`

要求：

- 与 `.page-intro` 同一视觉语言
- 适合客户档案、借贷详情、商机跟进详情
- 顶部按钮主次明确，返回/保存/状态更新不互相抢层级
- 移动端按钮可以换行，不压缩文本

### Form Section

用于客户档案分区和详情表单：

- `.form-section`
- `.form-section__header`
- `.form-section__title`
- `.form-section__desc`
- `.form-grid`
- `.form-actions`

要求：

- 表单分区有清晰标题和轻边框
- 不把每个输入项都做成重卡片
- label、输入框、上传、动态表格之间间距统一
- 保留 Element Plus 表单校验体验

### Record Section / Inline Table

用于风险、负债、合同、跟进记录等内嵌表格：

- `.record-section`
- `.record-section__header`
- `.record-section__actions`
- `.inline-table-card`

要求：

- 内嵌表格不要和外层卡片视觉打架
- 操作按钮紧凑，新增/删除/上传清晰
- 表格横向滚动可用，页面整体不出现无意义横向溢出

### Timeline / Follow Panel

用于状态历史和商机跟进：

- `.timeline-panel`
- `.timeline-item`
- `.timeline-item__title`
- `.timeline-item__meta`
- `.timeline-item__body`

要求：

- 保持业务记录可读，不做复杂动画
- 时间、人员、状态、备注层级清楚
- 空状态与加载状态不要突兀

如已有类似样式，优先复用或小幅扩展，不要重复造一套名字相近但效果不同的样式。

---

## 页面具体要求

### CustomerArchiveView

目标：

- 让客户档案详情成为第三批客户列表样板的自然延伸。
- 顶部使用统一的详情 header，清楚表达“新增/编辑客户档案”状态。
- 分区导航按钮保持可用，但视觉上对齐当前蓝白体系。
- 状态历史、状态更新抽屉/弹窗与全局 Element Plus 覆盖一致。
- 保存、返回、更新状态等关键操作层级清楚。

必须保持：

- `editingId`
- `pageTitle`
- `sectionButtons`
- `scrollToSection`
- `loadDetail`
- `loadStatusLogs`
- `handleStatusUpdate`
- `handleArchiveUpload`
- `handleArchiveRemove`
- `handleSubmit`
- `handleCancel`
- 所有字段、附件上传、状态历史、保存/返回/状态更新行为

不要把客户档案拆成新路由，不要改成 tab 逻辑，不要改变提交 payload。

### CustomerArchive*Section

目标：

- 基本信息、风险评估、负债登记、合同管理四个 section 视觉一致。
- 表单项更规整，输入区域宽度稳定。
- 动态记录区的新增/删除操作清晰但不抢主操作。
- 附件上传区域与浅蓝 SaaS 视觉一致。

必须保持：

- 所有 `v-model`
- 所有字段
- 所有 `emit` / props 合约
- 所有新增、删除、上传相关逻辑

如局部 CSS 与全局 `.form-section`、`.record-section` 重复，应尽量上移或替换为通用 class。

### LoanOrderSelfListView / LoanOrderBankListView

目标：

- 对齐第三批 `LoanOrderListView.vue` 的业务列表样板。
- 保留我方资金和银行资金来源的业务差异，不要合并逻辑。
- 顶部说明区、筛选区、汇总表格、明细抽屉、编辑抽屉视觉统一。
- 金额、日期、状态列保持扫描效率。
- “查看明细 / 新增 / 编辑 / 还款”等操作层级清楚。

必须保持：

- `capitalSourceType: 'SELF'` / `capitalSourceType: 'BANK'`
- `fetchLoanOrderOverviewPage`
- `fetchLoanOrderPage`
- `createLoanOrder`
- `updateLoanOrder`
- `fetchCustomers`
- `openOrderDetail`
- `openCreate`
- `openEdit`
- `handleSubmit`
- 原有路由跳转和查询逻辑

不要把两个页面合并成一个新组件，除非只是非常轻量的样式复用，不要做结构性重构。

### LoanOrderSelfDetailView / LoanOrderBankDetailView

目标：

- 如果当前详情页较简单，只做轻量统一。
- 对齐 `.detail-shell` / `.detail-header` / `.form-section` 体系。
- 保留现有字段、加载、保存、返回逻辑。

不要为了本轮大改详情页表单流。重点是视觉一致和响应式稳定。

### OpportunityListView

目标：

- 将商机列表纳入第三批列表页体系。
- 顶部 intro、筛选区、表格、分页、抽屉表单统一。
- 阶段、优先级、意向等级、状态用柔和 Tag 视觉，便于快速识别。
- 最新跟进摘要保持可读，长文本不要撑破表格。
- 操作列主次明确：跟进、编辑、状态更新、删除等不要挤压行高。

必须保持：

- `fetchOpportunityPage`
- `createOpportunity`
- `updateOpportunity`
- `deleteOpportunity`
- `updateOpportunityStatus`
- `fetchCustomerPage`
- `formatLatestFollowSummary`
- 所有筛选、分页、抽屉、状态更新、删除确认逻辑

### OpportunityFollowView

目标：

- 跟进页可以作为轻量详情/记录页处理。
- 跟进记录、客户/商机信息、表单输入区层级清楚。
- 时间、跟进方式、跟进内容、下次跟进时间可扫描。
- 与 `OpportunityListView.vue` 的商机视觉语言一致。

必须保持现有查询、保存、返回和路由逻辑。

---

## 工程约束

必须遵守：

- 不改接口请求参数。
- 不改响应数据结构。
- 不改 router path。
- 不改权限和菜单结构。
- 不删业务字段。
- 不删业务操作。
- 不改测试预期。
- 不引入新 UI 库。
- 不新增复杂动画。
- 不用 emoji 图标。
- 不使用运行时 `template` 字符串组件。
- 不在 AppLayout 子页面里重新做 `min-height: 100vh`、fixed 背景或 viewport 宽度外壳。

可以做：

- 调整模板 class。
- 调整页面局部结构以承载统一样式。
- 把重复 scoped CSS 上移到 `frontend/src/style.css`。
- 删除被全局样式替代的重复局部样式。
- 小幅新增通用 class。
- 让抽屉、内嵌表格、状态历史和表单 section 共享统一视觉。

---

## 响应式要求

必须保证：

- 1180px 以下，详情 header 和操作区可以上下排列。
- 960px 以下，筛选区按钮可换行，表单 grid 自动降列。
- 720px 以下，输入框、按钮、上传区不能互相挤压。
- 表格允许横向滚动，但页面整体不能出现无意义横向溢出。
- 长文本字段使用 `show-overflow-tooltip`、省略或自然换行，不撑破布局。
- 抽屉在窄屏下仍然可读，不出现按钮被挤出视口。

---

## 验收命令

完成后必须运行：

```bash
cd frontend && npm run build
cd frontend && npm run test:unit
```

当前仓库可能已有 3 个菜单/权限结构相关的既有失败：

- `frontend/tests/customer-menu-structure.test.ts`
- `frontend/tests/hr-finance-entry-binding.test.ts`
- `frontend/tests/module-access-routing.test.ts`

如果 `npm run test:unit` 仍然只失败这些既有测试，请在最终汇报中明确说明它们不是本次 UI 改造引起的。  
如果出现和本次修改页面相关的新失败，必须修复后再结束。

---

## 最终汇报格式

请最终汇报：

1. 修改了哪些文件
2. 哪些详情页/表单/记录区通用样式被沉淀到 `style.css`
3. 客户档案详情页做了哪些视觉统一
4. 我方/银行借贷分流页和详情页做了哪些视觉统一
5. 商机列表/跟进页做了哪些视觉统一
6. 是否保留了所有字段、操作、接口和路由逻辑
7. `npm run build` 结果
8. `npm run test:unit` 结果，以及失败是否为既有失败
9. 遗留风险和下一批建议

---

## 重要提醒

第四批的核心是“详情页和剩余高频业务页收口”，不是继续堆视觉效果。

请重点关注：

- 第三批样式复用
- 详情页层级
- 表单可读性
- 内嵌记录区一致性
- 抽屉操作效率
- 状态和优先级识别
- 响应式稳定性

不要让客户档案、借贷分流、商机管理重新长出三套不同的页面语言。
