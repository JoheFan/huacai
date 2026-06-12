# UI 改版继续修复提示词：只处理当前 4 个 review finding

你是负责前端 UI 改版落地的 agent。请先使用本地 UI 设计相关 skill 辅助判断：

- `$ui-ux-pro-max`
- `$frontend-design`
- 涉及折叠导航交互时，先用 `$brainstorming` 简短确认行为目标后再改代码

本轮只修下面 4 个 review finding。
不要做图标体系重做，不要补商机 page intro，不要做最终全局对齐，不要改业务逻辑。

---

## Finding 1：修复折叠态侧栏导航不可用

文件：

- `frontend/src/layout/AppLayout.vue`

当前问题：

折叠态下：

```css
.sidebar.is-collapsed .nav-group__label {
  display: none;
}

.sidebar.is-collapsed .nav-group__items {
  display: none;
}
```

但分组图标在 `.nav-group__label` 内，所以折叠后分组按钮变成空按钮。
同时子菜单整体隐藏，导致折叠态几乎不能导航。

### 必须修复

1. 折叠态必须保留可见图标导航能力。
2. 不允许空按钮。
3. 当前路由高亮仍必须可识别。
4. 折叠态点击导航仍能跳转。
5. 折叠态可以隐藏文字，但不能隐藏所有导航入口。

### 推荐实现方式

优先采用下面方案之一：

#### 方案 A：折叠态保留 group 图标，并让 group 点击展开/收起

- `.nav-group__label` 不要整体 `display: none`
- 只隐藏 label 内的文字 span
- 保留 `.nav-group__icon`
- 隐藏箭头可以接受
- 子菜单可以用图标列形式显示：
  - `.nav-group__items` 不要 `display: none`
  - 折叠态下移除缩进和竖线
  - 子菜单项只显示 icon，不显示文字
  - 每个 icon-only 链接加 `aria-label` 或 `title`

#### 方案 B：折叠态直接显示所有子菜单 icon

- group header 可以只作为分组 icon
- `.nav-group__items` 保留显示
- `.nav-item__label` 隐藏
- `.nav-item__icon` 居中
- active 状态通过背景色或左侧短线体现

不要使用 hover 弹出大菜单，除非实现非常轻量且不会影响可访问性。

### 验收标准

- 折叠后左侧仍能看到一列可点击导航图标。
- 鼠标悬停或无障碍属性能知道图标对应什么页面，至少要有 `title` 或 `aria-label`。
- 当前页面 active 状态仍明显。
- 折叠/展开不会导致布局闪烁或内容区错位。

---

## Finding 2：修复折叠态覆盖移动端单列布局

文件：

- `frontend/src/layout/AppLayout.vue`

当前问题：

`.shell--collapsed` 位于所有 media query 后：

```css
.shell--collapsed {
  grid-template-columns: 84px 1fr;
}
```

这会覆盖 `@media (max-width: 960px)` 里的单列布局。

### 必须修复

1. 把 `.shell--collapsed { grid-template-columns: 84px 1fr; }` 限制在桌面断点内。
2. 960px 以下必须强制单列布局。
3. 移动端下即使 `sidebarCollapsed === true`，也不能出现 `84px 1fr` 双列。
4. 移动端侧栏可以保持展开或紧凑，但不能压缩主内容为第二列。

### 推荐写法

```css
@media (min-width: 961px) {
  .shell--collapsed {
    grid-template-columns: 84px 1fr;
  }
}

@media (max-width: 960px) {
  .shell,
  .shell.shell--collapsed {
    grid-template-columns: 1fr;
  }
}
```

同时确认其他 `.sidebar.is-collapsed` 样式不会在移动端造成不可用布局；必要时在移动端覆盖：

```css
@media (max-width: 960px) {
  .sidebar.is-collapsed {
    width: auto;
    min-width: 0;
  }
}
```

---

## Finding 3：恢复商机列表备注列

文件：

- `frontend/src/views/opportunity/OpportunityListView.vue`

当前问题：

表格优化时删除了原来的 `remark` 备注列。
本轮要求收紧列宽，但明确禁止删除业务字段。

### 必须修复

1. 恢复 `remark` 备注列。
2. 使用较窄列宽和 tooltip，而不是删除：
   - 建议 `min-width="120"` 或 `min-width="140"`
   - 必须加 `show-overflow-tooltip`
3. 不要扩大操作列来补偿。
4. 不要改接口、数据结构、字段含义。

示例：

```vue
<el-table-column
  prop="remark"
  label="备注"
  min-width="120"
  show-overflow-tooltip
/>
```

---

## Finding 4：补齐高频表格页体验优化覆盖范围

当前问题：

这轮主要只改了：

- `CustomerListView.vue`
- `OpportunityListView.vue`

但任务要求优先处理高频业务列表页：

- `frontend/src/views/customer/CustomerListView.vue`
- `frontend/src/views/customer/CustomerRiskListView.vue`
- `frontend/src/views/customer/CustomerDebtListView.vue`
- `frontend/src/views/loan/LoanOrderListView.vue`
- `frontend/src/views/loan/LoanOrderSelfListView.vue`
- `frontend/src/views/loan/LoanOrderBankListView.vue`
- `frontend/src/views/opportunity/OpportunityListView.vue`

### 必须修复

对上述页面逐个检查，重点处理：

1. 操作列宽度
   - 避免 `width="180"`、`width="220"`、`width="260"` 这种过宽固定操作列。
   - 多操作按钮应收进 `el-dropdown` 或现有 `row-actions` 的“更多”菜单。
   - 常用主操作保留 1 个，其他放更多。
   - 建议操作列：
     - 普通 1-2 个操作：`110px - 148px`
     - 确实需要 3 个以上操作：优先 dropdown，不要硬撑宽度。

2. 长文本列宽
   - 长字段必须用 `show-overflow-tooltip`
   - 合理降低 `min-width`
   - 不要删除业务字段。
   - 不要把所有列都缩到不可读，优先收紧低频辅助字段。

3. 表格容器
   - 高频列表页应统一包裹 `.table-wrap`
   - 避免页面级横向滚动。
   - 横向滚动只能作为兜底。

4. 重点检查这些明显残留：
   - `LoanOrderSelfListView.vue` 主表操作列 `width="180"`
   - `LoanOrderBankListView.vue` 主表操作列 `width="180"`
   - `LoanOrderListView.vue` 主表操作列 `width="180"`
   - `IncrementDetailListView.vue` 如果属于当前高频路径且仍有 `width="220"`，也顺手收紧
   - `OpportunityListView.vue` 操作列 `width="200"` 仍偏宽，尽量通过 dropdown 收到 `140-160`

### 处理原则

- 不要删除任何业务列。
- 不要改业务逻辑。
- 不要改接口。
- 不要为了缩宽而让关键字段不可读。
- 客户名称、状态、金额、时间、操作是优先可见字段。
- 长备注、信用代码、地址、跟进记录等可以 tooltip 截断。

---

## 禁止事项

- 不要处理图标体系重做。
- 不要处理登录页图标。
- 不要处理首页指标卡图标。
- 不要补商机列表 `page-intro`。
- 不要做最终全局对齐。
- 不要新增临时文件，例如 `frontend/src/test.txt`。
- 不要删除任何业务字段。
- 不要改测试断言来绕过问题。

---

## 完成后必须汇报

请按下面格式输出：

1. 修改了哪些文件。
2. 折叠态导航如何保证 icon 可见和可点击。
3. 移动端单列布局如何保证不被 collapsed 覆盖。
4. 商机备注列是否已恢复。
5. 哪些高频表格页完成了列宽/操作列优化。
6. 是否仍有必须保留横向滚动的页面，以及原因。
7. `npm run build` 结果。
8. `npm run test:unit` 结果。
