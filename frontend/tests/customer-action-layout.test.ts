import assert from 'node:assert/strict'
import test from 'node:test'
import {
  getCustomerListRowActions,
  getCustomerListToolbarActions,
  getRecordListRowActions,
  getRecordListToolbarActions,
} from '../src/views/customer/customerActionLayout.ts'

test('customer list toolbar keeps refresh visible and folds import export into more menu', () => {
  const layout = getCustomerListToolbarActions()

  assert.deepEqual(layout.secondary.map((item) => item.key), ['refresh'])
  assert.equal(layout.primary.key, 'create')
  assert.deepEqual(layout.overflow.map((item) => item.key), ['import', 'export'])
  assert.equal(layout.overflow.every((item) => item.disabled), true)
})

test('customer list row keeps archive visible and folds related modules into more menu', () => {
  const layout = getCustomerListRowActions()

  assert.equal(layout.primary.key, 'archive')
  assert.equal(layout.primary.tone, 'primary')
  assert.deepEqual(layout.overflow.map((item) => item.key), ['risks', 'debts'])
  assert.equal(layout.overflow.every((item) => item.tone === 'default'), true)
})

test('record list layouts keep edit visible and send archive delete into more menu', () => {
  const toolbarLayout = getRecordListToolbarActions()
  const rowLayout = getRecordListRowActions()

  assert.deepEqual(toolbarLayout.secondary.map((item) => item.key), ['refresh'])
  assert.equal(toolbarLayout.primary.key, 'create')
  assert.deepEqual(toolbarLayout.overflow.map((item) => item.key), ['import', 'export'])

  assert.equal(rowLayout.primary.key, 'edit')
  assert.equal(rowLayout.primary.tone, 'primary')
  assert.deepEqual(rowLayout.overflow.map((item) => item.key), ['archive', 'delete'])
  assert.equal(rowLayout.overflow[1].tone, 'danger')
})
