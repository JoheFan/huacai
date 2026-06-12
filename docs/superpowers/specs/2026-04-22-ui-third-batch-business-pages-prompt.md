# 第三批 UI 改造提示词：高频业务页样式抽象与客户/借贷样板页

你是一个资深 Vue 3 + Element Plus 前端工程师，同时需要以 UI/UX 设计 agent 的标准完成这次改造。

本任务是第三批 UI 改造。第一批已经完成全局 token、Element Plus 基础覆盖和 AppLayout；第二批已经完成 LoginView 和 WorkbenchView，并修复了 review findings。现在要把这套浅蓝企业 SaaS 设计语言落到高频业务页面上，并沉淀可复用的业务页模式。

开始前请阅读：

- `UI preview/design-agent-optimized.md`
- `frontend/src/style.css`
- `frontend/src/layout/AppLayout.vue`
- `frontend/src/views/auth/LoginView.vue`
- `frontend/src/views/dashboard/WorkbenchView.vue`

请启用并遵循项目可用的 UI/UX 相关 skill，尤其是：

- `frontend-design`：保证业务页不是普通后台换色，而是生产级企业 SaaS UI。
- `ui-ux-pro-max`：检查可访问性、响应式、表格密度、按钮/输入框/Tag/卡片/筛选区体验。
- 如工作流要求设计判断，先基于现有 UI 规范和样板页总结方向，再进入修改。

---

## 目标

第三批目标不是全量改完所有业务页面，而是建立“高频业务页可复制样板”：

1. 抽出或统一业务列表页的通用样式。
2. 优先改客户管理相关页面，作为高密度表格页样板。
3. 再改借贷/还款相关页面，验证样式在更复杂业务表格中的表现。
4. 保留业务信息密度，不牺牲 B 端操作效率。
5. 不改接口、store、router、权限逻辑和后端。

---

## 推荐改造范围

优先修改这些文件：

### 全局样式

- `frontend/src/style.css`

只允许补充通用业务页样式，不要重写第一批 token，不要大改 Element Plus 基础覆盖。

### 客户管理高频页

- `frontend/src/views/customer/CustomerListView.vue`
- `frontend/src/views/customer/CustomerRiskListView.vue`
- `frontend/src/views/customer/CustomerDebtListView.vue`
- `frontend/src/views/customer/CustomerArchiveView.vue`

如时间有限，优先顺序：

1. `CustomerListView.vue`
2. `CustomerRiskListView.vue`
3. `CustomerDebtListView.vue`
4. `CustomerArchiveView.vue`

### 借贷/还款高频页

- `frontend/src/views/loan/LoanOrderListView.vue`
- `frontend/src/views/loan/LoanOrderSelfListView.vue`
- `frontend/src/views/loan/LoanOrderBankListView.vue`
- `frontend/src/views/loan/RepaymentListView.vue`

如时间有限，优先顺序：

1. `LoanOrderListView.vue`
2. `RepaymentListView.vue`
3. `LoanOrderSelfListView.vue`
4. `LoanOrderBankListView.vue`

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
- `UI preview/*`

除非构建必须，不要新增依赖。

---

## 设计方向

所有业务页必须延续前两批确定的设计语言：

- 浅蓝白背景
- 企业级、专业、克制
- 大圆角但不幼稚
- 轻边框、柔和阴影
- 表格通透但仍高效
- 筛选区清晰、紧凑、易操作
- 操作按钮主次明确
- 状态 Tag 柔和、胶囊化、可快速识别

不要做成：

- 营销官网
- 深色后台
- 绿橙旧主题
- 大面积紫色渐变
- 过度玻璃拟态
- 表格卡片化过重
- 视觉好看但业务效率下降

---

## 通用样式沉淀要求

请先检查业务页面中重复出现的结构。可以在 `frontend/src/style.css` 中补充或优化这些通用模式：

### Page Intro

用于页面顶部说明区：

- `.page-intro`
- `.page-intro__copy`
- `.page-intro__eyebrow`
- `.page-intro__actions`

要求：

- 白底或浅蓝白卡片
- 轻边框
- 22px-26px 圆角
- 标题、说明、操作区对齐
- 移动端可上下堆叠

### List Toolbar / Filter Panel

用于筛选区：

- `.list-toolbar`
- `.list-toolbar__filters`
- `.list-toolbar__actions`
- 可新增 `.filter-panel` 或 `.business-filter`，但不要和 Workbench 特有样式混淆

要求：

- 输入框宽度稳定
- 查询/重置按钮右侧或换行后对齐
- 浅蓝白底
- 轻边框
- 不要过度装饰

### Table Shell

用于表格卡片：

- 可复用 `.card` / `.card__section`
- 可新增 `.table-card`、`.table-wrap`、`.list-pagination`

要求：

- 表格仍允许横向滚动
- 表头浅蓝白
- 行高舒展但不要过大
- 固定操作列清晰
- 分页和总数信息位置统一

### Row Actions

用于表格操作列：

- `.row-actions`
- `.table-actions`
- `.action-dropdown__trigger`

要求：

- 主操作明显
- 更多操作不抢主操作
- 不要把操作按钮撑高表格行

### Status / Priority Tag

可以统一状态 Tag 视觉：

- 成功：浅青底 + 绿字
- 警示：浅橙底 + 橙字
- 风险/驳回：浅粉底 + 红字
- 普通/未开始：浅蓝灰底 + 蓝灰字

优先使用 Element Plus `el-tag` 的全局覆盖，不要每个页面单独写一套硬色块。

---

## 客户管理页面要求

### CustomerListView

目标：

- 保留现有客户字段和操作逻辑。
- 页面顶部介绍区统一成第三批业务页样板。
- 筛选区和表格区更精致。
- 客户名称仍然可以点击进入档案。
- 操作列仍保留详情和更多操作。
- 不删除任何字段。

重点检查：

- 宽表格横向滚动是否正常。
- 固定列是否和新表格样式兼容。
- `el-tag` 状态是否符合浅蓝 SaaS 体系。
- 移动端不会出现页面整体无意义横向溢出。

### CustomerRiskListView / CustomerDebtListView

目标：

- 复用 CustomerListView 的 page intro、filter、table、pagination 模式。
- 不改查询和弹窗逻辑。
- 风险、负债相关状态使用柔和 Tag。
- 表格密度要适合实际业务录入。

### CustomerArchiveView

目标：

- 详情页不是本批主战场，但应和列表页视觉不割裂。
- 只做必要的样式统一，不要大改复杂表单逻辑。
- 保留各分区结构和保存/返回等行为。

---

## 借贷/还款页面要求

### LoanOrderListView

目标：

- 保留现有列表字段、筛选、操作和路由跳转。
- 对齐客户列表页的业务页模式。
- 借贷金额、状态、来源等字段要保持扫描效率。
- 不要为了视觉删减列。

### RepaymentListView

目标：

- 保留还款列表的全局/客户维度逻辑。
- 汇总信息、表格和筛选区层次清楚。
- 金额、日期、状态字段保持可读。

### LoanOrderSelfListView / LoanOrderBankListView

目标：

- 用同一套列表页模式收敛视觉。
- 保留我方/银行资金来源差异。
- 不改现有业务判断。

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

可以做：

- 调整模板 class。
- 调整页面局部结构以更清晰地承载样式。
- 把重复 scoped CSS 上移到全局样式。
- 删除被全局样式替代的重复局部样式。
- 小幅新增通用 class，但要命名清晰，不污染业务逻辑。

---

## 响应式要求

必须保证：

- 1180px 以下，page intro 和操作区可以上下排列。
- 960px 以下，筛选区按钮可换行。
- 720px 以下，操作按钮和输入框不能互相挤压。
- 表格允许横向滚动，但页面整体不能出现无意义横向溢出。
- 长文本字段使用省略或 tooltip，不要撑破表格。

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
2. 哪些通用业务页样式被沉淀到 `style.css`
3. 客户管理页面做了哪些视觉统一
4. 借贷/还款页面做了哪些视觉统一
5. 是否保留了所有业务字段和操作
6. `npm run build` 结果
7. `npm run test:unit` 结果，以及失败是否为既有失败
8. 遗留风险和下一批建议

---

## 重要提醒

第三批的核心不是“继续加装饰”，而是把前两批确定的设计语言变成可复用的业务页模式。

请重点关注：

- 表格效率
- 筛选体验
- 操作层级
- 状态识别
- 页面一致性
- 响应式稳定性

不要让每个业务页面再次长出一套独立审美。
