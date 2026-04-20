# 华彩系统工作台 UI 优化 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 按 UI 交接文档把当前工作台收敛成可继续扩展的专业作业台风格，只调整样式基线、后台框架和工作台模板。

**Architecture:** 严格限制改动在 `frontend/src/style.css`、`frontend/src/layout/AppLayout.vue`、`frontend/src/views/dashboard/WorkbenchView.vue`。通过统一全局 token、收紧布局比例、重排工作台层级来完成本轮视觉升级，不改业务路由，不新增依赖。

**Tech Stack:** Vue 3, TypeScript, Vite, Element Plus, scoped CSS

---

### Task 1: 全局视觉基线

**Files:**
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/style.css`

- [ ] 切换全局颜色变量到 A1 钴蓝企业版
- [ ] 收敛标题、正文、辅助文字与卡片圆角比例
- [ ] 定义工作台通用卡片、弱底、表格区和辅助区样式

### Task 2: AppLayout 框架优化

**Files:**
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/layout/AppLayout.vue`

- [ ] 收紧左侧导航宽度和视觉密度
- [ ] 优化激活菜单态为钴蓝弱底 + 蓝色文字
- [ ] 调整顶部标题、用户区和操作区的视觉权重

### Task 3: Workbench 模板重构

**Files:**
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/views/dashboard/WorkbenchView.vue`

- [ ] 改造顶部区域为标题、说明、主操作
- [ ] 重建 3 到 4 个高密度统计卡
- [ ] 建立左侧主列表区与右侧辅助区结构
- [ ] 右侧仅保留待办、提醒、快捷入口，不做回款趋势

### Task 4: 验证

**Files:**
- Verify: `/Users/edy/Documents/tob /华彩系统1025/frontend`

- [ ] 运行 `npm run build`
- [ ] 检查是否符合交接文档中的验收要求
