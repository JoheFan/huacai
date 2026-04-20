import test from 'node:test'
import assert from 'node:assert/strict'
import {
  buildRolePermissionPayload,
  createRolePermissionDraft,
  summarizeRolePermission,
} from '../src/views/system/role/rolePermissionModel.ts'

const catalog = {
  pageItems: [
    { id: 1, permissionCode: '/system/users', permissionName: '系统用户', permissionType: 'PAGE', moduleCode: 'system.users' },
    { id: 2, permissionCode: '/customers', permissionName: '客户管理', permissionType: 'PAGE', moduleCode: 'customers' },
    { id: 3, permissionCode: '/loan-orders', permissionName: '借贷管理', permissionType: 'PAGE', moduleCode: 'loan-orders' },
  ],
  buttonItems: [
    { id: 11, permissionCode: 'system.roles:create', permissionName: '新增角色', permissionType: 'BUTTON', moduleCode: 'system.roles' },
    { id: 12, permissionCode: 'system.roles:assign-permission', permissionName: '配置角色权限', permissionType: 'BUTTON', moduleCode: 'system.roles' },
  ],
}

test('buildRolePermissionPayload only keeps catalog-backed permissions and visible business scopes', () => {
  assert.deepEqual(
    buildRolePermissionPayload(
      ['/customers', '/loan-orders', '/unknown'],
      ['system.roles:create', 'bad'],
      { customers: 'ORG', 'loan-orders': 'SELF', repayments: 'ALL' },
      catalog,
    ),
    {
      pagePermissions: ['/customers', '/loan-orders'],
      buttonPermissions: ['system.roles:create'],
      dataScopes: { customers: 'ORG', 'loan-orders': 'SELF' },
    },
  )
})

test('createRolePermissionDraft keeps template values and summary stays readable', () => {
  const profile = {
    roleId: 7,
    roleCode: 'STAFF',
    roleName: '普通用户',
    identityType: 'NORMAL_USER',
    pagePermissions: ['/customers'],
    buttonPermissions: ['system.roles:create'],
    dataScopes: { customers: 'ORG' },
    permissionSummary: '本部门数据权限',
  }

  assert.deepEqual(createRolePermissionDraft(profile), {
    pagePermissions: ['/customers'],
    buttonPermissions: ['system.roles:create'],
    dataScopes: { customers: 'ORG' },
  })
  assert.equal(summarizeRolePermission(profile), '普通用户 · 本部门数据权限')
})
