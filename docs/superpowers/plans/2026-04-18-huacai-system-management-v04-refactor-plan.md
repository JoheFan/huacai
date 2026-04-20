# 华彩系统管理 V0.4 重构计划

> 目标：基于当前项目骨架，把系统管理从“用户直配模块 + 简化角色 CRUD”迁移到 `身份 + 主角色 + 功能权限 + 数据权限 + 管理日志` 的一期实现。

## 结论

- 不做整仓重写。
- 保留现有登录、组织/用户/角色基础 CRUD、JWT、前端菜单与路由框架。
- 系统管理需要做一次**权限模型级重构**，不能再继续以 `visibleModuleKeys / sys_user_module` 为主模型扩展。

---

## 一、必改文件

### 1. 后端鉴权与登录态

- `backend/src/main/java/com/huacai/auth/service/impl/AuthServiceImpl.java`
  - 登录态返回从 `visibleModuleKeys` 改成 `identityType + role + pagePermissions + buttonPermissions + dataScopes + permissionSummary`。
- `backend/src/main/java/com/huacai/auth/vo/CurrentUserInfoVO.java`
  - 改造当前用户返回结构，承载身份、主角色、页面权限、按钮权限、数据权限摘要。
- `backend/src/main/java/com/huacai/security/AuthUser.java`
  - JWT 载荷从“角色 + 可见模块”升级为“身份 + 主角色 + 权限集 + 数据权限摘要”。
- `backend/src/main/java/com/huacai/security/ModuleAccessRegistry.java`
  - 现有模块权限注册表要重构为“页面/按钮权限注册表”或被新的权限注册中心替代。
- `backend/src/main/java/com/huacai/security/ModuleAccessFilter.java`
  - 现有按 `moduleKey` 拦截的过滤器要迁移为页面权限 / 管理边界 / 数据权限三段校验。
- `backend/src/main/java/com/huacai/config/SecurityConfig.java`
  - 注册新的权限过滤器顺序，移除旧的模块可见性假设。

### 2. 后端系统管理主链路

- `backend/src/main/java/com/huacai/system/service/SystemManageService.java`
- `backend/src/main/java/com/huacai/system/service/impl/SystemManageServiceImpl.java`
  - 重构用户、角色、权限保存与查询逻辑。
  - 删除 `visibleModuleKeys` 相关逻辑。
  - 补齐角色默认权限、用户权限覆盖、数据权限保存。
- `backend/src/main/java/com/huacai/system/controller/UserController.java`
  - 拆分“基础信息编辑”和“权限配置”职责。
- `backend/src/main/java/com/huacai/system/controller/RoleController.java`
  - 由 `menus` 分配接口迁移到“页面权限 + 按钮权限 + 数据权限模板”接口。
- `backend/src/main/java/com/huacai/system/controller/MenuController.java`
  - 当前假数据接口需要重构成权限项查询接口，或替换成 `PermissionController`。
- `backend/src/main/java/com/huacai/system/controller/OrgController.java`
  - 补同步入口与组织字段统一逻辑。
- `backend/src/main/java/com/huacai/system/controller/OperationLogController.java`
  - 当前空实现要改成真实分页查询。

### 3. 后端系统管理 DTO / VO / Entity / Mapper

- `backend/src/main/java/com/huacai/system/dto/UserCreateRequest.java`
- `backend/src/main/java/com/huacai/system/dto/UserUpdateRequest.java`
  - 去掉 `visibleModuleKeys`，新增身份、主角色、权限配置相关字段。
- `backend/src/main/java/com/huacai/system/dto/RoleCreateRequest.java`
- `backend/src/main/java/com/huacai/system/dto/RoleUpdateRequest.java`
- `backend/src/main/java/com/huacai/system/dto/RoleMenuAssignRequest.java`
  - 替换为新的角色权限模板保存 DTO。
- `backend/src/main/java/com/huacai/system/vo/UserVO.java`
- `backend/src/main/java/com/huacai/system/vo/RoleVO.java`
- `backend/src/main/java/com/huacai/system/vo/OrgVO.java`
  - VO 字段与文档页面对齐。
- `backend/src/main/java/com/huacai/system/entity/SysUser.java`
- `backend/src/main/java/com/huacai/system/entity/SysRole.java`
- `backend/src/main/java/com/huacai/system/entity/SysUserRole.java`
- `backend/src/main/java/com/huacai/system/entity/SysOrg.java`
  - 需要增加新字段或约束。
- `backend/src/main/java/com/huacai/system/mapper/UserMapper.java`
- `backend/src/main/java/com/huacai/system/mapper/UserRoleMapper.java`
- `backend/src/main/java/com/huacai/system/mapper/RoleMapper.java`
- `backend/src/main/java/com/huacai/system/mapper/OrgMapper.java`
- `backend/src/main/java/com/huacai/system/mapper/SysUserMapper.java`
- `backend/src/main/java/com/huacai/system/mapper/SysRoleMapper.java`
- `backend/src/main/java/com/huacai/system/mapper/SysOrgMapper.java`
  - 需要补新的查询、联表和约束加载逻辑。

### 4. 前端鉴权与菜单链路

- `frontend/src/api/auth.ts`
  - `CurrentUserInfo` 结构重构。
- `frontend/src/stores/auth.ts`
  - 存储身份、主角色、页面权限、按钮权限、数据权限摘要。
- `frontend/src/access/moduleAccess.ts`
  - 废弃模块权限判断，改为页面权限派生菜单可见性。
- `frontend/src/router/index.ts`
  - 路由守卫从 `moduleKey` 切到页面权限判断。
- `frontend/src/layout/AppLayout.vue`
  - 菜单显示基于页面权限推导。
- `frontend/src/router/menu.ts`
  - 加入 `管理日志` 菜单；去掉对 `visibleModuleKeys` 的绑定假设。
- `frontend/src/views/common/WelcomeView.vue`
  - 当前普通用户欢迎页依赖“模块可见性”摘要，需改成“可访问页面摘要”或降级为普通首页。

### 5. 前端系统管理页面

- `frontend/src/api/system.ts`
  - 用户、角色、权限项、日志接口模型重构。
- `frontend/src/views/system/user/UserManageView.vue`
  - 必须重构。拆分为“基础信息编辑”和“权限弹层”。
- `frontend/src/views/system/user/userModuleRegistry.ts`
  - 当前模块直配配置页需要废弃。
- `frontend/src/views/system/user/userRoleDisplay.ts`
  - 改成展示“主角色 + 身份 + 权限状态摘要”。
- `frontend/src/views/system/role/RoleManageView.vue`
  - 必须重构成“角色模板 + 默认权限模板配置”页面。
- `frontend/src/views/system/org/OrgManageView.vue`
  - 补同步入口、字段统一和文档口径。
- `frontend/src/views/system/org/orgTreeMeta.ts`
  - 需要更新字段映射。
- `frontend/src/views/system/log/OperationLogView.vue`
  - 当前空页面，必须补成真实列表页。

### 6. 测试文件

- `backend/src/test/java/com/huacai/system/service/impl/SystemManageServiceImplTest.java`
- `backend/src/test/java/com/huacai/security/ModuleAccessFilterTest.java`
- `backend/src/test/java/com/huacai/security/ModuleAccessRegistryTest.java`
- `frontend/tests/module-access-routing.test.ts`
- `frontend/tests/user-module-selection.test.ts`
  - 这些测试要么重写，要么删除后用新权限模型测试替代。

---

## 二、需要新增的表 / 字段

## A. 建议新增的表

### 1. `sys_permission_item`

用途：统一维护页面权限项和按钮权限项，不再把“菜单可见性”当成底层权限源。

建议字段：
- `id`
- `parent_id`
- `permission_code`
- `permission_name`
- `permission_type`：`PAGE` / `BUTTON`
- `module_code`
- `route_path`
- `button_code`
- `sort_no`
- `status`
- `remark`
- `created_by / created_at / updated_by / updated_at / deleted_flag / version`

### 2. `sys_role_permission`

用途：角色默认功能权限模板。

建议字段：
- `id`
- `role_id`
- `permission_item_id`
- `created_by`
- `created_at`

### 3. `sys_role_data_scope`

用途：角色默认数据权限模板，按模块维度配置。

建议字段：
- `id`
- `role_id`
- `module_code`
- `scope_type`：`ALL` / `ORG` / `ORG_AND_SUB` / `SELF`
- `created_by`
- `created_at`

### 4. `sys_user_permission`

用途：用户个性化功能权限授权，作为主角色模板的增补或收缩。

建议字段：
- `id`
- `user_id`
- `permission_item_id`
- `grant_mode`：`ALLOW` / `DENY`
- `created_by`
- `created_at`

### 5. `sys_user_data_scope`

用途：用户级数据权限覆盖表。

建议字段：
- `id`
- `user_id`
- `module_code`
- `scope_type`
- `created_by`
- `created_at`

## B. 建议新增或调整的字段

### `sys_user`

建议新增：
- `identity_type`：`SUPER_ADMIN` / `DEPT_ADMIN` / `NORMAL_USER`

说明：
- 身份是管理边界概念，必须和主角色分开存。
- `username` 继续作为登录账号字段使用，页面上可展示为“账号（手机号）”。

### `sys_role`

建议新增：
- `identity_type`

说明：
- 角色模板需要声明适用身份范围，避免部门管理员拿到超级管理员模板。

### `sys_user_role`

建议调整：
- 增加 `UNIQUE(user_id)`，强制一期一用户一主角色。

### `sys_org`

建议新增：
- `is_management_org` 或统一命名后的布尔字段

说明：
- 用来承接文档中的“是否管理机构 / 是否领导机构”。

### `sys_operation_log`

建议新增：
- `action_code`
- `action_desc`
- `target_type`
- `target_id`
- `result_status`
- `diff_summary`

说明：
- 现有表能存日志，但不够支撑“管理日志页 + 差异摘要”展示。

---

## 三、需要废弃或迁移的旧实现

### 1. 用户直配模块模型

需要废弃 / 迁移：
- 表：`sys_user_module`
- 迁移脚本：`sql/migration/V003_add_sys_user_module.sql`
- 实体：`backend/src/main/java/com/huacai/system/entity/SysUserModule.java`
- Mapper：`backend/src/main/java/com/huacai/system/mapper/UserModuleMapper.java`
- DTO/VO 字段中的 `visibleModuleKeys`
- 前端 `userModuleRegistry.ts`

迁移策略：
- 不立即物理删除。
- 第一阶段停止写入。
- 第二阶段把已有模块授权映射到新的页面权限表。
- 第三阶段删除旧表和旧代码。

### 2. 基于 `moduleKey` 的访问控制

需要迁移：
- `backend/src/main/java/com/huacai/security/ModuleAccessRegistry.java`
- `backend/src/main/java/com/huacai/security/ModuleAccessFilter.java`
- `frontend/src/access/moduleAccess.ts`
- `frontend/src/router/index.ts`
- `frontend/src/layout/AppLayout.vue`

迁移目标：
- 页面权限决定路由可访问性。
- 页面权限派生菜单显示。
- 按钮权限控制操作按钮显示与提交。
- 数据权限独立控制查询结果范围。

### 3. 角色菜单分配接口命名与语义

需要迁移：
- `fetchRoleMenus`
- `assignRoleMenus`
- `RoleMenuAssignRequest`
- `getRoleMenuIds`
- `assignMenus`

迁移目标：
- 从“菜单分配”升级为“页面权限 + 按钮权限 + 数据权限模板”。

### 4. 当前普通用户欢迎页定位

需要评估迁移：
- `frontend/src/views/common/WelcomeView.vue`

原因：
- 当前欢迎页建立在“按业务模块可见”模型上。
- `V0.4` 改成“页面权限推导菜单”，欢迎页要么弱化为普通首页，要么改成“权限摘要页”。

---

## 四、建议开发顺序

### 阶段 1：先重构权限模型与登录态

目标：
- 身份、主角色、页面权限、按钮权限、数据权限进入登录态。
- 停止继续扩展 `visibleModuleKeys`。

### 阶段 2：落角色模板与用户权限配置

目标：
- 角色管理先可配置默认权限模板。
- 系统用户页再承接主角色 + 用户个性化授权。

### 阶段 3：补数据权限执行链和管理边界

目标：
- 后端查询按模块数据权限过滤。
- 超管 / 部门管理员 / 普通用户管理边界落实。

### 阶段 4：补管理日志与组织同步

目标：
- 关键动作写日志。
- 日志页真实可查。
- 组织同步补成预留或真能力。

### 阶段 5：回头清理旧实现

目标：
- 清除 `sys_user_module` 及相关旧代码。
- 删除旧测试，替换成新权限模型测试。

---

## 五、建议本轮先不动的内容

- 客户管理页面本身先不跟着系统管理一起大改。
- 系统用户导入导出先保留预留态，不在第一阶段做真导入导出。
- 不在这一轮引入复杂审批流、多角色并行绑定、细粒度 ABAC。

---

## 六、阶段验收口径

本计划通过的最低标准：

- 登录态返回不再依赖 `visibleModuleKeys`。
- 用户只允许绑定一个主角色。
- 角色页能保存默认页面权限、按钮权限、数据权限模板。
- 用户页能在“编辑”和“权限”之间分工清晰。
- 管理日志能真实展示关键操作。
- 普通用户不能进入系统管理页；部门管理员只能管理边界内用户。
