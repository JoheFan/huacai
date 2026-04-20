-- 用户数据补充SQL
USE huacai_system;
SET NAMES utf8mb4;

-- 清理可能存在的测试用户（如果需要重新开始）
-- DELETE FROM sys_user_role WHERE user_id > 5;
-- DELETE FROM sys_user WHERE username IN ('dept_admin_market', 'dept_admin_sales', 'staff_market_01', 'staff_sales_01', 'staff_finance_01', 'staff_hr_01');

-- 插入部门管理员和市场部
INSERT INTO sys_user (username, password_hash, employee_code, real_name, phone, email, org_id, identity_type, job_title, employment_status, account_status, created_by)
SELECT 'dept_admin_market', password_hash, 'EMP100', '市场管理员', '13900001001', 'market@huacai.com', 10, 'DEPT_ADMIN', '市场总监', 'ON_JOB', 'ENABLE', 1
FROM sys_user WHERE username = 'admin' LIMIT 1
ON DUPLICATE KEY UPDATE username = username;

INSERT INTO sys_user (username, password_hash, employee_code, real_name, phone, email, org_id, identity_type, job_title, employment_status, account_status, created_by)
SELECT 'dept_admin_sales', password_hash, 'EMP101', '销售管理员', '13900001002', 'sales@huacai.com', 11, 'DEPT_ADMIN', '销售总监', 'ON_JOB', 'ENABLE', 1
FROM sys_user WHERE username = 'admin' LIMIT 1
ON DUPLICATE KEY UPDATE username = username;

INSERT INTO sys_user (username, password_hash, employee_code, real_name, phone, email, org_id, identity_type, job_title, employment_status, account_status, created_by)
SELECT 'staff_market_01', password_hash, 'EMP102', '市场专员张三', '13900001003', 'zhangsan@huacai.com', 10, 'NORMAL_USER', '市场专员', 'ON_JOB', 'ENABLE', 1
FROM sys_user WHERE username = 'admin' LIMIT 1
ON DUPLICATE KEY UPDATE username = username;

INSERT INTO sys_user (username, password_hash, employee_code, real_name, phone, email, org_id, identity_type, job_title, employment_status, account_status, created_by)
SELECT 'staff_sales_01', password_hash, 'EMP103', '销售专员李四', '13900001004', 'lisi@huacai.com', 11, 'NORMAL_USER', '销售专员', 'ON_JOB', 'ENABLE', 1
FROM sys_user WHERE username = 'admin' LIMIT 1
ON DUPLICATE KEY UPDATE username = username;

INSERT INTO sys_user (username, password_hash, employee_code, real_name, phone, email, org_id, identity_type, job_title, employment_status, account_status, created_by)
SELECT 'staff_finance_01', password_hash, 'EMP104', '财务专员王五', '13900001005', 'wangwu@huacai.com', 12, 'NORMAL_USER', '财务专员', 'ON_JOB', 'ENABLE', 1
FROM sys_user WHERE username = 'admin' LIMIT 1
ON DUPLICATE KEY UPDATE username = username;

INSERT INTO sys_user (username, password_hash, employee_code, real_name, phone, email, org_id, identity_type, job_title, employment_status, account_status, created_by)
SELECT 'staff_hr_01', password_hash, 'EMP105', '人事专员赵六', '13900001006', 'zhaoliu@huacai.com', 13, 'NORMAL_USER', '人事专员', 'ON_JOB', 'ENABLE', 1
FROM sys_user WHERE username = 'admin' LIMIT 1
ON DUPLICATE KEY UPDATE username = username;

-- 更新密码为已知值
UPDATE sys_user SET password_hash = (SELECT password_hash FROM (SELECT password_hash FROM sys_user WHERE username = 'admin') AS t)
WHERE username IN ('dept_admin_market', 'dept_admin_sales', 'staff_market_01', 'staff_sales_01', 'staff_finance_01', 'staff_hr_01');

-- 插入用户角色关联
INSERT INTO sys_user_role (user_id, role_id, created_by, created_at)
SELECT u.id, r.id, 1, NOW()
FROM sys_user u
CROSS JOIN sys_role r
WHERE u.username = 'dept_admin_market' AND r.role_code = 'DEPT_ADMIN'
AND NOT EXISTS (SELECT 1 FROM sys_user_role WHERE user_id = u.id AND role_id = r.id);

INSERT INTO sys_user_role (user_id, role_id, created_by, created_at)
SELECT u.id, r.id, 1, NOW()
FROM sys_user u
CROSS JOIN sys_role r
WHERE u.username = 'dept_admin_sales' AND r.role_code = 'DEPT_ADMIN'
AND NOT EXISTS (SELECT 1 FROM sys_user_role WHERE user_id = u.id AND role_id = r.id);

INSERT INTO sys_user_role (user_id, role_id, created_by, created_at)
SELECT u.id, r.id, 1, NOW()
FROM sys_user u
CROSS JOIN sys_role r
WHERE u.username = 'staff_market_01' AND r.role_code = 'STAFF'
AND NOT EXISTS (SELECT 1 FROM sys_user_role WHERE user_id = u.id AND role_id = r.id);

INSERT INTO sys_user_role (user_id, role_id, created_by, created_at)
SELECT u.id, r.id, 1, NOW()
FROM sys_user u
CROSS JOIN sys_role r
WHERE u.username = 'staff_sales_01' AND r.role_code = 'STAFF'
AND NOT EXISTS (SELECT 1 FROM sys_user_role WHERE user_id = u.id AND role_id = r.id);

INSERT INTO sys_user_role (user_id, role_id, created_by, created_at)
SELECT u.id, r.id, 1, NOW()
FROM sys_user u
CROSS JOIN sys_role r
WHERE u.username = 'staff_finance_01' AND r.role_code = 'STAFF'
AND NOT EXISTS (SELECT 1 FROM sys_user_role WHERE user_id = u.id AND role_id = r.id);

INSERT INTO sys_user_role (user_id, role_id, created_by, created_at)
SELECT u.id, r.id, 1, NOW()
FROM sys_user u
CROSS JOIN sys_role r
WHERE u.username = 'staff_hr_01' AND r.role_code = 'STAFF'
AND NOT EXISTS (SELECT 1 FROM sys_user_role WHERE user_id = u.id AND role_id = r.id);
