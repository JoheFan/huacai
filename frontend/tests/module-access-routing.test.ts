import assert from 'node:assert/strict'
import test from 'node:test'
import { canAccessPath, getDefaultHomePath, getVisibleMenuGroups } from '../src/access/moduleAccess.ts'
import type { CurrentUserInfo } from '../src/api/auth.ts'

const adminUser: CurrentUserInfo = {
  id: 1,
  username: 'admin',
  realName: '管理员',
  orgId: 1,
  orgName: '总部',
  identityType: 'SUPER_ADMIN',
  roles: ['ADMIN'],
  primaryRoleCode: 'ADMIN',
  primaryRoleName: '超级管理员',
  permissions: ['*:*:*'],
  pagePermissions: ['/dashboard', '/system/users', '/finance/payees'],
  buttonPermissions: ['system.users:create'],
  dataScopes: {
    customers: 'ALL',
  },
  permissionSummary: '全部数据权限',
}

const staffUser: CurrentUserInfo = {
  id: 7,
  username: 'zhangyiyi',
  realName: '张一一',
  orgId: 2,
  orgName: '财务',
  identityType: 'NORMAL_USER',
  roles: ['STAFF'],
  primaryRoleCode: 'STAFF',
  primaryRoleName: '普通用户',
  permissions: [],
  pagePermissions: ['/welcome', '/customers', '/customer-risks', '/customer-debts', '/repayments'],
  buttonPermissions: [],
  dataScopes: {
    customers: 'SELF',
    repayments: 'SELF',
  },
  permissionSummary: '仅本人数据权限',
}

test('admin keeps dashboard and all configured menu modules', () => {
  const visiblePaths = getVisibleMenuGroups(adminUser).flatMap((group) => group.items.map((item) => item.path))

  assert.equal(getDefaultHomePath(adminUser), '/dashboard')
  assert.equal(canAccessPath('/system/users', adminUser), true)
  assert.equal(canAccessPath('/finance/payees', adminUser), true)
  assert.ok(visiblePaths.includes('/dashboard'))
  assert.ok(visiblePaths.includes('/system/users'))
})

test('staff lands on welcome page and only sees granted business modules', () => {
  const visiblePaths = getVisibleMenuGroups(staffUser).flatMap((group) => group.items.map((item) => item.path))

  assert.equal(getDefaultHomePath(staffUser), '/welcome')
  assert.ok(visiblePaths.includes('/welcome'))
  assert.ok(visiblePaths.includes('/customers'))
  assert.ok(visiblePaths.includes('/customer-risks'))
  assert.ok(visiblePaths.includes('/customer-debts'))
  assert.ok(visiblePaths.includes('/hr/my-profile'))
  assert.equal(canAccessPath('/welcome', staffUser), true)
  assert.equal(canAccessPath('/customers', staffUser), true)
  assert.equal(canAccessPath('/customer-risks', staffUser), true)
  assert.equal(canAccessPath('/customer-debts', staffUser), true)
  assert.equal(canAccessPath('/dashboard', staffUser), false)
  assert.equal(canAccessPath('/opportunities', staffUser), false)
  assert.equal(canAccessPath('/system/users', staffUser), false)
})
