import { del, get, post, put, type PageData } from './http'

export interface CustomerRecord {
  id: number
  customerNo: string
  customerName: string
  mobile: string
  companyName: string
  creditCode: string
  industry: string
  businessAddress: string
  auditStatus: string
  bizStatus: string
  loanStatus: string
  serviceFee: number | null
  bankName: string
  bankAccount: string
  recommenderName: string
  recommenderRate: number | null
  taxRegistrationNormal: boolean | null
  testDate: string | null
  testLimit: number | null
  trafficValue: number | null
  compositeScore: number | null
  thirdPartyScore: number | null
  remark: string
  birthday: string | null
  establishedDate: string | null
  createdAt: string
  updatedAt: string
}

export interface CustomerQuery {
  pageNum: number
  pageSize: number
  keyword?: string
  auditStatus?: string
  loanStatus?: string
}

export interface CustomerAttachmentRecord {
  id: number
  fileName: string
}

export interface CustomerRiskRecord {
  id?: number
  customerId: number
  customerNo?: string
  customerName?: string
  mobile?: string
  companyName?: string
  creditCode?: string
  industry?: string
  businessAddress?: string
  testDate?: string | null
  testLimit?: number | null
  trafficValue?: number | null
  compositeScore?: number | null
  thirdPartyScore?: number | null
  remark?: string
  createdAt?: string
  updatedAt?: string
}

export interface CustomerDebtRecord {
  id?: number
  customerId: number
  customerNo?: string
  customerName?: string
  mobile?: string
  companyName?: string
  creditCode?: string
  debtType: string
  debtAmount?: number | null
  totalRepaymentAmount?: number | null
  advancePaidAmount?: number | null
  pendingAmount?: number | null
  installmentAmount?: number | null
  repaymentDay?: number | null
  remark?: string
  createdAt?: string
  updatedAt?: string
}

export interface CustomerContractRecord {
  id?: number
  customerId: number
  customerName: string
  companyName: string
  creditCode: string
  contractNo?: string
  contractName: string
  attachments: CustomerAttachmentRecord[]
  signDate?: string | null
  remark?: string
  createdAt?: string
  updatedAt?: string
}

export interface CustomerArchiveRecord {
  id: number
  customerNo: string
  customerName: string
  gender: string
  idCard: string
  birthday: string | null
  age: number | null
  mobile: string
  companyName: string
  creditCode: string
  establishedDate: string | null
  industry: string
  businessAddress: string
  bankName: string
  bankAccount: string
  recommenderName: string
  recommenderRate: number | null
  serviceFee: number | null
  auditStatus: string
  bizStatus: string
  loanStatus: string
  taxRegistrationNormal: boolean | null
  remark: string
  archiveAttachments: CustomerAttachmentRecord[]
  riskRecords: CustomerRiskRecord[]
  debtRecords: CustomerDebtRecord[]
  contractRecords: CustomerContractRecord[]
  createdAt: string
  updatedAt: string
}

export interface CustomerArchivePayload {
  customerNo?: string
  customerName: string
  gender?: string
  idCard?: string
  birthday?: string | null
  mobile?: string
  companyName?: string
  creditCode?: string
  establishedDate?: string | null
  industry?: string
  businessAddress?: string
  bankName?: string
  bankAccount?: string
  recommenderName?: string
  recommenderRate?: number | null
  serviceFee?: number | null
  bizStatus?: string
  taxRegistrationNormal?: boolean | null
  archiveFileIds: number[]
  riskRecords: Array<{
    id?: number
    customerId?: number
    testDate?: string | null
    testLimit?: number | null
    trafficValue?: number | null
    compositeScore?: number | null
    thirdPartyScore?: number | null
    remark?: string
  }>
  debtRecords: Array<{
    id?: number
    customerId?: number
    debtType: string
    debtAmount?: number | null
    totalRepaymentAmount?: number | null
    advancePaidAmount?: number | null
    pendingAmount?: number | null
    installmentAmount?: number | null
    repaymentDay?: number | null
    remark?: string
  }>
  contractRecords: Array<{
    id?: number
    customerId?: number
    customerName: string
    companyName: string
    creditCode: string
    contractNo?: string
    contractName: string
    fileIds: number[]
    signDate?: string | null
    remark?: string
  }>
}

export interface CustomerRiskQuery {
  pageNum: number
  pageSize: number
  customerId?: number | null
  keyword?: string
}

export interface CustomerDebtQuery {
  pageNum: number
  pageSize: number
  customerId?: number | null
  keyword?: string
}

export interface CustomerStatusLogRecord {
  id: number
  customerId: number
  statusCode: string
  statusName: string
  changedAt: string
  changedBy: number
  changedByName: string
  remark: string
}

export interface CustomerRiskPayload {
  id?: number
  customerId: number
  testDate?: string | null
  testLimit?: number | null
  trafficValue?: number | null
  compositeScore?: number | null
  thirdPartyScore?: number | null
  remark?: string
}

export interface CustomerDebtPayload {
  id?: number
  customerId: number
  debtType: string
  debtAmount?: number | null
  totalRepaymentAmount?: number | null
  advancePaidAmount?: number | null
  pendingAmount?: number | null
  installmentAmount?: number | null
  repaymentDay?: number | null
  remark?: string
}

export const fetchCustomerPage = (params: CustomerQuery) => get<PageData<CustomerRecord>>('/customers', { params })

export const fetchCustomerDetail = (id: number) => get<CustomerRecord>(`/customers/${id}`)

export const fetchCustomerArchive = (id: number) => get<CustomerArchiveRecord>(`/customers/${id}/archive`)

export const createCustomerArchive = (payload: CustomerArchivePayload) => post<void>('/customers/archive', payload)

export const updateCustomerArchive = (id: number, payload: CustomerArchivePayload) =>
  put<void>(`/customers/${id}/archive`, payload)

export const fetchCustomerRisks = (params: CustomerRiskQuery) =>
  get<PageData<CustomerRiskRecord>>('/customer-risks', { params })

export const createCustomerRisk = (payload: CustomerRiskPayload) => post<void>('/customer-risks', payload)

export const updateCustomerRisk = (id: number, payload: CustomerRiskPayload) =>
  put<void>(`/customer-risks/${id}`, payload)

export const deleteCustomerRisk = (id: number) => del<void>(`/customer-risks/${id}`)

export const fetchCustomerDebts = (params: CustomerDebtQuery) =>
  get<PageData<CustomerDebtRecord>>('/customer-debts', { params })

export const createCustomerDebt = (payload: CustomerDebtPayload) => post<void>('/customer-debts', payload)

export const updateCustomerDebt = (id: number, payload: CustomerDebtPayload) =>
  put<void>(`/customer-debts/${id}`, payload)

export const deleteCustomerDebt = (id: number) => del<void>(`/customer-debts/${id}`)

export const fetchCustomerStatusLogs = (customerId: number) =>
  get<CustomerStatusLogRecord[]>(`/customers/${customerId}/status-logs`)

export interface StatusUpdatePayload {
  status: string
  statusName: string
  remark?: string
}

export const updateCustomerStatus = (id: number, payload: StatusUpdatePayload) =>
  put<void>(`/customers/${id}/status`, payload)

export interface CustomerTradeRecord {
  id: number
  customerId: number
  customerNo: string
  customerName: string
  mobile: string
  companyName: string
  tradeType: string
  amount: number | null
  serviceFee: number | null
  actualReceived: number | null
  returnedAmount: number | null
  balanceAmount: number | null
  tradeDate: string | null
  remark: string
  createdAt: string
  updatedAt: string
}

export interface CustomerTradePayload {
  tradeType: string
  amount?: number | null
  serviceFee?: number | null
  actualReceived?: number | null
  returnedAmount?: number | null
  balanceAmount?: number | null
  tradeDate?: string | null
  remark?: string
}

export const fetchCustomerTrades = (customerId: number) =>
  get<CustomerTradeRecord[]>(`/customers/${customerId}/trades`)

export const fetchCustomerTradeDetail = (customerId: number, id: number) =>
  get<CustomerTradeRecord>(`/customers/${customerId}/trades/${id}`)

export const createCustomerTrade = (customerId: number, payload: CustomerTradePayload) =>
  post<void>(`/customers/${customerId}/trades`, payload)

export const updateCustomerTrade = (customerId: number, id: number, payload: CustomerTradePayload) =>
  put<void>(`/customers/${customerId}/trades/${id}`, payload)

export const deleteCustomerTrade = (customerId: number, id: number) =>
  del<void>(`/customers/${customerId}/trades/${id}`)
