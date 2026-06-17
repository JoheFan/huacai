-- 给 cust_customer_trade 补乐观锁 version 列
-- 背景：CustCustomerTrade 继承 AuditableEntity（带 @Version），但 V002 补 version 时漏了此表，
--       导致任何客户交易 update 都会抛 "Unknown column 'version'"。本脚本幂等。

SET @db_name := DATABASE();

SET @has_trade_version := (
  SELECT COUNT(*)
  FROM information_schema.columns
  WHERE table_schema = @db_name
    AND table_name = 'cust_customer_trade'
    AND column_name = 'version'
);
SET @sql := IF(
  @has_trade_version = 0,
  'ALTER TABLE `cust_customer_trade` ADD COLUMN `version` INT NOT NULL DEFAULT 0 COMMENT ''乐观锁版本号'' AFTER `deleted_flag`',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;