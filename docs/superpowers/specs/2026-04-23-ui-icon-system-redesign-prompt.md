# UI 改版第 3 项：图标体系一次性重做 Agent 提示词

你是负责前端 UI 改版落地的 agent。  
本轮只做 **图标体系一次性重做**，目标是让登录页、侧边栏导航、首页指标卡和左下角装饰统一对齐 `UI preview/dashboard.html` 与 `UI preview/login.html` 的线性 SVG 风格。

开始前必须先使用本地 UI 设计相关 skill：

- `$ui-ux-pro-max`
- `$frontend-design`
- 如需要先梳理交互/视觉约束，使用 `$brainstorming`

必须先阅读参考文件：

- `UI preview/dashboard.html`
- `UI preview/login.html`
- `UI preview/design-agent-optimized.md`

---

## 本轮目标

覆盖以下范围：

1. 登录页图标
2. 左侧导航栏底部装饰
3. 左侧导航组图标
4. 左侧导航项图标
5. 首页工作台 4 个指标卡图标

本轮不处理：

- 表格列宽
- page intro
- 全局布局
- 权限逻辑
- 路由行为
- 接口/store 行为
- 商机列表结构
- 其他页面视觉对齐

---

## 当前问题

### 1. 登录页 icon 偏离参考稿

当前登录页存在这些问题：

- 品牌 chip 使用星形 SVG，和参考稿的几何 logo 不一致。
- 登录 badge 使用普通锁形 SVG，视觉比较生硬。
- 输入框 icon 使用 Element Plus 默认 `User` / `Lock`，风格和页面左侧插画不统一。
- `UI preview/login.html` 的视觉方向是“轻线性 + 几何底板 + 蓝色企业感”，当前实现更像临时拼装。

### 2. 侧边栏导航 icon 语义重复

当前 `frontend/src/router/menu.ts` 中多个菜单项复用同类 icon：

- 客户相关多个页面都用 `UserFilled`
- 借贷相关多个页面都用 `Money`
- 人事相关多个页面都用 `User`
- 分组图标直接取 `group.items[0]?.icon`

这会导致导航扫视效率低，用户很难靠 icon 区分模块，也和 `UI preview/dashboard.html` 里每个一级模块独立线性 SVG 的方向不一致。

### 3. 首页 4 个指标卡缺少 icon

参考稿中首页指标卡结构是：

- 左上：标题 + 小型线性 icon
- 右侧/右下：轻量业务插画或装饰图形
- 数值区域层级明确

当前工作台 metric card 主要只有文字和数值，没有 icon 锚点，视觉完成度不足。

### 4. 左下角装饰没有形成统一符号语言

当前左下角装饰是 CSS 几何层叠，和参考稿接近，但没有和 icon system 形成同一套线性 SVG 语言。后续应把它当作“品牌插画组件”维护，而不是继续散落在 `AppLayout.vue` 的纯 CSS 装饰里。

---

## 设计方向

### 总原则

采用 `UI preview/dashboard.html` 的 icon 风格作为主参考：

- 线性 SVG
- `stroke="currentColor"`
- `fill="none"`
- `stroke-width` 约 `1.8 - 2`
- `stroke-linecap="round"`
- `stroke-linejoin="round"`
- 图标尺寸统一：
  - 导航组 icon：`20px - 22px`
  - 导航项 icon：`16px - 18px`
  - 指标卡 icon：`18px - 20px`
  - 登录主 badge：`24px - 28px`
- 图标底板统一：
  - 浅蓝渐变或浅蓝透明底
  - 圆角 `12px - 16px`
  - active 状态蓝底白线
  - normal 状态蓝灰线

### 禁止继续做的事

- 不要继续依赖 Element Plus 默认 icon 做业务语义 icon。
- 不要混用 emoji。
- 不要混用 filled icon 和 outline icon。
- 不要每个页面临时散落 SVG。
- 不要把 icon SVG 直接大量塞进业务页面。

---

## 推荐技术方案

### 新增统一 IconSymbol 组件

新增文件：

- `frontend/src/components/IconSymbol.vue`

职责：

- 接收 `name`
- 渲染项目自定义线性 SVG
- 默认使用 `currentColor`
- 统一 `viewBox="0 0 24 24"`
- 支持 `size`
- 不使用运行时 template 字符串

建议 API：

```vue
<IconSymbol name="customer" />
<IconSymbol name="briefcase" :size="20" />
<IconSymbol name="chart" />
```

组件内部维护 icon map。只放本轮实际需要的 icon，不要一次性塞几十个没用到的图标。

建议至少覆盖：

- `logo`
- `customer`
- `risk`
- `debt`
- `briefcase`
- `loan`
- `bankCard`
- `trend`
- `target`
- `chart`
- `calendarChart`
- `performance`
- `personBadge`
- `userCircle`
- `checkCard`
- `salary`
- `coins`
- `income`
- `expense`
- `wallet`
- `approval`
- `gear`
- `shield`
- `org`
- `document`
- `log`
- `lock`
- `mail`
- `calendar`
- `cardMoney`

---

## 文件改造范围

### 必改文件

1. `frontend/src/components/IconSymbol.vue`
   - 新增统一线性 SVG 组件。

2. `frontend/src/layout/AppLayout.vue`
   - 导航组 icon 改用 `IconSymbol`。
   - 导航项 icon 改用 `IconSymbol`。
   - 不再直接 import 大量 Element Plus icons 作为业务导航图标。
   - 折叠态必须继续保留 icon 可见。
   - icon-only 链接必须保留 `title` 或 `aria-label`。
   - 左下角装饰可以轻量抽象为组件，也可以先调整为统一 SVG 语言。

3. `frontend/src/router/menu.ts`
   - 替换重复的 Element Plus icon name。
   - 改成项目语义 icon key。

4. `frontend/src/views/auth/LoginView.vue`
   - 品牌 chip icon 改为 `IconSymbol name="logo"`。
   - 登录 badge 改为 `IconSymbol name="lock"`。
   - 输入框 prefix icon 改为统一 `IconSymbol`，推荐使用 `#prefix` slot。

5. `frontend/src/views/dashboard/WorkbenchView.vue`
   - 首页 4 个 metric card 加 icon。
   - 每张卡根据业务类型映射 icon。
   - 补轻量右下角线性业务插画或统一 decorative icon，不要过度复杂。

### 可选文件

- `frontend/src/components/SidebarArt.vue`
  - 如果重构左下角装饰，建议抽出组件。
  - 如果时间不足，可以先保留在 `AppLayout.vue`，但视觉要和线性 SVG 体系一致。

---

## 菜单 icon 映射要求

### 一级导航组

建议让 `MenuGroup` 支持 `icon?: string`。

如果类型影响较大，可以先在 `AppLayout.vue` 中维护 `groupIconMap` 过渡。

推荐映射：

| 分组 | icon |
|---|---|
| 客户管理 | `customer` |
| 经营管理 | `briefcase` |
| 经营分析 | `chart` |
| 人事管理 | `personBadge` |
| 财务管理 | `coins` |
| 我的审批 | `approval` |
| 系统管理 | `gear` |

### 二级导航项

推荐映射：

| 页面 | icon |
|---|---|
| 客户管理 | `customer` |
| 风险评估 | `risk` |
| 负债登记 | `debt` |
| 借贷管理（我方） | `cardMoney` |
| 借贷管理（银行） | `bankCard` |
| 增量详情 | `trend` |
| 商机管理 | `target` |
| 增量总览 | `chart` |
| 年度增量 | `calendarChart` |
| 员工绩效 | `performance` |
| 员工档案 | `personBadge` |
| 我的信息 | `userCircle` |
| 人事异动-转正 | `checkCard` |
| 人事异动-调薪 | `salary` |
| 工资管理 | `coins` |
| 管理记录 | `document` |
| 收入管理 | `income` |
| 支出管理 | `expense` |
| 收款账户 | `wallet` |
| 审批相关 | `approval` |
| 用户管理 | `userCircle` |
| 角色管理 | `shield` |
| 组织管理 | `org` |
| 字典管理 | `document` |
| 操作日志 | `log` |

---

## 登录页改造要求

### 品牌 chip

当前星形 icon 必须替换为几何 logo：

- 外形参考六边形或立方体轮廓
- 内部可用两层折线模拟系统/模块结构
- 使用 `currentColor`
- 底板保持蓝色渐变

示例：

```vue
<IconSymbol name="logo" :size="18" />
```

### 登录 badge

替换为统一线性锁：

```vue
<IconSymbol name="lock" :size="26" />
```

底板要求：

- `58px x 58px`
- `border-radius: 18px`
- 蓝色渐变
- 白色线性图标

### 输入框 icon

推荐用 prefix slot，不要继续使用 Element Plus 默认 prefix icon：

```vue
<el-input v-model="form.username" placeholder="请输入账号" size="large">
  <template #prefix>
    <IconSymbol name="userCircle" :size="18" />
  </template>
</el-input>
```

密码：

```vue
<template #prefix>
  <IconSymbol name="lock" :size="18" />
</template>
```

---

## AppLayout 改造要求

### 不再用 Element Plus icons 做业务导航图标

当前大量 import：

```ts
import {
  CollectionTag,
  Coin,
  CreditCard,
  ...
} from '@element-plus/icons-vue'
```

应替换为：

```ts
import IconSymbol from '../components/IconSymbol.vue'
```

渲染：

```vue
<IconSymbol :name="group.icon" :size="20" />
<IconSymbol :name="item.icon" :size="17" />
```

### 分组 icon 不要再取第一个 item icon

当前逻辑类似：

```vue
<component :is="resolveIcon(group.items[0]?.icon || 'HomeFilled')" />
```

必须改掉。

优先方案：

- 给 `MenuGroup` 增加 `icon?: string`
- 在 `menu.ts` 中直接声明 group icon

过渡方案：

- 在 `AppLayout.vue` 中维护 `groupIconMap`

### 折叠态要求

图标体系重做后，必须兼容现有 sidebar collapse：

- 折叠态保留 icon
- 折叠态隐藏文字
- icon-only 链接必须有 `title` 或 `aria-label`
- active icon 状态必须明显
- 不能重新引入“折叠后空按钮”的问题

---

## 首页工作台指标卡改造要求

文件：

- `frontend/src/views/dashboard/WorkbenchView.vue`

### 数据映射

如果接口没有稳定 icon key，可以前端集中映射。
优先用稳定 key；没有 key 时才用 title map。

示例：

```ts
const metricIconMap = {
  客户档案: 'customer',
  未结清: 'cardMoney',
  今日回款: 'calendar',
  资料缺口: 'mail',
}
```

### 卡片结构

每张 metric card 需要有：

- 左上小 icon
- 标题
- helper
- 数值
- 轻量右下角 decorative icon 或 line art

示例结构：

```vue
<div class="metric-card__top">
  <span class="metric-card__icon">
    <IconSymbol :name="resolveMetricIcon(card)" />
  </span>
  <div>
    <span class="metric-card__title">{{ card.title }}</span>
    <span class="metric-card__helper">{{ card.helper }}</span>
  </div>
</div>

<strong class="metric-card__value">{{ card.value }}</strong>

<div class="metric-card__art" aria-hidden="true">
  <IconSymbol :name="resolveMetricArt(card)" />
</div>
```

### 视觉要求

- icon 底板：`36px x 36px`
- active 卡 icon：蓝底白线
- 普通卡 icon：浅蓝底 + 蓝线
- 右下角 art：透明度低，不能抢数值信息
- 不要加 emoji
- 不要加大面积插画

---

## 左下角装饰改造要求

当前左下角装饰可以保留，但需要和本轮 icon 语言统一。

建议二选一：

### 方案 A：轻量调整

- 保留当前结构
- 调整线条、色彩、透明度
- 让它更接近 `dashboard.html` 的线性 SVG / 卡片轮廓风格

### 方案 B：组件化

新增：

- `frontend/src/components/SidebarArt.vue`

AppLayout 只引用：

```vue
<SidebarArt />
```

如果组件化会导致改动过大，可以先采用方案 A。

---

## 验收标准

### 登录页

- 品牌 chip 不再是星形 icon。
- 登录 badge 与品牌/导航 icon 风格一致。
- 输入框 icon 不再显得像 Element Plus 默认临时 icon。
- 整体接近 `UI preview/login.html` 的几何、线性、浅蓝企业风格。

### 侧边栏

- 一级分组 icon 全部不同，语义清晰。
- 二级菜单 icon 不再大量重复。
- 展开态、折叠态都能正常显示 icon。
- active 状态清晰。
- 不引入页面级横向滚动。

### 首页工作台

- 4 个指标卡都有对应业务 icon。
- icon 与参考稿中的 `metric-icon` 风格一致。
- 不影响接口数据加载。
- 不影响点击跳转和筛选功能。

### 技术

- 不使用运行时 template 字符串。
- 不混用 emoji。
- 不新增临时文件。
- `npm run build` 通过。
- `npm run test:unit` 通过。

---

## 推荐执行顺序

1. 新增 `IconSymbol.vue`
2. 从 `dashboard.html` / `login.html` 提炼本轮需要的 SVG path
3. 改 `menu.ts` 的 icon key，必要时扩展 `MenuGroup.icon`
4. 改 `AppLayout.vue` 导航 icon 渲染
5. 检查展开态和折叠态 sidebar
6. 改 `LoginView.vue`
7. 改 `WorkbenchView.vue` metric card icon
8. 轻量调整左下角装饰
9. 跑 build/test
10. 手动预览登录页、工作台、任意业务列表页

---

## 禁止事项

- 不要改表格列宽。
- 不要改布局尺寸。
- 不要改 page intro。
- 不要改业务逻辑。
- 不要改接口、store、权限逻辑。
- 不要新增 `frontend/src/test.txt` 或任何临时调试文件。
- 不要用 emoji 当 icon。
- 不要用运行时 template 字符串定义 icon。
- 不要通过修改测试绕过问题。

---

## 完成后必须汇报

请按下面格式输出：

1. 修改了哪些文件。
2. `IconSymbol.vue` 覆盖了哪些 icon。
3. 侧边栏一级/二级 icon 如何映射。
4. 登录页哪些 icon 已替换。
5. 工作台 4 个指标卡 icon 如何映射。
6. 折叠态 sidebar 是否仍保留 icon 可见和可点击。
7. 是否修改了左下角装饰，采用方案 A 还是方案 B。
8. `npm run build` 结果。
9. `npm run test:unit` 结果。
