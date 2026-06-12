USE huacai_system;

SET @db_name := DATABASE();

SET @has_action_code := (
  SELECT COUNT(*)
  FROM information_schema.columns
  WHERE table_schema = @db_name
    AND table_name = 'sys_operation_log'
    AND column_name = 'action_code'
);
SET @sql := IF(
  @has_action_code = 0,
  'ALTER TABLE `sys_operation_log` ADD COLUMN `action_code` VARCHAR(32) DEFAULT NULL COMMENT ''操作编码'' AFTER `biz_type`',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_action_desc := (
  SELECT COUNT(*)
  FROM information_schema.columns
  WHERE table_schema = @db_name
    AND table_name = 'sys_operation_log'
    AND column_name = 'action_desc'
);
SET @sql := IF(
  @has_action_desc = 0,
  'ALTER TABLE `sys_operation_log` ADD COLUMN `action_desc` VARCHAR(255) DEFAULT NULL COMMENT ''操作描述'' AFTER `action_code`',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_target_type := (
  SELECT COUNT(*)
  FROM information_schema.columns
  WHERE table_schema = @db_name
    AND table_name = 'sys_operation_log'
    AND column_name = 'target_type'
);
SET @sql := IF(
  @has_target_type = 0,
  'ALTER TABLE `sys_operation_log` ADD COLUMN `target_type` VARCHAR(64) DEFAULT NULL COMMENT ''对象类型'' AFTER `action_desc`',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_target_id := (
  SELECT COUNT(*)
  FROM information_schema.columns
  WHERE table_schema = @db_name
    AND table_name = 'sys_operation_log'
    AND column_name = 'target_id'
);
SET @sql := IF(
  @has_target_id = 0,
  'ALTER TABLE `sys_operation_log` ADD COLUMN `target_id` BIGINT UNSIGNED DEFAULT NULL COMMENT ''对象ID'' AFTER `target_type`',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
