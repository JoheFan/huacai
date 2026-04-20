import assert from 'node:assert/strict'
import test from 'node:test'
import { restoreAuthSession } from '../src/bootstrap/restoreAuthSession.ts'
import type { CurrentUserInfo } from '../src/api/auth.ts'

test('skips auth recovery when there is no token', async () => {
  let refreshed = 0
  let loggedOut = 0
  let redirectedTo = ''

  const result = await restoreAuthSession(
    {
      token: '',
      refreshUser: async () => {
        refreshed += 1
      },
      logout: async () => {
        loggedOut += 1
      },
    },
    {
      currentRoute: { value: { path: '/login' } },
      replace: async (path: string) => {
        redirectedTo = path
      },
    },
  )

  assert.equal(result, false)
  assert.equal(refreshed, 0)
  assert.equal(loggedOut, 0)
  assert.equal(redirectedTo, '')
})

test('refreshes current user before mount when token exists', async () => {
  let refreshed = 0
  let loggedOut = 0

  const result = await restoreAuthSession(
    {
      token: 'valid-token',
      refreshUser: async () => {
        refreshed += 1
      },
      logout: async () => {
        loggedOut += 1
      },
    },
    {
      currentRoute: { value: { path: '/customers' } },
      replace: async () => undefined,
    },
  )

  assert.equal(result, true)
  assert.equal(refreshed, 1)
  assert.equal(loggedOut, 0)
})

test('redirects to the permitted home page when refreshed user cannot access current route', async () => {
  let redirectedTo = ''
  const currentUser: CurrentUserInfo = {
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
    pagePermissions: ['/welcome', '/customers'],
    buttonPermissions: [],
    dataScopes: {
      customers: 'SELF',
    },
    permissionSummary: '仅本人数据权限',
  }

  const result = await restoreAuthSession(
    {
      token: 'valid-token',
      refreshUser: async () => currentUser,
      logout: async () => undefined,
    },
    {
      currentRoute: { value: { path: '/system/users' } },
      replace: async (path: string) => {
        redirectedTo = path
      },
    },
  )

  assert.equal(result, true)
  assert.equal(redirectedTo, '/welcome')
})

test('logs out and redirects to login when token refresh fails', async () => {
  let loggedOut = 0
  let redirectedTo = ''

  const result = await restoreAuthSession(
    {
      token: 'stale-token',
      refreshUser: async () => {
        throw new Error('invalid token')
      },
      logout: async () => {
        loggedOut += 1
      },
    },
    {
      currentRoute: { value: { path: '/customers' } },
      replace: async (path: string) => {
        redirectedTo = path
      },
    },
  )

  assert.equal(result, false)
  assert.equal(loggedOut, 1)
  assert.equal(redirectedTo, '/login')
})
