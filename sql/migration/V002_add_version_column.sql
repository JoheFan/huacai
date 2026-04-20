SET @cust_customer_version_exists = (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'cust_customer'
    AND COLUMN_NAME = 'version'
);

SET @cust_customer_version_sql = IF(
  @cust_customer_version_exists = 0,
  'ALTER TABLE `cust_customer` ADD COLUMN `version` INT NOT NULL DEFAULT 0 COMMENT ''乐观锁版本号'' AFTER `deleted_flag`',
  'SELECT 1'
);

PREPARE cust_customer_version_stmt FROM @cust_customer_version_sql;
EXECUTE cust_customer_version_stmt;
DEALLOCATE PREPARE cust_customer_version_stmt;

SET @loan_order_version_exists = (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'loan_order'
    AND COLUMN_NAME = 'version'
);

SET @loan_order_version_sql = IF(
  @loan_order_version_exists = 0,
  'ALTER TABLE `loan_order` ADD COLUMN `version` INT NOT NULL DEFAULT 0 COMMENT ''乐观锁版本号'' AFTER `deleted_flag`',
  'SELECT 1'
);

PREPARE loan_order_version_stmt FROM @loan_order_version_sql;
EXECUTE loan_order_version_stmt;
DEALLOCATE PREPARE loan_order_version_stmt;

SET @loan_repayment_version_exists = (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'loan_repayment'
    AND COLUMN_NAME = 'version'
);

SET @loan_repayment_version_sql = IF(
  @loan_repayment_version_exists = 0,
  'ALTER TABLE `loan_repayment` ADD COLUMN `version` INT NOT NULL DEFAULT 0 COMMENT ''乐观锁版本号'' AFTER `deleted_flag`',
  'SELECT 1'
);

PREPARE loan_repayment_version_stmt FROM @loan_repayment_version_sql;
EXECUTE loan_repayment_version_stmt;
DEALLOCATE PREPARE loan_repayment_version_stmt;

SET @customer_score_version_exists = (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'cust_customer_score'
    AND COLUMN_NAME = 'version'
);

SET @customer_score_version_sql = IF(
  @customer_score_version_exists = 0,
  'ALTER TABLE `cust_customer_score` ADD COLUMN `version` INT NOT NULL DEFAULT 0 COMMENT ''乐观锁版本号'' AFTER `deleted_flag`',
  'SELECT 1'
);

PREPARE customer_score_version_stmt FROM @customer_score_version_sql;
EXECUTE customer_score_version_stmt;
DEALLOCATE PREPARE customer_score_version_stmt;

SET @customer_debt_version_exists = (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'cust_customer_debt'
    AND COLUMN_NAME = 'version'
);

SET @customer_debt_version_sql = IF(
  @customer_debt_version_exists = 0,
  'ALTER TABLE `cust_customer_debt` ADD COLUMN `version` INT NOT NULL DEFAULT 0 COMMENT ''乐观锁版本号'' AFTER `deleted_flag`',
  'SELECT 1'
);

PREPARE customer_debt_version_stmt FROM @customer_debt_version_sql;
EXECUTE customer_debt_version_stmt;
DEALLOCATE PREPARE customer_debt_version_stmt;

SET @customer_contract_version_exists = (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'cust_customer_contract'
    AND COLUMN_NAME = 'version'
);

SET @customer_contract_version_sql = IF(
  @customer_contract_version_exists = 0,
  'ALTER TABLE `cust_customer_contract` ADD COLUMN `version` INT NOT NULL DEFAULT 0 COMMENT ''乐观锁版本号'' AFTER `deleted_flag`',
  'SELECT 1'
);

PREPARE customer_contract_version_stmt FROM @customer_contract_version_sql;
EXECUTE customer_contract_version_stmt;
DEALLOCATE PREPARE customer_contract_version_stmt;
