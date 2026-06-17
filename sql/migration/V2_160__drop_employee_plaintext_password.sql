-- 删除 hr_employee.system_password_plain 明文密码列
-- 背景：建/改员工时密码本应只 bcrypt 写入 sys_user，却同时把明文存进了 hr_employee。
--       后端已停止写入该列，此处删列以彻底清除存量明文。脚本幂等。

SET @db_name := DATABASE();

SET @has_plain_col := (
  SELECT COUNT(*)
  FROM information_schema.columns
  WHERE table_schema = @db_name
    AND table_name = 'hr_employee'
    AND column_name = 'system_password_plain'
);
SET @sql := IF(
  @has_plain_col = 1,
  'ALTER TABLE `hr_employee` DROP COLUMN `system_password_plain`',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;