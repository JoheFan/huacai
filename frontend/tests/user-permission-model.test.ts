import test from 'node:test'
import assert from 'node:assert/strict'
import {
  buildPermissionPayload,
  buildScopedModuleItems,
  createPermissionDraft,
  formatIdentityType,
  normalizePermissionCodes,
  summarizeRoleSource,
} from '../src/views/system/user/userPermissionModel.ts'

const catalog = {
  pageItems: [
    { id: 1, permissionCode: '/system/users', permissionName: '系统用户', permissionType: 'PAGE', moduleCode: 'system.users' },
    { id: 2, permissionCode: '/customers', permissionName: '客户管理', permissionType: 'PAGE', moduleCode: 'customers' },
    { id: 3, permissionCode: '/opportunities', permissionName: '商机管理', permissionType: 'PAGE', moduleCode: 'opportunities' },
  ],
  buttonItems: [
    { id: 11, permissionCode: 'system.users:create', permissionName: '新增用户', permissionType: 'BUTTON', moduleCode: 'system.users' },
    { id: 12, permissionCode: 'system.users:assign-permission', permissionName: '配置权限', permissionType: 'BUTTON', moduleCode: 'system.users' },
  ],
}

test('normalizePermissionCodes removes duplicates and unsupported entries', () => {
  assert.deepEqual(
    normalizePermissionCodes(['/customers', '/customers', '/unknown'], catalog.pageItems),
    ['/customers'],
  )
})

test('buildScopedModuleItems only keeps business modules from selected pages', () => {
  assert.deepEqual(
    buildScopedModuleItems(['/system/users', '/customers'], catalog.pageItems, { customers: 'ORG' }),
    [{ moduleCode: 'customers', moduleName: '客户管理', scopeType: 'ORG' }],
  )
})

test('buildPermissionPayload trims permissions to catalog and scopes to visible business modules', () => {
  assert.deepEqual(
    buildPermissionPayload(
      ['/customers', '/opportunities', '/unknown'],
      ['system.users:create', 'bad'],
      { customers: 'ORG', opportunities: 'SELF', repayments: 'ALL' },
      catalog,
    ),
    {
      pagePermissions: ['/customers', '/opportunities'],
      buttonPermissions: ['system.users:create'],
      dataScopes: { customers: 'ORG', opportunities: 'SELF' },
    },
  )
})

test('permission draft copies effective values and summary helpers stay readable', () => {
  const profile = {
    userId: 7,
    identityType: 'NORMAL_USER',
    primaryRoleId: 2,
    primaryRoleCode: 'STAFF',
    primaryRoleName: '普通用户',
    rolePagePermissions: ['/customers'],
    roleButtonPermissions: [],
    roleDataScopes: { customers: 'SELF' },
    effectivePagePermissions: ['/customers', '/opportunities'],
    effectiveButtonPermissions: ['system.users:create'],
    dataScopes: { customers: 'ORG', opportunities: 'SELF' },
    permissionSummary: '仅本人数据权限',
  }
  assert.deepEqual(createPermissionDraft(profile), {
    pagePermissions: ['/customers', '/opportunities'],
    buttonPermissions: ['system.users:create'],
    dataScopes: { customers: 'ORG', opportunities: 'SELF' },
  })
  assert.equal(formatIdentityType('DEPT_ADMIN'), '部门管理员')
  assert.equal(summarizeRoleSource(profile), '普通用户 · 仅本人数据权限')
})
