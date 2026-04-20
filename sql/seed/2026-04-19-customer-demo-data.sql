SET NAMES utf8mb4;
USE huacai_system;

START TRANSACTION;

SET @seed_prefix = 'DEMO-KH-20260419-';

DELETE FROM cust_customer_contract
WHERE customer_id IN (
    SELECT id FROM (
        SELECT id FROM cust_customer WHERE customer_no LIKE CONCAT(@seed_prefix, '%')
    ) seeded_customers
);

DELETE FROM cust_customer_debt
WHERE customer_id IN (
    SELECT id FROM (
        SELECT id FROM cust_customer WHERE customer_no LIKE CONCAT(@seed_prefix, '%')
    ) seeded_customers
);

DELETE FROM cust_customer_score
WHERE customer_id IN (
    SELECT id FROM (
        SELECT id FROM cust_customer WHERE customer_no LIKE CONCAT(@seed_prefix, '%')
    ) seeded_customers
);

DELETE FROM cust_customer_status_log
WHERE customer_id IN (
    SELECT id FROM (
        SELECT id FROM cust_customer WHERE customer_no LIKE CONCAT(@seed_prefix, '%')
    ) seeded_customers
);

DELETE FROM cust_customer
WHERE customer_no LIKE CONCAT(@seed_prefix, '%');

INSERT INTO cust_customer (
    customer_no,
    customer_name,
    gender,
    id_card,
    birthday,
    age,
    mobile,
    company_name,
    credit_code,
    established_date,
    industry,
    business_address,
    bank_name,
    bank_account,
    recommender_name,
    recommender_rate,
    service_fee,
    audit_status,
    biz_status,
    loan_status,
    tax_registration_normal,
    remark,
    created_by,
    updated_by
) VALUES
(
    'DEMO-KH-20260419-001',
    '张华',
    'MALE',
    '440101199201010011',
    '1992-01-01',
    34,
    '13800001001',
    '深圳市华彩供应链有限公司',
    '91440300DEMO000001',
    '2018-03-15',
    '供应链',
    '深圳市南山区科技园科苑路 18 号',
    '招商银行深圳南山支行',
    '6225888800001001',
    '胡XX',
    1.00,
    3000.00,
    'PENDING',
    'INIT',
    'NOT_STARTED',
    1,
    '演示客户：待审核、未开始借款',
    1,
    1
),
(
    'DEMO-KH-20260419-002',
    '李敏',
    'FEMALE',
    '440101198811020022',
    '1988-11-02',
    37,
    '13800001002',
    '广州远望科技有限公司',
    '91440101DEMO000002',
    '2016-07-08',
    '科技服务',
    '广州市天河区珠江新城华夏路 8 号',
    '中国银行广州天河支行',
    '6216612300001002',
    '陈XX',
    0.80,
    4500.00,
    'APPROVED',
    'FOLLOWING',
    'RUNNING',
    1,
    '演示客户：审批通过，存在垫付',
    1,
    1
),
(
    'DEMO-KH-20260419-003',
    '王强',
    'MALE',
    '440101198507150033',
    '1985-07-15',
    40,
    '13800001003',
    '佛山卓越制造有限公司',
    '91440600DEMO000003',
    '2013-09-20',
    '制造业',
    '佛山市顺德区陈村镇工业大道 66 号',
    '建设银行佛山顺德支行',
    '6227000000001003',
    '赵XX',
    1.20,
    5200.00,
    'APPROVED',
    'SIGNED',
    'SETTLED',
    1,
    '演示客户：审批通过，已结清',
    1,
    1
),
(
    'DEMO-KH-20260419-004',
    '周洁',
    'FEMALE',
    '440101199505080044',
    '1995-05-08',
    31,
    '13800001004',
    '东莞启航贸易有限公司',
    '91441900DEMO000004',
    '2020-06-10',
    '贸易',
    '东莞市南城街道宏图路 28 号',
    '工商银行东莞南城支行',
    '6222000000001004',
    NULL,
    NULL,
    2600.00,
    'REJECTED',
    'INIT',
    'NOT_STARTED',
    0,
    '演示客户：审核驳回',
    1,
    1
),
(
    'DEMO-KH-20260419-005',
    '黄凯',
    'MALE',
    '440101199101120055',
    '1991-01-12',
    35,
    '13800001005',
    '珠海云帆物流有限公司',
    '91440400DEMO000005',
    '2017-04-18',
    '物流',
    '珠海市香洲区梅华东路 99 号',
    '平安银行珠海香洲支行',
    '6221550000001005',
    '林XX',
    0.50,
    3800.00,
    'APPROVED',
    'FOLLOWING',
    'RUNNING',
    1,
    '演示客户：审批通过，持续还款中',
    1,
    1
),
(
    'DEMO-KH-20260419-006',
    '刘彬',
    'MALE',
    '440101199303030066',
    '1993-03-03',
    33,
    '13800001006',
    '中山海创电子有限公司',
    '91442000DEMO000006',
    '2019-11-11',
    '电子制造',
    '中山市火炬开发区会展东路 12 号',
    '农业银行中山火炬支行',
    '6228480000001006',
    '苏XX',
    1.50,
    6100.00,
    'PENDING',
    'FOLLOWING',
    'RUNNING',
    1,
    '演示客户：待审核但已录入风险和负债',
    1,
    1
);

INSERT INTO cust_customer_score (
    customer_id,
    test_date,
    test_limit,
    traffic_value,
    composite_score,
    third_party_score,
    score_result,
    remark,
    created_by,
    updated_by
)
SELECT id, '2026-03-01', 120.00, 195.00, 91.00, 113.00, 'PASS', '供应链客户评分通过', 1, 1
FROM cust_customer WHERE customer_no = 'DEMO-KH-20260419-001'
UNION ALL
SELECT id, '2026-03-15', 65.00, 68.00, 118.00, 112.00, 'PASS', '科技客户评分较高', 1, 1
FROM cust_customer WHERE customer_no = 'DEMO-KH-20260419-002'
UNION ALL
SELECT id, '2026-02-20', 50.00, 150.00, 100.00, 108.00, 'PASS', '制造业客户稳定', 1, 1
FROM cust_customer WHERE customer_no = 'DEMO-KH-20260419-003'
UNION ALL
SELECT id, '2026-04-02', 32.00, 44.00, 72.00, 86.00, 'REVIEW', '驳回客户，评分偏低', 1, 1
FROM cust_customer WHERE customer_no = 'DEMO-KH-20260419-004'
UNION ALL
SELECT id, '2026-03-28', 88.00, 128.00, 95.00, 110.00, 'PASS', '物流客户风险可控', 1, 1
FROM cust_customer WHERE customer_no = 'DEMO-KH-20260419-005'
UNION ALL
SELECT id, '2026-04-10', 73.00, 82.00, 84.00, 97.00, 'REVIEW', '待审核客户评分中等', 1, 1
FROM cust_customer WHERE customer_no = 'DEMO-KH-20260419-006';

INSERT INTO cust_customer_debt (
    customer_id,
    debt_type,
    debt_amount,
    repaid_amount,
    pending_amount,
    installment_amount,
    repayment_day,
    remark,
    created_by,
    updated_by
)
SELECT id, '信用卡', 18.00, 3.00, 15.00, 1.50, 12, '首张演示负债记录，单位万元', 1, 1
FROM cust_customer WHERE customer_no = 'DEMO-KH-20260419-001'
UNION ALL
SELECT id, '经营贷', 66.00, 20.00, 46.00, 3.80, 18, '经营贷款在还', 1, 1
FROM cust_customer WHERE customer_no = 'DEMO-KH-20260419-002'
UNION ALL
SELECT id, '网贷', 12.00, 12.00, 0.00, 1.00, 5, '已全部结清', 1, 1
FROM cust_customer WHERE customer_no = 'DEMO-KH-20260419-003'
UNION ALL
SELECT id, '消费贷', 8.00, 1.00, 7.00, 0.90, 9, '审核驳回客户仍有负债', 1, 1
FROM cust_customer WHERE customer_no = 'DEMO-KH-20260419-004'
UNION ALL
SELECT id, '供应链垫资', 35.00, 10.00, 25.00, 2.20, 20, '物流客户垫付中', 1, 1
FROM cust_customer WHERE customer_no = 'DEMO-KH-20260419-005'
UNION ALL
SELECT id, '信用贷', 22.00, 4.00, 18.00, 1.60, 25, '待审核客户，需持续跟进', 1, 1
FROM cust_customer WHERE customer_no = 'DEMO-KH-20260419-006';

INSERT INTO cust_customer_contract (
    customer_id,
    customer_name,
    company_name,
    credit_code,
    contract_no,
    contract_name,
    contract_file_id,
    sign_date,
    remark,
    created_by,
    updated_by
)
SELECT id, customer_name, company_name, credit_code, 'HT-2026-001', '华彩供应链服务合同', NULL, '2026-03-06', '首批演示合同', 1, 1
FROM cust_customer WHERE customer_no = 'DEMO-KH-20260419-001'
UNION ALL
SELECT id, customer_name, company_name, credit_code, 'HT-2026-002', '远望科技融资顾问协议', NULL, '2026-03-20', '科技客户合同', 1, 1
FROM cust_customer WHERE customer_no = 'DEMO-KH-20260419-002'
UNION ALL
SELECT id, customer_name, company_name, credit_code, 'HT-2026-003', '卓越制造结清确认书', NULL, '2026-02-28', '已结清客户合同', 1, 1
FROM cust_customer WHERE customer_no = 'DEMO-KH-20260419-003'
UNION ALL
SELECT id, customer_name, company_name, credit_code, 'HT-2026-004', '云帆物流合作协议', NULL, '2026-04-05', '物流客户合同', 1, 1
FROM cust_customer WHERE customer_no = 'DEMO-KH-20260419-005';

INSERT INTO cust_customer_status_log (
    customer_id,
    status_code,
    status_name,
    changed_at,
    changed_by,
    remark
)
SELECT id, 'PENDING', '待审核', '2026-03-01 09:00:00', 1, '客户建档'
FROM cust_customer WHERE customer_no = 'DEMO-KH-20260419-001'
UNION ALL
SELECT id, 'APPROVED', '已通过', '2026-03-18 11:00:00', 1, '审批通过'
FROM cust_customer WHERE customer_no = 'DEMO-KH-20260419-002'
UNION ALL
SELECT id, 'FOLLOWING', '开始借款', '2026-03-20 15:30:00', 1, '进入借款阶段'
FROM cust_customer WHERE customer_no = 'DEMO-KH-20260419-002'
UNION ALL
SELECT id, 'APPROVED', '已通过', '2026-02-21 10:00:00', 1, '审批通过'
FROM cust_customer WHERE customer_no = 'DEMO-KH-20260419-003'
UNION ALL
SELECT id, 'SIGNED', '已结清', '2026-04-01 16:20:00', 1, '全部结清'
FROM cust_customer WHERE customer_no = 'DEMO-KH-20260419-003'
UNION ALL
SELECT id, 'REJECTED', '已驳回', '2026-04-04 14:00:00', 1, '资料不齐，驳回'
FROM cust_customer WHERE customer_no = 'DEMO-KH-20260419-004';

COMMIT;
