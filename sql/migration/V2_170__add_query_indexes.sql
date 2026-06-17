-- 补查询热点索引：客户主/子表的"过滤+排序"字段、商机数据权限字段、报销部门字段
-- 均为幂等(存在则跳过)。背景：这些字段是分页/数据权限的高频条件，缺索引在数据增长后会 filesort/全表扫。

-- 通用幂等加索引：检查 information_schema.statistics，不存在才 CREATE INDEX
-- cust_customer：SELF 数据权限(created_by) + 列表排序(created_at)
SET @sql := IF((SELECT COUNT(*) FROM information_schema.statistics WHERE table_schema=DATABASE() AND table_name='cust_customer' AND index_name='idx_cust_customer_created_by_at')=0,
  'CREATE INDEX `idx_cust_customer_created_by_at` ON `cust_customer` (`created_by`, `created_at`)', 'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

SET @sql := IF((SELECT COUNT(*) FROM information_schema.statistics WHERE table_schema=DATABASE() AND table_name='cust_customer' AND index_name='idx_cust_customer_created_at')=0,
  'CREATE INDEX `idx_cust_customer_created_at` ON `cust_customer` (`created_at`)', 'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- 客户子表：customer_id + 各自排序字段
SET @sql := IF((SELECT COUNT(*) FROM information_schema.statistics WHERE table_schema=DATABASE() AND table_name='cust_customer_score' AND index_name='idx_cust_score_customer_test')=0,
  'CREATE INDEX `idx_cust_score_customer_test` ON `cust_customer_score` (`customer_id`, `test_date`)', 'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

SET @sql := IF((SELECT COUNT(*) FROM information_schema.statistics WHERE table_schema=DATABASE() AND table_name='cust_customer_debt' AND index_name='idx_cust_debt_customer_updated')=0,
  'CREATE INDEX `idx_cust_debt_customer_updated` ON `cust_customer_debt` (`customer_id`, `updated_at`)', 'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

SET @sql := IF((SELECT COUNT(*) FROM information_schema.statistics WHERE table_schema=DATABASE() AND table_name='cust_customer_contract' AND index_name='idx_cust_contract_customer_updated')=0,
  'CREATE INDEX `idx_cust_contract_customer_updated` ON `cust_customer_contract` (`customer_id`, `updated_at`)', 'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

SET @sql := IF((SELECT COUNT(*) FROM information_schema.statistics WHERE table_schema=DATABASE() AND table_name='cust_customer_trade' AND index_name='idx_cust_trade_customer_date')=0,
  'CREATE INDEX `idx_cust_trade_customer_date` ON `cust_customer_trade` (`customer_id`, `trade_date`)', 'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- 商机：行级数据权限按 created_by 过滤 + 列表按 created_at 排序
SET @sql := IF((SELECT COUNT(*) FROM information_schema.statistics WHERE table_schema=DATABASE() AND table_name='opp_opportunity' AND index_name='idx_opp_created_by_at')=0,
  'CREATE INDEX `idx_opp_created_by_at` ON `opp_opportunity` (`created_by`, `created_at`)', 'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- 报销：列表"全部"按申请人部门过滤(applicant_id 已有索引，仅补 applicant_org_id)
SET @sql := IF((SELECT COUNT(*) FROM information_schema.statistics WHERE table_schema=DATABASE() AND table_name='fin_reimbursement_apply' AND index_name='idx_fin_reimb_applicant_org')=0,
  'CREATE INDEX `idx_fin_reimb_applicant_org` ON `fin_reimbursement_apply` (`applicant_org_id`)', 'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;