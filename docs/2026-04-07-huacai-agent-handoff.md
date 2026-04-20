# 华彩系统接手交接文档

更新时间：2026-04-07

## 1. 项目当前结论

当前项目已经从“页面骨架”推进到“真实数据闭环可跑通”的状态，核心链路已经具备可用基础：

- 登录已接真实 `MySQL + JWT`
- 工作台首页已接真实统计与最近业务记录
- 客户列表、借贷列表、还款列表已接真实查询
- 客户、借贷、还款的新增/编辑抽屉已可真实提交并落库
- 当前前端 UI 基线已经收敛完成，不要再重做视觉方向

但这版还没有到“稳定可交付”的程度，主要短板在：

- 后端输入校验不完整
- 部分嵌套路由缺少归属校验
- 唯一约束冲突会掉到通用 500
- 范围外接口还保留占位实现

## 2. 接手范围与约束

这是接手时必须遵守的边界：

- 不要重做当前 UI 风格
- 不要偏离现有 `A1 钴蓝企业版` 基线
- 页面上不要再出现“`一期` / 联调 / 演示 / 骨架 / 真实 MySQL”之类提示性文案
- 当前优先模块只看：
  - `auth`
  - `workbench`
  - `customer`
  - `loan`
  - `repayment`
- 不要顺手扩到审批流、复杂报表、导入导出、细粒度权限
- 不要借机做大规模后端重构

一句话：继续围绕“当前页面已经存在，需要真实数据稳定驱动”推进。

## 3. 目录与关键文件

### 前端

- `/Users/edy/Documents/tob /华彩系统1025/frontend/src/layout/AppLayout.vue`
- `/Users/edy/Documents/tob /华彩系统1025/frontend/src/views/auth/LoginView.vue`
- `/Users/edy/Documents/tob /华彩系统1025/frontend/src/views/dashboard/WorkbenchView.vue`
- `/Users/edy/Documents/tob /华彩系统1025/frontend/src/views/customer/CustomerListView.vue`
- `/Users/edy/Documents/tob /华彩系统1025/frontend/src/views/loan/LoanOrderListView.vue`
- `/Users/edy/Documents/tob /华彩系统1025/frontend/src/views/loan/RepaymentListView.vue`
- `/Users/edy/Documents/tob /华彩系统1025/frontend/src/api/http.ts`
- `/Users/edy/Documents/tob /华彩系统1025/frontend/src/api/auth.ts`
- `/Users/edy/Documents/tob /华彩系统1025/frontend/src/api/customer.ts`
- `/Users/edy/Documents/tob /华彩系统1025/frontend/src/api/loan.ts`
- `/Users/edy/Documents/tob /华彩系统1025/frontend/src/api/workbench.ts`
- `/Users/edy/Documents/tob /华彩系统1025/frontend/src/stores/auth.ts`
- `/Users/edy/Documents/tob /华彩系统1025/frontend/src/router/index.ts`
- `/Users/edy/Documents/tob /华彩系统1025/frontend/src/router/menu.ts`
- `/Users/edy/Documents/tob /华彩系统1025/frontend/src/style.css`
- `/Users/edy/Documents/tob /华彩系统1025/frontend/vite.config.ts`

### 后端

- `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/resources/application.yml`
- `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/config/SecurityConfig.java`
- `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/config/DataInitializer.java`
- `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/auth/controller/AuthController.java`
- `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/auth/service/impl/AuthServiceImpl.java`
- `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/security/JwtAuthenticationFilter.java`
- `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/security/JwtTokenService.java`
- `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/customer/controller/CustomerController.java`
- `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/customer/service/impl/CustomerServiceImpl.java`
- `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/loan/controller/LoanController.java`
- `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/loan/service/impl/LoanManageServiceImpl.java`
- `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/workbench/controller/WorkbenchController.java`
- `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/workbench/service/impl/WorkbenchServiceImpl.java`
- `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/common/exception/GlobalExceptionHandler.java`

### 数据与脚本

- `/Users/edy/Documents/tob /华彩系统1025/sql/init/001_schema.sql`
- `/Users/edy/Documents/tob /华彩系统1025/docker-compose.mysql.yml`
- `/Users/edy/Documents/tob /华彩系统1025/README.md`

## 4. 当前运行状态

我在交接前重新确认过当前运行状态：

- 前端开发服务可访问：`http://127.0.0.1:5173/`
- 后端代理入口可访问：`http://127.0.0.1:18081/api/v1/health`
- MySQL 容器健康

当前 Docker 容器状态：

- `huacai-mysql`
- `huacai-backend-test`
- `huacai-backend-proxy-live`

当前确认可工作的访问地址：

- 前端：`http://127.0.0.1:5173/`
- 后端代理：`http://127.0.0.1:18081`
- MySQL：`127.0.0.1:3306`

说明：

- 前端代理默认不是走宿主机 `8080`，而是走 `18081`
- 之前宿主机 `8080` 被别的服务占用过，所以当前联调以 `18081` 为准

## 5. 当前数据库与初始化数据

数据库名：

- `huacai_system`

当前已确认存在并被实际使用的核心表：

- `sys_org`
- `sys_user`
- `sys_role`
- `sys_user_role`
- `cust_customer`
- `loan_order`
- `loan_repayment`

初始化账号：

- 用户名：`admin`
- 密码：`123456`

当前库里已经有真实样例数据，且我已经把会显示到页面上的测试命名改成正式中性数据：

- `华彩客户001`
- `华彩客户002`
- `华彩客户003`
- `华彩客户004`

已经清掉的页面敏感测试词包括：

- `一期`
- `联调`
- `PHASE1`
- `VERIFY`
- `CLOSURE`

## 6. 已完成内容

### 6.1 前端

- 登录页已接真实登录接口
- 登录状态可落本地并恢复
- 顶部工作台、客户、借贷、还款页已接真实接口
- 客户、借贷、还款抽屉已支持新增/编辑
- 基础 `loading / error` 已处理
- UI 已去掉开发提示、阶段提示、演示口吻
- 右上角用户信息已改为单行显示：`管理员 总部`

### 6.2 后端

- `auth` 已切到真实用户表和 JWT
- `workbench` 已返回真实统计数据
- `customer` 已支持分页、详情、新增、编辑
- `loan` 已支持分页、详情、新增、编辑
- `repayment` 已支持分页、详情、新增、编辑
- 还款落库后会联动刷新借贷单余额与客户借贷状态
- 启动时会自动初始化组织、角色、管理员账号

### 6.3 联调结果

已经实际跑通过以下链路：

- 登录
- 工作台概览
- 客户列表
- 借贷列表
- 还款列表
- 客户新增/编辑
- 借贷新增/编辑
- 还款新增/编辑

## 7. 我刚 review 出来的关键风险

这部分是最重要的接手清单。

### P1

1. 还款子资源路径没有校验父借贷单归属

- 文件：
  [LoanController.java](/Users/edy/Documents/tob%20/%E5%8D%8E%E5%BD%A9%E7%B3%BB%E7%BB%9F1025/backend/src/main/java/com/huacai/loan/controller/LoanController.java#L76)
  [LoanManageServiceImpl.java](/Users/edy/Documents/tob%20/%E5%8D%8E%E5%BD%A9%E7%B3%BB%E7%BB%9F1025/backend/src/main/java/com/huacai/loan/service/impl/LoanManageServiceImpl.java#L146)
- 实际验证过：
  `GET /api/v1/loan-orders/999999/repayments/1` 仍然能返回 `id=1` 的还款记录
- 说明：
  当前嵌套路由只是 URL 长得像子资源，实际没有做归属校验

2. 还款新增/编辑缺少后端必填校验，坏请求会掉到 500

- 文件：
  [LoanRepaymentSaveRequest.java](/Users/edy/Documents/tob%20/%E5%8D%8E%E5%BD%A9%E7%B3%BB%E7%BB%9F1025/backend/src/main/java/com/huacai/loan/dto/LoanRepaymentSaveRequest.java#L6)
  [LoanController.java](/Users/edy/Documents/tob%20/%E5%8D%8E%E5%BD%A9%E7%B3%BB%E7%BB%9F1025/backend/src/main/java/com/huacai/loan/controller/LoanController.java#L109)
  [LoanManageServiceImpl.java](/Users/edy/Documents/tob%20/%E5%8D%8E%E5%BD%A9%E7%B3%BB%E7%BB%9F1025/backend/src/main/java/com/huacai/loan/service/impl/LoanManageServiceImpl.java#L194)
- 实际验证过：
  缺少 `repaymentDate` 的 `POST /api/v1/repayments` 返回 HTTP 500

### P2

3. 客户编号重复时没有业务化报错，用户看到的是通用 500

- 文件：
  [CustomerServiceImpl.java](/Users/edy/Documents/tob%20/%E5%8D%8E%E5%BD%A9%E7%B3%BB%E7%BB%9F1025/backend/src/main/java/com/huacai/customer/service/impl/CustomerServiceImpl.java#L69)
  [GlobalExceptionHandler.java](/Users/edy/Documents/tob%20/%E5%8D%8E%E5%BD%A9%E7%B3%BB%E7%BB%9F1025/backend/src/main/java/com/huacai/common/exception/GlobalExceptionHandler.java#L58)
- 实际验证过：
  重复提交 `customerNo=HC20260401001` 会返回 HTTP 500

4. 新增借贷单时，余额和状态仍信任前端传值，首单数据可能不一致

- 文件：
  [LoanManageServiceImpl.java](/Users/edy/Documents/tob%20/%E5%8D%8E%E5%BD%A9%E7%B3%BB%E7%BB%9F1025/backend/src/main/java/com/huacai/loan/service/impl/LoanManageServiceImpl.java#L91)
  [LoanManageServiceImpl.java](/Users/edy/Documents/tob%20/%E5%8D%8E%E5%BD%A9%E7%B3%BB%E7%BB%9F1025/backend/src/main/java/com/huacai/loan/service/impl/LoanManageServiceImpl.java#L179)
  [LoanOrderListView.vue](/Users/edy/Documents/tob%20/%E5%8D%8E%E5%BD%A9%E7%B3%BB%E7%BB%9F1025/frontend/src/views/loan/LoanOrderListView.vue#L30)
- 说明：
  现在可以在前端直接提交不一致的 `balanceAmount / status`

### P3

5. 范围外接口仍有大量占位实现，不能误判成已完成

- 文件：
  [CustomerController.java](/Users/edy/Documents/tob%20/%E5%8D%8E%E5%BD%A9%E7%B3%BB%E7%BB%9F1025/backend/src/main/java/com/huacai/customer/controller/CustomerController.java#L62)
  [LoanController.java](/Users/edy/Documents/tob%20/%E5%8D%8E%E5%BD%A9%E7%B3%BB%E7%BB%9F1025/backend/src/main/java/com/huacai/loan/controller/LoanController.java#L59)
  [LoanController.java](/Users/edy/Documents/tob%20/%E5%8D%8E%E5%BD%A9%E7%B3%BB%E7%BB%9F1025/backend/src/main/java/com/huacai/loan/controller/LoanController.java#L121)
- 说明：
  删除、导入、客户评分/负债/合同/交易等接口大量还是 `success()` 占位

## 8. 推荐接手顺序

不要发散，建议按这个顺序继续：

1. 修后端校验
- 给 `LoanRepaymentSaveRequest`
- 给 `LoanOrderSaveRequest`
- 必要时补 `@Valid`
- 把明显的空字段问题从 500 改成 400

2. 修还款子资源归属校验
- `/loan-orders/{loanOrderId}/repayments/{id}`
- `/loan-orders/{loanOrderId}/repayments`
- 更新时要同时校验老记录归属和新归属

3. 修客户编号重复报错
- 捕获唯一约束异常
- 返回明确业务文案，而不是“系统异常，请稍后重试”

4. 收口借贷主单回算规则
- 创建时也统一走回算
- 不再把 `balanceAmount / status` 作为前端可信输入

5. 再做一次页面联调回归
- 登录
- 工作台
- 客户列表 + 抽屉
- 借贷列表 + 抽屉
- 还款列表 + 抽屉

## 9. 启动与验证命令

### MySQL

```bash
cd "/Users/edy/Documents/tob /华彩系统1025"
docker compose -f docker-compose.mysql.yml up -d
```

### 后端健康检查

```bash
curl -sS http://127.0.0.1:18081/api/v1/health
```

### 登录测试

```bash
curl -sS -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"123456"}' \
  http://127.0.0.1:18081/api/v1/auth/login
```

### 前端

```bash
cd "/Users/edy/Documents/tob /华彩系统1025/frontend"
npm run dev
```

### 前端构建

```bash
cd "/Users/edy/Documents/tob /华彩系统1025/frontend"
npm run build
```

## 10. 前端视觉与文案注意事项

这是用户已经明确说过的，不要回退：

- 不要重新设计整体 UI
- 保持当前字体、颜色、卡片、侧栏和工作台风格
- 页面不要再出现任何“`一期` / 联调 / 演示 / 骨架 / 真实数据接入说明”文案
- 顶部右上角用户区保持单行
- 左上品牌区保持当前正式中文文案，不要改回英文双行

## 11. 补充说明

- 当前目录不是一个可直接在这里执行 `git status` 的仓库根目录，交接时不要默认依赖 Git diff 来理解变更
- 之前为了验证 UI 走查，我使用过 Playwright 临时脚本，但临时文件已经删除
- 目前页面本身没有明显的阶段性提示残留，前端构建也通过

## 12. 交接建议

接手的 Agent 最好直接从“修后端稳定性”开始，而不是继续做新页面。

当前真正影响使用体验的不是 UI，而是这 4 个点：

- 还款路由归属校验
- DTO 必填校验
- 唯一约束业务化报错
- 借贷单创建回算一致性

把这 4 个点修掉，这版系统才算从“能跑”进入“能稳用”。
