-- V007_hr_module.sql
-- HR Module Database Schema

SET NAMES utf8mb4;

-- ============================================
-- 1. Employee Main Table (员工主档案)
-- ============================================
CREATE TABLE IF NOT EXISTS `hr_employee` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `employee_code` VARCHAR(32) NOT NULL COMMENT '员工编号',
  `real_name` VARCHAR(64) NOT NULL COMMENT '姓名',
  `gender` VARCHAR(8) DEFAULT NULL COMMENT '性别',
  `id_card_no` VARCHAR(18) DEFAULT NULL COMMENT '身份证号',
  `birthday` DATE DEFAULT NULL COMMENT '出生日期',
  `age` INT DEFAULT NULL COMMENT '年龄',
  `nation` VARCHAR(32) DEFAULT NULL COMMENT '民族',
  `political_status` VARCHAR(32) DEFAULT NULL COMMENT '政治面貌',
  `hometown` VARCHAR(128) DEFAULT NULL COMMENT '籍贯',
  `marital_status` VARCHAR(16) DEFAULT NULL COMMENT '婚姻状况',
  `phone` VARCHAR(32) DEFAULT NULL COMMENT '手机号',
  `email` VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
  `graduate_school` VARCHAR(128) DEFAULT NULL COMMENT '毕业学校',
  `highest_education` VARCHAR(32) DEFAULT NULL COMMENT '最高学历',
  `work_start_date` DATE DEFAULT NULL COMMENT '参加工作时间',
  `home_address` VARCHAR(256) DEFAULT NULL COMMENT '家庭住址',
  `emergency_contact` VARCHAR(64) DEFAULT NULL COMMENT '紧急联系人',
  `emergency_contact_phone` VARCHAR(32) DEFAULT NULL COMMENT '紧急联系人电话',
  `bank_card_no` VARCHAR(32) DEFAULT NULL COMMENT '银行卡号',
  `id_photo_url` VARCHAR(512) DEFAULT NULL COMMENT '证件照片URL',
  `employment_status` VARCHAR(16) NOT NULL DEFAULT 'ONBOARD' COMMENT '人员状态 ONBOARD-在职 OFFBOARD-离职 RETIRED-退休',
  `talent_flag` VARCHAR(64) DEFAULT NULL COMMENT '人才标识',
  `create_system_account` TINYINT NOT NULL DEFAULT 0 COMMENT '是否开通系统账号 0-否 1-是',
  `system_username` VARCHAR(64) DEFAULT NULL COMMENT '系统账号用户名',
  `system_password_plain` VARCHAR(128) DEFAULT NULL COMMENT '系统密码(明文,创建后加密)',
  `org_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '所属组织ID',
  `job_title` VARCHAR(64) DEFAULT NULL COMMENT '职位',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_hr_employee_code` (`employee_code`),
  KEY `idx_hr_employee_org` (`org_id`),
  KEY `idx_hr_employee_status` (`employment_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工主档案表';

-- ============================================
-- 2. Employee Job Info Table (任职信息)
-- ============================================
CREATE TABLE IF NOT EXISTS `hr_employee_job_info` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `employee_id` BIGINT UNSIGNED NOT NULL COMMENT '员工ID',
  `employee_code` VARCHAR(32) NOT NULL COMMENT '员工编号',
  `join_date` DATE DEFAULT NULL COMMENT '入职日期',
  `formal_date` DATE DEFAULT NULL COMMENT '转正日期',
  `work_unit` VARCHAR(128) DEFAULT NULL COMMENT '所属单位',
  `work_mode` VARCHAR(16) DEFAULT NULL COMMENT '工作方式 BORROW-借调 DISPATCH-外派',
  `borrow_dispatch_date` DATE DEFAULT NULL COMMENT '借调/外派日期',
  `department` VARCHAR(128) DEFAULT NULL COMMENT '部门',
  `rank_level` VARCHAR(32) DEFAULT NULL COMMENT '职级',
  `job_category` VARCHAR(32) DEFAULT NULL COMMENT '任职类别',
  `position` VARCHAR(64) DEFAULT NULL COMMENT '职位',
  `sort_no` INT DEFAULT NULL COMMENT '排序编号',
  `is编制` TINYINT NOT NULL DEFAULT 0 COMMENT '是否编制内 0-否 1-是',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
  PRIMARY KEY (`id`),
  KEY `idx_hr_job_employee` (`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任职信息表';

-- ============================================
-- 3. Employee Removal Table (调离信息)
-- ============================================
CREATE TABLE IF NOT EXISTS `hr_employee_removal` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `employee_id` BIGINT UNSIGNED NOT NULL COMMENT '员工ID',
  `removal_type` VARCHAR(16) NOT NULL COMMENT '调离类型 RESIGN-辞职 DISMISS-辞退 RETIRE-退休',
  `removal_date` DATE DEFAULT NULL COMMENT '辞职/辞退/退休时间',
  `expected_retire_date` DATE DEFAULT NULL COMMENT '预计退休时间',
  `reason` VARCHAR(500) DEFAULT NULL COMMENT '原因',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
  PRIMARY KEY (`id`),
  KEY `idx_hr_removal_employee` (`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='调离信息表';

-- ============================================
-- 4. Employee Certificate Table (资格证书)
-- ============================================
CREATE TABLE IF NOT EXISTS `hr_employee_certificate` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `employee_id` BIGINT UNSIGNED NOT NULL COMMENT '员工ID',
  `certificate_name` VARCHAR(128) NOT NULL COMMENT '证书名称',
  `certificate_no` VARCHAR(64) DEFAULT NULL COMMENT '证书号码',
  `issue_date` DATE DEFAULT NULL COMMENT '发证日期',
  `certificate_type` VARCHAR(64) DEFAULT NULL COMMENT '证书类型',
  `issue_org` VARCHAR(128) DEFAULT NULL COMMENT '发证单位',
  `is_permanent` TINYINT NOT NULL DEFAULT 0 COMMENT '是否永久 0-否 1-是',
  `expire_date` DATE DEFAULT NULL COMMENT '到期日期',
  `certificate_file_url` VARCHAR(512) DEFAULT NULL COMMENT '证书文件URL',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
  PRIMARY KEY (`id`),
  KEY `idx_hr_cert_employee` (`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资格证书表';

-- ============================================
-- 5. Employee Assessment Table (考核记录)
-- ============================================
CREATE TABLE IF NOT EXISTS `hr_employee_assessment` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `employee_id` BIGINT UNSIGNED NOT NULL COMMENT '员工ID',
  `assessment_month` DATE DEFAULT NULL COMMENT '考核年月',
  `assessment_score` DECIMAL(5,2) DEFAULT NULL COMMENT '考核分数',
  `assessment_grade` VARCHAR(32) DEFAULT NULL COMMENT '考核等级',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
  PRIMARY KEY (`id`),
  KEY `idx_hr_assess_employee` (`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考核记录表';

-- ============================================
-- 6. Employee Growth Record Table (工作成长记录)
-- ============================================
CREATE TABLE IF NOT EXISTS `hr_employee_growth` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `employee_id` BIGINT UNSIGNED NOT NULL COMMENT '员工ID',
  `start_date` DATE DEFAULT NULL COMMENT '开始时间',
  `end_date` DATE DEFAULT NULL COMMENT '结束时间',
  `work_name` VARCHAR(256) DEFAULT NULL COMMENT '工作/学习名称',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
  PRIMARY KEY (`id`),
  KEY `idx_hr_growth_employee` (`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工作成长记录表';

-- ============================================
-- 7. Employee Family Table (家庭成员)
-- ============================================
CREATE TABLE IF NOT EXISTS `hr_employee_family` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `employee_id` BIGINT UNSIGNED NOT NULL COMMENT '员工ID',
  `relation` VARCHAR(32) DEFAULT NULL COMMENT '称谓',
  `name` VARCHAR(64) DEFAULT NULL COMMENT '姓名',
  `birthday` DATE DEFAULT NULL COMMENT '出生年月',
  `id_card_no` VARCHAR(18) DEFAULT NULL COMMENT '身份证号',
  `political_status` VARCHAR(32) DEFAULT NULL COMMENT '政治面貌',
  `work_unit_position` VARCHAR(256) DEFAULT NULL COMMENT '工作单位及职务',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
  PRIMARY KEY (`id`),
  KEY `idx_hr_family_employee` (`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='家庭成员表';

-- ============================================
-- 8. Employee Change Record Table (变动信息)
-- ============================================
CREATE TABLE IF NOT EXISTS `hr_employee_change` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `employee_id` BIGINT UNSIGNED NOT NULL COMMENT '员工ID',
  `current_department` VARCHAR(128) DEFAULT NULL COMMENT '现任部门',
  `current_position` VARCHAR(64) DEFAULT NULL COMMENT '现任职务',
  `current_rank_level` VARCHAR(32) DEFAULT NULL COMMENT '现任职级',
  `original_department` VARCHAR(128) DEFAULT NULL COMMENT '原部门',
  `original_position` VARCHAR(64) DEFAULT NULL COMMENT '原职务',
  `original_rank_level` VARCHAR(32) DEFAULT NULL COMMENT '原职级',
  `report_date` DATE DEFAULT NULL COMMENT '报到日期',
  `change_type` VARCHAR(32) DEFAULT NULL COMMENT '变动性质',
  `change_reason` VARCHAR(500) DEFAULT NULL COMMENT '变动原因',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
  PRIMARY KEY (`id`),
  KEY `idx_hr_change_employee` (`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='变动信息表';

-- ============================================
-- 9. Employee Contract Table (人事合同)
-- ============================================
CREATE TABLE IF NOT EXISTS `hr_employee_contract` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `employee_id` BIGINT UNSIGNED NOT NULL COMMENT '员工ID',
  `contract_name` VARCHAR(128) NOT NULL COMMENT '合同名称',
  `contract_no` VARCHAR(64) DEFAULT NULL COMMENT '合同编号',
  `contract_start_date` DATE DEFAULT NULL COMMENT '合同开始日期',
  `contract_end_date` DATE DEFAULT NULL COMMENT '合同结束日期',
  `contract_file_url` VARCHAR(512) DEFAULT NULL COMMENT '合同附件URL',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
  PRIMARY KEY (`id`),
  KEY `idx_hr_contract_employee` (`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人事合同表';

-- ============================================
-- 10. Salary Standard Table (工资标准)
-- ============================================
CREATE TABLE IF NOT EXISTS `hr_salary_standard` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `salary_name` VARCHAR(128) NOT NULL COMMENT '工资名称',
  `amount` DECIMAL(12,2) NOT NULL COMMENT '工资金额',
  `job_title` VARCHAR(64) DEFAULT NULL COMMENT '所属岗位',
  `org_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '所属部门ID',
  `org_name` VARCHAR(128) DEFAULT NULL COMMENT '所属部门名称',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '工资描述',
  `sort_no` INT NOT NULL DEFAULT 0 COMMENT '排序',
  `status` VARCHAR(16) NOT NULL DEFAULT 'ENABLE' COMMENT '状态 ENABLE-有效 DISABLE-无效',
  `effective_date` DATE DEFAULT NULL COMMENT '生效日期',
  `expire_date` DATE DEFAULT NULL COMMENT '失效日期',
  `version_no` VARCHAR(32) DEFAULT NULL COMMENT '版本号',
  `applicable_scope` VARCHAR(256) DEFAULT NULL COMMENT '适用人员范围',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
  PRIMARY KEY (`id`),
  KEY `idx_hr_salary_org` (`org_id`),
  KEY `idx_hr_salary_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工资标准表';

-- ============================================
-- 11. Employee Salary Info Table (员工工资信息)
-- ============================================
CREATE TABLE IF NOT EXISTS `hr_employee_salary` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `employee_id` BIGINT UNSIGNED NOT NULL COMMENT '员工ID',
  `salary_standard_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '工资标准ID',
  `salary_name` VARCHAR(128) DEFAULT NULL COMMENT '工资名称',
  `amount` DECIMAL(12,2) NOT NULL COMMENT '工资金额',
  `effective_date` DATE DEFAULT NULL COMMENT '生效日期',
  `expire_date` DATE DEFAULT NULL COMMENT '失效日期',
  `change_reason` VARCHAR(256) DEFAULT NULL COMMENT '变动原因',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
  PRIMARY KEY (`id`),
  KEY `idx_hr_emp_salary_employee` (`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工工资信息表';

-- ============================================
-- 12. Transition Apply Table (转正申请表)
-- ============================================
CREATE TABLE IF NOT EXISTS `hr_transition_apply` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `apply_no` VARCHAR(32) NOT NULL COMMENT '申请单编号',
  `employee_id` BIGINT UNSIGNED NOT NULL COMMENT '转正员工ID',
  `employee_code` VARCHAR(32) DEFAULT NULL COMMENT '员工编号',
  `employee_name` VARCHAR(64) DEFAULT NULL COMMENT '员工姓名',
  `phone` VARCHAR(32) DEFAULT NULL COMMENT '联系电话',
  `department` VARCHAR(128) DEFAULT NULL COMMENT '申请部门',
  `org_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '部门组织ID',
  `position` VARCHAR(64) DEFAULT NULL COMMENT '职位',
  `join_date` DATE DEFAULT NULL COMMENT '入职日期',
  `expected_formal_date` DATE DEFAULT NULL COMMENT '预计转正日期',
  `apply_reason` TEXT DEFAULT NULL COMMENT '申请事由',
  `self_evaluation` TEXT DEFAULT NULL COMMENT '自我评价',
  `growth` TEXT DEFAULT NULL COMMENT '成长',
  `shortcomings` TEXT DEFAULT NULL COMMENT '不足',
  `development_suggestion` TEXT DEFAULT NULL COMMENT '发展建议',
  `draft_opinion` TEXT DEFAULT NULL COMMENT '拟稿意见',
  `hr_opinion` TEXT DEFAULT NULL COMMENT '人事审批意见',
  `company_opinion` TEXT DEFAULT NULL COMMENT '公司审批意见',
  `admin_opinion` TEXT DEFAULT NULL COMMENT '行政审批意见',
  `process_status` VARCHAR(32) NOT NULL DEFAULT 'DRAFT' COMMENT '流程状态 DRAFT-草稿 PENDING_DEPT-待部门审核 PENDING_HR-待人事审核 PENDING_COMPANY-待公司审批 PENDING_ADMIN-待行政审批 APPROVED-已通过 REJECTED-已驳回 CANCELLED-已取消',
  `current_node` VARCHAR(64) DEFAULT NULL COMMENT '当前节点',
  `applicant_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '申请人ID',
  `apply_time` DATETIME DEFAULT NULL COMMENT '申请时间',
  `submit_time` DATETIME DEFAULT NULL COMMENT '提交时间',
  `complete_time` DATETIME DEFAULT NULL COMMENT '办结时间',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_hr_transition_apply_no` (`apply_no`),
  KEY `idx_hr_transition_employee` (`employee_id`),
  KEY `idx_hr_transition_status` (`process_status`),
  KEY `idx_hr_transition_applicant` (`applicant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='转正申请表';

-- ============================================
-- 13. Salary Adjust Apply Table (调薪申请表)
-- ============================================
CREATE TABLE IF NOT EXISTS `hr_salary_adjust_apply` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `apply_no` VARCHAR(32) NOT NULL COMMENT '申请单编号',
  `employee_id` BIGINT UNSIGNED NOT NULL COMMENT '调薪员工ID',
  `employee_name` VARCHAR(64) DEFAULT NULL COMMENT '员工姓名',
  `department` VARCHAR(128) DEFAULT NULL COMMENT '申请部门',
  `org_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '部门组织ID',
  `current_salary` DECIMAL(12,2) NOT NULL COMMENT '当前薪资',
  `adjust_amount` DECIMAL(12,2) NOT NULL COMMENT '申请调薪幅度',
  `new_salary` DECIMAL(12,2) NOT NULL COMMENT '调薪后薪资',
  `apply_reason` TEXT DEFAULT NULL COMMENT '申请事由',
  `dept_opinion` TEXT DEFAULT NULL COMMENT '部门审核意见',
  `hr_opinion` TEXT DEFAULT NULL COMMENT '人事审核意见',
  `leader_opinion` TEXT DEFAULT NULL COMMENT '分管领导意见',
  `school_leader_opinion` TEXT DEFAULT NULL COMMENT '校领导意见',
  `finance_opinion` TEXT DEFAULT NULL COMMENT '财务办理意见',
  `draft_opinion` TEXT DEFAULT NULL COMMENT '拟稿意见',
  `process_status` VARCHAR(32) NOT NULL DEFAULT 'DRAFT' COMMENT '流程状态 DRAFT-草稿 PENDING_DEPT-待部门审核 PENDING_HR-待人事审核 PENDING_LEADER-待分管领导 PENDING_SCHOOL-待校领导 PENDING_FINANCE-待财务办理 APPROVED-已通过 REJECTED-已驳回 CANCELLED-已取消',
  `current_node` VARCHAR(64) DEFAULT NULL COMMENT '当前节点',
  `applicant_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '申请人ID',
  `apply_time` DATETIME DEFAULT NULL COMMENT '申请时间',
  `submit_time` DATETIME DEFAULT NULL COMMENT '提交时间',
  `effective_date` DATE DEFAULT NULL COMMENT '生效日期',
  `complete_time` DATETIME DEFAULT NULL COMMENT '办结时间',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_hr_salary_adjust_apply_no` (`apply_no`),
  KEY `idx_hr_salary_adjust_employee` (`employee_id`),
  KEY `idx_hr_salary_adjust_status` (`process_status`),
  KEY `idx_hr_salary_adjust_applicant` (`applicant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='调薪申请表';

-- ============================================
-- 14. Approval Node Record Table (审批节点记录)
-- ============================================
CREATE TABLE IF NOT EXISTS `hr_approval_record` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `biz_type` VARCHAR(32) NOT NULL COMMENT '业务类型 TRANSITION-转正 SALARY_ADJUST-调薪',
  `biz_id` BIGINT UNSIGNED NOT NULL COMMENT '业务ID',
  `node_name` VARCHAR(64) NOT NULL COMMENT '节点名称',
  `node_key` VARCHAR(32) NOT NULL COMMENT '节点键',
  `operator_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '处理人ID',
  `operator_name` VARCHAR(64) DEFAULT NULL COMMENT '处理人姓名',
  `operate_time` DATETIME DEFAULT NULL COMMENT '处理时间',
  `opinion` TEXT DEFAULT NULL COMMENT '审批意见',
  `result` VARCHAR(16) DEFAULT NULL COMMENT '处理结果 AGREE-同意 REJECT-驳回',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
  PRIMARY KEY (`id`),
  KEY `idx_hr_approval_biz` (`biz_type`, `biz_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批节点记录表';

-- ============================================
-- 15. HR Attachment Relation Table (附件关系表)
-- ============================================
CREATE TABLE IF NOT EXISTS `hr_attachment` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `biz_type` VARCHAR(32) NOT NULL COMMENT '业务类型 TRANSITION-转正 SALARY_ADJUST-调薪 EMPLOYEE-员工档案',
  `biz_id` BIGINT UNSIGNED NOT NULL COMMENT '业务ID',
  `file_name` VARCHAR(256) NOT NULL COMMENT '文件名',
  `file_size` BIGINT DEFAULT NULL COMMENT '文件大小(字节)',
  `file_url` VARCHAR(512) NOT NULL COMMENT '文件URL',
  `file_type` VARCHAR(32) DEFAULT NULL COMMENT '文件类型',
  `uploader_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '上传人ID',
  `uploader_name` VARCHAR(64) DEFAULT NULL COMMENT '上传人姓名',
  `upload_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
  PRIMARY KEY (`id`),
  KEY `idx_hr_attachment_biz` (`biz_type`, `biz_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='HR附件表';

-- ============================================
-- 16. Management Record Table (管理留痕表)
-- ============================================
CREATE TABLE IF NOT EXISTS `hr_management_record` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `employee_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '关联员工ID',
  `employee_name` VARCHAR(64) DEFAULT NULL COMMENT '员工姓名',
  `record_type` VARCHAR(32) NOT NULL COMMENT '记录类型 TRANSITION-转正 SALARY_ADJUST-调薪 PROBATION-试用期 EMPLOYEE_CREATE-员工创建 EMPLOYEE_UPDATE-员工变更',
  `content` TEXT DEFAULT NULL COMMENT '变动内容',
  `operator_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '操作人ID',
  `operator_name` VARCHAR(64) DEFAULT NULL COMMENT '操作人姓名',
  `operated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
  PRIMARY KEY (`id`),
  KEY `idx_hr_record_employee` (`employee_id`),
  KEY `idx_hr_record_type` (`record_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理留痕表';

-- ============================================
-- 17. Leave Record Table (请假记录 - 基础表)
-- ============================================
CREATE TABLE IF NOT EXISTS `hr_leave_record` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `employee_id` BIGINT UNSIGNED NOT NULL COMMENT '员工ID',
  `leave_type` VARCHAR(32) NOT NULL COMMENT '请假类型 ANNUAL-年假 SICK-病假 PERSONAL-事假 MARRIAGE-婚假 MATERNITY-产假 FUNERAL-丧假',
  `start_date` DATE NOT NULL COMMENT '开始日期',
  `end_date` DATE NOT NULL COMMENT '结束日期',
  `days` DECIMAL(5,1) DEFAULT NULL COMMENT '天数',
  `reason` VARCHAR(500) DEFAULT NULL COMMENT '请假原因',
  `status` VARCHAR(16) NOT NULL DEFAULT 'PENDING' COMMENT '状态 PENDING-待审批 APPROVED-已批准 REJECTED-已驳回 CANCELLED-已取消',
  `applicant_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '申请人ID',
  `applicant_name` VARCHAR(64) DEFAULT NULL COMMENT '申请人姓名',
  `apply_time` DATETIME DEFAULT NULL COMMENT '申请时间',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
  PRIMARY KEY (`id`),
  KEY `idx_hr_leave_employee` (`employee_id`),
  KEY `idx_hr_leave_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='请假记录表';

-- ============================================
-- Insert HR Permission Items
-- ============================================
INSERT IGNORE INTO `sys_permission_item`
(`parent_id`, `permission_code`, `permission_name`, `permission_type`, `module_code`, `route_path`, `sort_no`, `status`)
VALUES
  (0, '/hr/employees', '员工档案', 'PAGE', 'hr.employees', '/hr/employees', 120, 'ENABLE'),
  (0, '/hr/my-profile', '我的档案', 'PAGE', 'hr.my-profile', '/hr/my-profile', 121, 'ENABLE'),
  (0, '/hr/salaries', '工资管理', 'PAGE', 'hr.salaries', '/hr/salaries', 122, 'ENABLE'),
  (0, '/workflow/transitions', '人事异动-转正', 'PAGE', 'workflow.transitions', '/workflow/transitions', 130, 'ENABLE'),
  (0, '/workflow/salary-adjusts', '人事异动-调薪', 'PAGE', 'workflow.salary-adjusts', '/workflow/salary-adjusts', 131, 'ENABLE'),
  (0, 'hr.employees:create', '新增员工', 'BUTTON', 'hr.employees', NULL, 410, 'ENABLE'),
  (0, 'hr.employees:update', '编辑员工', 'BUTTON', 'hr.employees', NULL, 420, 'ENABLE'),
  (0, 'hr.employees:delete', '删除员工', 'BUTTON', 'hr.employees', NULL, 430, 'ENABLE'),
  (0, 'hr.employees:import', '导入档案', 'BUTTON', 'hr.employees', NULL, 440, 'ENABLE'),
  (0, 'hr.employees:export', '导出档案', 'BUTTON', 'hr.employees', NULL, 450, 'ENABLE'),
  (0, 'hr.salaries:create', '新增工资标准', 'BUTTON', 'hr.salaries', NULL, 460, 'ENABLE'),
  (0, 'hr.salaries:update', '编辑工资标准', 'BUTTON', 'hr.salaries', NULL, 470, 'ENABLE'),
  (0, 'hr.salaries:delete', '删除工资标准', 'BUTTON', 'hr.salaries', NULL, 480, 'ENABLE'),
  (0, 'workflow.transitions:create', '发起转正申请', 'BUTTON', 'workflow.transitions', NULL, 490, 'ENABLE'),
  (0, 'workflow.transitions:approve', '审批转正申请', 'BUTTON', 'workflow.transitions', NULL, 500, 'ENABLE'),
  (0, 'workflow.salary-adjusts:create', '发起调薪申请', 'BUTTON', 'workflow.salary-adjusts', NULL, 510, 'ENABLE'),
  (0, 'workflow.salary-adjusts:approve', '审批调薪申请', 'BUTTON', 'workflow.salary-adjusts', NULL, 520, 'ENABLE');

-- Grant HR permissions to ADMIN role
INSERT IGNORE INTO `sys_role_permission` (`role_id`, `permission_item_id`, `created_by`, `created_at`)
SELECT r.id, p.id, 1, NOW()
FROM sys_role r
INNER JOIN sys_permission_item p ON p.permission_code IN (
  '/hr/employees',
  '/hr/my-profile',
  '/hr/salaries',
  '/workflow/transitions',
  '/workflow/salary-adjusts',
  'hr.employees:create',
  'hr.employees:update',
  'hr.employees:delete',
  'hr.employees:import',
  'hr.employees:export',
  'hr.salaries:create',
  'hr.salaries:update',
  'hr.salaries:delete',
  'workflow.transitions:create',
  'workflow.transitions:approve',
  'workflow.salary-adjusts:create',
  'workflow.salary-adjusts:approve'
)
WHERE r.role_code = 'ADMIN';
