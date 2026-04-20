#!/bin/bash
# Verification script for demo data
docker exec huacai-mysql mysql -uroot -proot huacai_system -e "SELECT '=== sys_user ===' as info; SELECT id, username, real_name, identity_type FROM sys_user;"
docker exec huacai-mysql mysql -uroot -proot huacai_system -e "SELECT '=== cust_customer ===' as info; SELECT id, customer_no, customer_name, audit_status FROM cust_customer LIMIT 10;"
docker exec huacai-mysql mysql -uroot -proot huacai_system -e "SELECT '=== hr_employee ===' as info; SELECT id, employee_code, real_name, employment_status FROM hr_employee;"
docker exec huacai-mysql mysql -uroot -proot huacai_system -e "SELECT '=== fin_reimbursement ===' as info; SELECT id, apply_no, process_status FROM fin_reimbursement_apply;"
docker exec huacai-mysql mysql -uroot -proot huacai_system -e "SELECT '=== Counts ===' as info; SELECT 'sys_user' as tbl, COUNT(*) as cnt FROM sys_user UNION SELECT 'cust_customer', COUNT(*) FROM cust_customer UNION SELECT 'hr_employee', COUNT(*) FROM hr_employee UNION SELECT 'fin_reimbursement', COUNT(*) FROM fin_reimbursement_apply UNION SELECT 'wf_process', COUNT(*) FROM wf_process_instance UNION SELECT 'wf_action_log', COUNT(*) FROM wf_action_log;"
