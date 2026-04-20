export interface OpportunityFollowSummaryInput {
  latestFollowTime?: string | null
  latestFollowerName?: string | null
  latestFollowContent?: string | null
}

export interface OpportunityFollowSummary {
  timeText: string
  followerText: string
  contentText: string
}

const fallback = (value?: string | null) => {
  const trimmed = value?.trim()
  return trimmed ? trimmed : '-'
}

export const formatLatestFollowSummary = (
  input: OpportunityFollowSummaryInput,
): OpportunityFollowSummary => ({
  timeText: fallback(input.latestFollowTime),
  followerText: fallback(input.latestFollowerName),
  contentText: fallback(input.latestFollowContent),
})
