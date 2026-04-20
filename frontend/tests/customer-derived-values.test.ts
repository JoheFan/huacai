import assert from 'node:assert/strict'
import test from 'node:test'
import { calculateCustomerAge, derivePendingDebtAmount, toTotalRepaymentAmount } from '../src/views/customer/customerArchiveState.ts'

test('calculateCustomerAge returns null without birthday and derives current age from birthday', () => {
  assert.equal(calculateCustomerAge(''), null)
  assert.equal(calculateCustomerAge('1990-01-01', new Date('2026-04-15')), 36)
})

test('debt helpers derive total repayment and pending amount from debt amount and advance paid amount', () => {
  assert.equal(toTotalRepaymentAmount(100000), 100000)
  assert.equal(derivePendingDebtAmount(100000, 22000), 78000)
  assert.equal(derivePendingDebtAmount(100000, 120000), 0)
})
