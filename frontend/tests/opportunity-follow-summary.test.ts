import assert from 'node:assert/strict'
import test from 'node:test'
import { formatLatestFollowSummary } from '../src/views/opportunity/opportunityFollowSummary.ts'

test('latest follow summary prefers follow record fields over owner placeholder fields', () => {
  const summary = formatLatestFollowSummary({
    latestFollowTime: '2026-04-15 10:30:00',
    latestFollowerName: '顾问A',
    latestFollowContent: '已完成首次面谈，客户希望本周提交资料。',
  })

  assert.equal(summary.timeText, '2026-04-15 10:30:00')
  assert.equal(summary.followerText, '顾问A')
  assert.match(summary.contentText, /首次面谈/)
})

test('latest follow summary falls back to dash when there is no follow record', () => {
  const summary = formatLatestFollowSummary({
    latestFollowTime: '',
    latestFollowerName: '',
    latestFollowContent: '',
  })

  assert.deepEqual(summary, {
    timeText: '-',
    followerText: '-',
    contentText: '-',
  })
})
