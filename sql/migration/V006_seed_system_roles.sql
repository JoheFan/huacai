SET NAMES utf8mb4;

INSERT IGNORE INTO `sys_role`
(`role_code`, `role_name`, `identity_type`, `data_scope`, `status`, `remark`, `created_by`, `updated_by`)
VALUES
  ('ADMIN', '系统管理员', 'SUPER_ADMIN', 'ALL', 'ENABLE', '系统默认超级管理员角色', 1, 1),
  ('DEPT_ADMIN', '部门管理员', 'DEPT_ADMIN', 'ORG', 'ENABLE', '系统默认部门管理员角色', 1, 1),
  ('STAFF', '普通用户', 'NORMAL_USER', 'SELF', 'ENABLE', '系统默认普通用户角色', 1, 1);

UPDATE `sys_role` SET `identity_type` = 'SUPER_ADMIN', `data_scope` = 'ALL' WHERE `role_code` = 'ADMIN';
UPDATE `sys_role` SET `identity_type` = 'DEPT_ADMIN', `data_scope` = 'ORG' WHERE `role_code` = 'DEPT_ADMIN';
UPDATE `sys_role` SET `identity_type` = 'NORMAL_USER', `data_scope` = 'SELF' WHERE `role_code` = 'STAFF';

INSERT IGNORE INTO `sys_user_role` (`user_id`, `role_id`, `created_by`, `created_at`)
SELECT u.id,
       r.id,
       1,
       NOW()
FROM `sys_user` u
INNER JOIN `sys_role` r ON r.role_code = CASE
  WHEN u.identity_type = 'SUPER_ADMIN' THEN 'ADMIN'
  WHEN u.identity_type = 'DEPT_ADMIN' THEN 'DEPT_ADMIN'
  ELSE 'STAFF'
END
LEFT JOIN `sys_user_role` ur ON ur.user_id = u.id
WHERE ur.user_id IS NULL;

INSERT IGNORE INTO `sys_role_permission` (`role_id`, `permission_item_id`, `created_by`, `created_at`)
SELECT r.id, p.id, 1, NOW()
FROM `sys_role` r
INNER JOIN `sys_permission_item` p ON p.permission_code IN (
  '/system/users',
  'system.users:create',
  'system.users:update',
  'system.users:delete',
  'system.users:status',
  'system.users:reset-password',
  'system.users:assign-permission'
)
WHERE r.role_code = 'DEPT_ADMIN';

INSERT IGNORE INTO `sys_role_data_scope` (`role_id`, `module_code`, `scope_type`, `created_by`, `created_at`)
SELECT r.id, t.module_code, t.scope_type, 1, NOW()
FROM `sys_role` r
INNER JOIN (
  SELECT 'customers' AS module_code, 'ALL' AS scope_type UNION ALL
  SELECT 'customer-risks', 'ALL' UNION ALL
  SELECT 'customer-debts', 'ALL' UNION ALL
  SELECT 'opportunities', 'ALL' UNION ALL
  SELECT 'loan-orders', 'ALL' UNION ALL
  SELECT 'repayments', 'ALL'
) t ON 1 = 1
WHERE r.role_code = 'ADMIN';

INSERT IGNORE INTO `sys_role_data_scope` (`role_id`, `module_code`, `scope_type`, `created_by`, `created_at`)
SELECT r.id, t.module_code, t.scope_type, 1, NOW()
FROM `sys_role` r
INNER JOIN (
  SELECT 'customers' AS module_code, 'ORG' AS scope_type UNION ALL
  SELECT 'customer-risks', 'ORG' UNION ALL
  SELECT 'customer-debts', 'ORG' UNION ALL
  SELECT 'opportunities', 'ORG' UNION ALL
  SELECT 'loan-orders', 'ORG' UNION ALL
  SELECT 'repayments', 'ORG'
) t ON 1 = 1
WHERE r.role_code = 'DEPT_ADMIN';

INSERT IGNORE INTO `sys_role_data_scope` (`role_id`, `module_code`, `scope_type`, `created_by`, `created_at`)
SELECT r.id, t.module_code, t.scope_type, 1, NOW()
FROM `sys_role` r
INNER JOIN (
  SELECT 'customers' AS module_code, 'SELF' AS scope_type UNION ALL
  SELECT 'customer-risks', 'SELF' UNION ALL
  SELECT 'customer-debts', 'SELF' UNION ALL
  SELECT 'opportunities', 'SELF' UNION ALL
  SELECT 'loan-orders', 'SELF' UNION ALL
  SELECT 'repayments', 'SELF'
) t ON 1 = 1
WHERE r.role_code = 'STAFF';
