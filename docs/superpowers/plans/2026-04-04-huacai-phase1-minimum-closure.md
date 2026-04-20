# 华彩系统一期最小闭环 Implementation Plan

> 说明：本计划已被 `/Users/edy/Documents/tob /华彩系统1025/docs/superpowers/plans/2026-04-15-huacai-phase1-closure-redefinition.md` 覆盖。当前只能把它当作“最小真实链路闭环”的历史记录，不能继续作为一期最终验收口径。

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 让当前登录页、工作台、客户列表、借贷列表、还款列表和对应抽屉基于真实 MySQL 跑通，完成一期第一阶段最小可用闭环。

**Architecture:** 保持现有 `Vue 3 + Spring Boot + MySQL` 结构不变，只补最小必要的 `entity / mapper / service / controller / DTO` 和前端接口对接。后端按 `auth -> customer -> loan -> repayment` 顺序推进，并增加一个仅服务当前工作台的统计接口，不扩展到审批流、报表和导入导出。

**Tech Stack:** Vue 3, Vite, Element Plus, Pinia, Axios, Spring Boot 3, Spring Security, MyBatis-Plus, MySQL 8, Docker

---

### Task 1: 数据库与基础配置

**Files:**
- Modify: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/resources/application.yml`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/sql/init/001_schema.sql`
- Create: `/Users/edy/Documents/tob /华彩系统1025/docker-compose.mysql.yml`

- [ ] 对齐数据库名、逻辑删除字段和数据源配置
- [ ] 增加最小初始化数据所需 SQL 或启动期种子逻辑约束说明
- [ ] 提供 MySQL 8 Docker 启动方式

### Task 2: 认证闭环

**Files:**
- Modify: `/Users/edy/Documents/tob /华彩系统1025/backend/pom.xml`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/config/SecurityConfig.java`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/auth/controller/AuthController.java`
- Create: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/security/*`
- Create: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/auth/service/*`
- Create: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/system/entity/*`
- Create: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/system/mapper/*`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/stores/auth.ts`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/views/auth/LoginView.vue`
- Create: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/api/auth.ts`

- [ ] 接入真实用户表登录
- [ ] 落地最小 JWT 鉴权
- [ ] 打通前端登录、持久化 token 和获取当前用户

### Task 3: 客户闭环

**Files:**
- Modify: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/customer/controller/CustomerController.java`
- Create: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/customer/entity/*`
- Create: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/customer/mapper/*`
- Create: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/customer/service/*`
- Create: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/customer/vo/*`
- Create: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/views/customer/CustomerListView.vue`
- Create: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/api/customer.ts`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/router/index.ts`

- [ ] 实现客户分页查询
- [ ] 实现客户新增/编辑落库
- [ ] 前端列表与抽屉联调

### Task 4: 借贷闭环

**Files:**
- Modify: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/loan/controller/LoanController.java`
- Create: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/loan/entity/*`
- Create: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/loan/mapper/*`
- Create: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/loan/service/*`
- Create: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/loan/vo/*`
- Create: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/views/loan/LoanOrderListView.vue`
- Create: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/api/loan.ts`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/router/index.ts`

- [ ] 实现借贷列表和详情查询
- [ ] 实现借贷新增/编辑落库
- [ ] 提供客户选项接口支撑抽屉表单

### Task 5: 还款闭环

**Files:**
- Modify: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/loan/controller/LoanController.java`
- Create: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/views/loan/RepaymentListView.vue`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/api/loan.ts`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/router/index.ts`

- [ ] 增加顶层还款列表接口以支撑当前页面
- [ ] 实现还款新增/编辑落库
- [ ] 提供借贷单选项接口支撑抽屉表单

### Task 6: 工作台真实数据

**Files:**
- Create: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/workbench/*`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/views/dashboard/WorkbenchView.vue`
- Create: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/api/workbench.ts`

- [ ] 返回真实统计数据
- [ ] 返回最近客户/借贷相关主列表数据
- [ ] 保留现有 UI 基线，只替换数据来源和字段映射

### Task 7: 验证

**Files:**
- Modify: `/Users/edy/Documents/tob /华彩系统1025/README.md`

- [ ] 启动 MySQL 8
- [ ] 验证后端可启动
- [ ] 验证前后端联调
- [ ] 运行 `npm run build`
- [ ] 补充最小启动说明
