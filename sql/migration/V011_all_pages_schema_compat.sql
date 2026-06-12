SET @db_name := DATABASE();

SET @has_loan_customer_summary_version := (
  SELECT COUNT(*)
  FROM information_schema.columns
  WHERE table_schema = @db_name
    AND table_name = 'loan_customer_summary'
    AND column_name = 'version'
);
SET @sql := IF(
  @has_loan_customer_summary_version = 0,
  'ALTER TABLE `loan_customer_summary` ADD COLUMN `version` INT NOT NULL DEFAULT 0 COMMENT ''乐观锁版本号'' AFTER `deleted_flag`',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_opp_opportunity_version := (
  SELECT COUNT(*)
  FROM information_schema.columns
  WHERE table_schema = @db_name
    AND table_name = 'opp_opportunity'
    AND column_name = 'version'
);
SET @sql := IF(
  @has_opp_opportunity_version = 0,
  'ALTER TABLE `opp_opportunity` ADD COLUMN `version` INT NOT NULL DEFAULT 0 COMMENT ''乐观锁版本号'' AFTER `deleted_flag`',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_opp_follow_record_version := (
  SELECT COUNT(*)
  FROM information_schema.columns
  WHERE table_schema = @db_name
    AND table_name = 'opp_follow_record'
    AND column_name = 'version'
);
SET @sql := IF(
  @has_opp_follow_record_version = 0,
  'ALTER TABLE `opp_follow_record` ADD COLUMN `version` INT NOT NULL DEFAULT 0 COMMENT ''乐观锁版本号'' AFTER `deleted_flag`',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
