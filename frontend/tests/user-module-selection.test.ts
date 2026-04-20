import assert from 'node:assert/strict'
import test from 'node:test'
import {
  getModuleSelectionSummary,
  getUserModuleAccessMode,
  sanitizeVisibleModuleKeys,
  userModuleGroups,
} from '../src/views/system/user/userModuleRegistry.ts'

test('user module registry only exposes enabled business modules for direct assignment', () => {
  const keys = userModuleGroups.flatMap((group) => group.items.map((item) => item.key))

  assert.deepEqual(keys, [
    'customers',
    'customer-risks',
    'customer-debts',
    'opportunities',
    'loan-orders',
    'repayments',
  ])
})

test('sanitizeVisibleModuleKeys removes duplicates and unsupported entries in registry order', () => {
  assert.deepEqual(
    sanitizeVisibleModuleKeys(['repayments', 'unknown', 'customers', 'customers']),
    ['customers', 'repayments'],
  )
})

test('admin users use read only mode while normal users can edit visible modules', () => {
  const adminMode = getUserModuleAccessMode(['ADMIN'])
  const staffMode = getUserModuleAccessMode(['STAFF'])

  assert.equal(adminMode.editable, false)
  assert.match(adminMode.hint, /管理员/)
  assert.equal(staffMode.editable, true)
  assert.equal(getModuleSelectionSummary(['customers', 'repayments']), '已选 2 个模块')
})
