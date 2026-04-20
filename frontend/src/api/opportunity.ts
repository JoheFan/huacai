import { get, post, put, del, type PageData } from './http'

export interface OpportunityRecord {
  id: number
  customerId: number
  customerName: string
  customerNo: string
  mobile: string
  companyName: string
  creditCode: string
  priorityLevel: string
  stageCode: string
  ownerUserId: number
  ownerUserName: string
  estimatedAmount: number | null
  intentLevel: string
  status: string
  nextFollowTime: string | null
  latestFollowTime: string | null
  latestFollowerName: string
  latestFollowContent: string
  remark: string
  createdAt: string
  updatedAt: string
}

export interface OpportunityQuery {
  pageNum: number
  pageSize: number
  keyword?: string
  customerId?: number
  stageCode?: string
  status?: string
  ownerUserId?: number
}

export interface OpportunitySavePayload {
  customerId?: number
  priorityLevel?: string
  stageCode?: string
  ownerUserId?: number
  estimatedAmount?: number
  intentLevel?: string
  status?: string
  nextFollowTime?: string
  remark?: string
}

export const fetchOpportunityPage = (params: OpportunityQuery) => 
  get<PageData<OpportunityRecord>>('/opportunities', { params })

export const fetchOpportunityDetail = (id: number) => 
  get<OpportunityRecord>(`/opportunities/${id}`)

export const createOpportunity = (payload: OpportunitySavePayload) => 
  post<void>('/opportunities', payload)

export const updateOpportunity = (id: number, payload: OpportunitySavePayload) => 
  put<void>(`/opportunities/${id}`, payload)

export const deleteOpportunity = (id: number) => 
  del<void>(`/opportunities/${id}`)

export const updateOpportunityStatus = (id: number, status: string) =>
  put<void>(`/opportunities/${id}/status`, { status })

// ===================== 跟进记录类型 =====================
export interface FollowRecord {
  id: number
  opportunityId: number
  customerId: number | null
  followTime: string
  followerName: string
  followContent: string
  nextAction: string
  remark: string
  createdAt: string
  updatedAt: string
}

export interface FollowRecordQuery {
  pageNum: number
  pageSize: number
  opportunityId?: number
  keyword?: string
}

export interface FollowRecordSavePayload {
  opportunityId: number
  followTime?: string
  followerName?: string
  followContent?: string
  nextAction?: string
  remark?: string
}

// ===================== 跟进记录 API =====================
export const fetchFollowRecordPage = (params: FollowRecordQuery) =>
  get<PageData<FollowRecord>>('/opportunities/follow-records', { params })

export const fetchFollowRecordDetail = (id: number) =>
  get<FollowRecord>(`/opportunities/follow-records/${id}`)

export const createFollowRecord = (payload: FollowRecordSavePayload) =>
  post<void>('/opportunities/follow-records', payload)

export const updateFollowRecord = (id: number, payload: FollowRecordSavePayload) =>
  put<void>(`/opportunities/follow-records/${id}`, payload)

export const deleteFollowRecord = (id: number) =>
  del<void>(`/opportunities/follow-records/${id}`)
