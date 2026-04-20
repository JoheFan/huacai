import assert from 'node:assert/strict'
import test from 'node:test'
import { flatMenuItems } from '../src/router/menu.ts'

test('each enabled menu item declares its routed view in menu config', () => {
  const missingViewPaths = flatMenuItems
    .filter((item) => item.stage === '已启用')
    .filter((item) => !item.view)
    .map((item) => item.path)

  assert.deepEqual(missingViewPaths, [])
})
