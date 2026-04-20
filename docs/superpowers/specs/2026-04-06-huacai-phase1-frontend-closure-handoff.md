# 华彩系统一期第一阶段前端收口联调交接

## 文档目的

本文档记录 **2026-04-15** 这轮一期第一阶段前端收口的真实联调结果。

约束仍然保持不变：

- 不重做 UI，不改变既定视觉方向
- 不进入第二阶段
- 不扩 finance / opportunity / 审批流 / 报表 / 导入导出 / 复杂权限
- 目标是把一期第一阶段的真实可用状态和剩余问题写清楚，方便下一个 agent 直接接手

## 2026-04-15 补充：本地启动链稳定化

本轮又补充完成了本地联调启动链收口，目的是把之前依赖手工容器和 nginx 反代的方式替换掉。

新增文件：

- `docker-compose.local.yml`
- `scripts/dev/up-local-stack.sh`
- `scripts/dev/down-local-stack.sh`

当前推荐启动方式：

```bash
./scripts/dev/up-local-stack.sh
```

实际效果：

- `huacai-mysql` 改为由 Compose 正式托管，带 `restart` 和 `healthcheck`
- `huacai-backend-test` 改为容器主进程直接执行 `mvn -Dmaven.test.skip=true spring-boot:run`
- 宿主机 `127.0.0.1:18081` 现在直接映射后端容器，不再依赖 `huacai-backend-proxy-live`
- 脚本会自动把旧匿名 MySQL volume 迁移到命名卷 `1025_mysql-data`

本轮实际复核结果：

- `huacai-mysql` 已运行在命名卷 `1025_mysql-data`
- `huacai-backend-test` 已运行并达到 `healthy`
- 旧 `huacai-backend-proxy-live` 已移除
- `http://127.0.0.1:18081/api/v1/health` 返回 200
- `admin / 123456` 登录返回 200
- 真实库样例数据仍在：`cust_customer=6`、`loan_order=2`、`loan_repayment=3`
- 已执行一次 `./scripts/dev/down-local-stack.sh -> ./scripts/dev/up-local-stack.sh` 回归，结果通过

## 本轮修改文件

- `frontend/package.json`
- `frontend/src/views/auth/LoginView.vue`
- `frontend/src/layout/AppLayout.vue`
- `frontend/src/views/loan/LoanOrderListView.vue`
- `frontend/src/views/loan/loanOrderFormMode.ts`
- `frontend/tests/loan-order-form-mode.test.ts`
- `frontend/src/views/opportunity/OpportunityListView.vue`
- `frontend/src/views/system/org/OrgManageView.vue`
- `frontend/src/access/moduleAccess.ts`
- `frontend/src/bootstrap/restoreAuthSession.ts`
- `frontend/src/router/menu.ts`
- `frontend/src/router/index.ts`
- `frontend/src/views/common/WelcomeView.vue`
- `frontend/src/views/system/user/UserManageView.vue`
- `frontend/src/views/system/user/userModuleRegistry.ts`
- `frontend/tests/module-access-routing.test.ts`
- `frontend/tests/user-module-selection.test.ts`
- `docs/superpowers/specs/2026-04-06-huacai-phase1-frontend-closure-handoff.md`
- `backend/src/main/java/com/huacai/security/ModuleAccessRegistry.java`
- `backend/src/main/java/com/huacai/security/ModuleAccessFilter.java`
- `backend/src/main/java/com/huacai/security/AuthUser.java`
- `backend/src/main/java/com/huacai/auth/service/impl/AuthServiceImpl.java`
- `backend/src/main/java/com/huacai/config/SecurityConfig.java`
- `backend/src/main/java/com/huacai/system/service/impl/SystemManageServiceImpl.java`
- `backend/src/main/java/com/huacai/system/entity/SysUserModule.java`
- `backend/src/main/java/com/huacai/system/mapper/UserModuleMapper.java`
- `backend/src/main/java/com/huacai/system/dto/UserCreateRequest.java`
- `backend/src/main/java/com/huacai/system/dto/UserUpdateRequest.java`
- `backend/src/main/java/com/huacai/system/vo/UserVO.java`
- `backend/src/main/java/com/huacai/auth/vo/CurrentUserInfoVO.java`
- `backend/src/test/java/com/huacai/security/ModuleAccessRegistryTest.java`
- `backend/src/test/java/com/huacai/security/ModuleAccessFilterTest.java`
- `backend/src/test/java/com/huacai/system/service/impl/SystemManageServiceImplTest.java`
- `sql/init/001_schema.sql`
- `sql/migration/V003_add_sys_user_module.sql`

说明：

- `OpportunityListView.vue` 和 `OrgManageView.vue` 只做了最小 TypeScript 构建修复，不涉及业务扩展。
- 登录页提示、登录态恢复去重、loan 新增语义收口都已在本轮完成。
- 本轮没有改 UI 风格，没有扩 customer / loan / repayment 以外的新业务功能。

## 本轮环境事实

### 1. 联调开始时的真实状态

- `huacai-backend-test` 容器在运行
- `huacai-backend-proxy-live` 容器在运行，宿主机可通过 `127.0.0.1:18081` 访问后端
- `huacai-mysql` 初始状态为 **Exited (137)**

### 2. 为什么不能只看 `/health`

实际排查发现：

- 当前 `huacai-backend-test` 不是标准化启动容器
- 容器主进程是 `tail -f /dev/null`
- 容器内另起了一个后台 `mvn spring-boot:run`
- 后端数据库地址被写死成 `jdbc:mysql://172.17.0.4:3306/huacai_system...`

因此：

- `/api/v1/health` 返回 200 只能说明 Spring Boot 进程活着
- 不能证明登录、查询、落库链路真的可用

### 3. 本轮环境恢复动作

本轮实际执行：

```bash
docker start huacai-mysql
docker exec huacai-mysql sh -lc 'mysqladmin ping -h127.0.0.1 -uroot -proot --silent'
```

实际结果：

- `huacai-mysql` 已恢复运行
- `mysqld is alive`
- 当前 MySQL 在 `bridge` 网络上的 IP 仍为 `172.17.0.4`
- 与运行中的 `huacai-backend-test` 写死 JDBC 地址一致，所以本轮真实联调可继续执行

## 本轮验证方式

本轮没有直接做浏览器自动化操作，采用以下方式完成验证：

1. 读取前端页面代码路径，确认页面按钮、抽屉、提交和刷新链路对应的真实接口。
2. 直接调用真实后端接口，完成 customer / loan / repayment 的新增、编辑、分页和联动验证。
3. 直接查询 MySQL，确认落库后的 loan balance/status、customer.loan_status 和更新时间。
4. 运行前端登录态恢复测试与前端构建，补齐收口证据。

这符合当前项目要求：

- 优先真实页面验证
- 若当前环境不方便浏览器操作，也可以用前端代码路径 + 真实接口调用逐项验证提交链

## 核心验证命令

### 1. 后端存活与登录

```bash
curl -s -i http://127.0.0.1:18081/api/v1/health
curl --max-time 20 -s -i -X POST http://127.0.0.1:18081/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"123456"}'
```

实际结果：

- `/health` 返回 200
- `/auth/login` 返回 200
- 管理员账号可登录，返回真实 token 与 `管理员 / 总部`

### 2. 前端登录态恢复验证

```bash
cd frontend
npm run test:unit
```

实际结果：

- 5 个测试全部通过
- 已验证：
  - 无 token 时跳过恢复
  - 有 token 时启动前调用 `refreshUser`
  - 恢复失败时会登出并跳转 `/login`
  - loan 新增模式下隐藏后端回算字段并显示说明
  - loan 编辑模式下显示“借贷状态 / 当前余额”字段

### 3. 前端构建验证

```bash
cd frontend
npm run build
```

实际结果：

- 本轮开始时 build 失败
- 失败原因是两个占位页面的 TypeScript 编译问题，不在 customer / loan / repayment 主链上
- 本轮做了最小修复后重新执行，build 已通过

### 4. 本轮收尾问题验证

```bash
rg -n "开发联调默认管理员账号|新增时“借贷状态”“当前余额”" frontend/dist/assets
if rg -n "onMounted" frontend/src/layout/AppLayout.vue; then exit 1; else echo "AppLayout no longer contains onMounted auth restore"; fi
```

实际结果：

- 登录页显式联调提示已进入构建产物
- loan 新增说明已进入构建产物
- `AppLayout.vue` 已不再包含重复的 `onMounted -> refreshUser()` 逻辑

## 补充记录：用户直配业务模块 + 普通用户欢迎页 + 后端接口拦截

### 1. 本轮目标

- 新增/编辑用户时可直接勾选普通用户可见业务模块
- 普通用户登录后进入欢迎页，不再进入经营工作台
- 前端菜单和路由按 `visibleModuleKeys` 过滤
- 后端未授权业务接口和系统管理接口直接返回 403

### 2. 本轮新增验证命令

```bash
cd frontend
npm run test:unit
npm run build

docker exec huacai-backend-test sh -lc 'cd /workspace && mvn -Dtest=ModuleAccessRegistryTest,ModuleAccessFilterTest,SystemManageServiceImplTest test'

curl -s http://127.0.0.1:18081/api/v1/health
```

### 3. 本轮真实验证结果

- `frontend`：
  - `npm run test:unit` 通过，11/11
  - `npm run build` 通过
  - `http://127.0.0.1:4174` 返回 200
  - `http://127.0.0.1:4174/api/v1/health` 通过前端代理返回 200

- `backend`：
  - 在 `huacai-backend-test` 容器中执行新增测试，9/9 通过
  - 已新增 `sys_user_module` 表，并在真实 MySQL 中完成落表
  - 后端健康检查恢复 200

- `真实权限链路`：
  - 管理员 `admin` 登录后仍可访问工作台，`/api/v1/workbench/overview` 返回 200
  - 已将普通用户 `zhangyiyi(user_id=2)` 配置为：
    - `visibleModuleKeys = ["customers", "repayments"]`
  - `zhangyiyi` 重新登录后，`/api/v1/auth/me` 返回：
    - `roles = ["STAFF"]`
    - `visibleModuleKeys = ["customers", "repayments"]`
  - `zhangyiyi` 访问授权接口：
    - `/api/v1/customers` 返回 200
    - `/api/v1/repayments` 返回 200
  - `zhangyiyi` 访问未授权或管理员接口：
    - `/api/v1/loan-orders` 返回 403
    - `/api/v1/opportunities` 返回 403
    - `/api/v1/system/users` 返回 403
    - `/api/v1/workbench/overview` 返回 403

### 4. 交互收口结果

- 普通用户首页已切为欢迎页，基于当前授权模块展示入口卡片
- 左侧菜单对普通用户只保留欢迎页和已授权业务模块
- 用户抽屉新增“业务模块可见范围”区块：
  - 分组勾选
  - 已选数量摘要
  - 全选 / 清空 / 恢复默认
  - 管理员账号只读提示
- 登录态恢复已补充“刷新后无权访问当前路由则跳回默认首页”的处理

### 5. 当前剩余风险

- `huacai-backend-test` 容器的默认启动方式仍然脆弱，原始命令写死 MySQL IP；本轮为了恢复真实联调，实际改为在容器内使用 `huacai-mysql` 主机名重新拉起 Spring Boot 进程。
- 当前权限只到“模块访问控制”，还没有做数据范围隔离；给用户开放某个模块后，他仍能看到该模块当前全部数据。

## 本轮真实联调数据

本轮使用的真实联调客户：

- `customer_id = 6`
- `customer_no = KH-CLOSURE-0415104031`
- 最终客户名称：`一期收口客户-0415104031-更新`

本轮产生的真实借贷单：

- `loan_id = 1`，资金来源 `SELF`
- `loan_id = 2`，资金来源 `BANK`

本轮产生的真实还款记录：

- `repayment_id = 1`，作用于 `loan_id = 1`
- `repayment_id = 2`，作用于 `loan_id = 1`
- `repayment_id = 3`，作用于 `loan_id = 2`

## 页面联调结果

### 1. customer 页面

对应页面代码：

- `frontend/src/views/customer/CustomerListView.vue`
- `frontend/src/api/customer.ts`

实际验证内容：

- 列表加载：通过
- 分页：通过
- 抽屉新增：通过
- 抽屉编辑：通过
- 提交后刷新：通过

实际结果：

- 新增客户成功，生成 `customer_id = 6`
- 通过 `/api/v1/customers?keyword=KH-CLOSURE-0415104031` 可查到该记录
- 编辑后再次查询，客户名称已更新为 `一期收口客户-0415104031-更新`
- `pageSize=1` 时：
  - `pageNum=1` 返回一条记录
  - `pageNum=2` 返回另一条记录
  - 总数为 `6`

### 2. loan 页面

对应页面代码：

- `frontend/src/views/loan/LoanOrderListView.vue`
- `frontend/src/api/loan.ts`

实际验证内容：

- 列表加载：通过
- 分页：通过
- 抽屉新增：通过
- 抽屉编辑：通过
- 提交后刷新：通过

实际结果：

- 已成功创建 2 笔借贷单，`customer_id = 6` 下总数为 `2`
- `loan_id = 1` 编辑后，银行名称已更新为 `联调银行-A-0415104031-更新`
- `pageSize=1` 时：
  - `pageNum=1` 返回一条借贷单
  - `pageNum=2` 返回另一条借贷单
  - 总数为 `2`

### 3. repayment 页面

对应页面代码：

- `frontend/src/views/loan/RepaymentListView.vue`
- `frontend/src/api/loan.ts`

实际验证内容：

- 列表加载：通过
- 分页：通过
- 抽屉新增：通过
- 抽屉编辑：通过
- 提交后刷新：通过

实际结果：

- 对 `loan_id = 1` 创建了 2 笔还款
- `repayment_id = 2` 编辑后：
  - `repaymentChannel` 已更新
  - `repaymentAmount = 2260.00`
  - `interestAmount = 260.00`
- 对 `loan_id = 1` 的还款分页验证：
  - `pageSize=1`
  - `pageNum=1` / `pageNum=2` 均能取到记录
  - 总数为 `2`

## 联动链验证结果

### 1. repayment -> loan

实际验证过程：

- `repayment_id = 1` 对 `loan_id = 1` 归还本金 `1000`
- 此时 `loan_id = 1` 变为：
  - `balanceAmount = 2000`
  - `status = ACTIVE`

- `repayment_id = 2` 对 `loan_id = 1` 继续归还本金 `2000`
- 此时 `loan_id = 1` 变为：
  - `balanceAmount = 0`
  - `status = SETTLED`

- `repayment_id = 3` 对 `loan_id = 2` 归还本金 `800`
- 此时 `loan_id = 2` 变为：
  - `balanceAmount = 0`
  - `status = SETTLED`

MySQL 实查结果：

- `loan_id = 1`：`balance_amount = 0.00`，`status = SETTLED`
- `loan_id = 2`：`balance_amount = 0.00`，`status = SETTLED`

### 2. loan -> customer.loan_status

实际验证过程：

- 在存在运行中借贷单时，客户 `loanStatus = RUNNING`
- 两笔借贷单全部结清后，客户 `loanStatus = SETTLED`

MySQL 实查结果：

- `customer_id = 6`
- `loan_status = SETTLED`
- `updated_at = 2026-04-15 02:42:54`

### 3. customer / loan / repayment -> workbench

最终 `/api/v1/workbench/overview` 返回：

- `客户总数 = 6`
- `运行中借贷单 = 0`
- `今日还款登记 = 3`
- `资料待补客户 = 0`

`focusRows` 顶部记录已跟随本轮真实数据变化：

- 客户档案：`customer_id = 6`，状态 `SETTLED`
- 借贷主单：`loan_id = 2`，状态 `SETTLED`
- 还款记录：`repayment_id = 3`，关联 `借贷单 #2`
- 借贷主单：`loan_id = 1`，状态 `SETTLED`
- 还款记录：`repayment_id = 1`
- 还款记录：`repayment_id = 2`

结论：

- repayment 落库后，loan balance/status 会更新
- customer.loan_status 会更新
- workbench metricCards 和 focusRows 会跟随真实数据变化

## 本轮额外发现的真实问题

### 1. 联调运行环境仍然偏脆弱

实际现象：

- 本轮开始时 `huacai-mysql` 是退出状态
- `huacai-backend-test` 内部 Spring Boot 仍活着，所以 `/health` 还能返回 200
- 但登录和 CRUD 在 MySQL 未恢复前不能视为真实可用

风险：

- 当前后端测试容器依赖写死 IP `172.17.0.4`
- 如果 MySQL 容器被重建且 IP 改变，这条链路会再次失效

结论：

- 当前环境可以继续用
- 但不适合作为长期稳定的交接运行方式

## 本轮为恢复 build 做的最小修复

### 1. `frontend/src/views/opportunity/OpportunityListView.vue`

- 删除未使用的 `statusOptions`
- 将 `null` 显式归一为 `undefined`
- 目的是修复 TypeScript 编译错误，不改业务逻辑

### 2. `frontend/src/views/system/org/OrgManageView.vue`

- 去掉树节点模板中未使用的 `node` 变量
- 目的是修复 TypeScript 编译错误，不改业务逻辑

## 本轮已收口的事项

### 1. 登录页显式联调说明已补齐

当前现状：

- 页面仍不默认预填 `admin / 123456`
- 登录页已增加显式提示：开发联调默认管理员账号需要手动输入

### 2. 登录态恢复重复兜底已移除

当前代码位置：

- `frontend/src/main.ts`
- `frontend/src/layout/AppLayout.vue`

当前现状：

- 启动前恢复逻辑已经落到 `main.ts`
- `AppLayout.vue` 中重复的 `refreshUser()` 兜底已删除

### 3. loan 新增表单语义已与后端创建逻辑对齐

当前现状：

- 新增借贷单时不再展示“借贷状态 / 当前余额”字段
- 页面会明确提示这两个值由后端回算
- 编辑借贷单时仍保留这两个字段

## 阶段结论

截至 **2026-04-15**，一期第一阶段的真实状态是：

- customer / loan / repayment 三个页面的主链已在真实接口下完成联调
- workbench 统计和 focusRows 已验证会跟随真实数据变化
- 登录态恢复代码已落地，并通过启动恢复测试
- 登录页显式联调说明已补齐
- loan 新增抽屉与后端创建语义已收口
- 前端 build 已重新通过

但同时也要明确：

- 当前环境不是完全稳定的长期运行态
- 本地联调仍依赖一个写死 IP 的后端测试容器

一句话结论：

**一期第一阶段前端收口已基本完成，主链路真实可用，剩余重点已从页面行为收尾转为联调环境稳定性。**

## 下一步建议

下一位 agent 接手时，建议只做以下事情：

1. 若后续还需要频繁联调，优先把 `huacai-backend-test` 的数据库地址从写死 IP 改为稳定容器名。
2. 为 customer / loan / repayment 页补一层浏览器级自动化回归，减少重复人工联调。
3. 若要继续维护当前样例数据，建议把 `customer_id = 6` 这组记录标记为本轮联调样本，避免后续误删。

## 2026-04-15 原型对齐收口补充

### 1. 本轮拍板结论

- `D1 商机管理`：`最近一次跟进时间 / 跟进人 / 跟进记录` 统一按最近一条 follow record 取值。
- `D2 借贷管理`：主表一行代表客户汇总，不再把单笔借贷单直接当成一期最终表格口径。
- `D3 还款明细`：保留全局列表，同时支持按客户/借贷单下钻的 scoped 详情口径。
- `D4 角色管理`：接受原型错位，继续保留当前角色实体页，并在一期交付里作为“已批准替代口径”处理。
- `D5 组织管理`：保留树结构作为正式替代口径，不回退成平铺表格。

### 2. 本轮新增/修改文件

- `frontend/src/api/opportunity.ts`
- `frontend/src/api/loan.ts`
- `frontend/src/api/system.ts`
- `frontend/src/views/customer/CustomerListView.vue`
- `frontend/src/views/opportunity/OpportunityListView.vue`
- `frontend/src/views/loan/LoanOrderListView.vue`
- `frontend/src/views/loan/RepaymentListView.vue`
- `frontend/src/views/system/user/UserManageView.vue`
- `frontend/src/views/system/user/userRoleDisplay.ts`
- `frontend/src/views/system/org/OrgManageView.vue`
- `frontend/tests/user-role-display.test.ts`
- `backend/src/main/java/com/huacai/loan/controller/LoanController.java`
- `backend/src/main/java/com/huacai/loan/service/impl/LoanManageServiceImpl.java`
- `backend/src/main/java/com/huacai/opportunity/service/impl/OpportunityServiceImpl.java`
- `backend/src/main/java/com/huacai/system/service/impl/SystemManageServiceImpl.java`
- `backend/src/main/java/com/huacai/loan/service/LoanManageService.java`
- `backend/src/main/java/com/huacai/loan/vo/LoanOrderOverviewVO.java`
- `backend/src/main/java/com/huacai/loan/vo/LoanRepaymentSummaryVO.java`
- `backend/src/main/java/com/huacai/opportunity/vo/OpportunityVO.java`
- `backend/src/main/java/com/huacai/system/vo/OrgVO.java`
- `backend/src/test/java/com/huacai/opportunity/service/impl/OpportunityServiceImplTest.java`
- `backend/src/test/java/com/huacai/loan/service/impl/LoanManageServiceImplTest.java`
- `backend/src/test/java/com/huacai/system/service/impl/SystemManageServiceImplTest.java`

### 3. 页面收口结果

#### 3.1 customer

- 列表字段已切到原型核心口径：`客户ID / 客户名称 / 联系方式 / 企业名称 / 统一社会信用代码 / 推荐人 / 推荐人返点 / 状态 / 借贷状态 / 服务费`。
- 本轮未实现、也未纳入一期：`测试日期 / 测试额度 / 流量 / 综合评分 / 龙信商`。

#### 3.2 opportunity

- 列表已补 `联系方式 / 企业名称 / 统一社会信用代码 / 跟进摘要 / 备注`。
- 后端 `OpportunityVO` 已返回 `mobile / companyName / creditCode / latestFollowTime / latestFollowerName / latestFollowContent`。
- 若没有 follow record，前端按 `-` 回落，不再拿负责人字段冒充最近跟进人。

#### 3.3 loan-orders

- 新增 `/api/v1/loan-orders/overview`，主表按客户汇总返回借贷概览。
- 页面主表根据 `资金来源` 切换 `SELF/BANK` 两套原型字段口径。
- 原有单笔借贷单能力没有删，放到“借贷单明细”抽屉里继续承接编辑。

#### 3.4 repayments

- 新增 `/api/v1/repayments/summary`，供 scoped 详情口径显示总计摘要。
- 页面继续保留全局列表，同时支持通过 `customerId / customerName / loanOrderId / capitalSourceType` query 进入 scoped 模式。
- scoped 模式已统一字段文案为 `还款金额 / 每月利息 / 还款途径`，并补了 `序号 / 备注 / 总计摘要`。

#### 3.5 system-users

- 列表已补 `角色名称 / 创建时间`。
- 角色名称显示口径与认证链保持一致：无显式角色映射的普通账号默认按 `STAFF` 展示，避免 `auth/me` 与系统用户列表不一致。

#### 3.6 roles

- 当前角色实体页保留，不再继续按原型里的“人员权限页”补错对象字段。
- 该页在一期交付中按“原型错位，已批准替代口径”处理。

#### 3.7 orgs

- 继续保留树结构。
- 树节点已显式展示 `上级机构 / 是否管理机构 / 排序 / 备注`，后端 `OrgVO` 已返回 `parentName`。
- 新增/编辑抽屉可直接选择上级机构，作为树结构下的正式替代交互。

### 4. 本轮验证命令

```bash
cd frontend
npm run test:unit
npm run build

docker exec huacai-backend-test sh -lc 'cd /workspace && mvn -q -Dtest=OpportunityServiceImplTest test'
docker exec huacai-backend-test sh -lc 'cd /workspace && mvn -q -Dtest=LoanManageServiceImplTest test'
docker exec huacai-backend-test sh -lc 'cd /workspace && mvn -q -Dtest=SystemManageServiceImplTest test'

curl --max-time 10 -s -X POST http://127.0.0.1:18081/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"123456"}'

curl -s http://127.0.0.1:18081/api/v1/loan-orders/overview?pageNum=1&pageSize=5&capitalSourceType=SELF
curl -s http://127.0.0.1:18081/api/v1/repayments/summary?pageNum=1&pageSize=10&customerId=6&capitalSourceType=SELF
curl -s http://127.0.0.1:18081/api/v1/customers?pageNum=1&pageSize=3
curl -s http://127.0.0.1:18081/api/v1/system/users?pageNum=1&pageSize=3
curl -s http://127.0.0.1:18081/api/v1/system/orgs/tree
curl -s http://127.0.0.1:18081/api/v1/opportunities?pageNum=1&pageSize=3
```

### 5. 本轮实际验证结果

- `frontend`
  - `npm run test:unit` 通过，`20/20`
  - `npm run build` 通过

- `backend`
  - `OpportunityServiceImplTest` 通过
  - `LoanManageServiceImplTest` 通过
  - `SystemManageServiceImplTest` 通过

- `真实接口`
  - `/api/v1/auth/login` 恢复 200，可正常返回 token
  - `/api/v1/loan-orders/overview?pageNum=1&pageSize=5&capitalSourceType=SELF` 返回：
    - `customerId=6`
    - `customerNo=KH-CLOSURE-0415104031`
    - `customerName=一期收口客户-0415104031-更新`
    - `totalLoanAmount=3000`
    - `repaidAmount=3380`
    - `pendingAmount=0`
  - `/api/v1/repayments/summary?pageNum=1&pageSize=10&customerId=6&capitalSourceType=SELF` 返回：
    - `recordCount=2`
    - `repaymentAmountTotal=3380`
    - `principalAmountTotal=3000`
    - `interestAmountTotal=380`
  - `/api/v1/customers?pageNum=1&pageSize=3` 已返回 `creditCode / recommenderName / recommenderRate / bizStatus`
  - `/api/v1/system/users?pageNum=1&pageSize=3` 已返回 `createdAt / roleCodes`
  - `/api/v1/system/orgs/tree` 已返回 `parentName`
  - `/api/v1/opportunities?pageNum=1&pageSize=3` 已返回 `mobile / companyName / creditCode / latestFollowTime / latestFollowerName / latestFollowContent`

### 6. 本轮环境动作与风险

- 本轮验证中 `huacai-mysql` 一度再次变成 `Exited (137)`，导致登录超时。
- 恢复步骤：
  1. `docker start huacai-mysql`
  2. 在 `huacai-backend-test` 容器内重新拉起 Spring Boot，并显式注入：
     - `HUACAI_DB_URL=jdbc:mysql://huacai-mysql:3306/huacai_system?...`
     - `HUACAI_DB_USERNAME=root`
     - `HUACAI_DB_PASSWORD=root`
- 结论不变：当前一期主链已收口，但联调环境仍然脆弱，根因不是页面代码，而是运行时启动方式不稳定。

### 7. 当前仍未纳入一期的内容

- 工作台表格与视觉口径
- 客户页：`测试日期 / 测试额度 / 流量 / 综合评分 / 龙信商`
- 借贷银行版：`总增量金额 / 增量笔数 / 几年期`
- 还款银行版：`剩余期数`
- finance / 审批流 / 报表 / 导入导出 / 复杂权限
