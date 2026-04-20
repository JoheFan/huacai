import assert from 'node:assert/strict'
import test from 'node:test'
import { getLoanOrderFormMode } from '../src/views/loan/loanOrderFormMode.ts'

test('create mode hides backend-managed fields and shows explicit hint', () => {
  const mode = getLoanOrderFormMode(null)

  assert.equal(mode.isEditing, false)
  assert.equal(mode.showManagedFields, false)
  assert.match(mode.createHint, /新增时/)
  assert.match(mode.createHint, /当前余额/)
})

test('edit mode shows backend-managed fields and omits create hint', () => {
  const mode = getLoanOrderFormMode(12)

  assert.equal(mode.isEditing, true)
  assert.equal(mode.showManagedFields, true)
  assert.equal(mode.createHint, '')
})
