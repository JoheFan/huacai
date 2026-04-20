import assert from 'node:assert/strict'
import test from 'node:test'
import { buildRepaymentScopeState } from '../src/views/loan/repaymentScopeModel.ts'

test('global repayment list remains unscoped when no customer or loan order is provided', () => {
  const state = buildRepaymentScopeState({})

  assert.equal(state.isScoped, false)
  assert.equal(state.title, '还款明细')
  assert.equal(state.summaryLabel, '')
})

test('customer scoped repayment view exposes summary heading and keeps source label', () => {
  const state = buildRepaymentScopeState({
    customerId: 6,
    customerName: '张三',
    capitalSourceType: 'SELF',
  })

  assert.equal(state.isScoped, true)
  assert.match(state.title, /张三/)
  assert.match(state.title, /我方/)
  assert.match(state.summaryLabel, /总计/)
})
