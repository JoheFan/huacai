# 华彩系统 - 系统权限与角色种子数据乱码修复

**日期**: 2026-04-19
**问题根因**: 执行带中文种子数据的 SQL 时，连接层字符集是 `latin1`，导致中文被错编码入库
**修复性质**: 数据层修复 + 预防加固

---

## 一、确认受影响的表/列

### 1. `sys_permission_item.permission_name`
- **受影响范围**: 全部26条初始种子数据 + V007/V008 插入的人事/财务权限项
- **确认损坏数据**:
  - V005写入的系统管理/客户管理权限项: `/dashboard`, `/system/orgs`, `/system/users`, `/system/roles`, `/system/logs`, `/customers` 及相关按钮权限
  - V007写入的人事权限项: `/hr/employees`, `/hr/my-profile`, `/hr/salaries`, `/workflow/transitions`, `/workflow/salary-adjusts` 及相关按钮权限
  - V008写入的财务/审批权限项: `/finance/reimbursements`, `/workflow/my-approvals` 及相关按钮权限
- **乱码特征**: `å·¥ä½œå°`, `ç»„ç»�ç®¡ç�†` 等拉丁1编码的UTF-8字节序列

### 2. `sys_role` 种子数据真实损坏情况
| 字段 | role_code | 状态 | 是否修复 |
|------|-----------|------|----------|
| `role_name` | ADMIN | 正常 | 不修 |
| `remark` | ADMIN | 正常 | 不修 |
| `role_name` | DEPT_ADMIN | 业务数据='111' | 禁止重置，不修 |
| `remark` | DEPT_ADMIN | 乱码 | 需修 |
| `role_name` | STAFF | 乱码 | 需修 |
| `remark` | STAFF | 乱码 | 需修 |

**关键约束**: `DEPT_ADMIN.role_name='111'` 为现网业务数据，任何情况下禁止重置。

---

## 二、确认未受影响的表/列

- `sys_org.org_name` — 未受影响
- `sys_user.real_name` — 未受影响

---

## 三、本轮新增的修复 migration

| 文件 | 作用 |
|------|------|
| `sql/migration/V009_fix_encoding_corruption.sql` | 按 `permission_code` 定点修正 `sys_permission_item.permission_name`，按 `role_code` 修正 `sys_role` 相关字段 |

### 修复策略
- **首行 `SET NAMES utf8mb4;`** — 确保本文件执行时 MySQL 连接层使用正确字符集，不会二次写坏
- **幂等**: 所有 UPDATE 均按唯一键 `permission_code` 或 `role_code` 定点
- **零覆盖**: 仅修正确认损坏的字段，`DEPT_ADMIN.role_name='111'` 业务数据未触碰

### sys_role 最终修复范围
```sql
UPDATE `sys_role` SET `remark` = '系统默认部门管理员角色' WHERE `role_code` = 'DEPT_ADMIN';
UPDATE `sys_role` SET `role_name` = '普通用户' WHERE `role_code` = 'STAFF';
UPDATE `sys_role` SET `remark` = '系统默认普通用户角色' WHERE `role_code` = 'STAFF';
```

---

## 四、补了 `SET NAMES utf8mb4;` 的 SQL 文件

| 文件 | 状态 |
|------|------|
| `sql/init/001_schema.sql` | 已有 (第7行)，确认完整 |
| `sql/migration/V005_system_permission_model.sql` | 本轮新增 |
| `sql/migration/V006_seed_system_roles.sql` | 本轮新增 |
| `sql/migration/V007_hr_module.sql` | 本轮新增 |
| `sql/migration/V008_fin_workflow_module.sql` | 本轮新增 |

---

## 五、验证命令

### 文件级验证（无需数据库）
```bash
# 检查 V009 首行是否有 SET NAMES utf8mb4;
head -5 sql/migration/V009_fix_encoding_corruption.sql

# 检查 role 修复范围是否正确
grep -n "DEPT_ADMIN\|ADMIN\|STAFF" sql/migration/V009_fix_encoding_corruption.sql
```

预期：
- 第5行是 `SET NAMES utf8mb4;`
- 有 `DEPT_ADMIN.remark` 修复，无 `ADMIN.remark` 修复
- 无 `DEPT_ADMIN.role_name` 相关改写

### 数据库核查（如环境允许）
```sql
-- 检查 sys_role 原始值
SELECT id, role_code, role_name, remark
FROM sys_role
WHERE deleted_flag = 0
ORDER BY id;

-- 检查 permission_name 修复结果
SELECT id, permission_code, permission_name
FROM sys_permission_item
WHERE deleted_flag = 0
ORDER BY id;
```

### API 验证
```bash
curl -s http://localhost:8080/api/v1/system/permissions/catalog | jq '.data.pageItems[] | select(.permissionCode == "/dashboard" or .permissionCode == "/system/orgs" or .permissionCode == "/system/users" or .permissionCode == "/system/roles")'
```

预期返回:
- `/dashboard` → `工作台`
- `/system/orgs` → `组织管理`
- `/system/users` → `系统用户`
- `/system/roles` → `角色管理`

---

## 六、实际验证结果

### 1. V009 文件内容验证
```
-- V009_fix_encoding_corruption.sql
-- Fix encoding corruption for seed data inserted with latin1 connection
-- Root cause: SQL files executed without SET NAMES utf8mb4, Chinese was corrupted to latin1 bytes

SET NAMES utf8mb4;   ← 第5行，确认存在
```

### 2. sys_role 修复范围验证
```
grep 结果:
  69:--   ADMIN.role_name      - 正常，不修
  70:--   ADMIN.remark         - 正常，不修
  71:--   DEPT_ADMIN.role_name - 业务数据='111'，不修
  72:--   DEPT_ADMIN.remark    - 乱码，需修
  73:--   STAFF.role_name      - 乱码，需修
  74:--   STAFF.remark         - 乱码，需修
  77:UPDATE `sys_role` SET `remark` = '系统默认部门管理员角色' WHERE `role_code` = 'DEPT_ADMIN';
  78:UPDATE `sys_role` SET `role_name` = '普通用户' WHERE `role_code` = 'STAFF';
  79:UPDATE `sys_role` SET `remark` = '系统默认普通用户角色' WHERE `role_code` = 'STAFF';
```

确认：
- `DEPT_ADMIN.remark` 修复 ✓
- `STAFF.role_name` 修复 ✓
- `STAFF.remark` 修复 ✓
- `ADMIN.remark` 未修复 ✓
- `DEPT_ADMIN.role_name` 未修改 ✓

---

## 七、页面验证结果

> 待实际执行后填写

预期：
- 角色管理 → 权限配置弹层 → 权限名称正常显示
- 用户权限配置页面 → 权限目录正常显示

---

## 八、剩余问题

- 上版 V009 不可执行问题已解决（已补 `SET NAMES utf8mb4;`）
- 待确认：V009 执行后 API 响应和页面显示是否恢复正常
