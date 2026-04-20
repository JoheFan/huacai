import { get, post, put, del, type PageData } from './http'

// ===================== 增量总览类型 =====================
export interface IncrementSummaryRecord {
  id: number
  companyName: string
  businessAddress: string
  industry: string
  startDate: string
  janAmount: number | null
  febAmount: number | null
  marAmount: number | null
  aprAmount: number | null
  mayAmount: number | null
  junAmount: number | null
  julAmount: number | null
  augAmount: number | null
  sepAmount: number | null
  octAmount: number | null
  novAmount: number | null
  decAmount: number | null
  totalAmount: number | null
  remark: string
  createdAt: string
  updatedAt: string
}

export interface IncrementSummaryQuery {
  pageNum: number
  pageSize: number
  keyword?: string
  startDateFrom?: string
  startDateTo?: string
}

export interface IncrementSummarySavePayload {
  id?: number
  companyName: string
  businessAddress?: string
  industry?: string
  startDate?: string
  janAmount?: number | null
  febAmount?: number | null
  marAmount?: number | null
  aprAmount?: number | null
  mayAmount?: number | null
  junAmount?: number | null
  julAmount?: number | null
  augAmount?: number | null
  sepAmount?: number | null
  octAmount?: number | null
  novAmount?: number | null
  decAmount?: number | null
  remark?: string
}

// ===================== 增量详情类型 =====================
export interface IncrementDetailRecord {
  id: number
  customerId: number | null
  customerName: string
  incrementDate: string
  totalAmount: number | null
  businessAddress: string
  industry: string
  dailyCount: number | null
  remark: string
  createdAt: string
  updatedAt: string
}

export interface IncrementDetailQuery {
  pageNum: number
  pageSize: number
  keyword?: string
  customerId?: number
  incrementDateFrom?: string
  incrementDateTo?: string
}

export interface IncrementDetailSavePayload {
  id?: number
  customerId?: number
  customerName: string
  incrementDate: string
  totalAmount?: number | null
  businessAddress?: string
  industry?: string
  dailyCount?: number | null
  remark?: string
}

// ===================== 日增量类型 =====================
export interface DailyIncrementRecord {
  id: number
  detailId: number
  customerName: string
  incrementDate: string
  incrementAmount: number
  channelRate: number | null
  channelFee: number | null
  seqNo: number | null
  remark: string
  createdAt: string
  updatedAt: string
}

export interface DailyIncrementQuery {
  pageNum: number
  pageSize: number
  detailId?: number
}

export interface DailyIncrementSavePayload {
  id?: number
  detailId: number
  customerName?: string
  incrementDate?: string
  incrementAmount: number
  channelRate?: number | null
  channelFee?: number | null
  seqNo?: number | null
  remark?: string
}

// ===================== 员工绩效类型 =====================
export interface EmployeePerformanceRecord {
  id: number
  employeeId: number
  employeeName: string
  orgId: number | null
  orgName: string
  periodDate: string
  targetAmount: number | null
  actualAmount: number | null
  dealAmount: number | null
  dealCount: number | null
  remark: string
  updatedAt: string
}

export interface EmployeePerformanceQuery {
  pageNum: number
  pageSize: number
  keyword?: string
  employeeId?: number
  periodDate?: string
}

export interface EmployeePerformanceSavePayload {
  id?: number
  employeeId: number
  employeeName: string
  orgId?: number
  orgName?: string
  periodDate: string
  targetAmount?: number | null
  actualAmount?: number | null
  dealAmount?: number | null
  dealCount?: number | null
  remark?: string
}

// ===================== 增量总览 API =====================
export const fetchIncrementSummaryPage = (params: IncrementSummaryQuery) =>
  get<PageData<IncrementSummaryRecord>>('/analysis/increment-summary', { params })

export const fetchIncrementSummaryDetail = (id: number) =>
  get<IncrementSummaryRecord>(`/analysis/increment-summary/${id}`)

export const createIncrementSummary = (payload: IncrementSummarySavePayload) =>
  post<void>('/analysis/increment-summary', payload)

export const updateIncrementSummary = (id: number, payload: IncrementSummarySavePayload) =>
  put<void>(`/analysis/increment-summary/${id}`, payload)

export const deleteIncrementSummary = (id: number) =>
  del<void>(`/analysis/increment-summary/${id}`)

// ===================== 增量详情 API =====================
export const fetchIncrementDetailPage = (params: IncrementDetailQuery) =>
  get<PageData<IncrementDetailRecord>>('/analysis/increment-detail', { params })

export const fetchIncrementDetailDetail = (id: number) =>
  get<IncrementDetailRecord>(`/analysis/increment-detail/${id}`)

export const createIncrementDetail = (payload: IncrementDetailSavePayload) =>
  post<void>('/analysis/increment-detail', payload)

export const updateIncrementDetail = (id: number, payload: IncrementDetailSavePayload) =>
  put<void>(`/analysis/increment-detail/${id}`, payload)

export const deleteIncrementDetail = (id: number) =>
  del<void>(`/analysis/increment-detail/${id}`)

// ===================== 日增量 API =====================
export const fetchDailyIncrementPage = (params: DailyIncrementQuery) =>
  get<PageData<DailyIncrementRecord>>('/analysis/daily-increment', { params })

export const fetchDailyIncrementDetail = (id: number) =>
  get<DailyIncrementRecord>(`/analysis/daily-increment/${id}`)

export const createDailyIncrement = (payload: DailyIncrementSavePayload) =>
  post<void>('/analysis/daily-increment', payload)

export const updateDailyIncrement = (id: number, payload: DailyIncrementSavePayload) =>
  put<void>(`/analysis/daily-increment/${id}`, payload)

export const deleteDailyIncrement = (id: number) =>
  del<void>(`/analysis/daily-increment/${id}`)

// ===================== 员工绩效 API =====================
export const fetchEmployeePerformancePage = (params: EmployeePerformanceQuery) =>
  get<PageData<EmployeePerformanceRecord>>('/analysis/employee-performance', { params })

export const fetchEmployeePerformanceDetail = (id: number) =>
  get<EmployeePerformanceRecord>(`/analysis/employee-performance/${id}`)

export const createEmployeePerformance = (payload: EmployeePerformanceSavePayload) =>
  post<void>('/analysis/employee-performance', payload)

export const updateEmployeePerformance = (id: number, payload: EmployeePerformanceSavePayload) =>
  put<void>(`/analysis/employee-performance/${id}`, payload)

export const deleteEmployeePerformance = (id: number) =>
  del<void>(`/analysis/employee-performance/${id}`)
