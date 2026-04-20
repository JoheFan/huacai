-- 客户借贷汇总表：按客户+资金来源维度持久化汇总字段，支持手工维护
-- 用于替代 pageOrderOverview 的运行时聚合，提供银行口径所需字段的持久化能力
CREATE TABLE IF NOT EXISTS `loan_customer_summary` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `customer_id` BIGINT UNSIGNED NOT NULL COMMENT '客户ID',
  `capital_source_type` VARCHAR(16) NOT NULL COMMENT '资金来源 SELF/BANK',
  `total_increment_amount` DECIMAL(14,2) DEFAULT NULL COMMENT '总增量金额（银行口径）',
  `increment_count` INT DEFAULT NULL COMMENT '增量笔数（银行口径）',
  `years_term` VARCHAR(20) DEFAULT NULL COMMENT '几年期（银行口径）',
  `channel_rate` DECIMAL(6,4) DEFAULT NULL COMMENT '渠道费率（银行口径）',
  `channel_fee` DECIMAL(14,2) DEFAULT NULL COMMENT '渠道费（银行口径）',
  `referrer` VARCHAR(100) DEFAULT NULL COMMENT '推荐人（银行口径）',
  `self_total_loan_amount` DECIMAL(14,2) DEFAULT NULL COMMENT '我方总贷款金额',
  `bank_total_loan_amount` DECIMAL(14,2) DEFAULT NULL COMMENT '银行总贷款金额',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_loan_summary_customer_capital` (`customer_id`, `capital_source_type`, `deleted_flag`),
  KEY `idx_loan_summary_customer_id` (`customer_id`),
  KEY `idx_loan_summary_capital_source_type` (`capital_source_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户借贷汇总表';
