import { del, get, post, put } from './http'

export interface PayeeVO {
  id: number
  payeeName: string
  payeeType?: string
  bankName?: string
  bankAccount?: string
  contactName?: string
  contactPhone?: string
  status: string
  remark?: string
  createdAt?: string
}

export interface PayeeSaveRequest {
  payeeName: string
  payeeType?: string
  bankName?: string
  bankAccount?: string
  contactName?: string
  contactPhone?: string
  status?: string
  remark?: string
}

export interface IncomeVO {
  id: number
  incomeName: string
  incomeType: string
  bizDate: string
  amount: number
  payerName: string
  payerId: number
  fileId: number
  remark: string
}

export interface ExpenseVO {
  id: number
  expenseName: string
  expenseType: string
  bizDate: string
  amount: number
  payeeName: string
  payeeId: number
  fileId: number
  remark: string
}

export interface PageResponse<T> {
  records: T[]
  total: number
  pageNum: number
  pageSize: number
}

// ===================== 收款方管理 =====================
export const payeeApi = {
  page: (params: { keyword?: string; status?: string; pageNum?: number; pageSize?: number }) =>
    get<PageResponse<PayeeVO>>('/finance/payees', { params }),

  detail: (id: number) => get<PayeeVO>(`/finance/payees/${id}`),

  create: (data: PayeeSaveRequest) => post<void>('/finance/payees', data),

  update: (id: number, data: PayeeSaveRequest) => put<void>(`/finance/payees/${id}`, data),

  delete: (id: number) => del<void>(`/finance/payees/${id}`),

  updateStatus: (id: number, status: string) =>
    put<void>(`/finance/payees/${id}/status`, { status }),
}

// ===================== 支出管理 =====================
export const expenseApi = {
  page: (params: { keyword?: string; expenseType?: string; pageNum?: number; pageSize?: number }) =>
    get<PageResponse<ExpenseVO>>('/finance/expenses', { params }),

  detail: (id: number) => get<ExpenseVO>(`/finance/expenses/${id}`),

  create: (data: any) => post<void>('/finance/expenses', data),

  update: (id: number, data: any) => put<void>(`/finance/expenses/${id}`, data),

  delete: (id: number) => del<void>(`/finance/expenses/${id}`),
}

// ===================== 收入管理 =====================
export const incomeApi = {
  page: (params: { keyword?: string; incomeType?: string; pageNum?: number; pageSize?: number }) =>
    get<PageResponse<IncomeVO>>('/finance/incomes', { params }),

  detail: (id: number) => get<IncomeVO>(`/finance/incomes/${id}`),

  create: (data: any) => post<void>('/finance/incomes', data),

  update: (id: number, data: any) => put<void>(`/finance/incomes/${id}`, data),

  delete: (id: number) => del<void>(`/finance/incomes/${id}`),
}

// ===================== 报销管理 =====================
export interface ReimbursementVO {
  id: number
  applyNo: string
  applicantId: number
  applicantName: string
  applicantOrgId: number
  applicantOrgName: string
  reimbursementUserId: number
  reimbursementUserName: string
  amount: number
  reason: string
  processStatus: string
  currentNodeKey: string
  currentNodeName: string
  currentRound: number
  resubmitCount: number
  submitTime: string
  completeTime: string
  withdrawTime: string
  createdAt: string
  canApprove?: boolean
  canWithdraw?: boolean
  canEdit?: boolean
  canResubmit?: boolean
}

export interface ReimbursementPageQuery {
  pageNum?: number
  pageSize?: number
  keyword?: string
  processStatus?: string
  scope?: string
}

export const reimbursementApi = {
  page: (query: ReimbursementPageQuery) =>
    get<PageResponse<ReimbursementVO>>('/finance/reimbursements', { params: query }),

  detail: (id: number) =>
    get<ReimbursementVO>(`/finance/reimbursements/${id}`),

  create: (data: { amount: number; reason?: string }) =>
    post<void>('/finance/reimbursements', data),

  update: (id: number, data: { amount?: number; reason?: string }) =>
    put<void>(`/finance/reimbursements/${id}`, data),

  submit: (id: number, data: { amount: number; reason: string }) =>
    post<void>(`/finance/reimbursements/${id}/submit`, data),

  withdraw: (id: number) =>
    post<void>(`/finance/reimbursements/${id}/withdraw`, {}),

  resubmit: (id: number, data: { amount: number; reason: string }) =>
    post<void>(`/finance/reimbursements/${id}/resubmit`, data),
}
