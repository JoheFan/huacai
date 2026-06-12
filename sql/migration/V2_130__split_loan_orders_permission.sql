-- 借贷管理权限拆分：/loan-orders 拆为 /loan-orders-self（自营）与 /loan-orders-bank（机构）
-- 背景：后端 ModuleAccessRegistry 与前端菜单均已使用新权限码，
--       旧库中仍是 V005/V006 播种的 /loan-orders，会导致借贷接口被 403。
-- 脚本可重复执行（幂等）。

-- 1. 权限项：把旧的 /loan-orders 原地改名为 /loan-orders-self（保留角色/用户授权关联），
--    若 /loan-orders-self 已存在则跳过改名。
SET @has_self := (
  SELECT COUNT(*) FROM `sys_permission_item` WHERE `permission_code` = '/loan-orders-self'
);
UPDATE `sys_permission_item`
SET `permission_code` = '/loan-orders-self',
    `permission_name` = '借贷管理（自营）',
    `module_code` = 'loan-orders-self',
    `route_path` = '/loan-orders-self'
WHERE `permission_code` = '/loan-orders'
  AND @has_self = 0;

-- 2. 新增 /loan-orders-bank 权限项
INSERT IGNORE INTO `sys_permission_item`
(`parent_id`, `permission_code`, `permission_name`, `permission_type`, `module_code`, `route_path`, `sort_no`, `status`)
VALUES
  (0, '/loan-orders-bank', '借贷管理（机构）', 'PAGE', 'loan-orders-bank', '/loan-orders-bank', 101, 'ENABLE');

-- 3. 角色授权：持有 /loan-orders-self 的角色同步授予 /loan-orders-bank
INSERT IGNORE INTO `sys_role_permission` (`role_id`, `permission_item_id`, `created_by`, `created_at`)
SELECT rp.role_id, bank.id, 1, NOW()
FROM `sys_role_permission` rp
INNER JOIN `sys_permission_item` self ON self.id = rp.permission_item_id AND self.permission_code = '/loan-orders-self'
INNER JOIN `sys_permission_item` bank ON bank.permission_code = '/loan-orders-bank';

-- 4. 用户权限覆盖：把自营页的覆盖（ALLOW/DENY）复制到机构页
INSERT IGNORE INTO `sys_user_permission` (`user_id`, `permission_item_id`, `grant_mode`, `created_by`, `created_at`)
SELECT up.user_id, bank.id, up.grant_mode, 1, NOW()
FROM `sys_user_permission` up
INNER JOIN `sys_permission_item` self ON self.id = up.permission_item_id AND self.permission_code = '/loan-orders-self'
INNER JOIN `sys_permission_item` bank ON bank.permission_code = '/loan-orders-bank';

-- 5. 角色数据权限：loan-orders 拆为 loan-orders-self / loan-orders-bank
INSERT IGNORE INTO `sys_role_data_scope` (`role_id`, `module_code`, `scope_type`, `created_by`, `created_at`)
SELECT `role_id`, 'loan-orders-self', `scope_type`, 1, NOW()
FROM `sys_role_data_scope`
WHERE `module_code` = 'loan-orders';

INSERT IGNORE INTO `sys_role_data_scope` (`role_id`, `module_code`, `scope_type`, `created_by`, `created_at`)
SELECT `role_id`, 'loan-orders-bank', `scope_type`, 1, NOW()
FROM `sys_role_data_scope`
WHERE `module_code` = 'loan-orders';

DELETE FROM `sys_role_data_scope` WHERE `module_code` = 'loan-orders';

-- 6. 用户数据权限覆盖：同样拆分
INSERT IGNORE INTO `sys_user_data_scope` (`user_id`, `module_code`, `scope_type`, `created_by`, `created_at`)
SELECT `user_id`, 'loan-orders-self', `scope_type`, 1, NOW()
FROM `sys_user_data_scope`
WHERE `module_code` = 'loan-orders';

INSERT IGNORE INTO `sys_user_data_scope` (`user_id`, `module_code`, `scope_type`, `created_by`, `created_at`)
SELECT `user_id`, 'loan-orders-bank', `scope_type`, 1, NOW()
FROM `sys_user_data_scope`
WHERE `module_code` = 'loan-orders';

DELETE FROM `sys_user_data_scope` WHERE `module_code` = 'loan-orders';

-- 7. 旧版用户模块可见范围：loan-orders 拆为两个新模块标识
INSERT IGNORE INTO `sys_user_module` (`user_id`, `module_key`, `created_by`, `created_at`)
SELECT `user_id`, 'loan-orders-self', `created_by`, NOW()
FROM `sys_user_module`
WHERE `module_key` = 'loan-orders';

INSERT IGNORE INTO `sys_user_module` (`user_id`, `module_key`, `created_by`, `created_at`)
SELECT `user_id`, 'loan-orders-bank', `created_by`, NOW()
FROM `sys_user_module`
WHERE `module_key` = 'loan-orders';

DELETE FROM `sys_user_module` WHERE `module_key` = 'loan-orders';
