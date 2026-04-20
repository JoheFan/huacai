SET @customer_tax_registration_exists = (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'cust_customer'
    AND COLUMN_NAME = 'tax_registration_normal'
);

SET @customer_tax_registration_sql = IF(
  @customer_tax_registration_exists = 0,
  'ALTER TABLE `cust_customer` ADD COLUMN `tax_registration_normal` TINYINT(1) DEFAULT NULL COMMENT ''税务登记是否正常'' AFTER `loan_status`',
  'SELECT 1'
);

PREPARE customer_tax_registration_stmt FROM @customer_tax_registration_sql;
EXECUTE customer_tax_registration_stmt;
DEALLOCATE PREPARE customer_tax_registration_stmt;

SET @contract_customer_name_exists = (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'cust_customer_contract'
    AND COLUMN_NAME = 'customer_name'
);

SET @contract_customer_name_sql = IF(
  @contract_customer_name_exists = 0,
  'ALTER TABLE `cust_customer_contract` ADD COLUMN `customer_name` VARCHAR(64) DEFAULT NULL COMMENT ''客户名称'' AFTER `customer_id`',
  'SELECT 1'
);

PREPARE contract_customer_name_stmt FROM @contract_customer_name_sql;
EXECUTE contract_customer_name_stmt;
DEALLOCATE PREPARE contract_customer_name_stmt;

SET @contract_company_name_exists = (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'cust_customer_contract'
    AND COLUMN_NAME = 'company_name'
);

SET @contract_company_name_sql = IF(
  @contract_company_name_exists = 0,
  'ALTER TABLE `cust_customer_contract` ADD COLUMN `company_name` VARCHAR(200) DEFAULT NULL COMMENT ''公司名称'' AFTER `customer_name`',
  'SELECT 1'
);

PREPARE contract_company_name_stmt FROM @contract_company_name_sql;
EXECUTE contract_company_name_stmt;
DEALLOCATE PREPARE contract_company_name_stmt;

SET @contract_credit_code_exists = (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'cust_customer_contract'
    AND COLUMN_NAME = 'credit_code'
);

SET @contract_credit_code_sql = IF(
  @contract_credit_code_exists = 0,
  'ALTER TABLE `cust_customer_contract` ADD COLUMN `credit_code` VARCHAR(64) DEFAULT NULL COMMENT ''统一社会信用代码'' AFTER `company_name`',
  'SELECT 1'
);

PREPARE contract_credit_code_stmt FROM @contract_credit_code_sql;
EXECUTE contract_credit_code_stmt;
DEALLOCATE PREPARE contract_credit_code_stmt;
