-- 添加跟进人姓名字段到跟进记录表（替代 follower_user_id）
-- 一期跟进人按手输处理，不再使用用户ID关联
-- 幂等：仅当列不存在时才添加
-- 执行:ALTER TABLE `opp_follow_record` ADD COLUMN `follower_name` VARCHAR(100) DEFAULT NULL COMMENT '跟进人姓名' AFTER `follow_time`;
-- 兼容重复执行（MySQL 5.7+/8.0）
SET @dbname = DATABASE();
SET @tablename = 'opp_follow_record';
SET @columnname = 'follower_name';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
   WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @columnname) = 0,
  'ALTER TABLE `opp_follow_record` ADD COLUMN `follower_name` VARCHAR(100) DEFAULT NULL COMMENT ''跟进人姓名'' AFTER `follow_time`',
  'SELECT ''Column already exists, skipping'' as status'
));
PREPARE stmt FROM @preparedStatement;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
