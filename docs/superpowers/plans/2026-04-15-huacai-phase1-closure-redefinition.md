# 华彩系统一期收口重定义 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 把一期收口从“真实接口能跑通”重定义为“真实接口能跑通，且除工作台外的已开发页面表格字段与原型达成一致或有明确替代口径”，并据此完成前后端收口。

**Architecture:** 保持现有 `Vue 3 + Spring Boot + MySQL` 结构和既定 UI 风格不变，不重做视觉层，不扩到二期业务。前端优先在现有页面和抽屉上补齐原型字段；当原型与当前实现不是同一数据视图时，优先调整列表口径和接口 VO，而不是新增旁路页面。角色与组织这两类原型错位页面，需要先完成“目标页定义”再落代码，避免继续按错对象开发。

**Tech Stack:** Vue 3, Vite, Element Plus, Pinia, Axios, Spring Boot 3, Spring Security, MyBatis-Plus, MySQL 8, Docker

---

## 为什么之前会偏离原型

- 2026-04-04 的一期计划把目标定义成了“登录 + 工作台 + customer / loan / repayment 最小真实链路跑通”，重点是接口闭环，不是原型还原。
- 执行时默认沿用了后端现成实体粒度：客户主数据、单笔借贷单、全局还款列表，而不是原型里的客户汇总视图、下钻视图和跟进摘要视图。
- 这条路线解决了“能登录、能落库、能刷新”的问题，但没有解决“表格字段是否和原型一致”的问题。
- 结论：之前不是完全没按原型做，而是**一期目标定义错了**，把“最小可用闭环”放在了“原型对齐”前面，导致页面对象和原型对象越走越偏。

## 新的一期收口定义

本次重定义后，一期收口必须同时满足以下 6 条，缺一条都不能算收口完成：

- [ ] 真实 MySQL、真实登录、真实新增/编辑/刷新链路继续保持可用
- [ ] 刷新登录态恢复、普通用户模块权限、后端接口拦截继续保持可用
- [ ] 除工作台外，当前已开发页面的表格字段与原型达成一致，或有明确批准的替代字段口径
- [ ] 所有“模型改动”页面完成目标页定义，不允许继续边做边猜
- [ ] 前端 `npm run build`、单测、后端关键测试继续通过
- [ ] 新 handoff 文档明确记录：哪些页面已按原型收口，哪些字段做了替代，哪些问题被正式延后

## 本次一期收口的页面范围

必须纳入本轮收口：

- 客户管理
- 商机管理
- 借贷管理
- 还款明细
- 系统用户
- 角色管理
- 组织管理

明确不纳入本轮：

- 工作台视觉重做
- finance / opportunity 新模块扩展
- 审批流 / 报表 / 导入导出
- 复杂数据权限

## 页面目标定义

### 1. 客户管理

**目标页定义：**
- 保持当前客户主数据列表页不变
- 列表字段向原型 `客户管理.png` 靠拢

**一期必须完成：**
- [ ] `客户编号` 与原型 `客户ID` 统一命名
- [ ] `手机号` 与原型 `联系方式` 统一命名
- [ ] 列表补齐 `统一社会信用代码 / 推荐人 / 推荐人返点`
- [ ] 列表明确展示 `状态` 的业务口径，不能再只放 `借贷状态` 顶替
- [ ] 保留 `更新时间` 作为后台扩展列

**一期明确不做：**
- [ ] 不新增 `测试日期 / 测试额度 / 流量 / 综合评分 / 龙信商`

**目标文件：**
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/views/customer/CustomerListView.vue`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/api/customer.ts`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/customer/vo/CustomerVO.java`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/customer/service/impl/CustomerServiceImpl.java`

### 2. 商机管理

**目标页定义：**
- 保持当前商机列表页
- 但列表字段必须回到“客户信息 + 跟进结果摘要”的原型口径

**一期必须完成：**
- [ ] 列表补齐 `联系方式 / 企业名称 / 统一社会信用代码 / 备注`
- [ ] `预估金额` 文案统一成 `预计申请额度`
- [ ] 明确 `最近一次跟进时间 / 跟进人 / 跟进记录` 的实现口径：
  - 取最近一条 follow record
  - 若无 follow record，则展示 `-`
- [ ] `ownerUserName` 不再直接冒充跟进人，除非它就是最近跟进记录的提交人

**一期允许保留的扩展字段：**
- [ ] `商机阶段 / 意向等级 / 状态 / 更新时间`

**目标文件：**
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/views/opportunity/OpportunityListView.vue`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/api/opportunity.ts`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/opportunity/vo/OpportunityVO.java`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/opportunity/service/impl/OpportunityServiceImpl.java`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/opportunity/mapper/*`

### 3. 借贷管理

**目标页定义：**
- 当前 `/loan-orders` 不再按“单笔借贷单主列表”作为一期最终口径
- 一期目标改为：**在现有页面上按资金来源切换表头，使其分别对齐原型“我方借贷概览”和“银行借贷概览”**
- 也就是：
  - `SELF` 视图对齐 `借贷管理（我方）.png`
  - `BANK` 视图对齐 `借贷管理（银行）.png`

**一期必须完成：**
- [ ] 在现有页面顶部把 `资金来源` 过滤提升为主视图切换条件
- [ ] `SELF` 视图补齐：`客户ID / 联系电话 / 企业名称 / 理财定存黄金 / 垫还信用卡网贷 / 笔数 / 备注`
- [ ] `BANK` 视图补齐：`客户ID / 联系电话 / 企业名称 / 备注`
- [ ] 明确 `余额` 是否显示为 `余额（含手续费）`
- [ ] 对 `已还款 / 待还款` 提供客户维度聚合结果，而不是继续只显示单笔余额
- [ ] `总贷款金额（我方/银行）`、`借款日期（第一笔）/出款日期（银行）` 必须用客户维度聚合结果输出

**一期明确不做：**
- [ ] 不新增 `总增量金额 / 增量笔数 / 几年期（银行）`

**目标文件：**
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/views/loan/LoanOrderListView.vue`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/api/loan.ts`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/loan/vo/LoanOrderVO.java`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/loan/service/impl/LoanManageServiceImpl.java`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/loan/mapper/*`

### 4. 还款明细

**目标页定义：**
- 当前 `/repayments` 继续保留全局入口
- 但一期必须同时支持“按客户/借贷单下钻”的详情口径，以对齐原型“查看客户还款情况”

**一期必须完成：**
- [ ] 列表补 `序号 / 备注`
- [ ] 文案统一为 `还款金额 / 每月利息 / 还款途径`
- [ ] 支持通过 `customerId` 或 `loanOrderId` 进入 scoped 视图
- [ ] scoped 视图下补总计区或聚合摘要
- [ ] `SELF` 与 `BANK` 视图保持统一页面，但按资金来源保留差异字段扩展能力

**一期明确不做：**
- [ ] 不新增 `剩余期数`

**目标文件：**
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/views/loan/RepaymentListView.vue`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/api/loan.ts`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/loan/vo/LoanRepaymentVO.java`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/loan/service/impl/LoanManageServiceImpl.java`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/loan/mapper/*`

### 5. 系统用户

**目标页定义：**
- 当前系统用户页继续保留“人员主数据 + 账号管理”能力
- 但表格字段必须能覆盖原型最小基线

**一期必须完成：**
- [ ] 列表补 `角色名称`
- [ ] 列表补 `创建时间`
- [ ] 保留 `用户名 / 账号状态 / 操作`
- [ ] 允许继续保留 `真实姓名 / 工号 / 所属组织 / 岗位 / 手机号 / 更新时间`

**目标文件：**
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/views/system/user/UserManageView.vue`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/api/system.ts`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/system/vo/UserVO.java`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/system/service/impl/SystemManageServiceImpl.java`

### 6. 角色管理

**目标页定义：**
- 这一页先解决“原型对象错位”问题，再做代码
- 一期收口必须输出一个明确结论，不允许继续悬空：
  - 方案 A：确认 `原型图/角色管理.png` 实际上不是角色表，当前角色实体页继续保留
  - 方案 B：按原型重定义该路由，不再把它当角色实体页

**一期必须完成：**
- [ ] 产出一份页面映射结论，明确 `角色管理` 路由的真实业务对象
- [ ] 若选方案 A，则补一份原型错位说明并将当前角色页纳入“替代口径已批准”
- [ ] 若选方案 B，则按新对象改路由、菜单和页面字段

**目标文件：**
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/views/system/role/RoleManageView.vue`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/router/menu.ts`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/docs/superpowers/specs/2026-04-15-huacai-prototype-table-field-gap-checklist.md`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/docs/superpowers/specs/2026-04-06-huacai-phase1-frontend-closure-handoff.md`

### 7. 组织管理

**目标页定义：**
- 当前树结构可以保留
- 但一期必须补齐原型关注的字段可见性，并在交接中明确“结构采用树而非表”

**一期必须完成：**
- [ ] 在节点或详情区域显示 `上级机构 / 机构描述 / 排序`
- [ ] 明确 `是否管理机构` 与当前 `orgType` 的映射关系
- [ ] 交接文档里将“树替代表格”写成正式替代口径，不再口头约定

**目标文件：**
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/views/system/org/OrgManageView.vue`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/api/system.ts`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/system/vo/OrgVO.java`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/system/service/impl/SystemManageServiceImpl.java`

## 执行顺序

### Task 1: 固化新的一期验收口径

**Files:**
- Modify: `/Users/edy/Documents/tob /华彩系统1025/docs/superpowers/plans/2026-04-04-huacai-phase1-minimum-closure.md`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/docs/superpowers/specs/2026-04-06-huacai-phase1-frontend-closure-handoff.md`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/docs/superpowers/specs/2026-04-15-huacai-prototype-table-field-gap-checklist.md`

- [ ] 把旧的一期定义标注为“已被 2026-04-15 重定义覆盖”
- [ ] 在 handoff 文档中新增“表格字段对齐是一期必收项”
- [ ] 把每个页面的目标口径写成最终验收表

### Task 2: 先收可直接补齐的列表字段

**Files:**
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/views/customer/CustomerListView.vue`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/views/opportunity/OpportunityListView.vue`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/views/loan/LoanOrderListView.vue`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/views/loan/RepaymentListView.vue`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/views/system/user/UserManageView.vue`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/views/system/org/OrgManageView.vue`

- [ ] 先补所有当前模型已有、但列表未显示的字段
- [ ] 同步统一原型文案与当前文案
- [ ] 保持现有 UI 风格，不新增新的视觉系统

### Task 3: 再补后端 VO 缺口

**Files:**
- Modify: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/customer/vo/CustomerVO.java`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/opportunity/vo/OpportunityVO.java`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/loan/vo/LoanOrderVO.java`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/loan/vo/LoanRepaymentVO.java`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/system/vo/UserVO.java`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/system/vo/OrgVO.java`

- [ ] 将前端列表需要但当前 VO 未返回的字段补齐
- [ ] 禁止为了补一个列表字段新增一整套无验证的新业务模型
- [ ] 所有新增字段都要有真实接口返回验证

### Task 4: 解决三个口径类页面

**Files:**
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/views/opportunity/OpportunityListView.vue`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/views/loan/LoanOrderListView.vue`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/views/loan/RepaymentListView.vue`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/opportunity/service/impl/OpportunityServiceImpl.java`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/loan/service/impl/LoanManageServiceImpl.java`

- [ ] 商机页按最近跟进记录输出摘要
- [ ] 借贷页按资金来源输出不同原型口径列
- [ ] 还款页同时支持全局入口和 scoped 详情口径

### Task 5: 解决角色管理与组织管理的定义问题

**Files:**
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/views/system/role/RoleManageView.vue`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/views/system/org/OrgManageView.vue`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/router/menu.ts`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/docs/superpowers/specs/2026-04-06-huacai-phase1-frontend-closure-handoff.md`

- [ ] 角色页先出映射结论再改代码
- [ ] 组织页把树结构替代口径写死进文档和页面表现
- [ ] 菜单命名与页面对象保持一致

### Task 6: 真实联调回归

**Files:**
- Modify: `/Users/edy/Documents/tob /华彩系统1025/docs/superpowers/specs/2026-04-06-huacai-phase1-frontend-closure-handoff.md`
- Create: `/Users/edy/Documents/tob /华彩系统1025/docs/superpowers/specs/2026-04-15-huacai-phase1-table-alignment-handoff.md`

- [ ] 逐页验证 customer / opportunity / loan / repayment / system-user / role / org
- [ ] 每一页记录真实接口、真实页面、真实返回字段
- [ ] 重新执行前端构建和测试
- [ ] 重新执行后端关键测试
- [ ] 落新的 handoff 文档

## 最终验收标准

满足以下全部条件才算一期收口完成：

- [ ] customer / opportunity / loan / repayment / system-user / role / org 七个页面的表格字段问题已处理
- [ ] 每个页面都能说明：哪些字段按原型补齐，哪些字段是批准替代口径
- [ ] 不再存在“当前页和原型根本不是同一对象，但仍被算作已完成”的情况
- [ ] 登录、权限、真实 MySQL 联调能力没有回退
- [ ] 前端 `npm run build` 通过
- [ ] 前端单测通过
- [ ] 后端关键测试通过
- [ ] 新 handoff 文档可直接交给下一个 agent 执行

## 2026-04-15 执行结果补记

截至 2026-04-15 本轮开发结束，以上重定义口径的执行状态如下：

- 已完成：
  - customer 表头切到 `客户ID / 联系方式 / 统一社会信用代码 / 推荐人 / 推荐人返点 / 状态`
  - opportunity 列表补齐客户信息与最近跟进摘要
  - loan 主表改为客户汇总视图，并新增 `/api/v1/loan-orders/overview`
  - repayment 同时支持全局入口与 scoped 详情，并新增 `/api/v1/repayments/summary`
  - system-user 补 `角色名称 / 创建时间`
  - org 树结构补 `parentName / 是否管理机构 / 排序 / 备注`
  - role 页面按“原型错位，保留当前角色实体页”封口
  - 前端 `npm run test:unit`、`npm run build` 通过
  - 后端 `OpportunityServiceImplTest / LoanManageServiceImplTest / SystemManageServiceImplTest` 通过

- 已确认不纳入一期：
  - customer 的评分/测试类字段
  - 借贷银行版 `总增量金额 / 增量笔数 / 几年期`
  - 还款银行版 `剩余期数`

- 当前主要风险不在代码实现，而在联调运行时：
  - `huacai-mysql` 容器曾在本轮验证中退出，需要手工拉起
  - `huacai-backend-test` 仍依赖手工注入 `HUACAI_DB_URL` 重启 Spring Boot
