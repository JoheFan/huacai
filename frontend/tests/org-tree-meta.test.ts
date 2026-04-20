import assert from 'node:assert/strict'
import test from 'node:test'
import { getOrgManagementLabel, getOrgParentLabel } from '../src/views/system/org/orgTreeMeta.ts'

test('company orgs are treated as management orgs while departments are not', () => {
  assert.equal(getOrgManagementLabel('COMPANY'), '是')
  assert.equal(getOrgManagementLabel('DEPT'), '否')
})

test('parent label falls back to top-level when there is no parent name', () => {
  assert.equal(getOrgParentLabel('华彩技术有限公司'), '华彩技术有限公司')
  assert.equal(getOrgParentLabel(''), '顶层组织')
})
