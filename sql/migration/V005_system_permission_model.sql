SET NAMES utf8mb4;

SET @sys_user_identity_type_exists = (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'sys_user'
    AND COLUMN_NAME = 'identity_type'
);

SET @sys_user_identity_type_sql = IF(
  @sys_user_identity_type_exists = 0,
  'ALTER TABLE `sys_user` ADD COLUMN `identity_type` VARCHAR(32) NOT NULL DEFAULT ''NORMAL_USER'' COMMENT ''身份类型'' AFTER `org_id`',
  'SELECT 1'
);

PREPARE sys_user_identity_type_stmt FROM @sys_user_identity_type_sql;
EXECUTE sys_user_identity_type_stmt;
DEALLOCATE PREPARE sys_user_identity_type_stmt;

SET @sys_role_identity_type_exists = (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'sys_role'
    AND COLUMN_NAME = 'identity_type'
);

SET @sys_role_identity_type_sql = IF(
  @sys_role_identity_type_exists = 0,
  'ALTER TABLE `sys_role` ADD COLUMN `identity_type` VARCHAR(32) NOT NULL DEFAULT ''NORMAL_USER'' COMMENT ''适用身份类型'' AFTER `role_name`',
  'SELECT 1'
);

PREPARE sys_role_identity_type_stmt FROM @sys_role_identity_type_sql;
EXECUTE sys_role_identity_type_stmt;
DEALLOCATE PREPARE sys_role_identity_type_stmt;

CREATE TABLE IF NOT EXISTS `sys_permission_item` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `parent_id` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '父级权限项ID',
  `permission_code` VARCHAR(128) NOT NULL COMMENT '权限编码',
  `permission_name` VARCHAR(100) NOT NULL COMMENT '权限名称',
  `permission_type` VARCHAR(16) NOT NULL DEFAULT 'PAGE' COMMENT '权限类型 PAGE/BUTTON',
  `module_code` VARCHAR(64) DEFAULT NULL COMMENT '模块编码',
  `route_path` VARCHAR(255) DEFAULT NULL COMMENT '页面路由',
  `button_code` VARCHAR(128) DEFAULT NULL COMMENT '按钮编码',
  `sort_no` INT NOT NULL DEFAULT 0 COMMENT '排序号',
  `status` VARCHAR(16) NOT NULL DEFAULT 'ENABLE' COMMENT '状态',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_permission_item_code` (`permission_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限项表';

CREATE TABLE IF NOT EXISTS `sys_role_permission` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` BIGINT UNSIGNED NOT NULL COMMENT '角色ID',
  `permission_item_id` BIGINT UNSIGNED NOT NULL COMMENT '权限项ID',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_role_permission` (`role_id`, `permission_item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

CREATE TABLE IF NOT EXISTS `sys_user_permission` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
  `permission_item_id` BIGINT UNSIGNED NOT NULL COMMENT '权限项ID',
  `grant_mode` VARCHAR(16) NOT NULL DEFAULT 'ALLOW' COMMENT '授权模式 ALLOW/DENY',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_user_permission` (`user_id`, `permission_item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户权限覆盖表';

CREATE TABLE IF NOT EXISTS `sys_role_data_scope` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` BIGINT UNSIGNED NOT NULL COMMENT '角色ID',
  `module_code` VARCHAR(64) NOT NULL COMMENT '模块编码',
  `scope_type` VARCHAR(32) NOT NULL COMMENT '数据权限范围',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_role_data_scope` (`role_id`, `module_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色数据权限表';

CREATE TABLE IF NOT EXISTS `sys_user_data_scope` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
  `module_code` VARCHAR(64) NOT NULL COMMENT '模块编码',
  `scope_type` VARCHAR(32) NOT NULL COMMENT '数据权限范围',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_user_data_scope` (`user_id`, `module_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户数据权限覆盖表';

UPDATE `sys_role`
SET `identity_type` = CASE
  WHEN `role_code` = 'ADMIN' THEN 'SUPER_ADMIN'
  WHEN `role_code` IN ('DEPT_ADMIN', 'MANAGER') THEN 'DEPT_ADMIN'
  ELSE 'NORMAL_USER'
END
WHERE `identity_type` IS NULL OR `identity_type` = '' OR `identity_type` = 'NORMAL_USER';

UPDATE `sys_user` u
LEFT JOIN (
  SELECT ur.user_id,
         MIN(CASE
           WHEN r.role_code = 'ADMIN' THEN 'SUPER_ADMIN'
           WHEN r.role_code IN ('DEPT_ADMIN', 'MANAGER') THEN 'DEPT_ADMIN'
           ELSE 'NORMAL_USER'
         END) AS resolved_identity_type
  FROM sys_user_role ur
  INNER JOIN sys_role r ON r.id = ur.role_id
  GROUP BY ur.user_id
) t ON t.user_id = u.id
SET u.identity_type = COALESCE(t.resolved_identity_type, 'NORMAL_USER')
WHERE u.identity_type IS NULL OR u.identity_type = '' OR u.identity_type = 'NORMAL_USER';

INSERT IGNORE INTO `sys_permission_item`
(`parent_id`, `permission_code`, `permission_name`, `permission_type`, `module_code`, `route_path`, `sort_no`, `status`)
VALUES
  (0, '/dashboard', '工作台', 'PAGE', 'dashboard', '/dashboard', 10, 'ENABLE'),
  (0, '/system/orgs', '组织管理', 'PAGE', 'system.orgs', '/system/orgs', 20, 'ENABLE'),
  (0, '/system/users', '系统用户', 'PAGE', 'system.users', '/system/users', 30, 'ENABLE'),
  (0, '/system/roles', '角色管理', 'PAGE', 'system.roles', '/system/roles', 40, 'ENABLE'),
  (0, '/system/logs', '管理日志', 'PAGE', 'system.logs', '/system/logs', 50, 'ENABLE'),
  (0, '/customers', '客户管理', 'PAGE', 'customers', '/customers', 60, 'ENABLE'),
  (0, '/customer-risks', '风险评估', 'PAGE', 'customer-risks', '/customer-risks', 70, 'ENABLE'),
  (0, '/customer-debts', '负债登记', 'PAGE', 'customer-debts', '/customer-debts', 80, 'ENABLE'),
  (0, '/opportunities', '商机管理', 'PAGE', 'opportunities', '/opportunities', 90, 'ENABLE'),
  (0, '/loan-orders', '借贷管理', 'PAGE', 'loan-orders', '/loan-orders', 100, 'ENABLE'),
  (0, '/repayments', '还款明细', 'PAGE', 'repayments', '/repayments', 110, 'ENABLE'),
  (0, 'system.orgs:create', '新增组织', 'BUTTON', 'system.orgs', NULL, 210, 'ENABLE'),
  (0, 'system.orgs:update', '编辑组织', 'BUTTON', 'system.orgs', NULL, 220, 'ENABLE'),
  (0, 'system.orgs:delete', '删除组织', 'BUTTON', 'system.orgs', NULL, 230, 'ENABLE'),
  (0, 'system.orgs:sync', '同步组织', 'BUTTON', 'system.orgs', NULL, 240, 'ENABLE'),
  (0, 'system.users:create', '新增用户', 'BUTTON', 'system.users', NULL, 250, 'ENABLE'),
  (0, 'system.users:update', '编辑用户', 'BUTTON', 'system.users', NULL, 260, 'ENABLE'),
  (0, 'system.users:delete', '删除用户', 'BUTTON', 'system.users', NULL, 270, 'ENABLE'),
  (0, 'system.users:status', '启停账号', 'BUTTON', 'system.users', NULL, 280, 'ENABLE'),
  (0, 'system.users:reset-password', '重置密码', 'BUTTON', 'system.users', NULL, 290, 'ENABLE'),
  (0, 'system.users:assign-permission', '配置用户权限', 'BUTTON', 'system.users', NULL, 300, 'ENABLE'),
  (0, 'system.roles:create', '新增角色', 'BUTTON', 'system.roles', NULL, 310, 'ENABLE'),
  (0, 'system.roles:update', '编辑角色', 'BUTTON', 'system.roles', NULL, 320, 'ENABLE'),
  (0, 'system.roles:delete', '删除角色', 'BUTTON', 'system.roles', NULL, 330, 'ENABLE'),
  (0, 'system.roles:assign-permission', '配置角色权限', 'BUTTON', 'system.roles', NULL, 340, 'ENABLE'),
  (0, 'system.logs:query', '查看日志', 'BUTTON', 'system.logs', NULL, 350, 'ENABLE');

INSERT IGNORE INTO `sys_role_permission` (`role_id`, `permission_item_id`, `created_by`, `created_at`)
SELECT r.id, p.id, 1, NOW()
FROM sys_role r
INNER JOIN sys_permission_item p ON p.permission_code IN (
  '/dashboard',
  '/system/orgs',
  '/system/users',
  '/system/roles',
  '/system/logs',
  '/customers',
  '/customer-risks',
  '/customer-debts',
  '/opportunities',
  '/loan-orders',
  '/repayments',
  'system.orgs:create',
  'system.orgs:update',
  'system.orgs:delete',
  'system.orgs:sync',
  'system.users:create',
  'system.users:update',
  'system.users:delete',
  'system.users:status',
  'system.users:reset-password',
  'system.users:assign-permission',
  'system.roles:create',
  'system.roles:update',
  'system.roles:delete',
  'system.roles:assign-permission',
  'system.logs:query'
)
WHERE r.role_code = 'ADMIN';
