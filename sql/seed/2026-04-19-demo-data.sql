-- =============================================================================
-- 华彩系统演示数据初始化脚本
-- 用途：为系统管理、客户管理、人事管理、报销审批链路生成可联调的测试数据
-- 执行方式：docker exec huacai-mysql mysql -uroot -proot huacai_system < sql/seed/2026-04-19-demo-data.sql
-- 可重复执行：YES (使用 INSERT IGNORE 和条件判断保证幂等性)
-- 注意事项：不删除现有数据，不覆盖 admin 账号，不破坏 DEPT_ADMIN.role_name='111'
-- =============================================================================

USE huacai_system;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =============================================================================
-- 一、系统管理
-- =============================================================================

-- 1.1 组织机构 (INSERT IGNORE 保证幂等)
-- 总部 = id=1 (如果已存在则跳过)
INSERT IGNORE INTO `sys_org` (`id`, `parent_id`, `org_name`, `org_type`, `sort_no`, `status`, `created_by`, `updated_by`)
VALUES
  (1, 0, '华彩集团', 'COMPANY', 1, 'ENABLE', 1, 1),
  (10, 1, '市场部', 'DEPT', 10, 'ENABLE', 1, 1),
  (11, 1, '销售部', 'DEPT', 11, 'ENABLE', 1, 1),
  (12, 1, '财务部', 'DEPT', 12, 'ENABLE', 1, 1),
  (13, 1, '人事部', 'DEPT', 13, 'ENABLE', 1, 1);

-- 1.2 系统用户 (不覆盖已存在的 admin)
-- 使用 bcrypt 加密占位符 (有效密码 "Demo!2345")
-- 检查是否已存在同名用户，避免重复插入
INSERT IGNORE INTO `sys_user` (`username`, `password_hash`, `employee_code`, `real_name`, `phone`, `email`, `org_id`, `identity_type`, `job_title`, `employment_status`, `account_status`, `created_by`)
VALUES
  -- 部门管理员 - 市场部
  ('dept_admin_market', '$2a$10$XslJghP8dE9x5QxQ4kK.sOFXQRm1r3xGXr5cYJGhLmN8xqX5m7KWG', 'EMP100', '市场管理员', '13900001001', 'market@huacai.com', 10, 'DEPT_ADMIN', '市场总监', 'ON_JOB', 'ENABLE', 1),
  -- 部门管理员 - 销售部
  ('dept_admin_sales', '$2a$10$XslJghP8dE9x5QxQ4kK.sOFXQRm1r3xGXr5cYJGhLmN8xqX5m7KWG', 'EMP101', '销售管理员', '13900001002', 'sales@huacai.com', 11, 'DEPT_ADMIN', '销售总监', 'ON_JOB', 'ENABLE', 1),
  -- 普通用户 - 市场部
  ('staff_market_01', '$2a$10$XslJghP8dE9x5QxQ4kK.sOFXQRm1r3xGXr5cYJGhLmN8xqX5m7KWG', 'EMP102', '市场专员张三', '13900001003', 'zhangsan@huacai.com', 10, 'NORMAL_USER', '市场专员', 'ON_JOB', 'ENABLE', 1),
  -- 普通用户 - 销售部
  ('staff_sales_01', '$2a$10$XslJghP8dE9x5QxQ4kK.sOFXQRm1r3xGXr5cYJGhLmN8xqX5m7KWG', 'EMP103', '销售专员李四', '13900001004', 'lisi@huacai.com', 11, 'NORMAL_USER', '销售专员', 'ON_JOB', 'ENABLE', 1),
  -- 普通用户 - 财务部
  ('staff_finance_01', '$2a$10$XslJghP8dE9x5QxQ4kK.sOFXQRm1r3xGXr5cYJGhLmN8xqX5m7KWG', 'EMP104', '财务专员王五', '13900001005', 'wangwu@huacai.com', 12, 'NORMAL_USER', '财务专员', 'ON_JOB', 'ENABLE', 1),
  -- 普通用户 - 人事部
  ('staff_hr_01', '$2a$10$XslJghP8dE9x5QxQ4kK.sOFXQRm1r3xGXr5cYJGhLmN8xqX5m7KWG', 'EMP105', '人事专员赵六', '13900001006', 'zhaoliu@huacai.com', 13, 'NORMAL_USER', '人事专员', 'ON_JOB', 'ENABLE', 1);

-- 1.3 用户角色关联 (直接INSERT每用户的单角色，不使用 CROSS JOIN 批量赋双角色)
-- DEPT_ADMIN 用户只能有 DEPT_ADMIN 角色，STAFF 用户只能有 STAFF 角色
INSERT IGNORE INTO `sys_user_role` (`user_id`, `role_id`, `created_by`, `created_at`)
SELECT u.id, r.id, 1, NOW()
FROM `sys_user` u
INNER JOIN `sys_role` r ON r.role_code = 'DEPT_ADMIN'
WHERE u.username IN ('dept_admin_market', 'dept_admin_sales')
  AND NOT EXISTS (
    SELECT 1 FROM `sys_user_role` ur WHERE ur.user_id = u.id AND ur.role_id = r.id
  );

INSERT IGNORE INTO `sys_user_role` (`user_id`, `role_id`, `created_by`, `created_at`)
SELECT u.id, r.id, 1, NOW()
FROM `sys_user` u
INNER JOIN `sys_role` r ON r.role_code = 'STAFF'
WHERE u.username IN ('staff_market_01', 'staff_sales_01', 'staff_finance_01', 'staff_hr_01')
  AND NOT EXISTS (
    SELECT 1 FROM `sys_user_role` ur WHERE ur.user_id = u.id AND ur.role_id = r.id
  );

-- =============================================================================
-- 二、客户管理
-- =============================================================================

-- 2.1 客户主数据 (8~12 个，覆盖不同审核状态和业务状态)
INSERT IGNORE INTO `cust_customer` (`customer_no`, `customer_name`, `gender`, `mobile`, `company_name`, `credit_code`, `industry`, `business_address`, `recommender_name`, `recommender_rate`, `service_fee`, `audit_status`, `biz_status`, `loan_status`, `created_by`)
VALUES
  -- 待审核客户
  ('CUST_PEND_001', '待审核科技有限公司', 'M', '13800138001', '待审核科技有限公司', '91110000000000011X', '软件开发', '北京市朝阳区测试路1号', '推荐人A', 2.50, 10000.00, 'PENDING', 'INIT', 'NOT_STARTED', 1),
  ('CUST_PEND_002', '待审核贸易公司', 'F', '13800138002', '待审核贸易公司', '91110000000000012X', '贸易', '上海市浦东新区测试路2号', '推荐人B', 3.00, 15000.00, 'PENDING', 'INIT', 'NOT_STARTED', 1),
  -- 已驳回客户
  ('CUST_REJ_001', '已驳回建材公司', 'M', '13800138003', '已驳回建材公司', '91110000000000013X', '建材', '广州市天河区测试路3号', '推荐人C', 2.00, 8000.00, 'REJECTED', 'INIT', 'NOT_STARTED', 1),
  -- 已通过客户 - 未开始借款
  ('CUST_APP_001', '已通过科技公司', 'M', '13800138004', '已通过科技有限公司', '91110000000000014X', '科技', '深圳市南山区测试路4号', '推荐人D', 2.50, 12000.00, 'APPROVED', 'ACTIVE', 'NOT_STARTED', 1),
  ('CUST_APP_002', '已通过咨询公司', 'F', '13800138005', '已通过咨询有限公司', '91110000000000015X', '咨询', '杭州市西湖区测试路5号', '推荐人E', 3.00, 18000.00, 'APPROVED', 'ACTIVE', 'NOT_STARTED', 1),
  -- 已通过客户 - 开始借款
  ('CUST_LOAN_001', '贷款中制造公司', 'M', '13800138006', '贷款中制造有限公司', '91110000000000016X', '制造业', '武汉市光谷测试路6号', '推荐人F', 2.80, 20000.00, 'APPROVED', 'ACTIVE', 'IN_PROGRESS', 1),
  ('CUST_LOAN_002', '贷款中物流公司', 'F', '13800138007', '贷款中物流有限公司', '91110000000000017X', '物流', '成都市高新区测试路7号', '推荐人G', 2.50, 15000.00, 'APPROVED', 'ACTIVE', 'IN_PROGRESS', 1),
  -- 已通过客户 - 已结清
  ('CUST_SETTLE_001', '已结清电子公司', 'M', '13800138008', '已结清电子有限公司', '91110000000000018X', '电子', '西安市高新区测试路8号', '推荐人H', 3.00, 22000.00, 'APPROVED', 'ACTIVE', 'SETTLED', 1),
  -- 额外客户 - 覆盖更多场景
  ('CUST_APP_003', '已通过服务公司', 'M', '13800138009', '已通过服务有限公司', '91110000000000019X', '服务', '南京市江宁区测试路9号', '推荐人I', 2.20, 11000.00, 'APPROVED', 'ACTIVE', 'NOT_STARTED', 1),
  ('CUST_APP_004', '已通过食品公司', 'F', '13800138010', '已通过食品有限公司', '91110000000000020X', '食品', '重庆市渝北区测试路10号', '推荐人J', 2.80, 16000.00, 'APPROVED', 'ACTIVE', 'IN_PROGRESS', 1);

-- 2.2 风险评估 (至少 8 条，覆盖不同客户)
INSERT IGNORE INTO `cust_customer_score` (`customer_id`, `test_date`, `test_limit`, `traffic_value`, `composite_score`, `third_party_score`, `score_result`, `remark`, `created_by`)
SELECT c.id, DATE_SUB(CURDATE(), INTERVAL FLOOR(RAND()*30) DAY), FLOOR(50000 + RAND()*150000), FLOOR(1000 + RAND()*5000), ROUND(60 + RAND()*40, 2), ROUND(50 + RAND()*50, 2),
  CASE WHEN RAND() > 0.5 THEN 'PASS' ELSE 'FAIL' END,
  CONCAT('风险评估测试-', c.customer_name),
  1
FROM `cust_customer` c
WHERE c.customer_no IN ('CUST_APP_001', 'CUST_APP_002', 'CUST_LOAN_001', 'CUST_LOAN_002', 'CUST_SETTLE_001', 'CUST_APP_003', 'CUST_APP_004', 'CUST_PEND_001')
  AND NOT EXISTS (
    SELECT 1 FROM `cust_customer_score` cs WHERE cs.customer_id = c.id LIMIT 1
  );

-- 2.3 负债登记 (至少 8 条，覆盖不同客户)
INSERT IGNORE INTO `cust_customer_debt` (`customer_id`, `debt_type`, `debt_amount`, `repaid_amount`, `pending_amount`, `installment_amount`, `repayment_day`, `remark`, `created_by`)
SELECT c.id,
  ELT(FLOOR(1 + RAND()*4), '银行贷款', '民间借贷', '信用卡', '网贷'),
  FLOOR(10000 + RAND()*500000),
  FLOOR(1000 + RAND()*100000),
  FLOOR(5000 + RAND()*400000),
  FLOOR(500 + RAND()*10000),
  FLOOR(1 + RAND()*28),
  CONCAT('负债登记-', c.customer_name),
  1
FROM `cust_customer` c
WHERE c.customer_no IN ('CUST_APP_001', 'CUST_APP_002', 'CUST_LOAN_001', 'CUST_LOAN_002', 'CUST_SETTLE_001', 'CUST_APP_003', 'CUST_APP_004', 'CUST_PEND_001')
  AND NOT EXISTS (
    SELECT 1 FROM `cust_customer_debt` cd WHERE cd.customer_id = c.id LIMIT 1
  );

-- 2.4 合同 (至少 4 条，关联客户)
INSERT IGNORE INTO `cust_customer_contract` (`customer_id`, `customer_name`, `company_name`, `credit_code`, `contract_no`, `contract_name`, `sign_date`, `remark`, `created_by`)
SELECT c.id, c.customer_name, c.company_name, c.credit_code,
  CONCAT('HT', DATE_FORMAT(CURDATE(), '%Y%m%d'), LPAD(c.id, 4, '0')),
  CONCAT(c.customer_name, '-服务合同'),
  DATE_SUB(CURDATE(), INTERVAL FLOOR(RAND()*60) DAY),
  CONCAT('合同编号:', CONCAT('HT', DATE_FORMAT(CURDATE(), '%Y%m%d'), LPAD(c.id, 4, '0'))),
  1
FROM `cust_customer` c
WHERE c.audit_status = 'APPROVED'
  AND c.customer_no IN ('CUST_APP_001', 'CUST_LOAN_001', 'CUST_LOAN_002', 'CUST_SETTLE_001')
  AND NOT EXISTS (
    SELECT 1 FROM `cust_customer_contract` cc WHERE cc.customer_id = c.id LIMIT 1
  );

-- 2.5 客户状态历史 (至少 3 个客户，覆盖不同状态流转)
INSERT IGNORE INTO `cust_customer_status_log` (`customer_id`, `status_code`, `status_name`, `changed_at`, `changed_by`, `remark`)
SELECT id, 'PENDING', '待审核', DATE_SUB(NOW(), INTERVAL 10 DAY), 1, '初始创建'
FROM `cust_customer` WHERE customer_no = 'CUST_PEND_001'
AND NOT EXISTS (SELECT 1 FROM `cust_customer_status_log` WHERE customer_id = (SELECT id FROM cust_customer WHERE customer_no = 'CUST_PEND_001') AND status_code = 'PENDING');

INSERT IGNORE INTO `cust_customer_status_log` (`customer_id`, `status_code`, `status_name`, `changed_at`, `changed_by`, `remark`)
SELECT id, 'PENDING', '待审核', DATE_SUB(NOW(), INTERVAL 15 DAY), 1, '初始创建'
FROM `cust_customer` WHERE customer_no = 'CUST_REJ_001'
AND NOT EXISTS (SELECT 1 FROM `cust_customer_status_log` WHERE customer_id = (SELECT id FROM cust_customer WHERE customer_no = 'CUST_REJ_001') AND status_code = 'PENDING');

INSERT IGNORE INTO `cust_customer_status_log` (`customer_id`, `status_code`, `status_name`, `changed_at`, `changed_by`, `remark`)
SELECT id, 'REJECTED', '已驳回', DATE_SUB(NOW(), INTERVAL 12 DAY), 1, '资质审核不通过'
FROM `cust_customer` WHERE customer_no = 'CUST_REJ_001'
AND NOT EXISTS (SELECT 1 FROM `cust_customer_status_log` WHERE customer_id = (SELECT id FROM cust_customer WHERE customer_no = 'CUST_REJ_001') AND status_code = 'REJECTED');

INSERT IGNORE INTO `cust_customer_status_log` (`customer_id`, `status_code`, `status_name`, `changed_at`, `changed_by`, `remark`)
SELECT id, 'PENDING', '待审核', DATE_SUB(NOW(), INTERVAL 20 DAY), 1, '初始创建'
FROM `cust_customer` WHERE customer_no = 'CUST_LOAN_001'
AND NOT EXISTS (SELECT 1 FROM `cust_customer_status_log` WHERE customer_id = (SELECT id FROM cust_customer WHERE customer_no = 'CUST_LOAN_001') AND status_code = 'PENDING');

INSERT IGNORE INTO `cust_customer_status_log` (`customer_id`, `status_code`, `status_name`, `changed_at`, `changed_by`, `remark`)
SELECT id, 'APPROVED', '已通过', DATE_SUB(NOW(), INTERVAL 18 DAY), 1, '审核通过'
FROM `cust_customer` WHERE customer_no = 'CUST_LOAN_001'
AND NOT EXISTS (SELECT 1 FROM `cust_customer_status_log` WHERE customer_id = (SELECT id FROM cust_customer WHERE customer_no = 'CUST_LOAN_001') AND status_code = 'APPROVED');

INSERT IGNORE INTO `cust_customer_status_log` (`customer_id`, `status_code`, `status_name`, `changed_at`, `changed_by`, `remark`)
SELECT id, 'NOT_STARTED', '未开始借款', DATE_SUB(NOW(), INTERVAL 16 DAY), 1, '等待借款'
FROM `cust_customer` WHERE customer_no = 'CUST_LOAN_001'
AND NOT EXISTS (SELECT 1 FROM `cust_customer_status_log` WHERE customer_id = (SELECT id FROM cust_customer WHERE customer_no = 'CUST_LOAN_001') AND status_code = 'NOT_STARTED');

INSERT IGNORE INTO `cust_customer_status_log` (`customer_id`, `status_code`, `status_name`, `changed_at`, `changed_by`, `remark`)
SELECT id, 'IN_PROGRESS', '开始借款', DATE_SUB(NOW(), INTERVAL 14 DAY), 1, '借款生效'
FROM `cust_customer` WHERE customer_no = 'CUST_LOAN_001'
AND NOT EXISTS (SELECT 1 FROM `cust_customer_status_log` WHERE customer_id = (SELECT id FROM cust_customer WHERE customer_no = 'CUST_LOAN_001') AND status_code = 'IN_PROGRESS');

INSERT IGNORE INTO `cust_customer_status_log` (`customer_id`, `status_code`, `status_name`, `changed_at`, `changed_by`, `remark`)
SELECT id, 'PENDING', '待审核', DATE_SUB(NOW(), INTERVAL 25 DAY), 1, '初始创建'
FROM `cust_customer` WHERE customer_no = 'CUST_SETTLE_001'
AND NOT EXISTS (SELECT 1 FROM `cust_customer_status_log` WHERE customer_id = (SELECT id FROM cust_customer WHERE customer_no = 'CUST_SETTLE_001') AND status_code = 'PENDING');

INSERT IGNORE INTO `cust_customer_status_log` (`customer_id`, `status_code`, `status_name`, `changed_at`, `changed_by`, `remark`)
SELECT id, 'APPROVED', '已通过', DATE_SUB(NOW(), INTERVAL 23 DAY), 1, '审核通过'
FROM `cust_customer` WHERE customer_no = 'CUST_SETTLE_001'
AND NOT EXISTS (SELECT 1 FROM `cust_customer_status_log` WHERE customer_id = (SELECT id FROM cust_customer WHERE customer_no = 'CUST_SETTLE_001') AND status_code = 'APPROVED');

INSERT IGNORE INTO `cust_customer_status_log` (`customer_id`, `status_code`, `status_name`, `changed_at`, `changed_by`, `remark`)
SELECT id, 'IN_PROGRESS', '开始借款', DATE_SUB(NOW(), INTERVAL 20 DAY), 1, '借款生效'
FROM `cust_customer` WHERE customer_no = 'CUST_SETTLE_001'
AND NOT EXISTS (SELECT 1 FROM `cust_customer_status_log` WHERE customer_id = (SELECT id FROM cust_customer WHERE customer_no = 'CUST_SETTLE_001') AND status_code = 'IN_PROGRESS');

INSERT IGNORE INTO `cust_customer_status_log` (`customer_id`, `status_code`, `status_name`, `changed_at`, `changed_by`, `remark`)
SELECT id, 'SETTLED', '已结清', DATE_SUB(NOW(), INTERVAL 5 DAY), 1, '借款结清'
FROM `cust_customer` WHERE customer_no = 'CUST_SETTLE_001'
AND NOT EXISTS (SELECT 1 FROM `cust_customer_status_log` WHERE customer_id = (SELECT id FROM cust_customer WHERE customer_no = 'CUST_SETTLE_001') AND status_code = 'SETTLED');

-- =============================================================================
-- 三、人事管理
-- =============================================================================

-- 3.1 员工档案 (6 个，覆盖不同部门、不同岗位、在职/离职、开通/不开通系统账号)
INSERT IGNORE INTO `hr_employee` (`employee_code`, `real_name`, `gender`, `phone`, `email`, `org_id`, `job_title`, `employment_status`, `create_system_account`, `system_username`, `work_start_date`, `created_by`)
VALUES
  -- 在职员工 - 市场部 - 已开通系统账号
  ('EMP200', '市场专员张三', 'M', '13700002001', 'zhangsan_hr@huacai.com', 10, '市场专员', 'ONBOARD', 1, 'staff_market_01', '2024-01-15', 1),
  -- 在职员工 - 销售部 - 已开通系统账号
  ('EMP201', '销售专员李四', 'F', '13700002002', 'lisi_hr@huacai.com', 11, '销售专员', 'ONBOARD', 1, 'staff_sales_01', '2024-02-01', 1),
  -- 在职员工 - 财务部 - 已开通系统账号
  ('EMP202', '财务专员王五', 'M', '13700002003', 'wangwu_hr@huacai.com', 12, '财务专员', 'ONBOARD', 1, 'staff_finance_01', '2024-01-20', 1),
  -- 在职员工 - 人事部 - 未开通系统账号
  ('EMP203', '人事专员赵六', 'F', '13700002004', 'zhaoliu_hr@huacai.com', 13, '人事专员', 'ONBOARD', 0, NULL, '2024-03-01', 1),
  -- 离职员工 - 销售部 - 未开通系统账号
  ('EMP204', '离职员工胡七', 'M', '13700002005', 'huqi_hr@huacai.com', 11, '销售代表', 'OFFBOARD', 0, NULL, '2023-06-15', 1),
  -- 试用期员工 - 市场部 - 未开通系统账号
  ('EMP205', '试用期员工周八', 'F', '13700002006', 'zhouba_hr@huacai.com', 10, '市场助理', 'ONBOARD', 0, NULL, '2026-03-01', 1);

-- 3.2 工资标准 (至少 4 条)
INSERT IGNORE INTO `hr_salary_standard` (`salary_name`, `amount`, `job_title`, `org_id`, `org_name`, `description`, `status`, `effective_date`, `applicable_scope`, `created_by`)
VALUES
  ('市场专员-初级', 8000.00, '市场专员', 10, '市场部', '市场专员初级工资标准', 'ENABLE', '2024-01-01', '市场部市场专员', 1),
  ('市场专员-高级', 12000.00, '市场专员', 10, '市场部', '市场专员高级工资标准', 'ENABLE', '2024-01-01', '市场部高级市场专员', 1),
  ('销售专员-初级', 6000.00, '销售专员', 11, '销售部', '销售专员初级工资标准', 'ENABLE', '2024-01-01', '销售部销售专员', 1),
  ('销售专员-高级', 10000.00, '销售专员', 11, '销售部', '销售专员高级工资标准', 'ENABLE', '2024-01-01', '销售部高级销售专员', 1),
  ('财务专员-标准', 9000.00, '财务专员', 12, '财务部', '财务专员标准工资', 'ENABLE', '2024-01-01', '财务部财务专员', 1);

-- 3.3 管理记录 - 转正记录 (至少 2 条)
INSERT IGNORE INTO `hr_management_record` (`employee_id`, `employee_name`, `record_type`, `content`, `operator_id`, `operator_name`, `operated_at`, `created_by`)
SELECT e.id, e.real_name, 'TRANSITION',
  CONCAT(e.real_name, ' 于 ', DATE_FORMAT(DATE_ADD(e.work_start_date, INTERVAL 3 MONTH), '%Y-%m-%d'), ' 试用期表现良好，申请转正，经审批通过'),
  1, '系统管理员',
  DATE_ADD(e.work_start_date, INTERVAL 3 MONTH)
FROM `hr_employee` e
WHERE e.employee_code IN ('EMP200', 'EMP201', 'EMP202')
  AND NOT EXISTS (
    SELECT 1 FROM `hr_management_record` r WHERE r.employee_id = e.id AND r.record_type = 'TRANSITION'
  )
LIMIT 2;

-- 3.4 管理记录 - 离职记录 (至少 1 条)
INSERT IGNORE INTO `hr_management_record` (`employee_id`, `employee_name`, `record_type`, `content`, `operator_id`, `operator_name`, `operated_at`, `created_by`)
SELECT e.id, e.real_name, 'REMOVAL',
  CONCAT(e.real_name, ' 因个人原因申请离职，经审批同意，已办理离职手续'),
  1, '系统管理员',
  DATE_SUB(CURDATE(), INTERVAL 30 DAY)
FROM `hr_employee` e
WHERE e.employee_code = 'EMP204'
  AND NOT EXISTS (
    SELECT 1 FROM `hr_management_record` r WHERE r.employee_id = e.id AND r.record_type = 'REMOVAL'
  );

-- 3.5 管理记录 - 调岗记录 (至少 2 条)
INSERT IGNORE INTO `hr_management_record` (`employee_id`, `employee_name`, `record_type`, `content`, `operator_id`, `operator_name`, `operated_at`, `created_by`)
SELECT e.id, e.real_name, 'JOB_CHANGE',
  CONCAT(e.real_name, ' 从市场专员调岗至市场高级专员，薪资调整'),
  1, '系统管理员',
  DATE_SUB(CURDATE(), INTERVAL 60 DAY)
FROM `hr_employee` e
WHERE e.employee_code = 'EMP200'
  AND NOT EXISTS (
    SELECT 1 FROM `hr_management_record` r WHERE r.employee_id = e.id AND r.record_type = 'JOB_CHANGE'
  )
LIMIT 1;

INSERT IGNORE INTO `hr_management_record` (`employee_id`, `employee_name`, `record_type`, `content`, `operator_id`, `operator_name`, `operated_at`, `created_by`)
SELECT e.id, e.real_name, 'JOB_CHANGE',
  CONCAT(e.real_name, ' 从销售代表晋升为销售专员，薪资调整'),
  1, '系统管理员',
  DATE_SUB(CURDATE(), INTERVAL 45 DAY)
FROM `hr_employee` e
WHERE e.employee_code = 'EMP201'
  AND NOT EXISTS (
    SELECT 1 FROM `hr_management_record` r WHERE r.employee_id = e.id AND r.record_type = 'JOB_CHANGE'
  )
LIMIT 1;

-- =============================================================================
-- 四、报销与审批
-- =============================================================================

-- 4.1 报销申请 (至少 6 条，覆盖不同状态)
INSERT IGNORE INTO `fin_reimbursement_apply` (`apply_no`, `applicant_id`, `applicant_name`, `applicant_org_id`, `applicant_org_name`, `amount`, `reason`, `process_status`, `current_node_key`, `current_node_name`, `current_round`, `submit_time`, `created_by`)
SELECT
  CONCAT('BX', DATE_FORMAT(CURDATE(), '%Y%m%d'), LPAD(ROW_NUMBER() OVER (ORDER BY u.id), 4, '0')),
  u.id,
  u.real_name,
  u.org_id,
  (SELECT org_name FROM sys_org WHERE id = u.org_id),
  ROUND(100 + RAND()*5000, 2),
  ELT(ROW_NUMBER() OVER (ORDER BY u.id) % 5 + 1, '日常办公费用报销', '商务接待费用报销', '差旅费用报销', '培训费用报销', '设备采购费用报销'),
  ELT(ROW_NUMBER() OVER (ORDER BY u.id) % 5 + 1, 'DRAFT', 'PENDING_DEPT', 'APPROVED', 'REJECTED', 'WITHDRAWN'),
  ELT(ROW_NUMBER() OVER (ORDER BY u.id) % 5 + 1, NULL, 'DEPT_APPROVAL', NULL, 'DEPT_APPROVAL', NULL),
  ELT(ROW_NUMBER() OVER (ORDER BY u.id) % 5 + 1, NULL, '部门审批', NULL, '部门审批', NULL),
  1,
  ELT(ROW_NUMBER() OVER (ORDER BY u.id) % 5 + 1, NULL, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
  1
FROM `sys_user` u
WHERE u.username IN ('staff_market_01', 'staff_sales_01', 'staff_finance_01', 'staff_hr_01')
  AND NOT EXISTS (
    SELECT 1 FROM `fin_reimbursement_apply` r WHERE r.applicant_id = u.id AND r.created_at > DATE_SUB(NOW(), INTERVAL 1 DAY)
  );

-- 4.2 流程实例数据 (关联报销申请)
INSERT IGNORE INTO `wf_process_instance` (`biz_type`, `biz_id`, `biz_no`, `title`, `applicant_id`, `applicant_name`, `applicant_org_id`, `applicant_org_name`, `overall_status`, `current_node_key`, `current_node_name`, `current_round`, `started_at`, `last_action_at`, `created_by`)
SELECT
  'REIMBURSEMENT',
  r.id,
  r.apply_no,
  CONCAT(r.applicant_name, '的报销申请'),
  r.applicant_id,
  r.applicant_name,
  r.applicant_org_id,
  r.applicant_org_name,
  r.process_status,
  r.current_node_key,
  r.current_node_name,
  r.current_round,
  r.submit_time,
  r.submit_time,
  1
FROM `fin_reimbursement_apply` r
WHERE r.process_status != 'DRAFT'
  AND NOT EXISTS (
    SELECT 1 FROM `wf_process_instance` p WHERE p.biz_type = 'REIMBURSEMENT' AND p.biz_id = r.id
  );

-- 4.3 待办任务数据 (关联流程实例)
INSERT IGNORE INTO `wf_task` (`process_id`, `biz_type`, `biz_id`, `round_no`, `node_key`, `node_name`, `task_status`, `assignee_id`, `assignee_name`, `candidate_role_code`, `sort_no`, `created_by`, `created_at`)
SELECT
  p.id,
  'REIMBURSEMENT',
  r.id,
  1,
  'DEPT_APPROVAL',
  '部门审批',
  'PENDING',
  (SELECT id FROM sys_user WHERE username = 'dept_admin_sales' LIMIT 1),
  (SELECT real_name FROM sys_user WHERE username = 'dept_admin_sales' LIMIT 1),
  'DEPT_ADMIN',
  1,
  1,
  NOW()
FROM `fin_reimbursement_apply` r
INNER JOIN `wf_process_instance` p ON p.biz_type = 'REIMBURSEMENT' AND p.biz_id = r.id
WHERE r.process_status = 'PENDING_DEPT'
  AND NOT EXISTS (
    SELECT 1 FROM `wf_task` t WHERE t.process_id = p.id AND t.node_key = 'DEPT_APPROVAL'
  );

-- 4.4 动作日志数据
INSERT IGNORE INTO `wf_action_log` (`process_id`, `biz_type`, `biz_id`, `round_no`, `action_code`, `node_key`, `node_name`, `operator_id`, `operator_name`, `action_opinion`, `action_result`, `action_time`, `created_by`)
SELECT
  p.id,
  'REIMBURSEMENT',
  r.id,
  1,
  'SUBMIT',
  NULL,
  '提交申请',
  r.applicant_id,
  r.applicant_name,
  '提交报销申请，请领导审批',
  'SUBMITTED',
  r.submit_time,
  1
FROM `fin_reimbursement_apply` r
INNER JOIN `wf_process_instance` p ON p.biz_type = 'REIMBURSEMENT' AND p.biz_id = r.id
WHERE r.process_status IN ('PENDING_DEPT', 'APPROVED', 'REJECTED')
  AND NOT EXISTS (
    SELECT 1 FROM `wf_action_log` l WHERE l.process_id = p.id AND l.action_code = 'SUBMIT'
  );

-- 已审批记录的动作日志
INSERT IGNORE INTO `wf_action_log` (`process_id`, `biz_type`, `biz_id`, `round_no`, `action_code`, `node_key`, `node_name`, `operator_id`, `operator_name`, `action_opinion`, `action_result`, `action_time`, `created_by`)
SELECT
  p.id,
  'REIMBURSEMENT',
  r.id,
  1,
  'APPROVE',
  'DEPT_APPROVAL',
  '部门审批',
  (SELECT id FROM sys_user WHERE username = 'dept_admin_sales' LIMIT 1),
  (SELECT real_name FROM sys_user WHERE username = 'dept_admin_sales' LIMIT 1),
  '同意报销申请',
  'AGREE',
  DATE_ADD(r.submit_time, INTERVAL 1 DAY),
  1
FROM `fin_reimbursement_apply` r
INNER JOIN `wf_process_instance` p ON p.biz_type = 'REIMBURSEMENT' AND p.biz_id = r.id
WHERE r.process_status = 'APPROVED'
  AND NOT EXISTS (
    SELECT 1 FROM `wf_action_log` l WHERE l.process_id = p.id AND l.action_code = 'APPROVE'
  );

INSERT IGNORE INTO `wf_action_log` (`process_id`, `biz_type`, `biz_id`, `round_no`, `action_code`, `node_key`, `node_name`, `operator_id`, `operator_name`, `action_opinion`, `action_result`, `action_time`, `created_by`)
SELECT
  p.id,
  'REIMBURSEMENT',
  r.id,
  1,
  'REJECT',
  'DEPT_APPROVAL',
  '部门审批',
  (SELECT id FROM sys_user WHERE username = 'dept_admin_sales' LIMIT 1),
  (SELECT real_name FROM sys_user WHERE username = 'dept_admin_sales' LIMIT 1),
  '报销金额异常，需要补充材料',
  'REJECT',
  DATE_ADD(r.submit_time, INTERVAL 1 DAY),
  1
FROM `fin_reimbursement_apply` r
INNER JOIN `wf_process_instance` p ON p.biz_type = 'REIMBURSEMENT' AND p.biz_id = r.id
WHERE r.process_status = 'REJECTED'
  AND NOT EXISTS (
    SELECT 1 FROM `wf_action_log` l WHERE l.process_id = p.id AND l.action_code = 'REJECT'
  );

SET FOREIGN_KEY_CHECKS = 1;

-- =============================================================================
-- 执行结果统计 (可在执行后单独运行以下查询查看)
-- =============================================================================
SELECT '============= 数据插入统计 =============' AS '';
SELECT 'sys_user' AS tbl, COUNT(*) AS total, SUM(CASE WHEN deleted_flag = 0 THEN 1 ELSE 0 END) AS active FROM huacai_system.sys_user;
SELECT 'cust_customer' AS tbl, COUNT(*) AS total FROM huacai_system.cust_customer;
SELECT 'cust_customer_score' AS tbl, COUNT(*) AS total FROM huacai_system.cust_customer_score;
SELECT 'cust_customer_debt' AS tbl, COUNT(*) AS total FROM huacai_system.cust_customer_debt;
SELECT 'cust_customer_contract' AS tbl, COUNT(*) AS total FROM huacai_system.cust_customer_contract;
SELECT 'cust_customer_status_log' AS tbl, COUNT(*) AS total FROM huacai_system.cust_customer_status_log;
SELECT 'hr_employee' AS tbl, COUNT(*) AS total, SUM(employment_status = 'ONBOARD') AS onboard, SUM(employment_status = 'OFFBOARD') AS offboard FROM huacai_system.hr_employee;
SELECT 'hr_management_record' AS tbl, COUNT(*) AS total, SUM(record_type = 'TRANSITION') AS transition, SUM(record_type = 'REMOVAL') AS removal, SUM(record_type = 'JOB_CHANGE') AS job_change FROM huacai_system.hr_management_record;
SELECT 'hr_salary_standard' AS tbl, COUNT(*) AS total FROM huacai_system.hr_salary_standard;
SELECT 'fin_reimbursement_apply' AS tbl, COUNT(*) AS total, SUM(process_status = 'DRAFT') AS draft, SUM(process_status LIKE 'PENDING%') AS pending, SUM(process_status = 'APPROVED') AS approved, SUM(process_status = 'REJECTED') AS rejected FROM huacai_system.fin_reimbursement_apply;
SELECT 'wf_process_instance' AS tbl, COUNT(*) AS total FROM huacai_system.wf_process_instance;
SELECT 'wf_task' AS tbl, COUNT(*) AS total, SUM(task_status = 'PENDING') AS pending FROM huacai_system.wf_task;
SELECT 'wf_action_log' AS tbl, COUNT(*) AS total FROM huacai_system.wf_action_log;
