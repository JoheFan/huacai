# UI 图标体系收尾提示词：只修 3 个遗留问题

你是负责前端 UI 图标体系改造收尾的 agent。  
本轮只处理下面 3 个 review finding，不要扩大范围。

开始前必须先阅读：

- `UI preview/dashboard.html`
- `UI preview/login.html`
- `frontend/src/components/IconSymbol.vue`
- `frontend/src/layout/AppLayout.vue`
- `frontend/src/views/dashboard/WorkbenchView.vue`

建议先使用这些本地 skill：

- `$ui-ux-pro-max`
- `$frontend-design`
- 如需先梳理视觉约束，用 `$brainstorming`

---

## 本轮只修这 3 个问题

### Finding 1

文件：

- `frontend/src/views/dashboard/WorkbenchView.vue`

问题：

工作台快捷入口仍在混用 Element Plus 图标：

- `UserFilled`
- `List`
- `Bell`
- `FolderOpened`

这会让首页同一屏里同时出现两套图标语言，破坏统一性。

### Finding 2

文件：

- `frontend/src/views/dashboard/WorkbenchView.vue`

问题：

指标卡只补了左上 `IconSymbol`，但右下角仍只有通用 radial glow，没有业务 decorative icon / line art。

### Finding 3

文件：

- `frontend/src/layout/AppLayout.vue`

问题：

左下角装饰仍停留在旧的 CSS 几何层叠，没有纳入统一 SVG 体系。

---

## 必须完成的修改

### 1. 工作台快捷入口全部切到 IconSymbol

文件：

- `frontend/src/views/dashboard/WorkbenchView.vue`

要求：

1. 删除快捷入口中对 Element Plus 图标的依赖，不再使用：
   - `UserFilled`
   - `List`
   - `Bell`
   - `FolderOpened`

2. 快捷入口统一改成 `IconSymbol`。

3. 给快捷入口建立明确 icon 映射，例如：

```ts
const quickEntryIconMap = {
  新增客户: 'customer',
  借贷管理: 'cardMoney',
  收入管理: 'income',
  系统用户: 'userCircle',
}
```

或者直接在 `quickEntries` 中写：

```ts
{ label: '新增客户', path: '/customers', icon: 'customer' }
```

4. 模板里统一使用：

```vue
<IconSymbol :name="entry.icon" :size="16" />
```

5. 不要再出现两套 icon 风格混用。

---

### 2. 指标卡补右下角业务 decorative icon / line art

文件：

- `frontend/src/views/dashboard/WorkbenchView.vue`

要求：

1. 每张 metric card 除了左上角 icon 外，还要补右下角业务 decorative icon / line art。
2. decorative 层必须与业务语义相关，不要只用统一 glow。
3. decorative icon 必须也使用 `IconSymbol` 或基于 `IconSymbol` 的轻量 SVG 组合，不要回退到 Element Plus icon。
4. decorative 层只能做弱视觉，不允许抢数值和标题的层级。
5. 建议透明度较低、尺寸较大、靠右下角定位。

推荐做法：

```ts
const metricArtMap = {
  客户档案: 'customer',
  未结清: 'cardMoney',
  今日回款: 'calendar',
  资料缺口: 'mail',
}
```

模板结构示例：

```vue
<div class="metric-card__art" aria-hidden="true">
  <IconSymbol :name="resolveMetricArt(card)" :size="56" />
</div>
```

CSS 要求：

- `position: absolute`
- 靠右下角
- `opacity` 降低
- 不能影响 hover 和文本可读性

如果你认为单个 `IconSymbol` 不足以承接 decorative 层，可以在卡片里做很轻量的复合 SVG，但必须保持统一线性风格。

---

### 3. 左下角装饰纳入统一 SVG 体系

文件：

- `frontend/src/layout/AppLayout.vue`

推荐新增：

- `frontend/src/components/SidebarArt.vue`

要求：

1. 不再让 `AppLayout.vue` 直接维护大段旧的 `sidebar__art-*` 纯 CSS 几何装饰结构。
2. 左下角装饰要抽成独立组件 `SidebarArt.vue`。
3. 这个组件的视觉必须改成与当前 `IconSymbol` 一致的线性 SVG / 轻玻璃卡片语言。
4. 可以保留浅蓝玻璃基座，但至少要让主要视觉信号来自 SVG 线条，而不是纯 CSS 堆叠。
5. 折叠态下继续隐藏装饰。
6. 不要让装饰变得更厚重或更花哨。

### 推荐结构

`SidebarArt.vue` 内可以包含：

- 一个底部椭圆环
- 两到三层浅蓝玻璃底座
- 一组线性 SVG 业务卡片/折线/节点图形

重点：

- `stroke="currentColor"`
- `fill="none"` 为主
- 少量 `fill` 只做弱辅助
- 与 `dashboard.html` 左下角艺术装置风格靠拢

然后在 `AppLayout.vue` 中改成：

```vue
<SidebarArt class="sidebar__art" />
```

并删掉旧的装饰 DOM 结构与大部分无关样式。

如果某些尺寸定位样式必须保留在 `AppLayout.vue`，可以保留容器定位，但内部视觉实现必须迁移到 `SidebarArt.vue`。

---

## 禁止事项

- 不要改表格列宽。
- 不要改 page intro。
- 不要改业务逻辑。
- 不要改权限逻辑。
- 不要改路由行为。
- 不要新增临时文件。
- 不要再引入 Element Plus 业务图标到工作台快捷入口。
- 不要只做视觉 glow 敷衍 decorative 层。
- 不要保留旧的 `sidebar__art` DOM 结构不动却声称完成组件化。

---

## 验收标准

### 工作台

1. 快捷入口不再出现 Element Plus 图标。
2. 首页同一屏只保留统一线性 SVG 图标语言。
3. 4 个指标卡左上角和右下角都有语义一致的 icon / art。
4. decorative 层不会压住数值和标题。

### AppLayout

1. 左下角装饰已抽到 `SidebarArt.vue`，或至少主要视觉已迁移到独立 SVG 组件。
2. 装饰与导航 icon 是同一套线性语言。
3. 折叠态下装饰仍隐藏。

### 技术

1. `npm run build` 通过。
2. `npm run test:unit` 通过。
3. 不新增临时调试文件。

---

## 完成后必须汇报

请按下面格式输出：

1. 修改了哪些文件。
2. 工作台快捷入口如何从 Element Plus icon 切到 `IconSymbol`。
3. 4 个指标卡的左上 icon 和右下 decorative art 如何映射。
4. 是否新增了 `SidebarArt.vue`，如果没有，为什么。
5. `AppLayout.vue` 里旧的 `sidebar__art` 结构是否已删除或大幅收缩。
6. `npm run build` 结果。
7. `npm run test:unit` 结果。
