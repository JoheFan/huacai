#!/usr/bin/env node

import { execFileSync } from 'node:child_process';
import { mkdirSync, writeFileSync } from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const ROOT_DIR = path.resolve(__dirname, '../..');

const API_BASE = process.env.HUACAI_API_BASE ?? 'http://127.0.0.1:18081/api/v1';
const MYSQL_CONTAINER = process.env.HUACAI_MYSQL_CONTAINER ?? 'huacai-mysql';
const MYSQL_DATABASE = process.env.HUACAI_MYSQL_DATABASE ?? 'huacai_system';
const MYSQL_USER = process.env.HUACAI_MYSQL_USER ?? 'root';
const MYSQL_PASSWORD = process.env.HUACAI_MYSQL_PASSWORD ?? 'root';
const ADMIN_USERNAME = process.env.HUACAI_ADMIN_USERNAME ?? 'admin';
const ADMIN_PASSWORD = process.env.HUACAI_ADMIN_PASSWORD ?? 'huacai123';
const DEMO_PASSWORD = process.env.HUACAI_SEED_PASSWORD ?? 'Phase!2026';
const MARKER = 'phase-all-pages-20260513';
const REPORT_DIR = path.join(ROOT_DIR, 'tmp');
const REPORT_PATH = path.join(REPORT_DIR, 'phase-all-pages-report.json');

const ORG_DEFS = [
  { key: 'business', orgName: `业务一部（${MARKER}）`, orgType: 'DEPT', sortNo: 210, remark: `seed:${MARKER}` },
  { key: 'support', orgName: `运营支持部（${MARKER}）`, orgType: 'DEPT', sortNo: 220, remark: `seed:${MARKER}` },
];

const ROLE_DEFS = [
  {
    key: 'portalUser',
    roleCode: 'PHASE_PORTAL_USER',
    roleName: `演示员工入口（${MARKER}）`,
    identityType: 'NORMAL_USER',
    dataScope: 'SELF',
    remark: `seed:${MARKER}`,
    pagePermissions: ['/hr/my-profile', '/finance/reimbursements', '/workflow/my-approvals'],
    buttonPermissions: ['finance.reimbursements:create'],
    dataScopes: {
      'hr.my-profile': 'SELF',
      'finance.reimbursements': 'SELF',
      'workflow.my-approvals': 'SELF',
    },
  },
  {
    key: 'approverPortal',
    roleCode: 'PHASE_APPROVER_PORTAL',
    roleName: `演示审批入口（${MARKER}）`,
    identityType: 'NORMAL_USER',
    dataScope: 'SELF',
    remark: `seed:${MARKER}`,
    pagePermissions: ['/hr/my-profile', '/finance/reimbursements', '/workflow/my-approvals'],
    buttonPermissions: [
      'finance.reimbursements:create',
      'finance.reimbursements:approve',
      'finance.reimbursements:view-all',
      'workflow.my-approvals:approve',
    ],
    dataScopes: {
      'hr.my-profile': 'SELF',
      'finance.reimbursements': 'SELF',
      'workflow.my-approvals': 'SELF',
    },
  },
  {
    key: 'deptLeader',
    roleCode: 'DEPT_LEADER',
    roleName: '部门审批人',
    identityType: 'NORMAL_USER',
    dataScope: 'ORG',
    remark: `seed:${MARKER}`,
    pagePermissions: ['/workflow/my-approvals'],
    buttonPermissions: ['workflow.my-approvals:approve'],
    dataScopes: {
      'workflow.my-approvals': 'ORG',
    },
  },
];

const EMPLOYEE_DEFS = [
  {
    key: 'staff1',
    employeeCode: 'PAP-E001',
    username: 'phase_staff_01',
    realName: '阶段员工一',
    roleKeys: ['portalUser'],
    gender: 'FEMALE',
    orgKey: 'business',
    jobTitle: '客户经理',
    phone: '13900080001',
    email: 'phase.staff1@huacai.local',
    employmentStatus: 'ONBOARD',
    birthday: '1994-05-18',
    nation: '汉族',
    politicalStatus: '群众',
    hometown: '广东深圳',
    maritalStatus: 'MARRIED',
    graduateSchool: '深圳大学',
    highestEducation: '本科',
    workStartDate: '2021-03-08',
    homeAddress: `深圳市南山区演示路 1 号（${MARKER}）`,
    emergencyContact: '王女士',
    emergencyContactPhone: '13900089901',
    bankCardNo: '6222026051300000001',
    createSystemAccount: 1,
    jobInfo: {
      joinDate: '2024-02-01',
      formalDate: '2024-05-01',
      workUnit: '华彩系统',
      workMode: 'FULL_TIME',
      borrowDispatchDate: '2024-02-01',
      department: '业务一部',
      rankLevel: 'P2',
      jobCategory: '业务',
      position: '客户经理',
      sortNo: 1,
      is编制: 1,
    },
    certificates: [
      {
        certificateName: `客户服务资格证（${MARKER}）`,
        certificateNo: 'CERT-PAP-001',
        issueDate: '2024-03-10',
        certificateType: '职业资格',
        issueOrg: '华彩培训中心',
        isPermanent: 1,
        expireDate: '2030-03-09',
        certificateFileUrl: `seed://${MARKER}/employee/cert-001`,
      },
    ],
    assessments: [
      {
        assessmentMonth: '2026-03-01',
        assessmentScore: 92,
        assessmentGrade: 'A',
        remark: `月度考核良好（${MARKER}）`,
      },
    ],
    growthRecords: [
      {
        startDate: '2024-02-01',
        endDate: '2024-12-31',
        workName: `客户经营训练营（${MARKER}）`,
      },
    ],
    familyMembers: [
      {
        relation: '配偶',
        name: '陈先生',
        birthday: '1992-08-08',
        idCardNo: '440101199208080011',
        politicalStatus: '群众',
        workUnitPosition: '软件公司 / 产品经理',
      },
    ],
    contracts: [
      {
        contractName: `劳动合同（${MARKER}）`,
        contractNo: 'LAB-PAP-001',
        contractStartDate: '2024-02-01',
        contractEndDate: '2027-01-31',
        contractFileUrl: `seed://${MARKER}/employee/contract-001`,
        remark: `员工主合同（${MARKER}）`,
      },
    ],
    changes: [
      {
        currentDepartment: '业务一部',
        currentPosition: '客户经理',
        currentRankLevel: 'P2',
        originalDepartment: '业务支持组',
        originalPosition: '助理',
        originalRankLevel: 'P1',
        reportDate: '2025-01-15',
        changeType: 'PROMOTION',
        changeReason: `年度晋升（${MARKER}）`,
      },
    ],
    leaveRecord: {
      leaveType: 'ANNUAL',
      startDate: '2026-04-15',
      endDate: '2026-04-16',
      days: '2.0',
      reason: `年假出行（${MARKER}）`,
      status: 'APPROVED',
    },
  },
  {
    key: 'approver',
    employeeCode: 'PAP-E002',
    username: 'phase_approver_01',
    realName: '阶段审批人',
    roleKeys: ['approverPortal', 'deptLeader'],
    gender: 'MALE',
    orgKey: 'business',
    jobTitle: '业务主管',
    phone: '13900080002',
    email: 'phase.approver@huacai.local',
    employmentStatus: 'ONBOARD',
    birthday: '1990-09-12',
    nation: '汉族',
    politicalStatus: '党员',
    hometown: '湖南长沙',
    maritalStatus: 'MARRIED',
    graduateSchool: '中南大学',
    highestEducation: '硕士',
    workStartDate: '2018-06-01',
    homeAddress: `深圳市宝安区审批路 2 号（${MARKER}）`,
    emergencyContact: '刘女士',
    emergencyContactPhone: '13900089902',
    bankCardNo: '6222026051300000002',
    createSystemAccount: 1,
    jobInfo: {
      joinDate: '2023-08-01',
      formalDate: '2023-11-01',
      workUnit: '华彩系统',
      workMode: 'FULL_TIME',
      borrowDispatchDate: '2023-08-01',
      department: '业务一部',
      rankLevel: 'M1',
      jobCategory: '管理',
      position: '业务主管',
      sortNo: 2,
      is编制: 1,
    },
  },
  {
    key: 'staff2',
    employeeCode: 'PAP-E003',
    username: 'phase_staff_02',
    realName: '阶段员工二',
    roleKeys: ['portalUser'],
    gender: 'FEMALE',
    orgKey: 'support',
    jobTitle: '运营专员',
    phone: '13900080003',
    email: 'phase.staff2@huacai.local',
    employmentStatus: 'ONBOARD',
    birthday: '1997-02-21',
    nation: '汉族',
    politicalStatus: '群众',
    hometown: '湖北武汉',
    maritalStatus: 'UNMARRIED',
    graduateSchool: '华中师范大学',
    highestEducation: '本科',
    workStartDate: '2025-12-01',
    homeAddress: `深圳市福田区支持路 3 号（${MARKER}）`,
    emergencyContact: '周女士',
    emergencyContactPhone: '13900089903',
    bankCardNo: '6222026051300000003',
    createSystemAccount: 1,
    jobInfo: {
      joinDate: '2026-01-06',
      formalDate: '2026-04-06',
      workUnit: '华彩系统',
      workMode: 'FULL_TIME',
      borrowDispatchDate: '2026-01-06',
      department: '运营支持部',
      rankLevel: 'P1',
      jobCategory: '运营',
      position: '运营专员',
      sortNo: 3,
      is编制: 1,
    },
  },
];

const SALARY_STANDARD_DEFS = [
  {
    key: 'accountManager',
    salaryName: `客户经理标准（${MARKER}）`,
    amount: 9800,
    jobTitle: '客户经理',
    orgKey: 'business',
    description: `客户经理基础薪酬（${MARKER}）`,
    sortNo: 1,
    status: 'ENABLE',
    effectiveDate: '2026-01-01',
    expireDate: '2026-12-31',
    versionNo: 'V1',
    applicableScope: '业务一部 / 客户经理',
  },
  {
    key: 'teamLead',
    salaryName: `业务主管标准（${MARKER}）`,
    amount: 13800,
    jobTitle: '业务主管',
    orgKey: 'business',
    description: `业务主管基础薪酬（${MARKER}）`,
    sortNo: 2,
    status: 'ENABLE',
    effectiveDate: '2026-01-01',
    expireDate: '2026-12-31',
    versionNo: 'V1',
    applicableScope: '业务一部 / 业务主管',
  },
  {
    key: 'opsSpecialist',
    salaryName: `运营专员标准（${MARKER}）`,
    amount: 8600,
    jobTitle: '运营专员',
    orgKey: 'support',
    description: `运营专员基础薪酬（${MARKER}）`,
    sortNo: 3,
    status: 'ENABLE',
    effectiveDate: '2026-01-01',
    expireDate: '2026-12-31',
    versionNo: 'V1',
    applicableScope: '运营支持部 / 运营专员',
  },
];

const CUSTOMER_DEFS = [
  {
    key: 'noStart',
    customerNo: 'PAP-CUST-001',
    customerName: '阶段客户未借款',
    gender: 'FEMALE',
    idCard: '440101199405180021',
    birthday: '1994-05-18',
    mobile: '13800060001',
    companyName: `深圳启航服务有限公司（${MARKER}）`,
    creditCode: '91440300PAPCUST001',
    establishedDate: '2020-03-15',
    industry: '企业服务',
    businessAddress: '深圳市南山区科技园演示大道 1 号',
    bankName: '招商银行深圳科技园支行',
    bankAccount: '622588000000000001',
    recommenderName: '渠道顾问A',
    recommenderRate: 0.8,
    serviceFee: 5000,
    bizStatus: 'INIT',
    taxRegistrationNormal: true,
    riskRecords: [
      { testDate: '2026-03-01', testLimit: 120000, trafficValue: 180000, compositeScore: 88, thirdPartyScore: 91, remark: `客户画像稳定（${MARKER}）` },
    ],
    debtRecords: [
      { debtType: '信用卡', debtAmount: 80000, totalRepaymentAmount: 80000, advancePaidAmount: 12000, pendingAmount: 68000, installmentAmount: 4500, repaymentDay: 15, remark: `常规信用卡负债（${MARKER}）` },
    ],
    contractRecords: [
      { contractNo: 'PAP-HT-001', contractName: `档案服务协议（${MARKER}）`, signDate: '2026-03-05', remark: `客户档案合同（${MARKER}）` },
    ],
    statuses: [
      { code: 'PENDING', name: '待审核' },
      { code: 'APPROVED', name: '已通过' },
    ],
  },
  {
    key: 'running',
    customerNo: 'PAP-CUST-002',
    customerName: '阶段客户借款中',
    gender: 'MALE',
    idCard: '440101199009120031',
    birthday: '1990-09-12',
    mobile: '13800060002',
    companyName: `广州远望供应链有限公司（${MARKER}）`,
    creditCode: '91440101PAPCUST002',
    establishedDate: '2018-08-08',
    industry: '供应链',
    businessAddress: '广州市天河区珠江新城演示路 2 号',
    bankName: '中国银行广州天河支行',
    bankAccount: '621661000000000002',
    recommenderName: '渠道顾问B',
    recommenderRate: 1.2,
    serviceFee: 8500,
    bizStatus: 'FOLLOWING',
    taxRegistrationNormal: true,
    riskRecords: [
      { testDate: '2026-03-08', testLimit: 240000, trafficValue: 320000, compositeScore: 92, thirdPartyScore: 94, remark: `可持续放款（${MARKER}）` },
    ],
    debtRecords: [
      { debtType: '经营贷', debtAmount: 180000, totalRepaymentAmount: 180000, advancePaidAmount: 60000, pendingAmount: 120000, installmentAmount: 12000, repaymentDay: 18, remark: `经营贷在还（${MARKER}）` },
    ],
    contractRecords: [
      { contractNo: 'PAP-HT-002', contractName: `融资顾问协议（${MARKER}）`, signDate: '2026-03-10', remark: `借款中客户合同（${MARKER}）` },
    ],
    statuses: [
      { code: 'PENDING', name: '待审核' },
      { code: 'APPROVED', name: '已通过' },
    ],
  },
  {
    key: 'settled',
    customerNo: 'PAP-CUST-003',
    customerName: '阶段客户已结清',
    gender: 'FEMALE',
    idCard: '440101199702210041',
    birthday: '1997-02-21',
    mobile: '13800060003',
    companyName: `佛山卓越制造有限公司（${MARKER}）`,
    creditCode: '91440600PAPCUST003',
    establishedDate: '2016-06-16',
    industry: '制造业',
    businessAddress: '佛山市顺德区工业大道演示 3 号',
    bankName: '建设银行佛山顺德支行',
    bankAccount: '622700000000000003',
    recommenderName: '渠道顾问C',
    recommenderRate: 1.0,
    serviceFee: 9200,
    bizStatus: 'SIGNED',
    taxRegistrationNormal: true,
    riskRecords: [
      { testDate: '2026-02-18', testLimit: 300000, trafficValue: 360000, compositeScore: 95, thirdPartyScore: 96, remark: `历史表现优秀（${MARKER}）` },
    ],
    debtRecords: [
      { debtType: '设备贷', debtAmount: 120000, totalRepaymentAmount: 120000, advancePaidAmount: 120000, pendingAmount: 0, installmentAmount: 0, repaymentDay: 10, remark: `已全部结清（${MARKER}）` },
    ],
    contractRecords: [
      { contractNo: 'PAP-HT-003', contractName: `结清确认书（${MARKER}）`, signDate: '2026-02-20', remark: `已结清客户合同（${MARKER}）` },
    ],
    statuses: [
      { code: 'PENDING', name: '待审核' },
      { code: 'APPROVED', name: '已通过' },
    ],
  },
];

const LOAN_ORDER_DEFS = [
  {
    key: 'runningSelf',
    customerKey: 'running',
    capitalSourceType: 'SELF',
    bankName: '华彩内部资金池',
    loanDate: '2026-04-01',
    depositGoldAmount: 30000,
    creditCardRepaymentAmount: 25000,
    loanAmount: 150000,
    monthlyInterestAmount: 3200,
    loanCount: 12,
    remark: `我方借贷进行中（${MARKER}）`,
    summary: {
      totalIncrementAmount: 500000,
      incrementCount: 4,
      yearsTerm: '1年',
      channelRate: 0.8,
      channelFee: 1200,
      referrer: '渠道A',
      selfTotalLoanAmount: 150000,
      bankTotalLoanAmount: 0,
      remark: `我方客户汇总（${MARKER}）`,
    },
    repayments: [
      {
        repaymentDate: new Date().toISOString().slice(0, 10),
        repaymentAmount: 50000,
        principalAmount: 42000,
        interestAmount: 8000,
        repaymentChannel: 'BANK_TRANSFER',
        remark: `部分还款（${MARKER}）`,
      },
    ],
  },
  {
    key: 'runningBank',
    customerKey: 'running',
    capitalSourceType: 'BANK',
    bankName: '招商银行广州分行',
    loanDate: '2026-04-05',
    depositGoldAmount: 18000,
    creditCardRepaymentAmount: 0,
    loanAmount: 200000,
    monthlyInterestAmount: 2800,
    loanCount: 18,
    remark: `银行借贷进行中（${MARKER}）`,
    summary: {
      totalIncrementAmount: 420000,
      incrementCount: 3,
      yearsTerm: '18个月',
      channelRate: 0.65,
      channelFee: 900,
      referrer: '渠道B',
      selfTotalLoanAmount: 0,
      bankTotalLoanAmount: 200000,
      remark: `银行客户汇总（${MARKER}）`,
    },
    repayments: [
      {
        repaymentDate: '2026-05-10',
        repaymentAmount: 80000,
        principalAmount: 70000,
        interestAmount: 10000,
        repaymentChannel: 'BANK_TRANSFER',
        remark: `银行部分还款（${MARKER}）`,
      },
    ],
  },
  {
    key: 'settledSelf',
    customerKey: 'settled',
    capitalSourceType: 'SELF',
    bankName: '华彩内部资金池',
    loanDate: '2026-02-01',
    depositGoldAmount: 20000,
    creditCardRepaymentAmount: 18000,
    loanAmount: 90000,
    monthlyInterestAmount: 1600,
    loanCount: 6,
    remark: `我方借贷已结清（${MARKER}）`,
    summary: {
      totalIncrementAmount: 260000,
      incrementCount: 2,
      yearsTerm: '6个月',
      channelRate: 0.5,
      channelFee: 600,
      referrer: '渠道C',
      selfTotalLoanAmount: 90000,
      bankTotalLoanAmount: 0,
      remark: `我方结清汇总（${MARKER}）`,
    },
    repayments: [
      {
        repaymentDate: '2026-03-01',
        repaymentAmount: 90000,
        principalAmount: 90000,
        interestAmount: 0,
        repaymentChannel: 'BANK_TRANSFER',
        remark: `我方结清还款（${MARKER}）`,
      },
    ],
  },
  {
    key: 'settledBank',
    customerKey: 'settled',
    capitalSourceType: 'BANK',
    bankName: '建设银行佛山顺德支行',
    loanDate: '2026-02-10',
    depositGoldAmount: 15000,
    creditCardRepaymentAmount: 0,
    loanAmount: 120000,
    monthlyInterestAmount: 2100,
    loanCount: 9,
    remark: `银行借贷已结清（${MARKER}）`,
    summary: {
      totalIncrementAmount: 300000,
      incrementCount: 2,
      yearsTerm: '9个月',
      channelRate: 0.55,
      channelFee: 700,
      referrer: '渠道D',
      selfTotalLoanAmount: 0,
      bankTotalLoanAmount: 120000,
      remark: `银行结清汇总（${MARKER}）`,
    },
    repayments: [
      {
        repaymentDate: '2026-04-01',
        repaymentAmount: 120000,
        principalAmount: 120000,
        interestAmount: 0,
        repaymentChannel: 'BANK_TRANSFER',
        remark: `银行结清还款（${MARKER}）`,
      },
    ],
  },
];

const OPPORTUNITY_DEFS = [
  {
    key: 'opp1',
    customerKey: 'running',
    priorityLevel: 'HIGH',
    stageCode: 'NEGOTIATION',
    ownerEmployeeKey: 'approver',
    estimatedAmount: 280000,
    intentLevel: 'HIGH',
    status: 'OPEN',
    nextFollowTime: '2026-05-20T10:00:00',
    remark: `重点推进商机（${MARKER}）`,
    followRecords: [
      {
        followTime: '2026-05-11T09:30:00',
        followerName: '阶段审批人',
        followContent: `已与客户确认补件计划（${MARKER}）`,
        nextAction: '安排银行复核',
        remark: `首次跟进（${MARKER}）`,
      },
    ],
  },
  {
    key: 'opp2',
    customerKey: 'noStart',
    priorityLevel: 'MEDIUM',
    stageCode: 'CONTACTED',
    ownerEmployeeKey: 'staff1',
    estimatedAmount: 160000,
    intentLevel: 'MEDIUM',
    status: 'OPEN',
    nextFollowTime: '2026-05-18T14:00:00',
    remark: `初步接触商机（${MARKER}）`,
    followRecords: [
      {
        followTime: '2026-05-09T14:20:00',
        followerName: '阶段员工一',
        followContent: `首次电话沟通完成（${MARKER}）`,
        nextAction: '补充资料并预约面访',
        remark: `电话跟进（${MARKER}）`,
      },
    ],
  },
];

const INCREMENT_SUMMARY_DEF = {
  companyName: `华彩增量样本公司（${MARKER}）`,
  businessAddress: '深圳市南山区经营分析路 9 号',
  industry: '企业服务',
  startDate: '2026-01-01',
  janAmount: 120000,
  febAmount: 135000,
  marAmount: 148000,
  aprAmount: 152000,
  mayAmount: 160000,
  junAmount: 0,
  julAmount: 0,
  augAmount: 0,
  sepAmount: 0,
  octAmount: 0,
  novAmount: 0,
  decAmount: 0,
  remark: `增量总览样本（${MARKER}）`,
};

const INCREMENT_DETAIL_DEFS = [
  {
    key: 'inc1',
    customerKey: 'running',
    customerName: '阶段客户借款中',
    incrementDate: '2026-05-12',
    totalAmount: 68000,
    businessAddress: '广州市天河区珠江新城演示路 2 号',
    industry: '供应链',
    dailyCount: 2,
    remark: `增量详情一（${MARKER}）`,
    dailyRecords: [
      { incrementAmount: 30000, channelRate: 0.8, channelFee: 240, seqNo: 1, remark: `日增量一（${MARKER}）` },
      { incrementAmount: 38000, channelRate: 0.8, channelFee: 304, seqNo: 2, remark: `日增量二（${MARKER}）` },
    ],
  },
  {
    key: 'inc2',
    customerKey: 'settled',
    customerName: '阶段客户已结清',
    incrementDate: '2026-05-06',
    totalAmount: 42000,
    businessAddress: '佛山市顺德区工业大道演示 3 号',
    industry: '制造业',
    dailyCount: 1,
    remark: `增量详情二（${MARKER}）`,
    dailyRecords: [
      { incrementAmount: 42000, channelRate: 0.55, channelFee: 231, seqNo: 1, remark: `日增量三（${MARKER}）` },
    ],
  },
];

const PERFORMANCE_DEFS = [
  {
    employeeKey: 'staff1',
    periodDate: '2026-05',
    targetAmount: 300000,
    actualAmount: 240000,
    dealAmount: 190000,
    dealCount: 3,
    remark: `绩效样本一（${MARKER}）`,
  },
  {
    employeeKey: 'staff2',
    periodDate: '2026-05',
    targetAmount: 180000,
    actualAmount: 150000,
    dealAmount: 110000,
    dealCount: 2,
    remark: `绩效样本二（${MARKER}）`,
  },
];

const PAYEE_DEFS = [
  {
    payeeName: `渠道服务商A（${MARKER}）`,
    payeeType: 'SUPPLIER',
    bankName: '招商银行深圳分行',
    bankAccount: '622588000000009001',
    contactName: '赵主管',
    contactPhone: '13811110001',
    status: 'ENABLE',
    remark: `收款方样本 A（${MARKER}）`,
  },
  {
    payeeName: `外部顾问B（${MARKER}）`,
    payeeType: 'PERSON',
    bankName: '平安银行广州分行',
    bankAccount: '622155000000009002',
    contactName: '周顾问',
    contactPhone: '13811110002',
    status: 'ENABLE',
    remark: `收款方样本 B（${MARKER}）`,
  },
];

const INCOME_DEFS = [
  {
    incomeName: `服务费回款一（${MARKER}）`,
    incomeType: 'SERVICE_FEE',
    bizDate: '2026-05-11',
    amount: 68000,
    payerName: `深圳启航服务有限公司（${MARKER}）`,
    remark: `收入样本一（${MARKER}）`,
  },
  {
    incomeName: `顾问费回款二（${MARKER}）`,
    incomeType: 'CONSULTING',
    bizDate: '2026-05-08',
    amount: 42000,
    payerName: `广州远望供应链有限公司（${MARKER}）`,
    remark: `收入样本二（${MARKER}）`,
  },
];

const EXPENSE_DEFS = [
  {
    expenseName: `渠道服务费一（${MARKER}）`,
    expenseType: 'CHANNEL',
    bizDate: '2026-05-09',
    amount: 18000,
    payeeKey: 0,
    remark: `支出样本一（${MARKER}）`,
  },
  {
    expenseName: `差旅补贴二（${MARKER}）`,
    expenseType: 'TRAVEL',
    bizDate: '2026-05-07',
    amount: 6200,
    payeeKey: 1,
    remark: `支出样本二（${MARKER}）`,
  },
];

const report = {
  marker: MARKER,
  executedAt: new Date().toISOString(),
  localOnly: true,
  commands: [
    'bash scripts/dev/apply-local-migrations.sh',
    'node scripts/dev/seed-phase-all-pages.mjs',
    `sql/migration/V010_expand_operation_log_columns.sql`,
    `sql/migration/V011_all_pages_schema_compat.sql`,
  ],
  accounts: [],
  orgs: [],
  roles: [],
  customers: [],
  loanOrders: [],
  repayments: [],
  opportunities: [],
  incrementSummaries: [],
  incrementDetails: [],
  dailyIncrements: [],
  employeePerformances: [],
  employees: [],
  salaryStandards: [],
  workflows: {
    transition: null,
    salaryAdjust: null,
    reimbursements: [],
  },
  finance: {
    payees: [],
    incomes: [],
    expenses: [],
  },
  system: {
    operationLogs: [],
  },
  coverage: [],
};

function log(message) {
  console.log(`[phase-all-pages] ${message}`);
}

function sqlEscape(value) {
  if (value == null) {
    return 'NULL';
  }
  return `'${String(value).replaceAll('\\', '\\\\').replaceAll("'", "\\'")}'`;
}

function buildUrl(pathname, query = {}) {
  const base = API_BASE.endsWith('/') ? API_BASE : `${API_BASE}/`;
  const url = new URL(pathname.replace(/^\//, ''), base);
  for (const [key, value] of Object.entries(query)) {
    if (value === undefined || value === null || value === '') continue;
    url.searchParams.set(key, String(value));
  }
  return url;
}

async function apiRequest(method, pathname, { token, query, body, formData } = {}) {
  const headers = {};
  let payload;

  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }
  if (formData) {
    payload = formData;
  } else if (body !== undefined) {
    headers['Content-Type'] = 'application/json';
    payload = JSON.stringify(body);
  }

  const response = await fetch(buildUrl(pathname, query), {
    method,
    headers,
    body: payload,
  });
  const text = await response.text();
  let parsed = null;
  if (text) {
    try {
      parsed = JSON.parse(text);
    } catch {
      parsed = text;
    }
  }
  if (!response.ok) {
    const message = parsed?.message ?? parsed?.error ?? response.statusText;
    throw new Error(`${method} ${pathname} failed: ${message}`);
  }
  if (parsed && typeof parsed === 'object' && 'code' in parsed) {
    if (parsed.code !== 0) {
      throw new Error(`${method} ${pathname} failed: ${parsed.message ?? 'unknown error'}`);
    }
    return parsed.data;
  }
  return parsed;
}

const apiGet = (pathname, token, query) => apiRequest('GET', pathname, { token, query });
const apiPost = (pathname, token, body) => apiRequest('POST', pathname, { token, body });
const apiPut = (pathname, token, body) => apiRequest('PUT', pathname, { token, body });

async function login(username, password) {
  const result = await apiPost('/auth/login', null, { username, password });
  return { token: result.token, userInfo: result.userInfo };
}

function mysqlExec(sql) {
  return execFileSync(
    'docker',
    [
      'exec',
      '-i',
      MYSQL_CONTAINER,
      'mysql',
      '-h127.0.0.1',
      `-u${MYSQL_USER}`,
      `-p${MYSQL_PASSWORD}`,
      MYSQL_DATABASE,
    ],
    {
      input: sql,
      encoding: 'utf8',
      stdio: ['pipe', 'pipe', 'pipe'],
    },
  );
}

function mysqlQuery(sql) {
  const output = execFileSync(
    'docker',
    [
      'exec',
      '-i',
      MYSQL_CONTAINER,
      'mysql',
      '-h127.0.0.1',
      `-u${MYSQL_USER}`,
      `-p${MYSQL_PASSWORD}`,
      MYSQL_DATABASE,
      '-N',
      '-B',
      '-e',
      sql,
    ],
    {
      encoding: 'utf8',
      stdio: ['ignore', 'pipe', 'pipe'],
    },
  );
  return output.trim();
}

function mysqlScalar(sql) {
  return mysqlQuery(sql).split('\n').find(Boolean) ?? '';
}

function sleep(ms) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

function runLocalMigrations() {
  execFileSync('bash', [path.join(ROOT_DIR, 'scripts/dev/apply-local-migrations.sh')], {
    encoding: 'utf8',
    stdio: 'inherit',
  });
}

function flattenOrgs(nodes, bucket = []) {
  for (const node of nodes ?? []) {
    bucket.push(node);
    flattenOrgs(node.children ?? [], bucket);
  }
  return bucket;
}

async function fetchOrgTree(token) {
  return apiGet('/system/orgs/tree', token);
}

async function ensureOrg(token, definition) {
  const rootTree = await fetchOrgTree(token);
  const allOrgs = flattenOrgs(rootTree);
  let found = allOrgs.find((item) => item.orgName === definition.orgName);
  if (!found) {
    await apiPost('/system/orgs', token, {
      parentId: 1,
      orgName: definition.orgName,
      orgType: definition.orgType,
      sortNo: definition.sortNo,
      status: 'ENABLE',
      remark: definition.remark,
    });
    found = flattenOrgs(await fetchOrgTree(token)).find((item) => item.orgName === definition.orgName);
  } else {
    await apiPut(`/system/orgs/${found.id}`, token, {
      parentId: found.parentId ?? 1,
      orgName: definition.orgName,
      orgType: definition.orgType,
      sortNo: definition.sortNo,
      status: 'ENABLE',
      remark: definition.remark,
    });
    if (found.status !== 'ENABLE') {
      await apiPut(`/system/orgs/${found.id}/status`, token, { status: 'ENABLE' });
    }
    found = flattenOrgs(await fetchOrgTree(token)).find((item) => item.id === found.id);
  }
  report.orgs.push({
    id: found.id,
    orgName: found.orgName,
    parentId: found.parentId,
    stableId: definition.key,
  });
  return found;
}

async function fetchRolePage(token, keyword = '') {
  return apiGet('/system/roles', token, { pageNum: 1, pageSize: 200, keyword });
}

async function findRoleByCode(token, roleCode) {
  const page = await fetchRolePage(token, roleCode);
  return page.records.find((item) => item.roleCode === roleCode) ?? null;
}

async function ensureRole(token, definition) {
  let role = await findRoleByCode(token, definition.roleCode);
  if (!role) {
    await apiPost('/system/roles', token, {
      roleCode: definition.roleCode,
      roleName: definition.roleName,
      identityType: definition.identityType,
      dataScope: definition.dataScope,
      status: 'ENABLE',
      remark: definition.remark,
    });
    role = await findRoleByCode(token, definition.roleCode);
  } else {
    await apiPut(`/system/roles/${role.id}`, token, {
      roleCode: definition.roleCode,
      roleName: definition.roleName,
      identityType: definition.identityType,
      dataScope: definition.dataScope,
      status: 'ENABLE',
      remark: definition.remark,
    });
    if (role.status !== 'ENABLE') {
      await apiPut(`/system/roles/${role.id}/status`, token, { status: 'ENABLE' });
    }
    role = await findRoleByCode(token, definition.roleCode);
  }

  await apiPut(`/system/roles/${role.id}/permissions`, token, {
    pagePermissions: definition.pagePermissions,
    buttonPermissions: definition.buttonPermissions,
    dataScopes: definition.dataScopes,
  });

  report.roles.push({
    id: role.id,
    roleCode: role.roleCode,
    roleName: role.roleName,
    identityType: role.identityType,
    stableId: definition.key,
  });
  return role;
}

async function fetchEmployeePage(token, keyword = '') {
  return apiGet('/hr/employees', token, { pageNum: 1, pageSize: 200, keyword });
}

async function findEmployeeByCode(token, employeeCode) {
  const page = await fetchEmployeePage(token, employeeCode);
  return page.records.find((item) => item.employeeCode === employeeCode) ?? null;
}

async function findUserByUsername(token, username) {
  const page = await apiGet('/system/users', token, { pageNum: 1, pageSize: 200, keyword: username });
  return page.records.find((item) => item.username === username) ?? null;
}

async function ensureEmployee(token, orgMap, definition) {
  let employee = await findEmployeeByCode(token, definition.employeeCode);
  const payload = {
    employeeCode: definition.employeeCode,
    realName: definition.realName,
    gender: definition.gender,
    idCardNo: definition.idCardNo ?? definition.idCard ?? '',
    birthday: definition.birthday,
    nation: definition.nation,
    politicalStatus: definition.politicalStatus,
    hometown: definition.hometown,
    maritalStatus: definition.maritalStatus,
    phone: definition.phone,
    email: definition.email,
    graduateSchool: definition.graduateSchool,
    highestEducation: definition.highestEducation,
    workStartDate: definition.workStartDate,
    homeAddress: definition.homeAddress,
    emergencyContact: definition.emergencyContact,
    emergencyContactPhone: definition.emergencyContactPhone,
    bankCardNo: definition.bankCardNo,
    employmentStatus: definition.employmentStatus,
    createSystemAccount: definition.createSystemAccount,
    systemUsername: definition.username,
    systemPasswordPlain: DEMO_PASSWORD,
    orgId: orgMap[definition.orgKey].id,
    jobTitle: definition.jobTitle,
    remark: `seed:${MARKER}`,
  };

  if (!employee) {
    await apiPost('/hr/employees', token, payload);
  } else {
    await apiPut(`/hr/employees/${employee.id}`, token, {
      ...payload,
      systemPasswordPlain: undefined,
    });
  }

  employee = await findEmployeeByCode(token, definition.employeeCode);
  if (!employee) {
    throw new Error(`employee ${definition.employeeCode} not found after ensure`);
  }

  await apiPost('/hr/job-info', token, {
    employeeId: employee.id,
    employeeCode: definition.employeeCode,
    joinDate: definition.jobInfo.joinDate,
    formalDate: definition.jobInfo.formalDate,
    workUnit: definition.jobInfo.workUnit,
    workMode: definition.jobInfo.workMode,
    borrowDispatchDate: definition.jobInfo.borrowDispatchDate,
    department: definition.jobInfo.department,
    rankLevel: definition.jobInfo.rankLevel,
    jobCategory: definition.jobInfo.jobCategory,
    position: definition.jobInfo.position,
    sortNo: definition.jobInfo.sortNo,
    is编制: definition.jobInfo.is编制,
  });

  let detail = await apiGet(`/hr/employees/${employee.id}`, token);

  for (const certificate of definition.certificates ?? []) {
    const existing = detail.certificates.find((item) => item.certificateName === certificate.certificateName);
    await apiPost('/hr/certificates', token, { ...certificate, id: existing?.id, employeeId: employee.id });
  }

  detail = await apiGet(`/hr/employees/${employee.id}`, token);
  for (const assessment of definition.assessments ?? []) {
    const existing = detail.assessments.find((item) => item.remark === assessment.remark);
    await apiPost('/hr/assessments', token, { ...assessment, id: existing?.id, employeeId: employee.id });
  }

  detail = await apiGet(`/hr/employees/${employee.id}`, token);
  for (const growth of definition.growthRecords ?? []) {
    const existing = detail.growthRecords.find((item) => item.workName === growth.workName);
    await apiPost('/hr/growth', token, { ...growth, id: existing?.id, employeeId: employee.id });
  }

  detail = await apiGet(`/hr/employees/${employee.id}`, token);
  for (const family of definition.familyMembers ?? []) {
    const existing = detail.familyMembers.find((item) => item.name === family.name && item.relation === family.relation);
    await apiPost('/hr/family', token, { ...family, id: existing?.id, employeeId: employee.id });
  }

  detail = await apiGet(`/hr/employees/${employee.id}`, token);
  for (const contract of definition.contracts ?? []) {
    const existing = detail.contracts.find((item) => item.contractNo === contract.contractNo);
    await apiPost('/hr/contracts', token, { ...contract, id: existing?.id, employeeId: employee.id });
  }

  detail = await apiGet(`/hr/employees/${employee.id}`, token);
  for (const change of definition.changes ?? []) {
    const existing = detail.changeRecords.find((item) => item.changeReason === change.changeReason);
    await apiPost('/hr/changes', token, { ...change, id: existing?.id, employeeId: employee.id });
  }

  detail = await apiGet(`/hr/employees/${employee.id}`, token);
  report.employees.push({
    id: detail.id,
    employeeCode: detail.employeeCode,
    realName: detail.realName,
    orgId: detail.orgId,
    orgName: detail.orgName,
    username: definition.username,
    stableId: definition.key,
  });
  return detail;
}

async function syncUserAccount(token, definition, employeeDetail, roleIds, primaryRoleId) {
  let user = await findUserByUsername(token, definition.username);
  if (!user) {
    throw new Error(`user ${definition.username} not found after employee create`);
  }

  await apiPut(`/system/users/${user.id}`, token, {
    username: definition.username,
    employeeCode: definition.employeeCode,
    realName: definition.realName,
    phone: definition.phone,
    email: definition.email,
    orgId: employeeDetail.orgId,
    jobTitle: definition.jobTitle,
    employmentStatus: 'ON_JOB',
    accountStatus: 'ENABLE',
    remark: `seed:${MARKER}`,
    primaryRoleId,
  });
  await apiPut(`/system/users/${user.id}/roles`, token, { roleIds: [...roleIds].sort((a, b) => a - b) });
  ensureUserRoleLinks(user.id, roleIds, primaryRoleId);

  user = await findUserByUsername(token, definition.username);
  report.accounts.push({
    username: user.username,
    realName: user.realName,
    password: DEMO_PASSWORD,
    userId: user.id,
    employeeCode: definition.employeeCode,
    orgName: user.orgName,
    primaryRoleName: user.primaryRoleName,
    stableId: definition.key,
  });
  return user;
}

function ensureUserRoleLinks(userId, roleIds, primaryRoleId) {
  const normalizedRoleIds = [...new Set(roleIds)].sort((a, b) => a - b);
  if (normalizedRoleIds.length === 0) {
    mysqlExec(`DELETE FROM sys_user_role WHERE user_id = ${userId};`);
    return;
  }
  const values = normalizedRoleIds.map((roleId) => `(${userId}, ${roleId}, 1, NOW())`).join(',\n      ');
  mysqlExec(`
    DELETE FROM sys_user_role WHERE user_id = ${userId};
    INSERT INTO sys_user_role (user_id, role_id, created_by, created_at)
    VALUES
      ${values};
    UPDATE sys_user
    SET identity_type = (
      SELECT identity_type
      FROM sys_role
      WHERE id = ${primaryRoleId}
    ),
        updated_by = 1
    WHERE id = ${userId};
  `);
}

async function fetchSalaryStandards(token, keyword = '') {
  return apiGet('/hr/salaries', token, { pageNum: 1, pageSize: 200, keyword });
}

async function ensureSalaryStandard(token, orgMap, definition) {
  const page = await fetchSalaryStandards(token, definition.salaryName);
  let found = page.records.find((item) => item.salaryName === definition.salaryName) ?? null;
  const payload = {
    salaryName: definition.salaryName,
    amount: definition.amount,
    jobTitle: definition.jobTitle,
    orgId: orgMap[definition.orgKey].id,
    orgName: orgMap[definition.orgKey].orgName,
    description: definition.description,
    sortNo: definition.sortNo,
    status: definition.status,
    effectiveDate: definition.effectiveDate,
    expireDate: definition.expireDate,
    versionNo: definition.versionNo,
    applicableScope: definition.applicableScope,
  };

  if (!found) {
    await apiPost('/hr/salaries', token, payload);
  } else {
    await apiPut(`/hr/salaries/${found.id}`, token, payload);
  }

  found = (await fetchSalaryStandards(token, definition.salaryName)).records.find((item) => item.salaryName === definition.salaryName);
  report.salaryStandards.push({
    id: found.id,
    salaryName: found.salaryName,
    amount: found.amount,
    stableId: definition.key,
  });
  return found;
}

function ensureEmployeeBaselineSalary(employeeId, salaryStandardId, salaryName, amount, reason) {
  const existingId = mysqlQuery(`
    SELECT id
    FROM hr_employee_salary
    WHERE employee_id = ${employeeId}
      AND change_reason = ${sqlEscape(reason)}
      AND deleted_flag = 0
    LIMIT 1;
  `);
  if (existingId) {
    mysqlExec(`
      UPDATE hr_employee_salary
      SET salary_standard_id = ${salaryStandardId},
          salary_name = ${sqlEscape(salaryName)},
          amount = ${amount},
          effective_date = '2026-01-01',
          expire_date = NULL,
          updated_by = 1
      WHERE id = ${existingId};
    `);
    return Number(existingId);
  }

  mysqlExec(`
    INSERT INTO hr_employee_salary (
      employee_id, salary_standard_id, salary_name, amount, effective_date, expire_date, change_reason, created_by, updated_by
    ) VALUES (
      ${employeeId}, ${salaryStandardId}, ${sqlEscape(salaryName)}, ${amount}, '2026-01-01', NULL, ${sqlEscape(reason)}, 1, 1
    );
  `);
  return Number(mysqlQuery('SELECT LAST_INSERT_ID();'));
}

function ensureLeaveRecord(employeeId, applicantId, applicantName, definition) {
  const existingId = mysqlQuery(`
    SELECT id
    FROM hr_leave_record
    WHERE employee_id = ${employeeId}
      AND reason = ${sqlEscape(definition.reason)}
      AND deleted_flag = 0
    LIMIT 1;
  `);
  if (existingId) {
    mysqlExec(`
      UPDATE hr_leave_record
      SET leave_type = ${sqlEscape(definition.leaveType)},
          start_date = ${sqlEscape(definition.startDate)},
          end_date = ${sqlEscape(definition.endDate)},
          days = ${definition.days},
          status = ${sqlEscape(definition.status)},
          applicant_id = ${applicantId},
          applicant_name = ${sqlEscape(applicantName)},
          apply_time = NOW(),
          updated_by = 1
      WHERE id = ${existingId};
    `);
    return Number(existingId);
  }

  mysqlExec(`
    INSERT INTO hr_leave_record (
      employee_id, leave_type, start_date, end_date, days, reason, status, applicant_id, applicant_name, apply_time, created_by, updated_by
    ) VALUES (
      ${employeeId}, ${sqlEscape(definition.leaveType)}, ${sqlEscape(definition.startDate)}, ${sqlEscape(definition.endDate)},
      ${definition.days}, ${sqlEscape(definition.reason)}, ${sqlEscape(definition.status)},
      ${applicantId}, ${sqlEscape(applicantName)}, NOW(), 1, 1
    );
  `);
  return Number(mysqlQuery('SELECT LAST_INSERT_ID();'));
}

async function fetchCustomerPage(token, query = {}) {
  return apiGet('/customers', token, { pageNum: 1, pageSize: 200, ...query });
}

async function findCustomerByNo(token, customerNo) {
  const page = await fetchCustomerPage(token, { keyword: customerNo });
  return page.records.find((item) => item.customerNo === customerNo) ?? null;
}

async function ensureCustomerArchive(token, definition) {
  let customer = await findCustomerByNo(token, definition.customerNo);
  const payload = {
    customerNo: definition.customerNo,
    customerName: definition.customerName,
    gender: definition.gender,
    idCard: definition.idCard,
    birthday: definition.birthday,
    mobile: definition.mobile,
    companyName: definition.companyName,
    creditCode: definition.creditCode,
    establishedDate: definition.establishedDate,
    industry: definition.industry,
    businessAddress: definition.businessAddress,
    bankName: definition.bankName,
    bankAccount: definition.bankAccount,
    recommenderName: definition.recommenderName,
    recommenderRate: definition.recommenderRate,
    serviceFee: definition.serviceFee,
    bizStatus: definition.bizStatus,
    taxRegistrationNormal: definition.taxRegistrationNormal,
    archiveFileIds: [],
    riskRecords: definition.riskRecords,
    debtRecords: definition.debtRecords,
    contractRecords: definition.contractRecords.map((item) => ({
      customerName: definition.customerName,
      companyName: definition.companyName,
      creditCode: definition.creditCode,
      contractNo: item.contractNo,
      contractName: item.contractName,
      signDate: item.signDate,
      remark: item.remark,
      fileIds: [],
    })),
  };

  if (!customer) {
    await apiPost('/customers/archive', token, payload);
  } else {
    await apiPut(`/customers/${customer.id}/archive`, token, payload);
  }
  customer = await findCustomerByNo(token, definition.customerNo);
  if (!customer) {
    throw new Error(`customer ${definition.customerNo} not found after ensure`);
  }

  const statusLogs = await apiGet(`/customers/${customer.id}/status-logs`, token);
  const existingStatuses = new Set(statusLogs.map((item) => `${item.statusCode}:${item.statusName}`));
  for (const status of definition.statuses) {
    const key = `${status.code}:${status.name}`;
    if (!existingStatuses.has(key)) {
      await apiPut(`/customers/${customer.id}/status`, token, { status: status.code, statusName: status.name });
    }
  }

  const archive = await apiGet(`/customers/${customer.id}/archive`, token);
  report.customers.push({
    id: archive.id,
    customerNo: archive.customerNo,
    customerName: archive.customerName,
    auditStatus: archive.auditStatus,
    bizStatus: archive.bizStatus,
    loanStatus: archive.loanStatus,
    stableId: definition.key,
  });
  return archive;
}

async function fetchLoanOrders(token, query) {
  return apiGet('/loan-orders', token, { pageNum: 1, pageSize: 200, ...query });
}

async function ensureLoanOrder(token, customerMap, definition) {
  const customer = customerMap[definition.customerKey];
  let page = await fetchLoanOrders(token, { customerId: customer.id, capitalSourceType: definition.capitalSourceType });
  let found = page.records.find((item) => item.remark === definition.remark) ?? null;
  const payload = {
    customerId: customer.id,
    capitalSourceType: definition.capitalSourceType,
    bankName: definition.bankName,
    loanDate: definition.loanDate,
    depositGoldAmount: definition.depositGoldAmount,
    creditCardRepaymentAmount: definition.creditCardRepaymentAmount,
    loanAmount: definition.loanAmount,
    monthlyInterestAmount: definition.monthlyInterestAmount,
    loanCount: definition.loanCount,
    remark: definition.remark,
  };

  if (!found) {
    await apiPost('/loan-orders', token, payload);
  } else {
    await apiPut(`/loan-orders/${found.id}`, token, payload);
  }
  page = await fetchLoanOrders(token, { customerId: customer.id, capitalSourceType: definition.capitalSourceType });
  found = page.records.find((item) => item.remark === definition.remark);

  await apiPost('/loan-orders/customer-summary', token, {
    customerId: customer.id,
    capitalSourceType: definition.capitalSourceType,
    totalIncrementAmount: definition.summary.totalIncrementAmount,
    incrementCount: definition.summary.incrementCount,
    yearsTerm: definition.summary.yearsTerm,
    channelRate: definition.summary.channelRate,
    channelFee: definition.summary.channelFee,
    referrer: definition.summary.referrer,
    selfTotalLoanAmount: definition.summary.selfTotalLoanAmount,
    bankTotalLoanAmount: definition.summary.bankTotalLoanAmount,
    remark: definition.summary.remark,
  });

  for (const repayment of definition.repayments) {
    await ensureRepayment(token, found.id, repayment);
  }

  const detail = await apiGet(`/loan-orders/${found.id}`, token);
  report.loanOrders.push({
    id: detail.id,
    customerId: detail.customerId,
    customerName: detail.customerName,
    capitalSourceType: detail.capitalSourceType,
    status: detail.status,
    remark: detail.remark,
    stableId: definition.key,
  });
  return detail;
}

async function fetchRepayments(token, query) {
  return apiGet('/repayments', token, { pageNum: 1, pageSize: 200, ...query });
}

async function ensureRepayment(token, loanOrderId, definition) {
  let page = await fetchRepayments(token, { loanOrderId });
  let found = page.records.find((item) => item.remark === definition.remark) ?? null;
  const payload = {
    loanOrderId,
    repaymentDate: definition.repaymentDate,
    repaymentAmount: definition.repaymentAmount,
    principalAmount: definition.principalAmount,
    interestAmount: definition.interestAmount,
    repaymentChannel: definition.repaymentChannel,
    remark: definition.remark,
  };
  if (!found) {
    await apiPost('/repayments', token, payload);
  } else {
    await apiPut(`/repayments/${found.id}`, token, payload);
  }
  page = await fetchRepayments(token, { loanOrderId });
  found = page.records.find((item) => item.remark === definition.remark);
  report.repayments.push({
    id: found.id,
    loanOrderId: found.loanOrderId,
    customerId: found.customerId,
    customerName: found.customerName,
    capitalSourceType: found.capitalSourceType,
    repaymentAmount: found.repaymentAmount,
    remark: found.remark,
  });
  return found;
}

async function fetchOpportunities(token, query = {}) {
  return apiGet('/opportunities', token, { pageNum: 1, pageSize: 200, ...query });
}

async function ensureOpportunity(token, customerMap, userMap, definition) {
  const customer = customerMap[definition.customerKey];
  const owner = userMap[definition.ownerEmployeeKey];
  let page = await fetchOpportunities(token, { customerId: customer.id });
  let found = page.records.find((item) => item.remark === definition.remark) ?? null;
  const payload = {
    customerId: customer.id,
    priorityLevel: definition.priorityLevel,
    stageCode: definition.stageCode,
    ownerUserId: owner.id,
    estimatedAmount: definition.estimatedAmount,
    intentLevel: definition.intentLevel,
    status: definition.status,
    nextFollowTime: definition.nextFollowTime,
    remark: definition.remark,
  };
  if (!found) {
    await apiPost('/opportunities', token, payload);
  } else {
    await apiPut(`/opportunities/${found.id}`, token, payload);
  }
  page = await fetchOpportunities(token, { customerId: customer.id });
  found = page.records.find((item) => item.remark === definition.remark);

  for (const follow of definition.followRecords) {
    await ensureFollowRecord(token, found.id, follow);
  }

  const detail = await apiGet(`/opportunities/${found.id}`, token);
  report.opportunities.push({
    id: detail.id,
    customerId: detail.customerId,
    customerName: detail.customerName,
    stageCode: detail.stageCode,
    status: detail.status,
    ownerUserId: detail.ownerUserId,
    stableId: definition.key,
  });
  return detail;
}

async function ensureFollowRecord(token, opportunityId, definition) {
  let page = await apiGet('/opportunities/follow-records', token, { pageNum: 1, pageSize: 200, opportunityId });
  let found = page.records.find((item) => item.remark === definition.remark) ?? null;
  const payload = {
    opportunityId,
    followTime: definition.followTime,
    followerName: definition.followerName,
    followContent: definition.followContent,
    nextAction: definition.nextAction,
    remark: definition.remark,
  };
  if (!found) {
    await apiPost('/opportunities/follow-records', token, payload);
  } else {
    await apiPut(`/opportunities/follow-records/${found.id}`, token, payload);
  }
}

async function ensureIncrementSummary(token, definition) {
  let page = await apiGet('/analysis/increment-summary', token, { pageNum: 1, pageSize: 200, keyword: definition.companyName });
  let found = page.records.find((item) => item.companyName === definition.companyName) ?? null;
  if (!found) {
    await apiPost('/analysis/increment-summary', token, definition);
  } else {
    await apiPut(`/analysis/increment-summary/${found.id}`, token, definition);
  }
  page = await apiGet('/analysis/increment-summary', token, { pageNum: 1, pageSize: 200, keyword: definition.companyName });
  found = page.records.find((item) => item.companyName === definition.companyName);
  report.incrementSummaries.push({
    id: found.id,
    companyName: found.companyName,
    stableId: MARKER,
  });
  return found;
}

async function ensureIncrementDetail(token, customerMap, definition) {
  const customer = customerMap[definition.customerKey];
  let page = await apiGet('/analysis/increment-detail', token, { pageNum: 1, pageSize: 200, customerId: customer.id });
  let found = page.records.find((item) => item.remark === definition.remark) ?? null;
  const payload = {
    customerId: customer.id,
    customerName: definition.customerName,
    incrementDate: definition.incrementDate,
    totalAmount: definition.totalAmount,
    businessAddress: definition.businessAddress,
    industry: definition.industry,
    dailyCount: definition.dailyCount,
    remark: definition.remark,
  };
  if (!found) {
    await apiPost('/analysis/increment-detail', token, payload);
  } else {
    await apiPut(`/analysis/increment-detail/${found.id}`, token, payload);
  }
  page = await apiGet('/analysis/increment-detail', token, { pageNum: 1, pageSize: 200, customerId: customer.id });
  found = page.records.find((item) => item.remark === definition.remark);
  report.incrementDetails.push({
    id: found.id,
    customerId: found.customerId,
    customerName: found.customerName,
    stableId: definition.key,
  });
  for (const daily of definition.dailyRecords) {
    await ensureDailyIncrement(token, found.id, definition, daily);
  }
  return found;
}

async function ensureDailyIncrement(token, detailId, detailDefinition, definition) {
  let page = await apiGet('/analysis/daily-increment', token, { pageNum: 1, pageSize: 200, detailId });
  let found = page.records.find((item) => item.remark === definition.remark) ?? null;
  const payload = {
    detailId,
    customerName: detailDefinition.customerName,
    incrementDate: detailDefinition.incrementDate,
    incrementAmount: definition.incrementAmount,
    channelRate: definition.channelRate,
    channelFee: definition.channelFee,
    seqNo: definition.seqNo,
    remark: definition.remark,
  };
  if (!found) {
    await apiPost('/analysis/daily-increment', token, payload);
  } else {
    await apiPut(`/analysis/daily-increment/${found.id}`, token, payload);
  }
  page = await apiGet('/analysis/daily-increment', token, { pageNum: 1, pageSize: 200, detailId });
  found = page.records.find((item) => item.remark === definition.remark);
  report.dailyIncrements.push({
    id: found.id,
    detailId: found.detailId,
    customerName: found.customerName,
    stableId: definition.remark,
  });
  return found;
}

async function ensureEmployeePerformance(token, employeeMap, definition) {
  const employee = employeeMap[definition.employeeKey];
  let page = await apiGet('/analysis/employee-performance', token, {
    pageNum: 1,
    pageSize: 200,
    employeeId: employee.id,
    periodDate: definition.periodDate,
  });
  let found = page.records.find((item) => item.remark === definition.remark) ?? null;
  const payload = {
    employeeId: employee.id,
    employeeName: employee.realName,
    orgId: employee.orgId,
    orgName: employee.orgName,
    periodDate: definition.periodDate,
    targetAmount: definition.targetAmount,
    actualAmount: definition.actualAmount,
    dealAmount: definition.dealAmount,
    dealCount: definition.dealCount,
    remark: definition.remark,
  };
  if (!found) {
    await apiPost('/analysis/employee-performance', token, payload);
  } else {
    await apiPut(`/analysis/employee-performance/${found.id}`, token, payload);
  }
  page = await apiGet('/analysis/employee-performance', token, {
    pageNum: 1,
    pageSize: 200,
    employeeId: employee.id,
    periodDate: definition.periodDate,
  });
  found = page.records.find((item) => item.remark === definition.remark);
  report.employeePerformances.push({
    id: found.id,
    employeeId: found.employeeId,
    employeeName: found.employeeName,
    stableId: definition.remark,
  });
  return found;
}

async function fetchPayees(token, keyword = '') {
  return apiGet('/finance/payees', token, { pageNum: 1, pageSize: 200, keyword });
}

async function ensurePayee(token, definition) {
  let page = await fetchPayees(token, definition.payeeName);
  let found = page.records.find((item) => item.payeeName === definition.payeeName) ?? null;
  if (!found) {
    await apiPost('/finance/payees', token, definition);
  } else {
    await apiPut(`/finance/payees/${found.id}`, token, definition);
  }
  page = await fetchPayees(token, definition.payeeName);
  found = page.records.find((item) => item.payeeName === definition.payeeName);
  report.finance.payees.push({
    id: found.id,
    payeeName: found.payeeName,
    stableId: found.payeeName,
  });
  return found;
}

async function fetchIncomes(token, keyword = '') {
  return apiGet('/finance/incomes', token, { pageNum: 1, pageSize: 200, keyword });
}

async function ensureIncome(token, definition) {
  let page = await fetchIncomes(token, definition.incomeName);
  let found = page.records.find((item) => item.incomeName === definition.incomeName) ?? null;
  if (!found) {
    await apiPost('/finance/incomes', token, definition);
  } else {
    await apiPut(`/finance/incomes/${found.id}`, token, definition);
  }
  page = await fetchIncomes(token, definition.incomeName);
  found = page.records.find((item) => item.incomeName === definition.incomeName);
  report.finance.incomes.push({
    id: found.id,
    incomeName: found.incomeName,
    stableId: found.incomeName,
  });
  return found;
}

async function fetchExpenses(token, keyword = '') {
  return apiGet('/finance/expenses', token, { pageNum: 1, pageSize: 200, keyword });
}

async function ensureExpense(token, payees, definition) {
  const payee = payees[definition.payeeKey];
  const payload = {
    expenseName: definition.expenseName,
    expenseType: definition.expenseType,
    bizDate: definition.bizDate,
    amount: definition.amount,
    payeeName: payee.payeeName,
    payeeId: payee.id,
    fileId: null,
    remark: definition.remark,
  };
  let page = await fetchExpenses(token, definition.expenseName);
  let found = page.records.find((item) => item.expenseName === definition.expenseName) ?? null;
  if (!found) {
    await apiPost('/finance/expenses', token, payload);
  } else {
    await apiPut(`/finance/expenses/${found.id}`, token, payload);
  }
  page = await fetchExpenses(token, definition.expenseName);
  found = page.records.find((item) => item.expenseName === definition.expenseName);
  report.finance.expenses.push({
    id: found.id,
    expenseName: found.expenseName,
    stableId: found.expenseName,
  });
  return found;
}

async function findTransitionByMarker(token, employeeId) {
  const page = await apiGet('/workflow/transitions', token, { pageNum: 1, pageSize: 100, employeeId });
  for (const row of page.records) {
    const detail = await apiGet(`/workflow/transitions/${row.id}`, token);
    if ((detail.applyReason ?? '').includes(MARKER)) {
      return detail;
    }
  }
  return null;
}

async function ensureTransitionWorkflow(token, employeeId) {
  let detail = await findTransitionByMarker(token, employeeId);
  if (!detail) {
    await apiPost('/workflow/transitions', token, {
      employeeId,
      expectedFormalDate: '2026-05-31',
      applyReason: `转正流程样本（${MARKER}）`,
      selfEvaluation: `试用期目标完成情况良好（${MARKER}）`,
      growth: `已完成客户资料整理与流程梳理（${MARKER}）`,
      shortcomings: `横向协作节奏仍需提升（${MARKER}）`,
      developmentSuggestion: '继续加强跨部门沟通',
      draftOpinion: `同意发起（${MARKER}）`,
    });
    detail = await findTransitionByMarker(token, employeeId);
  }

  if (detail.processStatus === 'DRAFT' || detail.processStatus === 'REJECTED') {
    await apiPost(`/workflow/transitions/${detail.id}/submit`, token, {});
    detail = await apiGet(`/workflow/transitions/${detail.id}`, token);
  }

  while ((detail.processStatus ?? '').startsWith('PENDING')) {
    await apiPost(`/workflow/transitions/${detail.id}/approve`, token, {
      opinion: `通过（${MARKER}）`,
      result: 'APPROVE',
    });
    detail = await apiGet(`/workflow/transitions/${detail.id}`, token);
  }

  report.workflows.transition = {
    id: detail.id,
    applyNo: detail.applyNo,
    employeeId: detail.employeeId,
    employeeName: detail.employeeName,
    processStatus: detail.processStatus,
  };
  return detail;
}

async function findSalaryAdjustByMarker(token, employeeId) {
  const page = await apiGet('/workflow/salary-adjusts', token, { pageNum: 1, pageSize: 100, employeeId });
  for (const row of page.records) {
    const detail = await apiGet(`/workflow/salary-adjusts/${row.id}`, token);
    if ((detail.applyReason ?? '').includes(MARKER)) {
      return detail;
    }
  }
  return null;
}

async function ensureSalaryAdjustWorkflow(token, employeeId) {
  let detail = await findSalaryAdjustByMarker(token, employeeId);
  if (!detail) {
    await apiPost('/workflow/salary-adjusts', token, {
      employeeId,
      adjustAmount: 1200,
      applyReason: `调薪流程样本（${MARKER}）`,
      draftOpinion: `建议按月达成情况上调（${MARKER}）`,
    });
    detail = await findSalaryAdjustByMarker(token, employeeId);
  }

  if (detail.processStatus === 'DRAFT' || detail.processStatus === 'REJECTED') {
    await apiPost(`/workflow/salary-adjusts/${detail.id}/submit`, token, {});
    detail = await apiGet(`/workflow/salary-adjusts/${detail.id}`, token);
  }

  while ((detail.processStatus ?? '').startsWith('PENDING')) {
    await apiPost(`/workflow/salary-adjusts/${detail.id}/approve`, token, {
      opinion: `通过（${MARKER}）`,
      result: 'APPROVE',
    });
    detail = await apiGet(`/workflow/salary-adjusts/${detail.id}`, token);
  }

  report.workflows.salaryAdjust = {
    id: detail.id,
    applyNo: detail.applyNo,
    employeeId: detail.employeeId,
    employeeName: detail.employeeName,
    processStatus: detail.processStatus,
    newSalary: detail.newSalary,
  };
  return detail;
}

async function findReimbursementByMarker(token, reasonMarker, scope = 'my') {
  const page = await apiGet('/finance/reimbursements', token, { pageNum: 1, pageSize: 100, scope });
  return page.records.find((item) => (item.reason ?? '').includes(reasonMarker)) ?? null;
}

async function ensureReimbursement(token, reasonMarker, amount, scope = 'my') {
  let record = await findReimbursementByMarker(token, reasonMarker, scope);
  if (!record) {
    for (let attempt = 0; attempt < 3 && !record; attempt += 1) {
      try {
        await apiPost('/finance/reimbursements', token, {
          amount,
          reason: reasonMarker,
        });
      } catch (error) {
        if (!String(error?.message ?? '').includes('数据已存在')) {
          throw error;
        }
        await sleep(1100);
      }
      record = await findReimbursementByMarker(token, reasonMarker, scope);
    }
    if (!record) {
      throw new Error(`failed to create reimbursement for marker: ${reasonMarker}`);
    }
  } else if (record.processStatus === 'DRAFT' || record.processStatus === 'REJECTED') {
    await apiPut(`/finance/reimbursements/${record.id}`, token, {
      amount,
      reason: reasonMarker,
    });
    record = await apiGet(`/finance/reimbursements/${record.id}`, token);
  }
  return record;
}

async function ensureReimbursementAttachment(token, reimbursementId, fileName) {
  const attachments = await apiGet('/hr/workflow-attachments', token, { bizType: 'REIMBURSEMENT', bizId: reimbursementId });
  if (attachments.some((item) => item.fileName === fileName)) {
    return attachments.find((item) => item.fileName === fileName);
  }
  const formData = new FormData();
  formData.append(
    'file',
    new Blob([`attachment for ${MARKER}\nreimbursement=${reimbursementId}\n`], { type: 'text/plain' }),
    fileName,
  );
  formData.append('bizType', 'REIMBURSEMENT');
  formData.append('bizId', String(reimbursementId));
  return apiRequest('POST', '/hr/workflow-attachments/upload', { token, formData });
}

function upsertOperationLogs(entries) {
  for (const entry of entries) {
    const existingId = mysqlQuery(`
      SELECT id
      FROM sys_operation_log
      WHERE request_uri = ${sqlEscape(entry.requestUri)}
        AND result_msg = ${sqlEscape(entry.resultMsg)}
      LIMIT 1;
    `);
    if (existingId) {
      mysqlExec(`
        UPDATE sys_operation_log
        SET user_id = ${entry.userId},
            module_code = ${sqlEscape(entry.moduleCode)},
            biz_type = ${sqlEscape(entry.bizType)},
            action_code = ${sqlEscape(entry.actionCode)},
            action_desc = ${sqlEscape(entry.actionDesc)},
            target_type = ${sqlEscape(entry.targetType)},
            target_id = ${entry.targetId},
            request_method = ${sqlEscape(entry.requestMethod)},
            request_body = ${sqlEscape(entry.requestBody)},
            result_code = ${sqlEscape(entry.resultCode)},
            ip = ${sqlEscape(entry.ip)}
        WHERE id = ${existingId};
      `);
    } else {
      mysqlExec(`
        INSERT INTO sys_operation_log (
          user_id, module_code, biz_type, action_code, action_desc, target_type, target_id,
          request_uri, request_method, request_body, result_code, result_msg, ip
        ) VALUES (
          ${entry.userId}, ${sqlEscape(entry.moduleCode)}, ${sqlEscape(entry.bizType)}, ${sqlEscape(entry.actionCode)},
          ${sqlEscape(entry.actionDesc)}, ${sqlEscape(entry.targetType)}, ${entry.targetId},
          ${sqlEscape(entry.requestUri)}, ${sqlEscape(entry.requestMethod)}, ${sqlEscape(entry.requestBody)},
          ${sqlEscape(entry.resultCode)}, ${sqlEscape(entry.resultMsg)}, ${sqlEscape(entry.ip)}
        );
      `);
    }
  }
}

async function verifyCoverage(tokens, entities) {
  const adminToken = tokens.admin.token;
  const staffToken = tokens.staff.token;
  const approverToken = tokens.approver.token;

  const workbench = await apiGet('/workbench/overview', adminToken);
  const customerPage = await fetchCustomerPage(adminToken, {});
  const riskPage = await apiGet('/customer-risks', adminToken, { pageNum: 1, pageSize: 200 });
  const debtPage = await apiGet('/customer-debts', adminToken, { pageNum: 1, pageSize: 200 });
  const loanSelf = await apiGet('/loan-orders/overview', adminToken, { pageNum: 1, pageSize: 200, capitalSourceType: 'SELF' });
  const loanBank = await apiGet('/loan-orders/overview', adminToken, { pageNum: 1, pageSize: 200, capitalSourceType: 'BANK' });
  const repayments = await apiGet('/repayments', adminToken, { pageNum: 1, pageSize: 200 });
  const opportunities = await apiGet('/opportunities', adminToken, { pageNum: 1, pageSize: 200 });
  const followRecords = await apiGet('/opportunities/follow-records', adminToken, { pageNum: 1, pageSize: 200 });
  const incrementSummary = await apiGet('/analysis/increment-summary', adminToken, { pageNum: 1, pageSize: 200 });
  const incrementDetail = await apiGet('/analysis/increment-detail', adminToken, { pageNum: 1, pageSize: 200 });
  const dailyIncrement = await apiGet('/analysis/daily-increment', adminToken, { pageNum: 1, pageSize: 200, detailId: entities.incrementDetails[0].id });
  const performance = await apiGet('/analysis/employee-performance', adminToken, { pageNum: 1, pageSize: 200 });
  const employeePage = await apiGet('/hr/employees', adminToken, { pageNum: 1, pageSize: 200 });
  const staffProfile = await apiGet('/hr/my-profile', staffToken);
  const salaries = await apiGet('/hr/salaries', adminToken, { pageNum: 1, pageSize: 200 });
  const managementRecords = await apiGet('/hr/management-records', adminToken, { pageNum: 1, pageSize: 200 });
  const transitions = await apiGet('/workflow/transitions', adminToken, { pageNum: 1, pageSize: 200 });
  const salaryAdjusts = await apiGet('/workflow/salary-adjusts', adminToken, { pageNum: 1, pageSize: 200 });
  const reimbursementsAll = await apiGet('/finance/reimbursements', adminToken, { pageNum: 1, pageSize: 200, scope: 'all' });
  const reimbursementsMy = await apiGet('/finance/reimbursements', staffToken, { pageNum: 1, pageSize: 200, scope: 'my' });
  const reimbursementTimeline = await apiGet(`/workflow/processes/REIMBURSEMENT/${entities.reimbursements.approved.id}/timeline`, staffToken);
  const reimbursementAttachments = await apiGet('/hr/workflow-attachments', staffToken, {
    bizType: 'REIMBURSEMENT',
    bizId: entities.reimbursements.approved.id,
  });
  const myApprovalsTodos = await apiGet('/workflow/my-approvals/todos', approverToken, { pageNum: 1, pageSize: 50 });
  const myApprovalsInitiated = await apiGet('/workflow/my-approvals/initiated', approverToken, { pageNum: 1, pageSize: 50 });
  const myApprovalsProcessed = await apiGet('/workflow/my-approvals/processed', approverToken, { pageNum: 1, pageSize: 50 });
  const payees = await apiGet('/finance/payees', adminToken, { pageNum: 1, pageSize: 200 });
  const incomes = await apiGet('/finance/incomes', adminToken, { pageNum: 1, pageSize: 200 });
  const expenses = await apiGet('/finance/expenses', adminToken, { pageNum: 1, pageSize: 200 });
  const userPage = await apiGet('/system/users', adminToken, { pageNum: 1, pageSize: 200 });
  const rolePage = await apiGet('/system/roles', adminToken, { pageNum: 1, pageSize: 200 });
  const orgTree = await apiGet('/system/orgs/tree', adminToken);
  const operationLogs = await apiGet('/system/operation-logs', adminToken, { pageNum: 1, pageSize: 200, keyword: MARKER });

  report.system.operationLogs = operationLogs.records.map((item) => ({
    id: item.id,
    moduleCode: item.moduleCode,
    bizType: item.bizType,
    resultMsg: item.resultMsg,
  }));

  report.coverage = [
    { page: '/login', data: ['admin', 'phase_staff_01', 'phase_approver_01', 'phase_staff_02'], ready: true },
    {
      page: '/dashboard',
      data: {
        customerTotal: workbench.metricCards?.find((item) => item.title === '客户总数')?.value,
        activeLoanCount: workbench.metricCards?.find((item) => item.title === '运行中借贷单')?.value,
        todayRepaymentCount: workbench.metricCards?.find((item) => item.title === '今日还款登记')?.value,
      },
      ready: true,
    },
    { page: '/customers', data: { total: customerPage.total, seeded: report.customers.map((item) => item.customerNo) }, ready: true },
    { page: '/customers/archive/:id', data: { seeded: report.customers.map((item) => item.customerNo) }, ready: true },
    { page: '/customer-risks', data: { total: riskPage.total }, ready: true },
    { page: '/customer-debts', data: { total: debtPage.total }, ready: true },
    { page: '/loan-orders-self', data: { total: loanSelf.total }, ready: true },
    { page: '/loan-orders-self/:customerId', data: { customerIds: loanSelf.records.map((item) => item.customerId) }, ready: true },
    { page: '/loan-orders-bank', data: { total: loanBank.total }, ready: true },
    { page: '/loan-orders-bank/:customerId', data: { customerIds: loanBank.records.map((item) => item.customerId) }, ready: true },
    { page: '/repayments', data: { total: repayments.total, todayScoped: workbench.metricCards?.find((item) => item.title === '今日还款登记')?.value }, ready: true },
    { page: '/opportunities', data: { total: opportunities.total }, ready: true },
    { page: '/opportunities/:id/follow', data: { total: followRecords.total }, ready: true },
    { page: '/analysis/increment-overview', data: { total: incrementSummary.total }, ready: true },
    { page: '/increment-details', data: { total: incrementDetail.total }, ready: true },
    { page: '/increment-details/daily/:summaryId', data: { total: dailyIncrement.total }, ready: true },
    { page: '/analysis/performance', data: { total: performance.total }, ready: true },
    { page: '/hr/employees', data: { total: employeePage.total }, ready: true },
    { page: '/hr/employees/:id', data: { fullDetailEmployeeCode: 'PAP-E001' }, ready: true },
    { page: '/hr/my-profile', data: { employeeCode: staffProfile.employeeCode }, ready: true },
    { page: '/hr/salaries', data: { total: salaries.total }, ready: true },
    { page: '/hr/management-records', data: { total: managementRecords.total }, ready: true },
    { page: '/workflow/transitions', data: { total: transitions.total }, ready: true },
    { page: '/workflow/salary-adjusts', data: { total: salaryAdjusts.total }, ready: true },
    { page: '/finance/reimbursements', data: { allTotal: reimbursementsAll.total, myTotal: reimbursementsMy.total, attachments: reimbursementAttachments.length }, ready: true },
    {
      page: '/workflow/my-approvals',
      data: { todos: myApprovalsTodos.total, initiated: myApprovalsInitiated.total, processed: myApprovalsProcessed.total, timeline: reimbursementTimeline.length },
      ready: myApprovalsTodos.total > 0 && myApprovalsInitiated.total > 0 && myApprovalsProcessed.total > 0 && reimbursementTimeline.length > 0,
    },
    { page: '/finance/payees', data: { total: payees.total }, ready: true },
    { page: '/finance/incomes', data: { total: incomes.total }, ready: true },
    { page: '/finance/expenses', data: { total: expenses.total }, ready: true },
    { page: '/system/users', data: { total: userPage.total }, ready: true },
    { page: '/system/roles', data: { total: rolePage.total }, ready: true },
    { page: '/system/orgs', data: { total: flattenOrgs(orgTree).length }, ready: true },
    { page: '/system/logs', data: { total: operationLogs.total, keywordFilter: MARKER }, ready: operationLogs.total > 0 },
    {
      page: '/welcome',
      data: '当前为路由目标视图缺失，viewRegistry 会回退占位页；不是测试数据问题',
      ready: false,
    },
    {
      page: '/analysis/daily-increment',
      data: '当前为路由目标视图缺失，viewRegistry 会回退占位页；不是测试数据问题',
      ready: false,
    },
  ];
}

function applyOperationLogMigration() {
  const columns = [
    {
      name: 'action_code',
      definition: "VARCHAR(32) DEFAULT NULL COMMENT '操作编码' AFTER `biz_type`",
    },
    {
      name: 'action_desc',
      definition: "VARCHAR(255) DEFAULT NULL COMMENT '操作描述' AFTER `action_code`",
    },
    {
      name: 'target_type',
      definition: "VARCHAR(64) DEFAULT NULL COMMENT '对象类型' AFTER `action_desc`",
    },
    {
      name: 'target_id',
      definition: "BIGINT UNSIGNED DEFAULT NULL COMMENT '对象ID' AFTER `target_type`",
    },
  ];
  for (const column of columns) {
    const exists = Number(
      mysqlScalar(
        `SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'sys_operation_log' AND column_name = '${column.name}'`,
      ),
    );
    if (exists === 0) {
      mysqlExec(`ALTER TABLE \`sys_operation_log\` ADD COLUMN \`${column.name}\` ${column.definition};`);
    }
  }
}

async function main() {
  mkdirSync(REPORT_DIR, { recursive: true });

  log('applying local migrations');
  runLocalMigrations();

  log('applying local operation-log migration');
  applyOperationLogMigration();

  log('logging in as admin');
  const admin = await login(ADMIN_USERNAME, ADMIN_PASSWORD);
  const orgMap = {};
  for (const definition of ORG_DEFS) {
    orgMap[definition.key] = await ensureOrg(admin.token, definition);
  }

  const roleMap = {};
  for (const definition of ROLE_DEFS) {
    roleMap[definition.key] = await ensureRole(admin.token, definition);
  }

  const employeeMap = {};
  for (const definition of EMPLOYEE_DEFS) {
    const employee = await ensureEmployee(admin.token, orgMap, definition);
    const roleIds = definition.roleKeys.map((key) => roleMap[key].id);
    await syncUserAccount(admin.token, definition, employee, roleIds, roleIds[0]);
    employeeMap[definition.key] = await apiGet(`/hr/employees/${employee.id}`, admin.token);
  }

  const userMap = {};
  for (const definition of EMPLOYEE_DEFS) {
    userMap[definition.key] = await findUserByUsername(admin.token, definition.username);
  }

  const salaryStandardMap = {};
  for (const definition of SALARY_STANDARD_DEFS) {
    salaryStandardMap[definition.key] = await ensureSalaryStandard(admin.token, orgMap, definition);
  }

  ensureEmployeeBaselineSalary(
    employeeMap.staff1.id,
    salaryStandardMap.accountManager.id,
    salaryStandardMap.accountManager.salaryName,
    salaryStandardMap.accountManager.amount,
    `baseline:${MARKER}:PAP-E001`,
  );
  ensureEmployeeBaselineSalary(
    employeeMap.approver.id,
    salaryStandardMap.teamLead.id,
    salaryStandardMap.teamLead.salaryName,
    salaryStandardMap.teamLead.amount,
    `baseline:${MARKER}:PAP-E002`,
  );
  ensureEmployeeBaselineSalary(
    employeeMap.staff2.id,
    salaryStandardMap.opsSpecialist.id,
    salaryStandardMap.opsSpecialist.salaryName,
    salaryStandardMap.opsSpecialist.amount,
    `baseline:${MARKER}:PAP-E003`,
  );

  ensureLeaveRecord(
    employeeMap.staff1.id,
    userMap.staff1.id,
    userMap.staff1.realName,
    EMPLOYEE_DEFS.find((item) => item.key === 'staff1').leaveRecord,
  );

  const customerMap = {};
  for (const definition of CUSTOMER_DEFS) {
    customerMap[definition.key] = await ensureCustomerArchive(admin.token, definition);
  }

  const loanMap = {};
  for (const definition of LOAN_ORDER_DEFS) {
    loanMap[definition.key] = await ensureLoanOrder(admin.token, customerMap, definition);
  }

  const payees = [];
  for (const definition of PAYEE_DEFS) {
    payees.push(await ensurePayee(admin.token, definition));
  }

  for (const definition of INCOME_DEFS) {
    await ensureIncome(admin.token, definition);
  }

  for (const definition of EXPENSE_DEFS) {
    await ensureExpense(admin.token, payees, definition);
  }

  const opportunityMap = {};
  for (const definition of OPPORTUNITY_DEFS) {
    opportunityMap[definition.key] = await ensureOpportunity(admin.token, customerMap, userMap, definition);
  }

  const incrementSummary = await ensureIncrementSummary(admin.token, INCREMENT_SUMMARY_DEF);
  const incrementDetails = [];
  for (const definition of INCREMENT_DETAIL_DEFS) {
    incrementDetails.push(await ensureIncrementDetail(admin.token, customerMap, definition));
  }

  for (const definition of PERFORMANCE_DEFS) {
    await ensureEmployeePerformance(admin.token, employeeMap, definition);
  }

  await ensureTransitionWorkflow(admin.token, employeeMap.staff2.id);
  await ensureSalaryAdjustWorkflow(admin.token, employeeMap.staff1.id);

  const staffLogin = await login('phase_staff_01', DEMO_PASSWORD);
  const approverLogin = await login('phase_approver_01', DEMO_PASSWORD);

  let staffReimbursement = await ensureReimbursement(
    staffLogin.token,
    `员工报销已审批样本（${MARKER}）`,
    1860,
    'my',
  );
  if (staffReimbursement.processStatus === 'DRAFT') {
    await apiPost(`/finance/reimbursements/${staffReimbursement.id}/submit`, staffLogin.token, {
      amount: 1860,
      reason: `员工报销已审批样本（${MARKER}）`,
    });
  }
  staffReimbursement = await apiGet(`/finance/reimbursements/${staffReimbursement.id}`, staffLogin.token);
  await ensureReimbursementAttachment(staffLogin.token, staffReimbursement.id, `reimbursement-approved-${MARKER}.txt`);
  if (staffReimbursement.processStatus === 'PENDING_DEPT') {
    const todos = await apiGet('/workflow/my-approvals/todos', approverLogin.token, { pageNum: 1, pageSize: 50 });
    const task = todos.records.find((item) => item.bizType === 'REIMBURSEMENT' && item.bizId === staffReimbursement.id);
    if (task) {
      await apiPost(`/workflow/tasks/${task.taskId}/approve`, approverLogin.token, {
        opinion: `部门已审（${MARKER}）`,
        result: 'APPROVE',
      });
    }
  }
  staffReimbursement = await apiGet(`/finance/reimbursements/${staffReimbursement.id}`, staffLogin.token);

  let approverReimbursement = await ensureReimbursement(
    approverLogin.token,
    `审批人自发起样本（${MARKER}）`,
    980,
    'my',
  );
  if (approverReimbursement.processStatus === 'DRAFT') {
    await apiPost(`/finance/reimbursements/${approverReimbursement.id}/submit`, approverLogin.token, {
      amount: 980,
      reason: `审批人自发起样本（${MARKER}）`,
    });
  }
  approverReimbursement = await apiGet(`/finance/reimbursements/${approverReimbursement.id}`, approverLogin.token);

  report.workflows.reimbursements = [
    {
      id: staffReimbursement.id,
      applyNo: staffReimbursement.applyNo,
      applicantName: staffReimbursement.applicantName,
      processStatus: staffReimbursement.processStatus,
      stableId: 'approved-sample',
    },
    {
      id: approverReimbursement.id,
      applyNo: approverReimbursement.applyNo,
      applicantName: approverReimbursement.applicantName,
      processStatus: approverReimbursement.processStatus,
      stableId: 'approver-initiated-sample',
    },
  ];

  upsertOperationLogs([
    {
      userId: 1,
      moduleCode: 'system.orgs',
      bizType: 'CREATE',
      actionCode: 'CREATE',
      actionDesc: `创建组织（${MARKER}）`,
      targetType: 'ORG',
      targetId: orgMap.business.id,
      requestUri: '/api/v1/system/orgs',
      requestMethod: 'POST',
      requestBody: JSON.stringify({ orgId: orgMap.business.id, marker: MARKER }),
      resultCode: 'SUCCESS',
      resultMsg: `org:${orgMap.business.orgName}:${MARKER}`,
      ip: '127.0.0.1',
    },
    {
      userId: 1,
      moduleCode: 'system.roles',
      bizType: 'PERMISSION',
      actionCode: 'PERMISSION',
      actionDesc: `配置角色权限（${MARKER}）`,
      targetType: 'ROLE',
      targetId: roleMap.approverPortal.id,
      requestUri: `/api/v1/system/roles/${roleMap.approverPortal.id}/permissions`,
      requestMethod: 'PUT',
      requestBody: JSON.stringify({ roleCode: roleMap.approverPortal.roleCode, marker: MARKER }),
      resultCode: 'SUCCESS',
      resultMsg: `role:${roleMap.approverPortal.roleCode}:${MARKER}`,
      ip: '127.0.0.1',
    },
    {
      userId: 1,
      moduleCode: 'system.users',
      bizType: 'STATUS',
      actionCode: 'STATUS',
      actionDesc: `启用审批账号（${MARKER}）`,
      targetType: 'USER',
      targetId: userMap.approver.id,
      requestUri: `/api/v1/system/users/${userMap.approver.id}/status`,
      requestMethod: 'PUT',
      requestBody: JSON.stringify({ username: userMap.approver.username, marker: MARKER }),
      resultCode: 'SUCCESS',
      resultMsg: `user:${userMap.approver.username}:${MARKER}`,
      ip: '127.0.0.1',
    },
    {
      userId: 1,
      moduleCode: 'customers',
      bizType: 'UPDATE',
      actionCode: 'UPDATE',
      actionDesc: `更新客户档案（${MARKER}）`,
      targetType: 'CUSTOMER',
      targetId: customerMap.running.id,
      requestUri: `/api/v1/customers/${customerMap.running.id}/archive`,
      requestMethod: 'PUT',
      requestBody: JSON.stringify({ customerNo: customerMap.running.customerNo, marker: MARKER }),
      resultCode: 'SUCCESS',
      resultMsg: `customer:${customerMap.running.customerNo}:${MARKER}`,
      ip: '127.0.0.1',
    },
    {
      userId: userMap.staff1.id,
      moduleCode: 'customers',
      bizType: 'CREATE',
      actionCode: 'CREATE',
      actionDesc: `发起报销申请（${MARKER}）`,
      targetType: 'REIMBURSEMENT',
      targetId: staffReimbursement.id,
      requestUri: `/api/v1/finance/reimbursements/${staffReimbursement.id}/submit`,
      requestMethod: 'POST',
      requestBody: JSON.stringify({ applyNo: staffReimbursement.applyNo, marker: MARKER }),
      resultCode: 'SUCCESS',
      resultMsg: `reimbursement:${staffReimbursement.applyNo}:${MARKER}`,
      ip: '127.0.0.1',
    },
  ]);

  await verifyCoverage(
    { admin, staff: staffLogin, approver: approverLogin },
    {
      incrementDetails,
      reimbursements: {
        approved: staffReimbursement,
        initiated: approverReimbursement,
      },
    },
  );

  writeFileSync(REPORT_PATH, JSON.stringify(report, null, 2));
  log(`seed completed; report written to ${REPORT_PATH}`);
}

main().catch((error) => {
  console.error(`[phase-all-pages] ERROR: ${error.stack || error.message}`);
  process.exitCode = 1;
});
