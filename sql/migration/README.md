# 数据库迁移脚本 (Database Migration Scripts)

## 命名规则

所有迁移脚本按以下格式命名：

```
V2_{三位编号}__{描述}.sql
```

示例：`V2_001__create_sys_dict_tables.sql`

## 编号分配

| 编号段 | 模块 | 负责 Agent |
|--------|------|-----------|
| V2_001 ~ V2_009 | 数据字典 (sys_dict_type / sys_dict_item) | Agent 8 |
| V2_010 ~ V2_019 | 操作日志 (sys_operation_log) | Agent 8 |
| V2_020 ~ V2_039 | 人事管理 (hr_employee / hr_salary_standard / hr_management_record) | Agent 6 |
| V2_040 ~ V2_059 | 审批流 (wf_process_instance / wf_task / wf_*_apply) | Agent 9 |
| V2_060 ~ V2_079 | 增量经营 (ana_increment_*) | Agent 7 |
| V2_080 ~ V2_089 | 经营分析 (ana_employee_performance) | Agent 10 |
| V2_090 ~ V2_099 | 财务完善 (fin_payee / fin_income / fin_expense) | Agent 3 |

## 编写规则

1. **仅新增**：不修改 `sql/init/001_schema.sql`，该文件只保留一期初始化脚本
2. **幂等设计**：每个脚本应使用 `CREATE TABLE IF NOT EXISTS`
3. **完整审计字段**：新表统一包含 `created_by`, `created_at`, `updated_by`, `updated_at`, `deleted_flag`, `version`
4. **字符集统一**：使用 `CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci`
5. **索引规范**：常用查询字段建立索引，外键字段建立索引

## 执行方式

当前阶段手动执行，后续可接入 Flyway 自动管理。

```bash
# 按编号顺序执行
mysql -u root -p huacai_system < sql/migration/V2_001__create_sys_dict_tables.sql
```
