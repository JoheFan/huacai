-- V009_fix_encoding_corruption.sql
-- Fix encoding corruption for seed data inserted with latin1 connection
-- Root cause: SQL files executed without SET NAMES utf8mb4, Chinese was corrupted to latin1 bytes

SET NAMES utf8mb4;

-- ============================================
-- 1. Fix sys_permission_item.permission_name
-- ============================================

UPDATE `sys_permission_item` SET `permission_name` = '工作台' WHERE `permission_code` = '/dashboard';
UPDATE `sys_permission_item` SET `permission_name` = '组织管理' WHERE `permission_code` = '/system/orgs';
UPDATE `sys_permission_item` SET `permission_name` = '系统用户' WHERE `permission_code` = '/system/users';
UPDATE `sys_permission_item` SET `permission_name` = '角色管理' WHERE `permission_code` = '/system/roles';
UPDATE `sys_permission_item` SET `permission_name` = '管理日志' WHERE `permission_code` = '/system/logs';
UPDATE `sys_permission_item` SET `permission_name` = '客户管理' WHERE `permission_code` = '/customers';
UPDATE `sys_permission_item` SET `permission_name` = '风险评估' WHERE `permission_code` = '/customer-risks';
UPDATE `sys_permission_item` SET `permission_name` = '负债登记' WHERE `permission_code` = '/customer-debts';
UPDATE `sys_permission_item` SET `permission_name` = '商机管理' WHERE `permission_code` = '/opportunities';
UPDATE `sys_permission_item` SET `permission_name` = '借贷管理' WHERE `permission_code` = '/loan-orders';
UPDATE `sys_permission_item` SET `permission_name` = '还款明细' WHERE `permission_code` = '/repayments';
UPDATE `sys_permission_item` SET `permission_name` = '新增组织' WHERE `permission_code` = 'system.orgs:create';
UPDATE `sys_permission_item` SET `permission_name` = '编辑组织' WHERE `permission_code` = 'system.orgs:update';
UPDATE `sys_permission_item` SET `permission_name` = '删除组织' WHERE `permission_code` = 'system.orgs:delete';
UPDATE `sys_permission_item` SET `permission_name` = '同步组织' WHERE `permission_code` = 'system.orgs:sync';
UPDATE `sys_permission_item` SET `permission_name` = '新增用户' WHERE `permission_code` = 'system.users:create';
UPDATE `sys_permission_item` SET `permission_name` = '编辑用户' WHERE `permission_code` = 'system.users:update';
UPDATE `sys_permission_item` SET `permission_name` = '删除用户' WHERE `permission_code` = 'system.users:delete';
UPDATE `sys_permission_item` SET `permission_name` = '启停账号' WHERE `permission_code` = 'system.users:status';
UPDATE `sys_permission_item` SET `permission_name` = '重置密码' WHERE `permission_code` = 'system.users:reset-password';
UPDATE `sys_permission_item` SET `permission_name` = '配置用户权限' WHERE `permission_code` = 'system.users:assign-permission';
UPDATE `sys_permission_item` SET `permission_name` = '新增角色' WHERE `permission_code` = 'system.roles:create';
UPDATE `sys_permission_item` SET `permission_name` = '编辑角色' WHERE `permission_code` = 'system.roles:update';
UPDATE `sys_permission_item` SET `permission_name` = '删除角色' WHERE `permission_code` = 'system.roles:delete';
UPDATE `sys_permission_item` SET `permission_name` = '配置角色权限' WHERE `permission_code` = 'system.roles:assign-permission';
UPDATE `sys_permission_item` SET `permission_name` = '查看日志' WHERE `permission_code` = 'system.logs:query';

-- HR Module permissions
UPDATE `sys_permission_item` SET `permission_name` = '员工档案' WHERE `permission_code` = '/hr/employees';
UPDATE `sys_permission_item` SET `permission_name` = '我的档案' WHERE `permission_code` = '/hr/my-profile';
UPDATE `sys_permission_item` SET `permission_name` = '工资管理' WHERE `permission_code` = '/hr/salaries';
UPDATE `sys_permission_item` SET `permission_name` = '人事异动-转正' WHERE `permission_code` = '/workflow/transitions';
UPDATE `sys_permission_item` SET `permission_name` = '人事异动-调薪' WHERE `permission_code` = '/workflow/salary-adjusts';
UPDATE `sys_permission_item` SET `permission_name` = '新增员工' WHERE `permission_code` = 'hr.employees:create';
UPDATE `sys_permission_item` SET `permission_name` = '编辑员工' WHERE `permission_code` = 'hr.employees:update';
UPDATE `sys_permission_item` SET `permission_name` = '删除员工' WHERE `permission_code` = 'hr.employees:delete';
UPDATE `sys_permission_item` SET `permission_name` = '导入档案' WHERE `permission_code` = 'hr.employees:import';
UPDATE `sys_permission_item` SET `permission_name` = '导出档案' WHERE `permission_code` = 'hr.employees:export';
UPDATE `sys_permission_item` SET `permission_name` = '新增工资标准' WHERE `permission_code` = 'hr.salaries:create';
UPDATE `sys_permission_item` SET `permission_name` = '编辑工资标准' WHERE `permission_code` = 'hr.salaries:update';
UPDATE `sys_permission_item` SET `permission_name` = '删除工资标准' WHERE `permission_code` = 'hr.salaries:delete';
UPDATE `sys_permission_item` SET `permission_name` = '发起转正申请' WHERE `permission_code` = 'workflow.transitions:create';
UPDATE `sys_permission_item` SET `permission_name` = '审批转正申请' WHERE `permission_code` = 'workflow.transitions:approve';
UPDATE `sys_permission_item` SET `permission_name` = '发起调薪申请' WHERE `permission_code` = 'workflow.salary-adjusts:create';
UPDATE `sys_permission_item` SET `permission_name` = '审批调薪申请' WHERE `permission_code` = 'workflow.salary-adjusts:approve';

-- Finance/Workflow permissions
UPDATE `sys_permission_item` SET `permission_name` = '报销管理' WHERE `permission_code` = '/finance/reimbursements';
UPDATE `sys_permission_item` SET `permission_name` = '我的审批' WHERE `permission_code` = '/workflow/my-approvals';
UPDATE `sys_permission_item` SET `permission_name` = '发起报销申请' WHERE `permission_code` = 'finance.reimbursements:create';
UPDATE `sys_permission_item` SET `permission_name` = '审批报销申请' WHERE `permission_code` = 'finance.reimbursements:approve';
UPDATE `sys_permission_item` SET `permission_name` = '查看全部报销' WHERE `permission_code` = 'finance.reimbursements:view-all';
UPDATE `sys_permission_item` SET `permission_name` = '执行审批操作' WHERE `permission_code` = 'workflow.my-approvals:approve';

-- ============================================
-- 2. Fix sys_role.role_name and remark for seed roles
--
-- Confirmed actual database state:
--   ADMIN.role_name      - 正常，不修
--   ADMIN.remark         - 正常，不修
--   DEPT_ADMIN.role_name - 业务数据='111'，不修
--   DEPT_ADMIN.remark    - 乱码，需修
--   STAFF.role_name      - 乱码，需修
--   STAFF.remark         - 乱码，需修
-- ============================================

UPDATE `sys_role` SET `remark` = '系统默认部门管理员角色' WHERE `role_code` = 'DEPT_ADMIN';
UPDATE `sys_role` SET `role_name` = '普通用户' WHERE `role_code` = 'STAFF';
UPDATE `sys_role` SET `remark` = '系统默认普通用户角色' WHERE `role_code` = 'STAFF';
