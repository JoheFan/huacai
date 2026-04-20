import assert from 'node:assert/strict'
import test from 'node:test'
import { handleUnauthorizedResponse } from '../src/api/http-auth.ts'

test('clears persisted auth and redirects to login on 401 response', () => {
  const removedKeys: string[] = []
  let redirectedTo = ''

  const handled = handleUnauthorizedResponse(401, {
    currentPath: '/customers',
    removeItem: (key: string) => {
      removedKeys.push(key)
    },
    redirectTo: (path: string) => {
      redirectedTo = path
    },
  })

  assert.equal(handled, true)
  assert.deepEqual(removedKeys, ['huacai-token', 'huacai-user'])
  assert.equal(redirectedTo, '/login')
})

test('does not redirect again when already on login page', () => {
  let redirectedTo = ''

  const handled = handleUnauthorizedResponse(401, {
    currentPath: '/login',
    removeItem: () => undefined,
    redirectTo: (path: string) => {
      redirectedTo = path
    },
  })

  assert.equal(handled, true)
  assert.equal(redirectedTo, '')
})

test('ignores non-401 responses', () => {
  let removed = 0
  let redirected = 0

  const handled = handleUnauthorizedResponse(500, {
    currentPath: '/customers',
    removeItem: () => {
      removed += 1
    },
    redirectTo: () => {
      redirected += 1
    },
  })

  assert.equal(handled, false)
  assert.equal(removed, 0)
  assert.equal(redirected, 0)
})
