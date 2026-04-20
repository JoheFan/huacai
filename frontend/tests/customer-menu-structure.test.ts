import assert from 'node:assert/strict'
import test from 'node:test'
import { canAccessPath, getVisibleMenuGroups } from '../src/access/moduleAccess.ts'
import type { CurrentUserInfo } from '../src/api/auth.ts'
import { menuGroups } from '../src/router/menu.ts'

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
  pagePermissions: ['/dashboard', '/system/users', '/opportunities'],
  buttonPermissions: ['system.users:create'],
  dataScopes: { customers: 'ALL' },
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
  dataScopes: { customers: 'SELF', repayments: 'SELF' },
  permissionSummary: '仅本人数据权限',
}

test('customer menu group exposes customer modules at the same level and moves opportunities into management', () => {
  const customerGroup = menuGroups.find((group) => group.title === '客户管理')
  const operationGroup = menuGroups.find((group) => group.title === '经营管理')

  assert.ok(customerGroup)
  assert.ok(operationGroup)
  assert.deepEqual(customerGroup.items.map((item) => item.title), ['客户管理', '风险评估', '负债登记'])
  assert.equal(customerGroup.items.every((item) => !item.children?.length), true)
  assert.deepEqual(operationGroup.items.map((item) => item.title), ['借贷管理（我方）', '借贷管理（银行）', '增量详情', '商机管理'])
})

test('staff sees customer routes when corresponding module keys are granted', () => {
  const visibleGroups = getVisibleMenuGroups(staffUser)
  const customerGroup = visibleGroups.find((group) => group.title === '客户管理')

  assert.ok(customerGroup)
  assert.deepEqual(customerGroup.items.map((item) => item.path), ['/customers', '/customer-risks', '/customer-debts'])
  assert.equal(canAccessPath('/customer-risks', staffUser), true)
  assert.equal(canAccessPath('/customer-debts', staffUser), true)
  assert.equal(canAccessPath('/opportunities', staffUser), false)
})

test('admin can still access relocated opportunities route', () => {
  assert.equal(canAccessPath('/opportunities', adminUser), true)
})
