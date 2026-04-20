import assert from 'node:assert/strict'
import test from 'node:test'
import { formatPrimaryRole, formatUserRoleNames } from '../src/views/system/user/userRoleDisplay.ts'

test('user role display joins multiple role codes and falls back cleanly', () => {
  assert.equal(formatUserRoleNames(['ADMIN', 'STAFF']), '系统管理员 / 普通用户')
  assert.equal(formatUserRoleNames([]), '-')
  assert.equal(formatPrimaryRole('', 'DEPT_ADMIN'), '部门管理员')
})
