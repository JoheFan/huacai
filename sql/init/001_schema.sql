CREATE DATABASE IF NOT EXISTS `huacai_system`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_general_ci;

USE `huacai_system`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

/*
  说明：
  1. 当前为一期建表草案，优先保证结构清晰、便于开发。
  2. 一期不强制加外键约束，避免导入、初始化、历史数据迁移时联调成本过高。
  3. 统一采用逻辑删除字段 deleted_flag。
  4. 客户唯一规则当前仅对 customer_no 做唯一约束；credit_code 是否唯一，待业务确认后再调整。
*/

DROP TABLE IF EXISTS `fin_expense`;
DROP TABLE IF EXISTS `fin_income`;
DROP TABLE IF EXISTS `fin_payee`;
DROP TABLE IF EXISTS `loan_import_task`;
DROP TABLE IF EXISTS `loan_repayment`;
DROP TABLE IF EXISTS `loan_order`;
DROP TABLE IF EXISTS `opp_follow_record`;
DROP TABLE IF EXISTS `opp_opportunity`;
DROP TABLE IF EXISTS `cust_customer_trade`;
DROP TABLE IF EXISTS `cust_customer_contract`;
DROP TABLE IF EXISTS `cust_customer_debt`;
DROP TABLE IF EXISTS `cust_customer_status_log`;
DROP TABLE IF EXISTS `cust_customer_score`;
DROP TABLE IF EXISTS `cust_customer`;
DROP TABLE IF EXISTS `sys_file`;
DROP TABLE IF EXISTS `sys_operation_log`;
DROP TABLE IF EXISTS `sys_dict_item`;
DROP TABLE IF EXISTS `sys_dict_type`;
DROP TABLE IF EXISTS `sys_user_data_scope`;
DROP TABLE IF EXISTS `sys_role_data_scope`;
DROP TABLE IF EXISTS `sys_user_permission`;
DROP TABLE IF EXISTS `sys_role_permission`;
DROP TABLE IF EXISTS `sys_permission_item`;
DROP TABLE IF EXISTS `sys_role_menu`;
DROP TABLE IF EXISTS `sys_menu`;
DROP TABLE IF EXISTS `sys_user_module`;
DROP TABLE IF EXISTS `sys_user_role`;
DROP TABLE IF EXISTS `sys_role`;
DROP TABLE IF EXISTS `sys_user`;
DROP TABLE IF EXISTS `sys_org`;

CREATE TABLE `sys_org` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `parent_id` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '上级组织ID',
  `org_name` VARCHAR(100) NOT NULL COMMENT '组织名称',
  `org_type` VARCHAR(32) NOT NULL DEFAULT 'DEPT' COMMENT '组织类型',
  `sort_no` INT NOT NULL DEFAULT 0 COMMENT '排序号',
  `status` VARCHAR(16) NOT NULL DEFAULT 'ENABLE' COMMENT '状态',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
  PRIMARY KEY (`id`),
  KEY `idx_sys_org_parent_id` (`parent_id`),
  KEY `idx_sys_org_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='组织机构表';

CREATE TABLE `sys_user` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` VARCHAR(64) NOT NULL COMMENT '登录账号',
  `password_hash` VARCHAR(255) NOT NULL COMMENT '密码哈希',
  `employee_code` VARCHAR(64) DEFAULT NULL COMMENT '员工编号',
  `real_name` VARCHAR(64) NOT NULL COMMENT '真实姓名',
  `phone` VARCHAR(32) DEFAULT NULL COMMENT '手机号',
  `email` VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
  `org_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '所属组织ID',
  `identity_type` VARCHAR(32) NOT NULL DEFAULT 'NORMAL_USER' COMMENT '身份类型',
  `job_title` VARCHAR(64) DEFAULT NULL COMMENT '岗位/职位',
  `employment_status` VARCHAR(16) NOT NULL DEFAULT 'ON_JOB' COMMENT '任职状态',
  `account_status` VARCHAR(16) NOT NULL DEFAULT 'ENABLE' COMMENT '账号状态',
  `last_login_at` DATETIME DEFAULT NULL COMMENT '最后登录时间',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_user_username` (`username`),
  KEY `idx_sys_user_org_id` (`org_id`),
  KEY `idx_sys_user_account_status` (`account_status`),
  KEY `idx_sys_user_employment_status` (`employment_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

CREATE TABLE `sys_role` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_code` VARCHAR(64) NOT NULL COMMENT '角色编码',
  `role_name` VARCHAR(100) NOT NULL COMMENT '角色名称',
  `identity_type` VARCHAR(32) NOT NULL DEFAULT 'NORMAL_USER' COMMENT '适用身份类型',
  `data_scope` VARCHAR(32) NOT NULL DEFAULT 'SELF' COMMENT '数据权限范围',
  `status` VARCHAR(16) NOT NULL DEFAULT 'ENABLE' COMMENT '状态',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_role_role_code` (`role_code`),
  KEY `idx_sys_role_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

CREATE TABLE `sys_user_role` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
  `role_id` BIGINT UNSIGNED NOT NULL COMMENT '角色ID',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_user_role_user_role` (`user_id`, `role_id`),
  KEY `idx_sys_user_role_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

CREATE TABLE `sys_user_module` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
  `module_key` VARCHAR(64) NOT NULL COMMENT '业务模块标识',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_user_module_user_module` (`user_id`, `module_key`),
  KEY `idx_sys_user_module_module_key` (`module_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户业务模块可见范围表';

CREATE TABLE `sys_permission_item` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `parent_id` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '父级权限项ID',
  `permission_code` VARCHAR(128) NOT NULL COMMENT '权限编码',
  `permission_name` VARCHAR(100) NOT NULL COMMENT '权限名称',
  `permission_type` VARCHAR(16) NOT NULL DEFAULT 'PAGE' COMMENT '权限类型 PAGE/BUTTON',
  `module_code` VARCHAR(64) DEFAULT NULL COMMENT '模块编码',
  `route_path` VARCHAR(255) DEFAULT NULL COMMENT '页面路由',
  `button_code` VARCHAR(128) DEFAULT NULL COMMENT '按钮编码',
  `sort_no` INT NOT NULL DEFAULT 0 COMMENT '排序号',
  `status` VARCHAR(16) NOT NULL DEFAULT 'ENABLE' COMMENT '状态',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_permission_item_code` (`permission_code`),
  KEY `idx_sys_permission_item_parent_id` (`parent_id`),
  KEY `idx_sys_permission_item_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限项表';

CREATE TABLE `sys_role_permission` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` BIGINT UNSIGNED NOT NULL COMMENT '角色ID',
  `permission_item_id` BIGINT UNSIGNED NOT NULL COMMENT '权限项ID',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_role_permission` (`role_id`, `permission_item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

CREATE TABLE `sys_user_permission` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
  `permission_item_id` BIGINT UNSIGNED NOT NULL COMMENT '权限项ID',
  `grant_mode` VARCHAR(16) NOT NULL DEFAULT 'ALLOW' COMMENT '授权模式 ALLOW/DENY',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_user_permission` (`user_id`, `permission_item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户权限覆盖表';

CREATE TABLE `sys_role_data_scope` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` BIGINT UNSIGNED NOT NULL COMMENT '角色ID',
  `module_code` VARCHAR(64) NOT NULL COMMENT '模块编码',
  `scope_type` VARCHAR(32) NOT NULL COMMENT '数据权限范围',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_role_data_scope` (`role_id`, `module_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色数据权限表';

CREATE TABLE `sys_user_data_scope` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
  `module_code` VARCHAR(64) NOT NULL COMMENT '模块编码',
  `scope_type` VARCHAR(32) NOT NULL COMMENT '数据权限范围',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_user_data_scope` (`user_id`, `module_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户数据权限覆盖表';

CREATE TABLE `sys_menu` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `parent_id` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '父菜单ID',
  `menu_name` VARCHAR(100) NOT NULL COMMENT '菜单名称',
  `menu_type` VARCHAR(16) NOT NULL DEFAULT 'MENU' COMMENT '类型 MENU/BUTTON',
  `route_path` VARCHAR(255) DEFAULT NULL COMMENT '前端路由地址',
  `component_path` VARCHAR(255) DEFAULT NULL COMMENT '前端组件路径',
  `permission_code` VARCHAR(128) DEFAULT NULL COMMENT '权限编码',
  `icon` VARCHAR(64) DEFAULT NULL COMMENT '图标',
  `sort_no` INT NOT NULL DEFAULT 0 COMMENT '排序号',
  `status` VARCHAR(16) NOT NULL DEFAULT 'ENABLE' COMMENT '状态',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
  PRIMARY KEY (`id`),
  KEY `idx_sys_menu_parent_id` (`parent_id`),
  KEY `idx_sys_menu_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单权限表';

CREATE TABLE `sys_role_menu` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` BIGINT UNSIGNED NOT NULL COMMENT '角色ID',
  `menu_id` BIGINT UNSIGNED NOT NULL COMMENT '菜单ID',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_role_menu_role_menu` (`role_id`, `menu_id`),
  KEY `idx_sys_role_menu_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

CREATE TABLE `sys_dict_type` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `dict_code` VARCHAR(64) NOT NULL COMMENT '字典编码',
  `dict_name` VARCHAR(100) NOT NULL COMMENT '字典名称',
  `status` VARCHAR(16) NOT NULL DEFAULT 'ENABLE' COMMENT '状态',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_dict_type_dict_code` (`dict_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典类型表';

CREATE TABLE `sys_dict_item` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `dict_type_id` BIGINT UNSIGNED NOT NULL COMMENT '字典类型ID',
  `item_code` VARCHAR(64) NOT NULL COMMENT '字典项编码',
  `item_name` VARCHAR(100) NOT NULL COMMENT '字典项名称',
  `item_value` VARCHAR(100) NOT NULL COMMENT '字典项值',
  `sort_no` INT NOT NULL DEFAULT 0 COMMENT '排序号',
  `status` VARCHAR(16) NOT NULL DEFAULT 'ENABLE' COMMENT '状态',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_sys_dict_item_dict_type_id` (`dict_type_id`),
  KEY `idx_sys_dict_item_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典项表';

CREATE TABLE `sys_operation_log` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '操作人ID',
  `module_code` VARCHAR(64) DEFAULT NULL COMMENT '模块编码',
  `biz_type` VARCHAR(64) DEFAULT NULL COMMENT '业务类型',
  `request_uri` VARCHAR(255) DEFAULT NULL COMMENT '请求地址',
  `request_method` VARCHAR(16) DEFAULT NULL COMMENT '请求方法',
  `request_body` LONGTEXT COMMENT '请求参数',
  `result_code` VARCHAR(32) DEFAULT NULL COMMENT '结果码',
  `result_msg` VARCHAR(500) DEFAULT NULL COMMENT '结果说明',
  `ip` VARCHAR(64) DEFAULT NULL COMMENT 'IP地址',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_sys_operation_log_user_id` (`user_id`),
  KEY `idx_sys_operation_log_module_code` (`module_code`),
  KEY `idx_sys_operation_log_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

CREATE TABLE `sys_file` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `biz_type` VARCHAR(64) DEFAULT NULL COMMENT '业务类型',
  `biz_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '业务ID',
  `file_name` VARCHAR(255) NOT NULL COMMENT '原始文件名',
  `file_path` VARCHAR(500) NOT NULL COMMENT '存储路径',
  `file_ext` VARCHAR(32) DEFAULT NULL COMMENT '文件后缀',
  `file_size` BIGINT UNSIGNED DEFAULT NULL COMMENT '文件大小',
  `storage_type` VARCHAR(32) NOT NULL DEFAULT 'LOCAL' COMMENT '存储类型',
  `uploader_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '上传人ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `deleted_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_sys_file_biz_type_biz_id` (`biz_type`, `biz_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件元数据表';

CREATE TABLE `cust_customer` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `customer_no` VARCHAR(64) NOT NULL COMMENT '客户编号',
  `customer_name` VARCHAR(64) NOT NULL COMMENT '客户名称',
  `gender` VARCHAR(16) DEFAULT NULL COMMENT '性别',
  `id_card` VARCHAR(32) DEFAULT NULL COMMENT '身份证号',
  `birthday` DATE DEFAULT NULL COMMENT '出生日期',
  `age` INT DEFAULT NULL COMMENT '年龄',
  `mobile` VARCHAR(32) DEFAULT NULL COMMENT '手机号',
  `company_name` VARCHAR(200) DEFAULT NULL COMMENT '企业名称',
  `credit_code` VARCHAR(64) DEFAULT NULL COMMENT '统一社会信用代码',
  `established_date` DATE DEFAULT NULL COMMENT '成立日期',
  `industry` VARCHAR(100) DEFAULT NULL COMMENT '行业',
  `business_address` VARCHAR(255) DEFAULT NULL COMMENT '经营地址',
  `bank_name` VARCHAR(100) DEFAULT NULL COMMENT '开户行',
  `bank_account` VARCHAR(100) DEFAULT NULL COMMENT '银行账号',
  `recommender_name` VARCHAR(64) DEFAULT NULL COMMENT '推荐人',
  `recommender_rate` DECIMAL(10,2) DEFAULT NULL COMMENT '推荐人返点',
  `service_fee` DECIMAL(12,2) DEFAULT NULL COMMENT '服务费',
  `audit_status` VARCHAR(16) NOT NULL DEFAULT 'PENDING' COMMENT '审核状态',
  `biz_status` VARCHAR(16) NOT NULL DEFAULT 'INIT' COMMENT '业务状态',
  `loan_status` VARCHAR(16) NOT NULL DEFAULT 'NOT_STARTED' COMMENT '借贷状态',
  `tax_registration_normal` TINYINT(1) DEFAULT NULL COMMENT '税务登记是否正常',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_cust_customer_customer_no` (`customer_no`),
  KEY `idx_cust_customer_mobile` (`mobile`),
  KEY `idx_cust_customer_company_name` (`company_name`),
  KEY `idx_cust_customer_credit_code` (`credit_code`),
  KEY `idx_cust_customer_audit_status` (`audit_status`),
  KEY `idx_cust_customer_loan_status` (`loan_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户主表';

CREATE TABLE `cust_customer_score` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `customer_id` BIGINT UNSIGNED NOT NULL COMMENT '客户ID',
  `test_date` DATE DEFAULT NULL COMMENT '测试日期',
  `test_limit` DECIMAL(14,2) DEFAULT NULL COMMENT '测试额度',
  `traffic_value` DECIMAL(14,2) DEFAULT NULL COMMENT '流量值',
  `composite_score` DECIMAL(10,2) DEFAULT NULL COMMENT '综合评分',
  `third_party_score` DECIMAL(10,2) DEFAULT NULL COMMENT '第三方评分',
  `score_result` VARCHAR(32) DEFAULT NULL COMMENT '评分结果',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_cust_customer_score_customer_id` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户评估测试信息表';

CREATE TABLE `cust_customer_status_log` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `customer_id` BIGINT UNSIGNED NOT NULL COMMENT '客户ID',
  `status_code` VARCHAR(32) NOT NULL COMMENT '状态编码',
  `status_name` VARCHAR(64) NOT NULL COMMENT '状态名称',
  `changed_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '变更时间',
  `changed_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '变更人',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_cust_customer_status_log_customer_id` (`customer_id`),
  KEY `idx_cust_customer_status_log_changed_at` (`changed_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户状态历史表';

CREATE TABLE `cust_customer_debt` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `customer_id` BIGINT UNSIGNED NOT NULL COMMENT '客户ID',
  `debt_type` VARCHAR(64) NOT NULL COMMENT '负债类型',
  `debt_amount` DECIMAL(14,2) DEFAULT NULL COMMENT '负债总额',
  `total_repayment_amount` DECIMAL(14,2) DEFAULT NULL COMMENT '总需还金额',
  `repaid_amount` DECIMAL(14,2) DEFAULT NULL COMMENT '已偿还金额',
  `pending_amount` DECIMAL(14,2) DEFAULT NULL COMMENT '待偿还金额',
  `installment_amount` DECIMAL(14,2) DEFAULT NULL COMMENT '每期金额',
  `repayment_day` INT DEFAULT NULL COMMENT '还款日',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_cust_customer_debt_customer_id` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户负债信息表';

CREATE TABLE `cust_customer_contract` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `customer_id` BIGINT UNSIGNED NOT NULL COMMENT '客户ID',
  `customer_name` VARCHAR(64) DEFAULT NULL COMMENT '客户名称',
  `company_name` VARCHAR(200) DEFAULT NULL COMMENT '公司名称',
  `credit_code` VARCHAR(64) DEFAULT NULL COMMENT '统一社会信用代码',
  `contract_no` VARCHAR(64) DEFAULT NULL COMMENT '合同编号',
  `contract_name` VARCHAR(200) NOT NULL COMMENT '合同名称',
  `contract_file_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '合同附件ID',
  `sign_date` DATE DEFAULT NULL COMMENT '签订日期',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_cust_customer_contract_customer_id` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户合同表';

CREATE TABLE `cust_customer_trade` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `customer_id` BIGINT UNSIGNED NOT NULL COMMENT '客户ID',
  `trade_type` VARCHAR(32) NOT NULL COMMENT '交易类型',
  `amount` DECIMAL(14,2) DEFAULT NULL COMMENT '金额',
  `service_fee` DECIMAL(14,2) DEFAULT NULL COMMENT '手续费',
  `actual_received` DECIMAL(14,2) DEFAULT NULL COMMENT '实际到账',
  `returned_amount` DECIMAL(14,2) DEFAULT NULL COMMENT '回款金额',
  `balance_amount` DECIMAL(14,2) DEFAULT NULL COMMENT '余额',
  `trade_date` DATE DEFAULT NULL COMMENT '交易日期',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_cust_customer_trade_customer_id` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户交易信息表';

CREATE TABLE `opp_opportunity` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `customer_id` BIGINT UNSIGNED NOT NULL COMMENT '客户ID',
  `priority_level` VARCHAR(16) DEFAULT NULL COMMENT '优先级',
  `stage_code` VARCHAR(32) DEFAULT NULL COMMENT '阶段编码',
  `owner_user_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '跟进人',
  `estimated_amount` DECIMAL(14,2) DEFAULT NULL COMMENT '预估金额',
  `intent_level` VARCHAR(32) DEFAULT NULL COMMENT '意向等级',
  `status` VARCHAR(16) NOT NULL DEFAULT 'OPEN' COMMENT '状态',
  `next_follow_time` DATETIME DEFAULT NULL COMMENT '下次跟进时间',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_opp_opportunity_customer_id` (`customer_id`),
  KEY `idx_opp_opportunity_owner_user_id` (`owner_user_id`),
  KEY `idx_opp_opportunity_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商机主表';

CREATE TABLE `opp_follow_record` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `opportunity_id` BIGINT UNSIGNED NOT NULL COMMENT '商机ID',
  `customer_id` BIGINT UNSIGNED NOT NULL COMMENT '客户ID',
  `follow_time` DATETIME NOT NULL COMMENT '跟进时间',
  `follower_user_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '跟进人',
  `follow_content` VARCHAR(2000) NOT NULL COMMENT '跟进内容',
  `next_action` VARCHAR(500) DEFAULT NULL COMMENT '下一步动作',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_opp_follow_record_opportunity_id` (`opportunity_id`),
  KEY `idx_opp_follow_record_customer_id` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商机跟进记录表';

CREATE TABLE `loan_order` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `customer_id` BIGINT UNSIGNED NOT NULL COMMENT '客户ID',
  `capital_source_type` VARCHAR(16) NOT NULL COMMENT '资金来源 SELF/BANK',
  `bank_name` VARCHAR(100) DEFAULT NULL COMMENT '银行名称',
  `loan_date` DATE DEFAULT NULL COMMENT '借款日期',
  `deposit_gold_amount` DECIMAL(14,2) DEFAULT NULL COMMENT '理财定存/黄金金额',
  `credit_card_repayment_amount` DECIMAL(14,2) DEFAULT NULL COMMENT '垫还信用卡网贷金额',
  `loan_amount` DECIMAL(14,2) DEFAULT NULL COMMENT '借款金额',
  `balance_amount` DECIMAL(14,2) DEFAULT NULL COMMENT '余额',
  `monthly_interest_amount` DECIMAL(14,2) DEFAULT NULL COMMENT '每月利息',
  `loan_count` INT DEFAULT NULL COMMENT '贷款笔数',
  `status` VARCHAR(16) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_loan_order_customer_id` (`customer_id`),
  KEY `idx_loan_order_capital_source_type` (`capital_source_type`),
  KEY `idx_loan_order_status` (`status`),
  KEY `idx_loan_order_loan_date` (`loan_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='借贷主表';

CREATE TABLE `loan_repayment` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `loan_order_id` BIGINT UNSIGNED NOT NULL COMMENT '借贷单ID',
  `customer_id` BIGINT UNSIGNED NOT NULL COMMENT '客户ID',
  `capital_source_type` VARCHAR(16) NOT NULL COMMENT '资金来源 SELF/BANK',
  `repayment_date` DATE NOT NULL COMMENT '还款日期',
  `repayment_amount` DECIMAL(14,2) DEFAULT NULL COMMENT '还款总金额',
  `principal_amount` DECIMAL(14,2) DEFAULT NULL COMMENT '本金',
  `interest_amount` DECIMAL(14,2) DEFAULT NULL COMMENT '利息',
  `repayment_channel` VARCHAR(64) DEFAULT NULL COMMENT '还款途径',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_loan_repayment_loan_order_id` (`loan_order_id`),
  KEY `idx_loan_repayment_customer_id` (`customer_id`),
  KEY `idx_loan_repayment_repayment_date` (`repayment_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='还款明细表';

CREATE TABLE `loan_import_task` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `capital_source_type` VARCHAR(16) NOT NULL COMMENT '资金来源 SELF/BANK',
  `file_id` BIGINT UNSIGNED NOT NULL COMMENT '导入文件ID',
  `task_status` VARCHAR(16) NOT NULL DEFAULT 'INIT' COMMENT '任务状态',
  `total_count` INT NOT NULL DEFAULT 0 COMMENT '总记录数',
  `success_count` INT NOT NULL DEFAULT 0 COMMENT '成功数',
  `fail_count` INT NOT NULL DEFAULT 0 COMMENT '失败数',
  `result_message` VARCHAR(1000) DEFAULT NULL COMMENT '结果说明',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_loan_import_task_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='借贷导入任务表';

CREATE TABLE `fin_payee` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `payee_name` VARCHAR(200) NOT NULL COMMENT '收款方/往来方名称',
  `payee_type` VARCHAR(32) DEFAULT NULL COMMENT '类型',
  `bank_name` VARCHAR(100) DEFAULT NULL COMMENT '开户行',
  `bank_account` VARCHAR(100) DEFAULT NULL COMMENT '账号',
  `contact_name` VARCHAR(64) DEFAULT NULL COMMENT '联系人',
  `contact_phone` VARCHAR(32) DEFAULT NULL COMMENT '联系电话',
  `status` VARCHAR(16) NOT NULL DEFAULT 'ENABLE' COMMENT '状态',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_fin_payee_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收款方/往来方表';

CREATE TABLE `fin_income` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `income_name` VARCHAR(200) NOT NULL COMMENT '收入名称',
  `income_type` VARCHAR(32) DEFAULT NULL COMMENT '收入类型',
  `biz_date` DATE NOT NULL COMMENT '业务日期',
  `amount` DECIMAL(14,2) NOT NULL DEFAULT 0.00 COMMENT '金额',
  `payer_name` VARCHAR(200) DEFAULT NULL COMMENT '转入方名称',
  `payer_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '转入方ID',
  `file_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '附件ID',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_fin_income_biz_date` (`biz_date`),
  KEY `idx_fin_income_income_type` (`income_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收入表';

CREATE TABLE `fin_expense` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `expense_name` VARCHAR(200) NOT NULL COMMENT '支出名称',
  `expense_type` VARCHAR(32) DEFAULT NULL COMMENT '支出类型',
  `biz_date` DATE NOT NULL COMMENT '业务日期',
  `amount` DECIMAL(14,2) NOT NULL DEFAULT 0.00 COMMENT '金额',
  `payee_name` VARCHAR(200) DEFAULT NULL COMMENT '支出对象名称',
  `payee_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '支出对象ID',
  `file_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '附件ID',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_fin_expense_biz_date` (`biz_date`),
  KEY `idx_fin_expense_expense_type` (`expense_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支出表';

INSERT IGNORE INTO `sys_role`
(`role_code`, `role_name`, `identity_type`, `data_scope`, `status`, `remark`, `created_by`, `updated_by`)
VALUES
  ('ADMIN', '系统管理员', 'SUPER_ADMIN', 'ALL', 'ENABLE', '系统默认超级管理员角色', 1, 1),
  ('DEPT_ADMIN', '部门管理员', 'DEPT_ADMIN', 'ORG', 'ENABLE', '系统默认部门管理员角色', 1, 1),
  ('STAFF', '普通用户', 'NORMAL_USER', 'SELF', 'ENABLE', '系统默认普通用户角色', 1, 1);

INSERT IGNORE INTO `sys_permission_item`
(`parent_id`, `permission_code`, `permission_name`, `permission_type`, `module_code`, `route_path`, `sort_no`, `status`)
VALUES
  (0, '/dashboard', '工作台', 'PAGE', 'dashboard', '/dashboard', 10, 'ENABLE'),
  (0, '/system/orgs', '组织管理', 'PAGE', 'system.orgs', '/system/orgs', 20, 'ENABLE'),
  (0, '/system/users', '系统用户', 'PAGE', 'system.users', '/system/users', 30, 'ENABLE'),
  (0, '/system/roles', '角色管理', 'PAGE', 'system.roles', '/system/roles', 40, 'ENABLE'),
  (0, '/system/logs', '管理日志', 'PAGE', 'system.logs', '/system/logs', 50, 'ENABLE'),
  (0, '/customers', '客户管理', 'PAGE', 'customers', '/customers', 60, 'ENABLE'),
  (0, '/customer-risks', '风险评估', 'PAGE', 'customer-risks', '/customer-risks', 70, 'ENABLE'),
  (0, '/customer-debts', '负债登记', 'PAGE', 'customer-debts', '/customer-debts', 80, 'ENABLE'),
  (0, '/opportunities', '商机管理', 'PAGE', 'opportunities', '/opportunities', 90, 'ENABLE'),
  (0, '/loan-orders-self', '借贷管理（自营）', 'PAGE', 'loan-orders-self', '/loan-orders-self', 100, 'ENABLE'),
  (0, '/loan-orders-bank', '借贷管理（机构）', 'PAGE', 'loan-orders-bank', '/loan-orders-bank', 101, 'ENABLE'),
  (0, '/repayments', '还款明细', 'PAGE', 'repayments', '/repayments', 110, 'ENABLE'),
  (0, 'system.orgs:create', '新增组织', 'BUTTON', 'system.orgs', NULL, 210, 'ENABLE'),
  (0, 'system.orgs:update', '编辑组织', 'BUTTON', 'system.orgs', NULL, 220, 'ENABLE'),
  (0, 'system.orgs:delete', '删除组织', 'BUTTON', 'system.orgs', NULL, 230, 'ENABLE'),
  (0, 'system.orgs:sync', '同步组织', 'BUTTON', 'system.orgs', NULL, 240, 'ENABLE'),
  (0, 'system.users:create', '新增用户', 'BUTTON', 'system.users', NULL, 250, 'ENABLE'),
  (0, 'system.users:update', '编辑用户', 'BUTTON', 'system.users', NULL, 260, 'ENABLE'),
  (0, 'system.users:delete', '删除用户', 'BUTTON', 'system.users', NULL, 270, 'ENABLE'),
  (0, 'system.users:status', '启停账号', 'BUTTON', 'system.users', NULL, 280, 'ENABLE'),
  (0, 'system.users:reset-password', '重置密码', 'BUTTON', 'system.users', NULL, 290, 'ENABLE'),
  (0, 'system.users:assign-permission', '配置用户权限', 'BUTTON', 'system.users', NULL, 300, 'ENABLE'),
  (0, 'system.roles:create', '新增角色', 'BUTTON', 'system.roles', NULL, 310, 'ENABLE'),
  (0, 'system.roles:update', '编辑角色', 'BUTTON', 'system.roles', NULL, 320, 'ENABLE'),
  (0, 'system.roles:delete', '删除角色', 'BUTTON', 'system.roles', NULL, 330, 'ENABLE'),
  (0, 'system.roles:assign-permission', '配置角色权限', 'BUTTON', 'system.roles', NULL, 340, 'ENABLE'),
  (0, 'system.logs:query', '查看日志', 'BUTTON', 'system.logs', NULL, 350, 'ENABLE');

INSERT IGNORE INTO `sys_role_permission` (`role_id`, `permission_item_id`, `created_by`, `created_at`)
SELECT r.id, p.id, 1, NOW()
FROM `sys_role` r
INNER JOIN `sys_permission_item` p ON p.permission_code IN (
  '/dashboard',
  '/system/orgs',
  '/system/users',
  '/system/roles',
  '/system/logs',
  '/customers',
  '/customer-risks',
  '/customer-debts',
  '/opportunities',
  '/loan-orders-self',
  '/loan-orders-bank',
  '/repayments',
  'system.orgs:create',
  'system.orgs:update',
  'system.orgs:delete',
  'system.orgs:sync',
  'system.users:create',
  'system.users:update',
  'system.users:delete',
  'system.users:status',
  'system.users:reset-password',
  'system.users:assign-permission',
  'system.roles:create',
  'system.roles:update',
  'system.roles:delete',
  'system.roles:assign-permission',
  'system.logs:query'
)
WHERE r.role_code = 'ADMIN';

INSERT IGNORE INTO `sys_role_permission` (`role_id`, `permission_item_id`, `created_by`, `created_at`)
SELECT r.id, p.id, 1, NOW()
FROM `sys_role` r
INNER JOIN `sys_permission_item` p ON p.permission_code IN (
  '/system/users',
  'system.users:create',
  'system.users:update',
  'system.users:delete',
  'system.users:status',
  'system.users:reset-password',
  'system.users:assign-permission'
)
WHERE r.role_code = 'DEPT_ADMIN';

INSERT IGNORE INTO `sys_role_data_scope` (`role_id`, `module_code`, `scope_type`, `created_by`, `created_at`)
SELECT r.id, t.module_code, t.scope_type, 1, NOW()
FROM `sys_role` r
INNER JOIN (
  SELECT 'customers' AS module_code, 'ALL' AS scope_type UNION ALL
  SELECT 'customer-risks', 'ALL' UNION ALL
  SELECT 'customer-debts', 'ALL' UNION ALL
  SELECT 'opportunities', 'ALL' UNION ALL
  SELECT 'loan-orders-self', 'ALL' UNION ALL
  SELECT 'loan-orders-bank', 'ALL' UNION ALL
  SELECT 'repayments', 'ALL'
) t ON 1 = 1
WHERE r.role_code = 'ADMIN';

INSERT IGNORE INTO `sys_role_data_scope` (`role_id`, `module_code`, `scope_type`, `created_by`, `created_at`)
SELECT r.id, t.module_code, t.scope_type, 1, NOW()
FROM `sys_role` r
INNER JOIN (
  SELECT 'customers' AS module_code, 'ORG' AS scope_type UNION ALL
  SELECT 'customer-risks', 'ORG' UNION ALL
  SELECT 'customer-debts', 'ORG' UNION ALL
  SELECT 'opportunities', 'ORG' UNION ALL
  SELECT 'loan-orders-self', 'ORG' UNION ALL
  SELECT 'loan-orders-bank', 'ORG' UNION ALL
  SELECT 'repayments', 'ORG'
) t ON 1 = 1
WHERE r.role_code = 'DEPT_ADMIN';

INSERT IGNORE INTO `sys_role_data_scope` (`role_id`, `module_code`, `scope_type`, `created_by`, `created_at`)
SELECT r.id, t.module_code, t.scope_type, 1, NOW()
FROM `sys_role` r
INNER JOIN (
  SELECT 'customers' AS module_code, 'SELF' AS scope_type UNION ALL
  SELECT 'customer-risks', 'SELF' UNION ALL
  SELECT 'customer-debts', 'SELF' UNION ALL
  SELECT 'opportunities', 'SELF' UNION ALL
  SELECT 'loan-orders-self', 'SELF' UNION ALL
  SELECT 'loan-orders-bank', 'SELF' UNION ALL
  SELECT 'repayments', 'SELF'
) t ON 1 = 1
WHERE r.role_code = 'STAFF';

SET FOREIGN_KEY_CHECKS = 1;
