# 华彩客户管理重构交接

日期：2026-04-15

## 本轮目标

按最新原型重做客户管理模块，范围限定为：

- 左侧一级分组改为 `客户管理`
- 二级菜单改为 `客户管理 / 风险评估 / 负债登记`
- `商机管理` 挪入 `经营管理`
- 客户新增/编辑改为整页客户档案
- 风险评估、负债登记新增左侧独立入口，但数据仍来自客户子表
- 合同管理只保留在客户档案页内，不单独做菜单
- 导入档案、导出按钮先置灰，不接真实能力

## 设计落点

- 正式设计说明：
  - `docs/superpowers/specs/2026-04-15-huacai-customer-management-rework-design.md`
- 实施计划：
  - `docs/superpowers/plans/2026-04-15-huacai-customer-management-rework.md`

## 代码改动概要

### 前端

- 导航与权限：
  - 菜单支持客户管理二级菜单和经营管理分组迁移
  - 用户模块选择新增 `customer-risks / customer-debts`
  - 动态档案路由 `/customers/archive/create`、`/customers/archive/:id`
- 页面：
  - 客户主列表改为风险摘要视图
  - 新增客户档案页
  - 新增风险评估列表页
  - 新增负债登记列表页
- 上传：
  - 客户资料原件、合同附件接入真实上传接口

### 后端

- 客户模块：
  - 新增 customer archive DTO / VO / query / entity / mapper
  - `/api/v1/customers/archive`
  - `/api/v1/customers/{id}/archive`
  - `/api/v1/customer-risks`
  - `/api/v1/customer-debts`
  - 嵌套 `/api/v1/customers/{customerId}/scores`
  - 嵌套 `/api/v1/customers/{customerId}/debts`
- 文件模块：
  - `/api/v1/files/upload` 从占位改为真实落库 + 本地存储
  - `detail / download / delete` 改为读取真实 `sys_file`
- 权限：
  - 后端模块访问注册表新增 `customer-risks / customer-debts`

### 数据库

- `cust_customer` 新增 `tax_registration_normal`
- `cust_customer_contract` 新增：
  - `customer_name`
  - `company_name`
  - `credit_code`
- customer 相关表补齐 `version`
- 新增幂等 migration：
  - `sql/migration/V002_add_version_column.sql`
  - `sql/migration/V004_customer_archive_rework.sql`

## 实际验证

### 前端验证

命令：

```bash
cd frontend && npm run build
cd frontend && npm run test:unit
```

结果：

- `npm run build` 通过
- `npm run test:unit` 通过，29/29

### 后端验证

命令：

```bash
docker exec huacai-backend-test sh -lc 'cd /workspace && mvn -q -DskipTests compile'
docker exec huacai-backend-test sh -lc 'cd /workspace && mvn -q -Dtest=CustomerServiceImplTest test'
```

结果：

- `mvn -q -DskipTests compile` 通过
- `mvn -q -Dtest=CustomerServiceImplTest test` 通过

### 真实接口验证

本地环境：

- MySQL：`huacai-mysql`
- 后端：`huacai-backend-test`
- 健康检查：`http://127.0.0.1:18081/api/v1/health`

实际跑通链路：

1. `POST /api/v1/auth/login`
2. `POST /api/v1/files/upload` 上传 2 个客户资料原件 + 2 个合同附件
3. `POST /api/v1/customers/archive` 创建整页客户档案
4. `GET /api/v1/customers` 查询客户主列表
5. `GET /api/v1/customers/{id}/archive` 查询客户档案详情
6. `POST /api/v1/customer-risks` 新增一条独立风险评估记录
7. `POST /api/v1/customer-debts` 新增一条独立负债登记记录
8. `GET /api/v1/customer-risks` 校验风险评估分页
9. `GET /api/v1/customer-debts` 校验负债登记分页
10. `PUT /api/v1/customers/{id}/archive` 更新客户档案
11. `GET /api/v1/customers` 再查主列表，确认更新回显

实际样例客户：

- `customerNo = KH-ARCHIVE-1776244849`
- `customerId = 9`

关键结果：

- 客户主列表返回 200，并能看到：
  - `businessAddress = 深圳南山`
  - `serviceFee = 1680`
  - `testDate = 2026-04-16`
  - `compositeScore = 126`
  - `thirdPartyScore = 118`
- 客户档案详情返回 200，并能看到：
  - `archiveAttachments = 2`
  - `riskRecords = 2`
  - `debtRecords = 2`
  - `contractRecords = 1`
  - 第一条合同 `attachments = 2`
- 独立风险评估分页返回 200，`total = 2`
- 独立负债登记分页返回 200，`total = 2`

## 本轮未做

- 合同管理左侧独立模块
- 导入档案 / 导出真实能力
- 手工浏览器逐页点击回归

## 剩余注意点

- 这轮已经通过真实 API 验证主链路，但还没有做人工浏览器操作录屏。
- `CustomerArchiveView.vue` 当前 contract 附件是多附件真实上传，前端用的是上传后即时 file id 绑定模式。
- 本地 MySQL 需要保证已执行：
  - `sql/migration/V002_add_version_column.sql`
  - `sql/migration/V004_customer_archive_rework.sql`

## 推荐下一步

1. 用浏览器手工走一轮：
   - 新增客户档案
   - 编辑客户档案
   - 风险评估页新增/编辑
   - 负债登记页新增/编辑
2. 如果页面行为符合预期，再继续收口商机管理迁移后的原型对齐。
