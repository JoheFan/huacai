# 华彩项目二期 — 多窗口模块 Agent Prompt

> 阶段 0 已完成：路由/菜单已预注册并锁定，公共 composable 已就位，migration 编号已分配。
> 以下 prompt 按阶段分组，同一阶段内的 Agent 可以并行启动。

---

## 通用前提（复制到每个 prompt 顶部）

```text
## 通用前提（所有模块 Agent 共享）

项目路径：
/Users/edy/Documents/tob /华彩系统1025

技术栈：
- 前端：Vue 3 + TypeScript + Vite + Element Plus + Pinia
- 后端：Spring Boot 3 + MyBatis-Plus + MySQL 8 + Java 21
- 认证：JWT（已实现）

UI 风格：
- 保持当前 A1 钴蓝企业版基线，不要重新设计
- 页面不要出现"一期 / 联调 / 演示 / 骨架"等文案

公共工具（已由阶段 0 提供，直接使用）：
- `src/composables/useListPage.ts` — 列表页分页/搜索/loading 统一封装
- `src/composables/useFormDialog.ts` — 新增/编辑弹窗统一封装
- `src/utils/format.ts` — formatNumber / formatDate / formatCurrency
- `src/api/http.ts` — HTTP 客户端（已有 401 拦截、token 注入）
- 后端 `com.huacai.common.model.PageQuery` — 分页查询基类
- 后端 `com.huacai.common.model.ApiResponse` — 统一响应包装
- 后端 `com.huacai.common.model.PageData` — 分页响应包装

数据库迁移：
- 如需新建表，在 `sql/migration/V2_{你的编号段}__{表名}.sql` 创建
- 参考 `sql/migration/README.md` 查看编号分配
- **不要修改** `sql/init/001_schema.sql`

统一禁改文件：
- `frontend/src/router/menu.ts`（已锁定）
- `frontend/src/router/index.ts`（已锁定）
- `frontend/src/router/viewRegistry.ts`（已锁定）
- `frontend/src/layout/AppLayout.vue`
- `frontend/src/stores/auth.ts`
- `frontend/src/style.css`（可新增 scoped style）
- `docker-compose.*.yml`
- `scripts/dev/**`

交付标准：
1. 页面字段与原型截图一致
2. 所有 CRUD 接口真实可用（不留占位实现）
3. 后端 DTO 有 @Valid 校验
4. `cd frontend && npm run build` 通过
5. 后端能正常编译
6. 写 handoff 文档到 `docs/superpowers/specs/`

输出格式：
- 修改/新增的文件清单
- 验证命令和实际结果
- 剩余问题
```

---

## 阶段 1（可并行启动 5 个 Agent）

### Agent 1：客户管理完善

```text
你现在接手华彩项目里的【客户管理】模块，全面完善到可交互状态。

（粘贴上面的通用前提）

原型参考（按这些截图对齐页面）：
- /Users/edy/Documents/tob /华彩系统1025/原型图/客户管理.png
- /Users/edy/Documents/tob /华彩系统1025/原型图/客户基本情况.png
- /Users/edy/Documents/tob /华彩系统1025/原型图/客户风险综合评估.png
- /Users/edy/Documents/tob /华彩系统1025/原型图/负债情况登记.png
- /Users/edy/Documents/tob /华彩系统1025/原型图/新增.png

你的职责范围：
- 前端可改：`frontend/src/views/customer/**`、`frontend/src/api/customer.ts`、可在 `frontend/src/components/` 新增客户子组件
- 后端可改：`backend/src/main/java/com/huacai/customer/**`
- 测试可改：`backend/src/test/java/com/huacai/customer/**`、`frontend/tests/customer*`

目标：
1. 客户列表页对照原型完善：增加全部列字段（客户ID、客户名称、联系方式、企业名称、统一社会信用代码、审核状态、状态、推荐人、推荐人返点、服务费、测试日期、测试额度、流量、综合评分、龙信商、垫付情况）
2. 客户列表工具栏：新增、导入档案、导出、高级筛选
3. 客户档案整页（对照原型"客户基本情况"）：基本信息表单 + 客户风险综合评估内嵌表格 + 负债情况登记内嵌表格 + 合同管理内嵌表格 + 交易订单信息内嵌表格
4. 状态历史面板（点击状态列弹出）
5. 客户资料附件上传
6. 导入导出功能（使用 Apache POI）
7. 后端 DTO 校验完善，唯一约束异常业务化报错
8. 补充核心 Service 单元测试

禁改：loan、finance、opportunity、system 等其他模块
```

### Agent 2：借贷 + 还款完善

```text
你现在接手华彩项目里的【借贷管理 + 还款明细】模块，全面完善到可交互状态。

（粘贴上面的通用前提）

原型参考：
- /Users/edy/Documents/tob /华彩系统1025/原型图/借贷管理（我方）.png
- /Users/edy/Documents/tob /华彩系统1025/原型图/借贷管理（银行）.png
- /Users/edy/Documents/tob /华彩系统1025/原型图/借贷详情（我方）.png
- /Users/edy/Documents/tob /华彩系统1025/原型图/借贷详情_（银行）.png
- /Users/edy/Documents/tob /华彩系统1025/原型图/查看客户还款（我方）情况.png
- /Users/edy/Documents/tob /华彩系统1025/原型图/查看客户还款（银行）情况.png

你的职责范围：
- 前端可改：`frontend/src/views/loan/**`、`frontend/src/api/loan.ts`
- 后端可改：`backend/src/main/java/com/huacai/loan/**`
- 测试可改：`backend/src/test/java/com/huacai/loan/**`、`frontend/tests/loan*`、`frontend/tests/repayment*`

目标：
1. 借贷管理我方/银行双视图（tab 或路由切换），对照原型字段
2. 借贷详情页（从列表点击"查看"进入）
3. 还款明细完整 CRUD，对照原型字段
4. 金额回算：创建借贷时后端计算余额，还款时更新借贷余额
5. 还款子资源归属校验修复
6. 导入导出功能
7. 后端 DTO @Valid 校验

禁改：customer、finance、system 等其他模块
```

### Agent 3：财务管理（收入/支出/收款方）

```text
你现在接手华彩项目里的【财务管理】模块，从零搭建完整功能。

（粘贴上面的通用前提）

原型参考：
- /Users/edy/Documents/tob /华彩系统1025/原型图/收入管理.png
- /Users/edy/Documents/tob /华彩系统1025/原型图/支出管理.png（注：原型截图标题是"支出管理"但列表展示收入数据，以字段为准）
- /Users/edy/Documents/tob /华彩系统1025/原型图/收款方管理.png

你的职责范围：
- 前端可改：`frontend/src/views/finance/**`（已有占位文件）、`frontend/src/api/finance.ts`（已有类型定义）
- 后端可改：`backend/src/main/java/com/huacai/finance/**`
- 新建表使用编号段：V2_090 ~ V2_099（在 `sql/migration/` 下）

目标：
1. 收款方管理：列表 + 新增/编辑弹窗，维护名称、类型、银行、联系人、状态
2. 收入管理：列表（对照原型：收入名称、收入类型、日期、金额、转入方、附件、备注、操作） + 新增/编辑侧抽屉 + 附件上传 + 导出
3. 支出管理：列表 + 新增/编辑 + 收款方关联 + 附件 + 导出
4. 后端实现完整的 Controller → Service → Mapper 三层
5. DTO 校验

禁改：customer、loan、system 等其他模块
```

### Agent 4：商机管理完善

```text
你现在接手华彩项目里的【商机管理】模块，全面完善到可交互状态。

（粘贴上面的通用前提）

原型参考：
- /Users/edy/Documents/tob /华彩系统1025/原型图/商机管理.png
- /Users/edy/Documents/tob /华彩系统1025/原型图/查看跟进记录.png
- /Users/edy/Documents/tob /华彩系统1025/原型图/新增_1.png

你的职责范围：
- 前端可改：`frontend/src/views/opportunity/**`、`frontend/src/api/opportunity.ts`
- 后端可改：`backend/src/main/java/com/huacai/opportunity/**`
- 测试可改：`backend/src/test/java/com/huacai/opportunity/**`

目标：
1. 商机列表完善字段对齐原型
2. 新增/编辑商机表单
3. 跟进记录弹窗/面板：查看和新增跟进记录
4. 后端 DTO 校验

禁改：customer（只读引用客户数据）、loan、finance、system 等
```

### Agent 5：系统管理三合一

```text
你现在接手华彩项目里的【系统管理】模块，包含组织管理、系统用户、角色管理三个子模块。

（粘贴上面的通用前提）

原型参考：
- /Users/edy/Documents/tob /华彩系统1025/原型图/系统用户.png
- /Users/edy/Documents/tob /华彩系统1025/原型图/组织管理.png
- /Users/edy/Documents/tob /华彩系统1025/原型图/角色管理.png
- /Users/edy/Documents/tob /华彩系统1025/原型图/管理日志.png

你的职责范围：
- 前端可改：`frontend/src/views/system/**`、`frontend/src/api/system.ts`
- 后端可改：`backend/src/main/java/com/huacai/system/**`
- 测试可改：`backend/src/test/java/com/huacai/system/**`

目标（按此顺序串行完成）：
1. 组织管理：树形展示 + 新增/编辑 + 状态维护
2. 角色管理：列表 + 新增/编辑 + 菜单权限授权树 + 状态管理
3. 系统用户：列表 + 新增/编辑 + 角色绑定 + 重置密码 + 启停状态
4. 操作日志：查看列表 + 检索 + 翻页

禁改：customer、loan、finance、opportunity 等其他模块
```

---

## 阶段 2（阶段 1 集成完成后启动，可并行 3 个 Agent）

### Agent 6：人事管理

```text
你现在接手华彩项目里的【人事管理】模块，从零搭建完整功能。

（粘贴上面的通用前提）

原型参考：
- /Users/edy/Documents/tob /华彩系统1025/原型图/员工档案.png
- /Users/edy/Documents/tob /华彩系统1025/原型图/我的档案.png
- /Users/edy/Documents/tob /华彩系统1025/原型图/工资管理.png
- /Users/edy/Documents/tob /华彩系统1025/原型图/管理记录.png

你的职责范围：
- 前端可改：`frontend/src/views/hr/**`（已有占位文件）、`frontend/src/api/hr.ts`（已有类型定义）
- 后端可改：在 `backend/src/main/java/com/huacai/hr/` 下创建完整结构（controller/service/mapper/entity/dto/vo/query）
- 新建表使用编号段：V2_020 ~ V2_039

目标：
1. 员工档案管理：对照原型——在职/全部 tab、员工编号、姓名、性别、出生年月、手机号、参加工作时间、籍贯、政治面貌、部门、职位、人员状态；工具栏：新增、导入档案、快速处理、导入考核、同步、导出
2. 我的档案：tab 结构（基本信息、任职信息、资格证书、考核记录、工作成长记录、家庭成员、人事合同）
3. 工资管理：工资名称、金额、所属岗位、所属部门、描述、排序、状态
4. 管理记录：离职记录、调岗记录、转正类型、操作人、操作时间
5. 后端完整 CRUD + DTO 校验 + 单元测试

依赖数据：读取 sys_org（部门）和 sys_user（关联账号），使用只读 API
禁改：customer、loan、finance、system、opportunity 等其他模块
```

### Agent 7：增量经营

```text
你现在接手华彩项目里的【增量经营】模块，从零搭建完整功能。

（粘贴上面的通用前提）

原型参考：
- /Users/edy/Documents/tob /华彩系统1025/原型图/增量总览.png
- /Users/edy/Documents/tob /华彩系统1025/原型图/增量详情.png
- /Users/edy/Documents/tob /华彩系统1025/原型图/日增量详情.png

你的职责范围：
- 前端可改：`frontend/src/views/analysis/IncrementOverviewView.vue`（已有占位）、可新增 `IncrementDetailView.vue`、`DailyIncrementView.vue`
- 前端可改：`frontend/src/api/analysis.ts`（已有类型定义）
- 后端可改：在 `backend/src/main/java/com/huacai/analysis/` 下创建增量相关结构
- 新建表使用编号段：V2_060 ~ V2_079

注意：增量总览路由已在 menu.ts 注册为 `/analysis/increments`。
增量详情和日增量详情是下钻页面，需要在 router/index.ts 的 extraRoutes 中注册。
**特殊权限**：本 Agent 允许在 `frontend/src/router/index.ts` 的 `extraRoutes` 数组中新增路由条目（仅限增量相关的下钻路由），不得修改其他部分。

目标：
1. 增量总览：对照原型——公司名称、经营地址、行业、开始日期、一月至十二月列、累计、备注、操作（删除/查看），工具栏：新增、导入数据、导出
2. 增量详情（下钻）：客户名称、增量日期、增量总额、经营地址、经营行业、当天共几笔、备注
3. 日增量详情（下钻）：增量金额、增量渠道费率、增量渠道费、备注
4. 原型备注规则："查看时除了备注，其他字段不可修改"
5. 数据按更新时间倒序排列

禁改：customer、loan、finance、hr、workflow 等其他模块
```

### Agent 8：数据字典 + 操作日志

```text
你现在接手华彩项目里的【数据字典】和【操作日志】两个系统子模块。

（粘贴上面的通用前提）

原型参考：
- /Users/edy/Documents/tob /华彩系统1025/原型图/管理日志.png
-（数据字典无原型，按标准后台字典管理页实现：左侧字典类型列表 + 右侧字典项列表）

你的职责范围：
- 前端可改：`frontend/src/views/system/dict/**`（已有占位）、`frontend/src/views/system/log/**`（已有占位）、`frontend/src/api/system.ts`
- 后端可改：`backend/src/main/java/com/huacai/system/`（新增 dict 和 log 相关 controller/service/mapper）
- 新建表使用编号段：V2_001 ~ V2_019

目标：
1. 数据字典管理页：左右分栏布局，左侧字典类型列表（新增/编辑/删除），右侧对应字典项列表（新增/编辑/删除/排序/状态）
2. 初始字典数据：为各模块预初始化常用字典（客户状态、借贷状态、收入类型、支出类型等）
3. 操作日志页：对照原型——操作人、登录时间、操作内容、支持日志检索和翻页
4. 操作日志后端：拦截器或 AOP 记录关键操作到 sys_operation_log 表

禁改：customer、loan、finance、hr、workflow 等其他模块（可读取 sys_user 关联用户信息）
```

---

## 阶段 3（阶段 2 集成完成后启动，可并行 2 个 Agent）

### Agent 9：审批流

```text
你现在接手华彩项目里的【审批流】模块，从零搭建完整功能。

（粘贴上面的通用前提）

原型参考：
- /Users/edy/Documents/tob /华彩系统1025/原型图/人事异动-转正.png
- /Users/edy/Documents/tob /华彩系统1025/原型图/人事异动-调薪.png
- /Users/edy/Documents/tob /华彩系统1025/原型图/人事异动-报销.png
- /Users/edy/Documents/tob /华彩系统1025/原型图/我的审批.png
- /Users/edy/Documents/tob /华彩系统1025/原型图/已审批.png

你的职责范围：
- 前端可改：`frontend/src/views/workflow/**`（已有占位文件）、`frontend/src/api/workflow.ts`（已有类型定义）
- 后端可改：在 `backend/src/main/java/com/huacai/workflow/` 下创建完整结构
- 新建表使用编号段：V2_040 ~ V2_059

审批流设计：
- 采用固定状态机（不引入工作流引擎），代码写死流转节点
- 基本流程：发起 → 部门领导审批 → 人事/财务审批 → 完成/拒绝
- 各类型申请单独建表

目标：
1. 转正申请：发起表单（申请事由、转正人、职位、入职日期、预计转正日期）
2. 调薪申请：发起表单（当前薪资、申请调薪幅度、调薪后薪资、拟稿意见）
3. 报销申请：发起表单（报销人员、报销金额、拟稿意见）
4. 我的审批：待处理列表（文件标题、文件类型、发起人、发送时间、流程状态）、批量办理
5. 已审批：已处理列表

依赖数据：读取 hr_employee（员工信息），使用只读 API
禁改：customer、loan、finance、system、analysis 等其他模块
```

### Agent 10：经营分析（员工绩效）

```text
你现在接手华彩项目里的【员工绩效】模块。

（粘贴上面的通用前提）

原型参考：
- /Users/edy/Documents/tob /华彩系统1025/原型图/员工绩效.png

你的职责范围：
- 前端可改：`frontend/src/views/analysis/PerformanceView.vue`（已有占位）、`frontend/src/api/analysis.ts`（已有类型定义）
- 后端可改：在 `backend/src/main/java/com/huacai/analysis/` 下新增 performance 相关结构
- 新建表使用编号段：V2_080 ~ V2_089

目标：
1. 员工绩效列表：对照原型——员工名称、部门、日期、绩效目标、实际达成、成交数、更新时间、备注，操作（查看/删除）
2. 新增/编辑绩效记录
3. 后端完整 CRUD
4. 可选：引入 ECharts 做简单的绩效可视化图表

禁改：其他所有模块
```
