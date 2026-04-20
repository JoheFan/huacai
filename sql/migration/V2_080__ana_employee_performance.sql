-- 员工绩效表：按员工+月份维度维护绩效台账
CREATE TABLE IF NOT EXISTS `ana_employee_performance` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `employee_id` BIGINT UNSIGNED NOT NULL COMMENT '员工ID',
  `employee_name` VARCHAR(100) NOT NULL COMMENT '员工名称',
  `org_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '部门ID',
  `org_name` VARCHAR(200) DEFAULT NULL COMMENT '部门名称',
  `period_date` VARCHAR(7) NOT NULL COMMENT '日期（年月，格式：YYYY-MM）',
  `target_amount` DECIMAL(18,2) DEFAULT NULL COMMENT '绩效目标',
  `actual_amount` DECIMAL(18,2) DEFAULT NULL COMMENT '实际达成',
  `deal_amount` DECIMAL(18,2) DEFAULT NULL COMMENT '成交金额',
  `deal_count` INT DEFAULT NULL COMMENT '成交数',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
  PRIMARY KEY (`id`),
  KEY `idx_ana_employee_performance_employee` (`employee_id`),
  KEY `idx_ana_employee_performance_period` (`period_date`),
  KEY `idx_ana_employee_performance_deleted` (`deleted_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='员工绩效表';
