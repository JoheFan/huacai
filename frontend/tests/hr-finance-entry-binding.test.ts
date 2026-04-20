import assert from 'node:assert/strict'
import test from 'node:test'
import { flatMenuItems } from '../src/router/menu.ts'

test('报销管理菜单绑定到最终 workflow 页面', () => {
  const item = flatMenuItems.find((menu) => menu.path === '/finance/reimbursements')
  assert.ok(item)
  assert.equal(item?.view, 'workflow/ReimbursementApplyView')
})

test('人事管理包含管理记录入口', () => {
  const item = flatMenuItems.find((menu) => menu.path === '/hr/management-records')
  assert.ok(item)
  assert.equal(item?.view, 'hr/ManagementRecordView')
})
