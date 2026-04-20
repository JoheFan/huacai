# 华彩项目多窗口模块提示词

本文档用于给其他窗口/其他 agent 分发模块任务。  
当前默认前提：

- 项目路径：`/Users/edy/Documents/tob /华彩系统1025`
- UI 基线已定，禁止重做视觉方向
- 当前正在按模块逐个收口
- `customer / 风险评估 / 负债登记` 这条线正在单独梳理，其他窗口不要碰
- 本地启动链统一使用：`./scripts/dev/up-local-stack.sh`
- 不进入第二阶段，不扩审批流、报表、导入导出、复杂权限

统一禁改文件：

- `frontend/src/router/menu.ts`
- `frontend/src/router/index.ts`
- `frontend/src/layout/AppLayout.vue`
- `frontend/src/stores/auth.ts`
- `frontend/src/views/customer/**`
- `backend/src/main/java/com/huacai/customer/**`
- `docker-compose.local.yml`
- `docker-compose.mysql.yml`
- `scripts/dev/**`

统一输出要求：

- 只做分配到的模块，不改别的模块
- 不要只汇报“应该可以”，每一项都要给实际验证结果
- 最后必须输出：
  - 修改文件
  - 验证命令
  - 实际结果
  - 剩余问题
- 需写 handoff 文档到：
  - `/Users/edy/Documents/tob /华彩系统1025/docs/superpowers/specs/YYYY-MM-DD-<模块名>-handoff.md`

## 系统用户

```text
你现在接手华彩项目里的【系统用户】模块，只做这个模块，不要改别的模块。

项目路径：
/Users/edy/Documents/tob /华彩系统1025

当前背景：
- UI 基线已定，禁止重做视觉方向
- 当前正在按模块逐个收口
- customer / 风险评估 / 负债登记 这条线正在单独处理，你不要碰
- 启动链已经收口，使用 `./scripts/dev/up-local-stack.sh`
- 不进入第二阶段，不扩审批流、报表、导入导出、复杂权限

你的职责范围：
- 只负责系统用户模块
- 可改文件范围：
  - `frontend/src/views/system/user/UserManageView.vue`
  - `frontend/src/api/system.ts`
  - `backend/src/main/java/com/huacai/system/controller/UserController.java`
  - `backend/src/main/java/com/huacai/system/service/impl/SystemManageServiceImpl.java`
  - `backend/src/main/java/com/huacai/system/service/SystemManageService.java`
  - `backend/src/main/java/com/huacai/system/dto/UserCreateRequest.java`
  - `backend/src/main/java/com/huacai/system/dto/UserUpdateRequest.java`
  - `backend/src/main/java/com/huacai/system/vo/UserVO.java`
  - `backend/src/main/java/com/huacai/system/query/UserPageQuery.java`
  - `backend/src/main/java/com/huacai/system/entity/SysUser.java`
  - `backend/src/main/java/com/huacai/system/entity/SysUserRole.java`
  - `backend/src/main/java/com/huacai/system/entity/SysUserModule.java`
  - `backend/src/main/java/com/huacai/system/mapper/UserMapper.java`
  - `backend/src/main/java/com/huacai/system/mapper/UserRoleMapper.java`
  - `backend/src/main/java/com/huacai/system/mapper/UserModuleMapper.java`
  - `backend/src/test/java/com/huacai/system/service/impl/SystemManageServiceImplTest.java`
- 不允许改：
  - `frontend/src/router/menu.ts`
  - `frontend/src/router/index.ts`
  - `frontend/src/layout/AppLayout.vue`
  - `frontend/src/views/customer/**`
  - `backend/src/main/java/com/huacai/customer/**`
  - `docker-compose.local.yml`
  - `scripts/dev/**`

目标：
1. 对照原型完成系统用户页面字段和交互收口
2. 真实接口联调可用
3. 列表 / 分页 / 新增 / 编辑链路验证通过
4. 前端构建通过
5. 写 handoff 文档

强约束：
- 不改 UI 风格
- 不扩无关模块
- 不做系统级大重构
- 如果必须补一个后端字段或 VO，只能围绕系统用户模块

交付标准：
- 系统用户列表字段和原型一致或有明确差异说明
- 新增/编辑用户可通过真实接口保存和回显
- `cd frontend && npm run build` 通过
- 如涉及后端，测试或接口验证通过
- handoff 文档已落盘

最后输出：
- 改了哪些文件
- 跑了哪些验证命令
- 实际结果
- 剩余问题
```

## 组织管理

```text
你现在接手华彩项目里的【组织管理】模块，只做这个模块，不要改别的模块。

项目路径：
/Users/edy/Documents/tob /华彩系统1025

当前背景：
- UI 基线已定，禁止重做视觉方向
- 当前正在按模块逐个收口
- customer / 风险评估 / 负债登记 这条线正在单独处理，你不要碰
- 启动链已经收口，使用 `./scripts/dev/up-local-stack.sh`
- 组织管理当前允许保留树结构作为正式替代方案

你的职责范围：
- 只负责组织管理模块
- 可改文件范围：
  - `frontend/src/views/system/org/OrgManageView.vue`
  - `frontend/src/api/system.ts`
  - `backend/src/main/java/com/huacai/system/controller/OrgController.java`
  - `backend/src/main/java/com/huacai/system/service/impl/SystemManageServiceImpl.java`
  - `backend/src/main/java/com/huacai/system/service/SystemManageService.java`
  - `backend/src/main/java/com/huacai/system/dto/OrgCreateRequest.java`
  - `backend/src/main/java/com/huacai/system/dto/OrgUpdateRequest.java`
  - `backend/src/main/java/com/huacai/system/vo/OrgVO.java`
  - `backend/src/main/java/com/huacai/system/entity/SysOrg.java`
  - `backend/src/main/java/com/huacai/system/mapper/OrgMapper.java`
  - `backend/src/main/java/com/huacai/system/mapper/SysOrgMapper.java`
  - `backend/src/test/java/com/huacai/system/service/impl/SystemManageServiceImplTest.java`
- 不允许改：
  - `frontend/src/router/menu.ts`
  - `frontend/src/router/index.ts`
  - `frontend/src/layout/AppLayout.vue`
  - `frontend/src/views/customer/**`
  - `backend/src/main/java/com/huacai/customer/**`
  - `docker-compose.local.yml`
  - `scripts/dev/**`

目标：
1. 按“树结构保留，但字段可见性对齐原型”收口组织管理
2. 完成列表/树、新增、编辑、状态链路验证
3. 前端构建通过
4. 写 handoff 文档

强约束：
- 不改成完全不同的信息架构
- 不额外扩组织权限体系
- 不触碰客户、借贷、还款模块

交付标准：
- 组织管理的字段展示与原型差异被明确收口
- 真实接口可查、可增、可改
- `cd frontend && npm run build` 通过
- 如涉及后端，测试或接口验证通过
- handoff 文档已落盘

最后输出：
- 改了哪些文件
- 跑了哪些验证命令
- 实际结果
- 剩余问题
```

## 角色管理

```text
你现在接手华彩项目里的【角色管理】模块，只做这个模块，不要改别的模块。

项目路径：
/Users/edy/Documents/tob /华彩系统1025

当前背景：
- UI 基线已定，禁止重做视觉方向
- 当前正在按模块逐个收口
- customer / 风险评估 / 负债登记 这条线正在单独处理，你不要碰
- 启动链已经收口，使用 `./scripts/dev/up-local-stack.sh`
- 角色管理已确认“接受原型错位并保留当前角色实体页”

你的职责范围：
- 只负责角色管理模块
- 可改文件范围：
  - `frontend/src/views/system/role/RoleManageView.vue`
  - `frontend/src/api/system.ts`
  - `backend/src/main/java/com/huacai/system/controller/RoleController.java`
  - `backend/src/main/java/com/huacai/system/service/impl/SystemManageServiceImpl.java`
  - `backend/src/main/java/com/huacai/system/service/SystemManageService.java`
  - `backend/src/main/java/com/huacai/system/dto/RoleCreateRequest.java`
  - `backend/src/main/java/com/huacai/system/dto/RoleUpdateRequest.java`
  - `backend/src/main/java/com/huacai/system/dto/RoleMenuAssignRequest.java`
  - `backend/src/main/java/com/huacai/system/vo/RoleVO.java`
  - `backend/src/main/java/com/huacai/system/entity/SysRole.java`
  - `backend/src/main/java/com/huacai/system/mapper/RoleMapper.java`
  - `backend/src/main/java/com/huacai/system/mapper/SysRoleMapper.java`
  - `backend/src/test/java/com/huacai/system/service/impl/SystemManageServiceImplTest.java`
- 不允许改：
  - `frontend/src/router/menu.ts`
  - `frontend/src/router/index.ts`
  - `frontend/src/layout/AppLayout.vue`
  - `frontend/src/views/customer/**`
  - `backend/src/main/java/com/huacai/customer/**`
  - `docker-compose.local.yml`
  - `scripts/dev/**`

目标：
1. 以“角色实体页”为准完成角色管理收口
2. 对齐当前确认口径，不再按错位原型改成员工页
3. 列表 / 分页 / 新增 / 编辑 / 状态链路验证通过
4. 前端构建通过
5. 写 handoff 文档

强约束：
- 不重定义路由对象
- 不扩复杂菜单授权体系
- 不触碰系统用户和组织管理以外的共享逻辑

交付标准：
- 角色页按当前角色实体口径收口
- 真实接口返回正常
- `cd frontend && npm run build` 通过
- 如涉及后端，测试或接口验证通过
- handoff 文档已落盘

最后输出：
- 改了哪些文件
- 跑了哪些验证命令
- 实际结果
- 剩余问题
```

## 借贷管理

```text
你现在接手华彩项目里的【借贷管理】模块，只做这个模块，不要改别的模块。

项目路径：
/Users/edy/Documents/tob /华彩系统1025

当前背景：
- UI 基线已定，禁止重做视觉方向
- 当前正在按模块逐个收口
- customer / 风险评估 / 负债登记 这条线正在单独处理，你不要碰
- 启动链已经收口，使用 `./scripts/dev/up-local-stack.sh`
- 借贷管理当前已确认：主列表一行代表客户汇总

你的职责范围：
- 只负责借贷管理模块
- 可改文件范围：
  - `frontend/src/views/loan/LoanOrderListView.vue`
  - `frontend/src/views/loan/loanOverviewMode.ts`
  - `frontend/src/views/loan/loanOrderFormMode.ts`
  - `frontend/src/api/loan.ts`
  - `frontend/tests/loan-overview-mode.test.ts`
  - `frontend/tests/loan-order-form-mode.test.ts`
  - `backend/src/main/java/com/huacai/loan/controller/LoanController.java`
  - `backend/src/main/java/com/huacai/loan/service/impl/LoanManageServiceImpl.java`
  - `backend/src/main/java/com/huacai/loan/service/LoanManageService.java`
  - `backend/src/main/java/com/huacai/loan/query/LoanOrderPageQuery.java`
  - `backend/src/main/java/com/huacai/loan/dto/LoanOrderSaveRequest.java`
  - `backend/src/main/java/com/huacai/loan/vo/LoanOrderVO.java`
  - `backend/src/main/java/com/huacai/loan/vo/LoanOrderOverviewVO.java`
  - `backend/src/main/java/com/huacai/loan/entity/LoanOrder.java`
  - `backend/src/main/java/com/huacai/loan/mapper/LoanOrderMapper.java`
  - `backend/src/test/java/com/huacai/loan/service/impl/LoanManageServiceImplTest.java`
- 不允许改：
  - `frontend/src/router/menu.ts`
  - `frontend/src/router/index.ts`
  - `frontend/src/layout/AppLayout.vue`
  - `frontend/src/views/customer/**`
  - `backend/src/main/java/com/huacai/customer/**`
  - `docker-compose.local.yml`
  - `scripts/dev/**`

目标：
1. 对照当前确认口径完成借贷管理收口
2. 维持“一行代表客户汇总”的主表
3. 保持新增/编辑/明细/跳转还款链路可用
4. 前端构建通过
5. 写 handoff 文档

强约束：
- 不把主列表改回单笔订单视图
- 不扩银行期限、增量金额等延期字段
- 不触碰客户模块代码

交付标准：
- 列表字段和当前确认口径一致
- `/api/v1/loan-orders/overview` 等真实接口可用
- `cd frontend && npm run build` 通过
- 如涉及后端，测试或接口验证通过
- handoff 文档已落盘

最后输出：
- 改了哪些文件
- 跑了哪些验证命令
- 实际结果
- 剩余问题
```

## 还款明细

```text
你现在接手华彩项目里的【还款明细】模块，只做这个模块，不要改别的模块。

项目路径：
/Users/edy/Documents/tob /华彩系统1025

当前背景：
- UI 基线已定，禁止重做视觉方向
- 当前正在按模块逐个收口
- customer / 风险评估 / 负债登记 这条线正在单独处理，你不要碰
- 启动链已经收口，使用 `./scripts/dev/up-local-stack.sh`
- 还款明细当前已确认：保留全局列表，同时支持按客户/借贷单下钻

你的职责范围：
- 只负责还款明细模块
- 可改文件范围：
  - `frontend/src/views/loan/RepaymentListView.vue`
  - `frontend/src/views/loan/repaymentScopeModel.ts`
  - `frontend/src/api/loan.ts`
  - `frontend/tests/repayment-scope-model.test.ts`
  - `backend/src/main/java/com/huacai/loan/controller/LoanController.java`
  - `backend/src/main/java/com/huacai/loan/service/impl/LoanManageServiceImpl.java`
  - `backend/src/main/java/com/huacai/loan/service/LoanManageService.java`
  - `backend/src/main/java/com/huacai/loan/query/LoanRepaymentPageQuery.java`
  - `backend/src/main/java/com/huacai/loan/dto/LoanRepaymentSaveRequest.java`
  - `backend/src/main/java/com/huacai/loan/vo/LoanRepaymentVO.java`
  - `backend/src/main/java/com/huacai/loan/vo/LoanRepaymentSummaryVO.java`
  - `backend/src/main/java/com/huacai/loan/entity/LoanRepayment.java`
  - `backend/src/main/java/com/huacai/loan/mapper/LoanRepaymentMapper.java`
  - `backend/src/test/java/com/huacai/loan/service/impl/LoanManageServiceImplTest.java`
- 不允许改：
  - `frontend/src/router/menu.ts`
  - `frontend/src/router/index.ts`
  - `frontend/src/layout/AppLayout.vue`
  - `frontend/src/views/customer/**`
  - `backend/src/main/java/com/huacai/customer/**`
  - `docker-compose.local.yml`
  - `scripts/dev/**`

目标：
1. 对照当前确认口径完成还款明细收口
2. 保留全局列表入口，同时支持按客户/借贷单下钻
3. 新增/编辑/列表/汇总链路验证通过
4. 前端构建通过
5. 写 handoff 文档

强约束：
- 不把页面改成只有下钻入口
- 不扩剩余期数等延期字段
- 不触碰借贷模块主列表定义

交付标准：
- 还款页支持全局列表和 scoped 详情
- `/api/v1/repayments`、`/api/v1/repayments/summary` 等真实接口可用
- `cd frontend && npm run build` 通过
- 如涉及后端，测试或接口验证通过
- handoff 文档已落盘

最后输出：
- 改了哪些文件
- 跑了哪些验证命令
- 实际结果
- 剩余问题
```
