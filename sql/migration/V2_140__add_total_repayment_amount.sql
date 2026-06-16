-- 负债登记补充「总需还金额」列
-- 背景：CustCustomerDebt 实体新增了 totalRepaymentAmount 字段（映射列 total_repayment_amount），
--       但存量库的 cust_customer_debt 表缺这一列，导致负债的任何读写都抛
--       "Unknown column 'total_repayment_amount'"。补列使表与实体一致。
-- 脚本幂等，可重复执行。

SET @db_name := DATABASE();

SET @has_total_repayment_amount := (
  SELECT COUNT(*)
  FROM information_schema.columns
  WHERE table_schema = @db_name
    AND table_name = 'cust_customer_debt'
    AND column_name = 'total_repayment_amount'
);
SET @sql := IF(
  @has_total_repayment_amount = 0,
  'ALTER TABLE `cust_customer_debt` ADD COLUMN `total_repayment_amount` DECIMAL(14,2) DEFAULT NULL COMMENT ''总需还金额'' AFTER `debt_amount`',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 历史数据回填：总需还按当前产品口径等于负债总额
UPDATE `cust_customer_debt`
SET `total_repayment_amount` = `debt_amount`
WHERE `total_repayment_amount` IS NULL
  AND `debt_amount` IS NOT NULL;
