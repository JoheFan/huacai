# 第二批 UI 改造 Review 修复提示词

你是一个资深 Vue 3 + Element Plus 前端工程师。当前任务是修复第二批 UI 改造后的 code review findings。

请严格只修复下面两个问题，不要继续扩展 UI，不要重构业务页面，不要改接口、store、router、权限逻辑，不要改后端，不要改 AppLayout，不要改 UI preview 文件，不要引入新依赖。

## 本次允许修改的文件

1. `frontend/src/views/auth/LoginView.vue`
2. `frontend/src/views/dashboard/WorkbenchView.vue`

## 项目背景

- 前端目录：`frontend`
- 技术栈：Vue 3 + TypeScript + Vite + Element Plus
- 第一批已完成全局 token、Element Plus 基础覆盖和 AppLayout 改造
- 第二批已完成 LoginView 和 WorkbenchView 的视觉改造
- 当前只需要修复 review findings

---

## Finding 1：LoginView 不要用运行时 template 字符串做输入框图标

### 当前问题

在 `frontend/src/views/auth/LoginView.vue` 中，当前代码使用了普通 `<script lang="ts">` 定义运行时 template 字符串组件：

```ts
const UserIcon = {
  template: `<svg ...>...</svg>`,
}

const LockIcon = {
  template: `<svg ...>...</svg>`,
}
```

然后在 Element Plus 输入框中这样使用：

```vue
:prefix-icon="UserIcon"
:prefix-icon="LockIcon"
```

这不合适。虽然 `npm run build` 可能通过，但生产运行不应依赖 Vue runtime template compiler。这类写法可能导致浏览器运行时报 runtime compiler 警告，或者图标无法稳定渲染。

### 修复要求

请改成使用 Element Plus 已编译图标组件：

1. 删除普通 `<script lang="ts">` 中的 `UserIcon` 和 `LockIcon` 定义。
2. 删除整个额外的普通 `<script lang="ts">` 块，如果它只用于定义这两个图标。
3. 在顶部 `<script setup lang="ts">` 中从 `@element-plus/icons-vue` 导入图标组件：

```ts
import { User, Lock } from '@element-plus/icons-vue'
```

4. 修改模板：

```vue
:prefix-icon="User"
:prefix-icon="Lock"
```

5. 不要新增自定义 runtime template 组件。
6. 不要使用字符串模板。
7. 不要改变登录业务逻辑。

### 必须保持不变

以下逻辑和行为必须保持：

- `form.username`
- `form.password`
- `loading`
- `handleSubmit`
- 空账号/密码时 `ElMessage.warning('请输入账号和密码')`
- `authStore.login(form)`
- 登录成功后 `router.push('/dashboard')`
- 登录失败时 `ElMessage.error(...)`
- 登录按钮 loading 状态
- 账号输入框、密码输入框和登录按钮仍然存在

### 期望方向示例

```ts
<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useAuthStore } from '../../stores/auth'

// 原有逻辑保持
</script>
```

```vue
<el-input
  v-model="form.username"
  placeholder="请输入账号"
  size="large"
  :prefix-icon="User"
/>

<el-input
  v-model="form.password"
  type="password"
  show-password
  placeholder="请输入密码"
  size="large"
  :prefix-icon="Lock"
/>
```

---

## Finding 2：WorkbenchView 不要在 AppLayout 内重复做整屏背景和 viewport 高度

### 当前问题

`frontend/src/views/dashboard/WorkbenchView.vue` 是 AppLayout 的子页面，渲染在 AppLayout 的 `<el-main>` 内部。

但当前 WorkbenchView 又自己做了一套整屏页面壳层：

```vue
<div class="workbench-root">
  <div class="workbench-root__bg" aria-hidden="true"></div>
  ...
</div>
```

并且 CSS 中有：

```css
.workbench-root {
  position: relative;
  min-height: 100vh;
  padding: 14px;
  box-sizing: border-box;
}

.workbench-root__bg {
  position: fixed;
  inset: 0;
  z-index: -1;
  background: ...;
  pointer-events: none;
}

.workbench-shell {
  width: min(1800px, calc(100vw - 28px));
  margin: 0 auto;
  min-height: calc(100vh - 28px);
}
```

这会和 AppLayout 自身的背景、header、padding、主内容区域叠加，容易造成：

- 重复背景层
- 默认多一屏高度
- 无意义纵向滚动
- 内容页和 AppLayout 的布局边界不一致

### 修复要求

请让 WorkbenchView 成为 AppLayout 内部的内容页，而不是再创建一套整屏应用壳层。

具体要求：

1. 删除模板里的：

```vue
<div class="workbench-root__bg" aria-hidden="true"></div>
```

2. 删除 `.workbench-root__bg` 相关 CSS。
3. `.workbench-root` 不要使用 `min-height: 100vh`。
4. `.workbench-root` 不要再承担整屏背景职责。
5. `.workbench-shell` 不要使用 `min-height: calc(100vh - 28px)`。
6. `.workbench-shell` 不要使用 `width: min(1800px, calc(100vw - 28px))` 这类基于 viewport 的外层宽度，因为 AppLayout 已经控制主内容区域。
7. 推荐改成：

```css
.workbench-root {
  position: relative;
  min-height: 100%;
  box-sizing: border-box;
}

.workbench-shell {
  width: 100%;
  margin: 0;
}
```

8. 可以保留 `.main-surface` 的卡片化视觉：

- 大圆角
- 浅蓝白背景
- 轻玻璃感
- 浅蓝边框
- 柔和阴影
- 内部 `gap`

9. 工作台的视觉品质不要倒退，但不要再创建 full-screen fixed background。

### 必须保持不变

以下逻辑和业务结构必须保持：

- `fetchWorkbenchOverview`
- `WorkbenchMetric`
- `WorkbenchRecord`
- `WorkbenchTodo`
- `WorkbenchReminder`
- `filters.keyword`
- `loadOverview`
- `handleSearch`
- `resetFilters`
- `goTo`
- `metricCards`
- `focusRows`
- `todoItems`
- `reminderItems`
- `quickEntries` 的 `label` 和 `path`
- 表格字段：
  - 客户名称
  - 记录类型
  - 关联信息
  - 最近日期
  - 当前状态
  - 优先级
  - 操作
- 表格操作 `goTo(row.routePath)`
- 顶部操作按钮：
  - 新增客户
  - 登记借贷
  - 重置视图
- 今日待办、运行提醒、快捷入口模块仍然存在

### 可以小幅调整

只允许为了修复布局问题小幅调整这些样式：

- `.workbench-root`
- `.workbench-shell`
- `.main-surface`
- 相关响应式中的 `.workbench-root`
- 如果删除背景节点后有多余样式，可以清理

不要重写整个 WorkbenchView。

---

## 验收命令

完成后必须运行：

```bash
cd frontend && npm run build
cd frontend && npm run test:unit
```

## 关于 `test:unit`

当前仓库可能已有 3 个菜单/权限结构相关的既有失败：

- `frontend/tests/customer-menu-structure.test.ts`
- `frontend/tests/hr-finance-entry-binding.test.ts`
- `frontend/tests/module-access-routing.test.ts`

如果 `npm run test:unit` 仍然只失败这些既有测试，请在最终汇报中明确说明它们不是本次 LoginView / WorkbenchView 修复引起的。

如果出现任何和 `LoginView.vue`、`WorkbenchView.vue`、Vue 编译、Element Plus 图标、模板渲染相关的新失败，必须修复后再结束。

---

## 最终汇报格式

请最终汇报：

1. 修改了哪些文件
2. LoginView 图标问题如何修复
3. WorkbenchView 整屏背景/viewport 高度问题如何修复
4. `npm run build` 结果
5. `npm run test:unit` 结果
6. 是否还有遗留风险

再次强调：

- 只修复 review findings
- 不要继续扩展 UI
- 不要改业务逻辑
- 不要改 AppLayout
- 不要改 UI preview 文件
- 不要引入新依赖
