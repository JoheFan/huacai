import assert from 'node:assert/strict'
import test from 'node:test'
import { getLoanOverviewMode } from '../src/views/loan/loanOverviewMode.ts'

test('self capital source uses customer summary columns aligned to self-fund prototype', () => {
  const mode = getLoanOverviewMode('SELF')

  assert.equal(mode.scopeLabel, '我方')
  assert.equal(mode.actionLabel, '查看')
  assert.deepEqual(
    mode.columns.map((column) => column.key),
    [
      'customerNo',
      'customerName',
      'mobile',
      'companyName',
      'depositGoldAmount',
      'creditCardRepaymentAmount',
      'balanceAmount',
      'loanCount',
      'monthlyInterestAmount',
      'totalLoanAmount',
      'firstLoanDate',
      'repaidAmount',
      'pendingAmount',
      'remark',
    ],
  )
})

test('bank capital source uses customer summary columns aligned to bank prototype', () => {
  const mode = getLoanOverviewMode('BANK')

  assert.equal(mode.scopeLabel, '银行')
  assert.equal(mode.actionLabel, '查看')
  assert.deepEqual(
    mode.columns.map((column) => column.key),
    [
      'customerNo',
      'customerName',
      'mobile',
      'companyName',
      'totalLoanAmount',
      'firstLoanDate',
      'repaidAmount',
      'pendingAmount',
      'remark',
    ],
  )
})
