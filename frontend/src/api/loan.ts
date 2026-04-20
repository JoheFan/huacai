import { del, get, post, put, type PageData } from './http'

export interface LoanOrderRecord {
  id: number
  customerId: number
  customerName: string
  customerNo: string
  capitalSourceType: string
  bankName: string
  loanDate: string | null
  depositGoldAmount: number | null
  creditCardRepaymentAmount: number | null
  loanAmount: number | null
  balanceAmount: number | null
  monthlyInterestAmount: number | null
  loanCount: number | null
  status: string
  remark: string
  createdAt: string
  updatedAt: string
}

export interface LoanOrderOverviewRecord {
  customerId: number
  customerNo: string
  customerName: string
  mobile: string
  companyName: string
  capitalSourceType: string
  depositGoldAmount: number | null
  creditCardRepaymentAmount: number | null
  balanceAmount: number | null
  loanCount: number | null
  monthlyInterestAmount: number | null
  totalLoanAmount: number | null
  firstLoanDate: string | null
  repaidAmount: number | null
  pendingAmount: number | null
  remark: string
  status: string
  // 银行口径字段（持久化）
  totalIncrementAmount: number | null
  incrementCount: number | null
  yearsTerm: string | null
  channelRate: number | null
  channelFee: number | null
  referrer: string | null
  selfTotalLoanAmount: number | null
  bankTotalLoanAmount: number | null
}

export interface LoanCustomerSummaryPayload {
  customerId: number | null
  capitalSourceType: string
  totalIncrementAmount: number | null
  incrementCount: number | null
  yearsTerm: string | null
  channelRate: number | null
  channelFee: number | null
  referrer: string | null
  selfTotalLoanAmount: number | null
  bankTotalLoanAmount: number | null
  remark: string
}

export interface LoanCustomerSummaryResponse {
  id: number
  customerId: number
  customerNo: string
  customerName: string
  mobile: string
  companyName: string
  capitalSourceType: string
  totalIncrementAmount: number | null
  incrementCount: number | null
  yearsTerm: string | null
  channelRate: number | null
  channelFee: number | null
  referrer: string | null
  selfTotalLoanAmount: number | null
  bankTotalLoanAmount: number | null
  remark: string
}

export interface LoanOrderQuery {
  pageNum: number
  pageSize: number
  keyword?: string
  customerId?: number
  capitalSourceType?: string
  status?: string
}

export interface LoanOrderSavePayload {
  customerId: number | null
  capitalSourceType: string
  bankName?: string
  loanDate?: string | null
  depositGoldAmount?: number | null
  creditCardRepaymentAmount?: number | null
  loanAmount?: number | null
  balanceAmount?: number | null
  monthlyInterestAmount?: number | null
  loanCount?: number | null
  status?: string
  remark?: string
}

export interface LoanRepaymentRecord {
  id: number
  loanOrderId: number
  customerId: number
  customerName: string
  customerNo: string
  capitalSourceType: string
  repaymentDate: string
  repaymentAmount: number | null
  principalAmount: number | null
  interestAmount: number | null
  repaymentChannel: string
  remark: string
  createdAt: string
  updatedAt: string
}

export interface LoanRepaymentSummary {
  customerId: number | null
  customerName: string
  loanOrderId: number | null
  capitalSourceType: string
  recordCount: number
  repaymentAmountTotal: number | null
  principalAmountTotal: number | null
  interestAmountTotal: number | null
}

export interface LoanRepaymentQuery {
  pageNum: number
  pageSize: number
  keyword?: string
  loanOrderId?: number
  customerId?: number
  capitalSourceType?: string
}

export interface LoanRepaymentSavePayload {
  loanOrderId: number | null
  repaymentDate?: string | null
  repaymentAmount?: number | null
  principalAmount?: number | null
  interestAmount?: number | null
  repaymentChannel?: string
  remark?: string
}

export const fetchLoanOrderPage = (params: LoanOrderQuery) =>
  get<PageData<LoanOrderRecord>>('/loan-orders', { params })

export const fetchLoanOrderOverviewPage = (params: LoanOrderQuery) =>
  get<PageData<LoanOrderOverviewRecord>>('/loan-orders/overview', { params })

export const fetchLoanOrderDetail = (id: number) => get<LoanOrderRecord>(`/loan-orders/${id}`)

export const createLoanOrder = (payload: LoanOrderSavePayload) => post<void>('/loan-orders', payload)

export const updateLoanOrder = (id: number, payload: LoanOrderSavePayload) =>
  put<void>(`/loan-orders/${id}`, payload)

export const fetchRepaymentPage = (params: LoanRepaymentQuery) =>
  get<PageData<LoanRepaymentRecord>>('/repayments', { params })

export const fetchRepaymentSummary = (params: LoanRepaymentQuery) =>
  get<LoanRepaymentSummary>('/repayments/summary', { params })

export const fetchRepaymentDetail = (id: number) => get<LoanRepaymentRecord>(`/repayments/${id}`)

export const createRepayment = (payload: LoanRepaymentSavePayload) => post<void>('/repayments', payload)

export const updateRepayment = (id: number, payload: LoanRepaymentSavePayload) =>
  put<void>(`/repayments/${id}`, payload)

export const fetchCustomerSummary = (customerId: number, capitalSourceType: string) =>
  get<LoanCustomerSummaryResponse>(`/loan-orders/customer-summary?customerId=${customerId}&capitalSourceType=${capitalSourceType}`)

export const saveCustomerSummary = (payload: LoanCustomerSummaryPayload) =>
  post<void>('/loan-orders/customer-summary', payload)

export const deleteCustomerSummary = (customerId: number, capitalSourceType: string) =>
  del<void>(`/loan-orders/customer-summary?customerId=${customerId}&capitalSourceType=${capitalSourceType}`)
