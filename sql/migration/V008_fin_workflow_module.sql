-- V008_fin_workflow_module.sql
-- Financial Reimbursement and Workflow Tables

SET NAMES utf8mb4;

-- ============================================
-- 1. Reimbursement Apply Table (报销申请表)
-- ============================================
CREATE TABLE IF NOT EXISTS `fin_reimbursement_apply` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `apply_no` VARCHAR(32) NOT NULL COMMENT '申请单编号',
  `applicant_id` BIGINT UNSIGNED NOT NULL COMMENT '申请人ID',
  `applicant_name` VARCHAR(64) DEFAULT NULL COMMENT '申请人姓名',
  `applicant_org_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '申请人组织ID',
  `applicant_org_name` VARCHAR(128) DEFAULT NULL COMMENT '申请人组织名称',
  `reimbursement_user_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '报销人员ID',
  `reimbursement_user_name` VARCHAR(64) DEFAULT NULL COMMENT '报销人员姓名',
  `amount` DECIMAL(12,2) NOT NULL COMMENT '报销金额',
  `reason` TEXT DEFAULT NULL COMMENT '申请事由',
  `process_status` VARCHAR(32) NOT NULL DEFAULT 'DRAFT' COMMENT '流程状态 DRAFT-草稿 PENDING_DEPT-待部门审核 PENDING_HR-待人事审核 PENDING_LEADER-待分管领导 PENDING_FINANCE-待财务审核 APPROVED-已通过 REJECTED-已驳回 WITHDRAWN-已撤回',
  `current_node_key` VARCHAR(32) DEFAULT NULL COMMENT '当前节点键',
  `current_node_name` VARCHAR(64) DEFAULT NULL COMMENT '当前节点名称',
  `current_round` INT NOT NULL DEFAULT 1 COMMENT '当前轮次',
  `resubmit_count` INT NOT NULL DEFAULT 0 COMMENT '重提次数',
  `submit_time` DATETIME DEFAULT NULL COMMENT '提交时间',
  `complete_time` DATETIME DEFAULT NULL COMMENT '办结时间',
  `withdraw_time` DATETIME DEFAULT NULL COMMENT '撤回时间',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_fin_reimbursement_apply_no` (`apply_no`),
  KEY `idx_fin_reimb_applicant` (`applicant_id`),
  KEY `idx_fin_reimb_status` (`process_status`),
  KEY `idx_fin_reimb_created` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报销申请表';

-- ============================================
-- 2. Workflow Process Instance Table (通用流程实例表)
-- ============================================
CREATE TABLE IF NOT EXISTS `wf_process_instance` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `biz_type` VARCHAR(32) NOT NULL COMMENT '业务类型 TRANSITION-转正 SALARY_ADJUST-调薪 REIMBURSEMENT-报销',
  `biz_id` BIGINT UNSIGNED NOT NULL COMMENT '业务ID',
  `biz_no` VARCHAR(64) DEFAULT NULL COMMENT '业务单号',
  `title` VARCHAR(256) NOT NULL COMMENT '流程标题',
  `applicant_id` BIGINT UNSIGNED NOT NULL COMMENT '申请人ID',
  `applicant_name` VARCHAR(64) DEFAULT NULL COMMENT '申请人姓名',
  `applicant_org_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '申请人组织ID',
  `applicant_org_name` VARCHAR(128) DEFAULT NULL COMMENT '申请人组织名称',
  `overall_status` VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '总体状态 PENDING-审批中 APPROVED-已通过 REJECTED-已驳回 WITHDRAWN-已撤回',
  `current_node_key` VARCHAR(32) DEFAULT NULL COMMENT '当前节点键',
  `current_node_name` VARCHAR(64) DEFAULT NULL COMMENT '当前节点名称',
  `current_round` INT NOT NULL DEFAULT 1 COMMENT '当前轮次',
  `started_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '流程开始时间',
  `completed_at` DATETIME DEFAULT NULL COMMENT '流程完成时间',
  `last_action_at` DATETIME DEFAULT NULL COMMENT '最后操作时间',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
  PRIMARY KEY (`id`),
  KEY `idx_wf_process_biz` (`biz_type`, `biz_id`),
  KEY `idx_wf_process_applicant` (`applicant_id`),
  KEY `idx_wf_process_status` (`overall_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通用流程实例表';

-- ============================================
-- 3. Workflow Task Table (通用待办任务表)
-- ============================================
CREATE TABLE IF NOT EXISTS `wf_task` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `process_id` BIGINT UNSIGNED NOT NULL COMMENT '流程实例ID',
  `biz_type` VARCHAR(32) NOT NULL COMMENT '业务类型',
  `biz_id` BIGINT UNSIGNED NOT NULL COMMENT '业务ID',
  `round_no` INT NOT NULL DEFAULT 1 COMMENT '轮次',
  `node_key` VARCHAR(32) NOT NULL COMMENT '节点键',
  `node_name` VARCHAR(64) NOT NULL COMMENT '节点名称',
  `task_status` VARCHAR(16) NOT NULL DEFAULT 'PENDING' COMMENT '任务状态 PENDING-待处理 DONE-已处理 WITHDRAWN-已撤回',
  `assignee_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '指定审批人ID',
  `assignee_name` VARCHAR(64) DEFAULT NULL COMMENT '指定审批人姓名',
  `candidate_role_code` VARCHAR(32) DEFAULT NULL COMMENT '候选角色编码',
  `action_result` VARCHAR(16) DEFAULT NULL COMMENT '处理结果 AGREE-同意 REJECT-驳回 WITHDRAW-撤回',
  `action_opinion` TEXT DEFAULT NULL COMMENT '处理意见',
  `action_time` DATETIME DEFAULT NULL COMMENT '处理时间',
  `sort_no` INT NOT NULL DEFAULT 0 COMMENT '排序号',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
  PRIMARY KEY (`id`),
  KEY `idx_wf_task_process` (`process_id`),
  KEY `idx_wf_task_biz` (`biz_type`, `biz_id`),
  KEY `idx_wf_task_assignee` (`assignee_id`),
  KEY `idx_wf_task_status` (`task_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通用待办任务表';

-- ============================================
-- 4. Workflow Action Log Table (通用动作日志表)
-- ============================================
CREATE TABLE IF NOT EXISTS `wf_action_log` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `process_id` BIGINT UNSIGNED NOT NULL COMMENT '流程实例ID',
  `biz_type` VARCHAR(32) NOT NULL COMMENT '业务类型',
  `biz_id` BIGINT UNSIGNED NOT NULL COMMENT '业务ID',
  `round_no` INT NOT NULL DEFAULT 1 COMMENT '轮次',
  `action_code` VARCHAR(32) NOT NULL COMMENT '动作编码 SUBMIT-提交 APPROVE-同意 REJECT-驳回 WITHDRAW-撤回 RESUBMIT-重提',
  `node_key` VARCHAR(32) DEFAULT NULL COMMENT '节点键',
  `node_name` VARCHAR(64) DEFAULT NULL COMMENT '节点名称',
  `operator_id` BIGINT UNSIGNED NOT NULL COMMENT '操作人ID',
  `operator_name` VARCHAR(64) DEFAULT NULL COMMENT '操作人姓名',
  `action_opinion` TEXT DEFAULT NULL COMMENT '操作意见',
  `action_result` VARCHAR(16) DEFAULT NULL COMMENT '操作结果',
  `action_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `extra_json` TEXT DEFAULT NULL COMMENT '扩展信息JSON',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
  PRIMARY KEY (`id`),
  KEY `idx_wf_action_process` (`process_id`),
  KEY `idx_wf_action_biz` (`biz_type`, `biz_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通用动作日志表';

-- ============================================
-- 5. Insert Financial and Workflow Permission Items
-- ============================================
INSERT IGNORE INTO `sys_permission_item`
(`parent_id`, `permission_code`, `permission_name`, `permission_type`, `module_code`, `route_path`, `sort_no`, `status`)
VALUES
  (0, '/finance/reimbursements', '报销管理', 'PAGE', 'finance.reimbursements', '/finance/reimbursements', 150, 'ENABLE'),
  (0, '/workflow/my-approvals', '我的审批', 'PAGE', 'workflow.my-approvals', '/workflow/my-approvals', 140, 'ENABLE'),
  (0, 'finance.reimbursements:create', '发起报销申请', 'BUTTON', 'finance.reimbursements', NULL, 530, 'ENABLE'),
  (0, 'finance.reimbursements:approve', '审批报销申请', 'BUTTON', 'finance.reimbursements', NULL, 540, 'ENABLE'),
  (0, 'finance.reimbursements:view-all', '查看全部报销', 'BUTTON', 'finance.reimbursements', NULL, 541, 'ENABLE'),
  (0, 'workflow.my-approvals:approve', '执行审批操作', 'BUTTON', 'workflow.my-approvals', NULL, 550, 'ENABLE');

-- Grant Financial and Workflow permissions to ADMIN role
INSERT IGNORE INTO `sys_role_permission` (`role_id`, `permission_item_id`, `created_by`, `created_at`)
SELECT r.id, p.id, 1, NOW()
FROM sys_role r
INNER JOIN sys_permission_item p ON p.permission_code IN (
  '/finance/reimbursements',
  '/workflow/my-approvals',
  'finance.reimbursements:create',
  'finance.reimbursements:approve',
  'finance.reimbursements:view-all',
  'workflow.my-approvals:approve'
)
WHERE r.role_code = 'ADMIN';
