# 华彩客户管理重构 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 把客户经营模块收成“客户档案页 + 风险评估列表 + 负债登记列表 + 二级菜单结构”的可交付版本，同时保持真实接口与现有联调链路可用。

**Architecture:** 保留现有 `Vue 3 + Spring Boot + MySQL` 骨架，围绕现有 `cust_customer / cust_customer_score / cust_customer_debt / cust_customer_contract / sys_file` 扩展真实 CRUD 和整页提交流程。前端以“一个客户档案页 + 两个独立记录页 + 一套共享菜单权限”组织实现，不引入新的视觉体系。

**Tech Stack:** Vue 3, Vue Router, Element Plus, Axios, Spring Boot 3, Spring Security, MyBatis-Plus, MySQL 8, Docker

---

### Task 1: 补菜单与路由二级结构

**Files:**
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/types/navigation.ts`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/router/menu.ts`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/router/index.ts`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/layout/AppLayout.vue`
- Test: `/Users/edy/Documents/tob /华彩系统1025/frontend/tests/customer-menu-structure.test.ts`

- [ ] 写 failing test，验证客户管理分组下存在二级菜单 `客户管理 / 风险评估 / 负债登记`，并且 `商机管理` 归属 `经营管理`
- [ ] 运行 `cd /Users/edy/Documents/tob\ /华彩系统1025/frontend && node --test --experimental-strip-types tests/customer-menu-structure.test.ts`
- [ ] 实现 `navigation` 类型、菜单配置、路由映射和布局渲染的最小改动
- [ ] 再次运行上述测试，确认通过

### Task 2: 补客户与子表的前端模型

**Files:**
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/api/customer.ts`
- Create: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/views/customer/customerModels.ts`
- Test: `/Users/edy/Documents/tob /华彩系统1025/frontend/tests/customer-models.test.ts`

- [ ] 写 failing test，覆盖客户档案 payload、风险评估记录、负债记录、合同记录的序列化规则
- [ ] 运行 `cd /Users/edy/Documents/tob\ /华彩系统1025/frontend && node --test --experimental-strip-types tests/customer-models.test.ts`
- [ ] 实现前端类型：
  - `CustomerArchivePayload`
  - `CustomerRiskRecord`
  - `CustomerDebtRecord`
  - `CustomerContractRecord`
  - `CustomerAttachment`
- [ ] 再次运行测试，确认通过

### Task 3: 先写后端 customer service 的 failing tests

**Files:**
- Create: `/Users/edy/Documents/tob /华彩系统1025/backend/src/test/java/com/huacai/customer/service/impl/CustomerServiceImplTest.java`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/customer/service/CustomerService.java`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/customer/service/impl/CustomerServiceImpl.java`

- [ ] 写 failing tests，至少覆盖：
  - 创建客户档案时保存主表、风险评估、负债、合同
  - 客户列表能取到最新风险评估摘要
  - 风险评估独立分页按记录返回
  - 负债独立分页按记录返回
- [ ] 运行 `cd /Users/edy/Documents/tob\ /华彩系统1025/backend && mvn -Dtest=CustomerServiceImplTest test`
- [ ] 确认测试先失败，再进入实现

### Task 4: 补后端 customer 子表真实模型

**Files:**
- Create: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/customer/entity/CustCustomerScore.java`
- Create: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/customer/entity/CustCustomerDebt.java`
- Create: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/customer/entity/CustCustomerContract.java`
- Create: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/customer/mapper/CustomerScoreMapper.java`
- Create: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/customer/mapper/CustomerDebtMapper.java`
- Create: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/customer/mapper/CustomerContractMapper.java`

- [ ] 添加 customer 子表 entity/mapper
- [ ] 只实现本轮真实 CRUD 所需字段，不扩无关行为
- [ ] 重新运行 `mvn -Dtest=CustomerServiceImplTest test`

### Task 5: 扩展后端 DTO / VO

**Files:**
- Modify: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/customer/dto/CustomerSaveRequest.java`
- Create: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/customer/dto/CustomerArchiveSaveRequest.java`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/customer/dto/CustomerScoreSaveRequest.java`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/customer/dto/CustomerDebtSaveRequest.java`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/customer/dto/CustomerContractSaveRequest.java`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/customer/vo/CustomerVO.java`
- Create: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/customer/vo/CustomerRiskVO.java`
- Create: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/customer/vo/CustomerDebtVO.java`
- Create: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/customer/vo/CustomerContractVO.java`
- Create: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/customer/vo/CustomerArchiveVO.java`

- [ ] 给客户主表增加 `taxRegistrationNormal` 字段
- [ ] 风险评估补 `trafficValue / thirdPartyScore`
- [ ] 负债 DTO/VO 把 `repaidAmount` 统一转成“已垫还”语义
- [ ] 合同 DTO 支持多附件 `fileIds`
- [ ] 重新运行 `mvn -Dtest=CustomerServiceImplTest test`

### Task 6: 实现后端真实接口

**Files:**
- Modify: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/customer/controller/CustomerController.java`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/customer/service/CustomerService.java`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/customer/service/impl/CustomerServiceImpl.java`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/customer/entity/CustCustomer.java`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/customer/mapper/CustomerMapper.java`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/sql/init/001_schema.sql`

- [ ] 实现客户档案整页创建/编辑
- [ ] 实现客户档案详情返回主表 + 子表
- [ ] 实现风险评估独立分页查询
- [ ] 实现负债登记独立分页查询
- [ ] 实现合同 CRUD
- [ ] 更新 SQL 初始化脚本中的客户主表新增字段
- [ ] 运行 `cd /Users/edy/Documents/tob\ /华彩系统1025/backend && mvn -Dtest=CustomerServiceImplTest test`

### Task 7: 补前端客户档案页 failing tests

**Files:**
- Create: `/Users/edy/Documents/tob /华彩系统1025/frontend/tests/customer-archive-model.test.ts`
- Create: `/Users/edy/Documents/tob /华彩系统1025/frontend/tests/customer-derived-values.test.ts`

- [ ] 写 failing tests，覆盖：
  - 年龄自动计算
  - 负债剩余需还额度计算
  - 客户档案 payload 规范化
- [ ] 运行：
  - `cd /Users/edy/Documents/tob\ /华彩系统1025/frontend && node --test --experimental-strip-types tests/customer-archive-model.test.ts`
  - `cd /Users/edy/Documents/tob\ /华彩系统1025/frontend && node --test --experimental-strip-types tests/customer-derived-values.test.ts`
- [ ] 确认测试先失败

### Task 8: 实现前端客户档案页

**Files:**
- Create: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/views/customer/CustomerArchiveView.vue`
- Create: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/views/customer/customerArchiveState.ts`
- Create: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/views/customer/customerArchiveSubmit.ts`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/views/customer/CustomerListView.vue`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/router/index.ts`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/api/customer.ts`

- [ ] 新建客户档案页，替代旧 drawer
- [ ] 列表“新增/编辑”跳转到档案页
- [ ] 基本信息区块、风险评估区块、负债区块、合同区块都按 spec 字段落地
- [ ] 导入导出按钮置灰
- [ ] 重新运行客户档案相关前端 tests

### Task 9: 实现风险评估与负债登记独立页

**Files:**
- Create: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/views/customer/CustomerRiskListView.vue`
- Create: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/views/customer/CustomerDebtListView.vue`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/router/index.ts`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/api/customer.ts`

- [ ] 风险评估页支持客户筛选、列表、新增、编辑
- [ ] 负债登记页支持客户筛选、列表、新增、编辑
- [ ] 点击客户名称回到客户档案页
- [ ] 导入导出按钮置灰

### Task 10: 扩展文件上传前端接入

**Files:**
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/api/http.ts`
- Create: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/api/file.ts`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/views/customer/CustomerArchiveView.vue`

- [ ] 增加文件上传 API 封装
- [ ] 客户资料原件支持多附件
- [ ] 合同记录支持多附件
- [ ] 本轮只要求文件元数据引用打通，不要求真实文件存储

### Task 11: 联调回归与文档

**Files:**
- Modify: `/Users/edy/Documents/tob /华彩系统1025/docs/superpowers/specs/2026-04-06-huacai-phase1-frontend-closure-handoff.md`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/docs/superpowers/specs/2026-04-15-huacai-prototype-table-field-gap-checklist.md`

- [ ] 运行前端单测：`cd /Users/edy/Documents/tob\ /华彩系统1025/frontend && npm run test:unit`
- [ ] 运行前端构建：`cd /Users/edy/Documents/tob\ /华彩系统1025/frontend && npm run build`
- [ ] 运行后端客户测试：`cd /Users/edy/Documents/tob\ /华彩系统1025/backend && mvn -Dtest=CustomerServiceImplTest test`
- [ ] 启动联调环境：`cd /Users/edy/Documents/tob\ /华彩系统1025 && ./scripts/dev/up-local-stack.sh`
- [ ] 验证真实接口：
  - `/api/v1/customers`
  - `/api/v1/customers/{id}`
  - `/api/v1/customers/archive`
  - `/api/v1/customer-risks`
  - `/api/v1/customer-debts`
- [ ] 更新 handoff 文档和差异清单
