-- 新增用户业务模块可见范围表
-- 执行前请确认已在测试环境验证

CREATE TABLE IF NOT EXISTS `sys_user_module` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
  `module_key` VARCHAR(64) NOT NULL COMMENT '业务模块标识',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_user_module_user_module` (`user_id`, `module_key`),
  KEY `idx_sys_user_module_module_key` (`module_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户业务模块可见范围表';
