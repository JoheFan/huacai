import assert from 'node:assert/strict'
import test from 'node:test'
import {
  buildCustomerArchivePayload,
  createEmptyCustomerArchiveForm,
  normalizeCustomerAttachments,
} from '../src/views/customer/customerModels.ts'

test('normalize customer attachments removes blanks and preserves order', () => {
  assert.deepEqual(
    normalizeCustomerAttachments([
      { id: 3, fileName: '税票.pdf' },
      { id: 0, fileName: '' },
      { id: 4, fileName: '营业执照.pdf' },
    ]),
    [
      { id: 3, fileName: '税票.pdf' },
      { id: 4, fileName: '营业执照.pdf' },
    ],
  )
})

test('empty customer archive form starts with one editable row per section', () => {
  const form = createEmptyCustomerArchiveForm()

  assert.equal(form.riskRecords.length, 1)
  assert.equal(form.debtRecords.length, 1)
  assert.equal(form.contractRecords.length, 1)
  assert.equal(form.taxRegistrationNormal, null)
})

test('buildCustomerArchivePayload keeps manually entered scores and computes debt totals', () => {
  const payload = buildCustomerArchivePayload({
    customerNo: 'KH-001',
    customerName: '张三',
    gender: 'MALE',
    idCard: '440100199001011234',
    birthday: '1990-01-01',
    mobile: '13800000000',
    companyName: '华彩科技',
    creditCode: '91440101123456789X',
    establishedDate: '2020-01-01',
    industry: '零售',
    businessAddress: '广州',
    bankName: '建设银行',
    bankAccount: '6222000000001',
    recommenderName: '李四',
    recommenderRate: 8.5,
    serviceFee: 1280,
    bizStatus: 'INIT',
    taxRegistrationNormal: true,
    archiveAttachments: [{ id: 11, fileName: '税票.pdf' }],
    riskRecords: [
      {
        testDate: '2026-04-15',
        testLimit: 90,
        trafficValue: 1950000,
        compositeScore: 118,
        thirdPartyScore: 113,
        remark: '评分通过',
      },
    ],
    debtRecords: [
      {
        debtType: '信用卡',
        debtAmount: 100000,
        advancePaidAmount: 22000,
        installmentAmount: 3000,
        repaymentDay: 15,
        remark: '正常',
      },
    ],
    contractRecords: [
      {
        customerName: '张三',
        companyName: '华彩科技',
        creditCode: '91440101123456789X',
        contractNo: 'HT-001',
        contractName: '授信合同',
        remark: '已签署',
        fileIds: [21, 22],
      },
    ],
  })

  assert.equal(payload.riskRecords[0].compositeScore, 118)
  assert.equal(payload.debtRecords[0].totalRepaymentAmount, 100000)
  assert.equal(payload.debtRecords[0].pendingAmount, 78000)
  assert.deepEqual(payload.archiveFileIds, [11])
  assert.deepEqual(payload.contractRecords[0].fileIds, [21, 22])
})
