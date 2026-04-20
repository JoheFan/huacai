import { get, post, put } from './http'
import type { PageData } from './http'

export interface TransitionApplyVO {
  id: number
  applyNo: string
  employeeId: number
  employeeCode: string
  employeeName: string
  phone: string
  department: string
  orgId: number
  position: string
  joinDate: string
  expectedFormalDate: string
  applyReason: string
  selfEvaluation: string
  growth: string
  shortcomings: string
  developmentSuggestion: string
  draftOpinion: string
  hrOpinion: string
  companyOpinion: string
  adminOpinion: string
  processStatus: string
  currentNode: string
  applicantId: number
  applicantName: string
  applyTime: string
  submitTime: string
  completeTime: string
  createdAt: string
  canApprove?: boolean
}

export interface SalaryAdjustApplyVO {
  id: number
  applyNo: string
  employeeId: number
  employeeName: string
  department: string
  orgId: number
  currentSalary: number
  adjustAmount: number
  newSalary: number
  applyReason: string
  draftOpinion: string
  deptOpinion: string
  hrOpinion: string
  leaderOpinion: string
  schoolLeaderOpinion: string
  financeOpinion: string
  processStatus: string
  currentNode: string
  applicantId: number
  applicantName: string
  applyTime: string
  submitTime: string
  effectiveDate: string
  completeTime: string
  createdAt: string
  canApprove?: boolean
}

export interface ApprovalRecordVO {
  id: number
  bizType: string
  bizId: number
  nodeName: string
  nodeKey: string
  operatorId: number
  operatorName: string
  operateTime: string
  opinion: string
  result: string
}

export interface ManagementRecordVO {
  id: number
  employeeId: number
  employeeName: string
  recordType: string
  content: string
  operatorId: number
  operatorName: string
  operatedAt: string
  remark: string
}

export interface MyApprovalTaskVO {
  taskId: number
  processId: number
  bizType: string
  bizId: number
  bizNo: string
  title: string
  nodeKey: string
  nodeName: string
  taskStatus: string
  applicantName: string
  applicantOrgName: string
  createdAt: string
  taskCreatedAt: string
}

export interface ActionLogVO {
  id: number
  processId: number
  bizType: string
  bizId: number
  roundNo: number
  actionCode: string
  nodeKey: string
  nodeName: string
  operatorId: number
  operatorName: string
  actionOpinion: string
  actionResult: string
  actionTime: string
}

export interface TransitionApplyPageQuery {
  pageNum?: number
  pageSize?: number
  keyword?: string
  processStatus?: string
  scope?: string
  employeeId?: number
}

export interface SalaryAdjustApplyPageQuery {
  pageNum?: number
  pageSize?: number
  keyword?: string
  processStatus?: string
  scope?: string
  employeeId?: number
}

export interface TransitionApplyCreateRequest {
  employeeId: number
  expectedFormalDate: string
  applyReason?: string
  selfEvaluation?: string
  growth?: string
  shortcomings?: string
  developmentSuggestion?: string
  draftOpinion?: string
}

export interface TransitionApplyUpdateRequest {
  employeeId?: number
  expectedFormalDate?: string
  applyReason?: string
  selfEvaluation?: string
  growth?: string
  shortcomings?: string
  developmentSuggestion?: string
  draftOpinion?: string
  hrOpinion?: string
  companyOpinion?: string
  adminOpinion?: string
}

export interface SalaryAdjustApplyCreateRequest {
  employeeId: number
  adjustAmount: number
  applyReason?: string
  draftOpinion?: string
}

export interface SalaryAdjustApplyUpdateRequest {
  employeeId?: number
  adjustAmount?: number
  applyReason?: string
  draftOpinion?: string
  deptOpinion?: string
  hrOpinion?: string
  leaderOpinion?: string
  schoolLeaderOpinion?: string
  financeOpinion?: string
}

export interface ApprovalOpinionRequest {
  opinion: string
  result: string
}

export interface ManagementRecordPageQuery {
  pageNum?: number
  pageSize?: number
  employeeId?: number
  recordType?: string
}

export const transitionApi = {
  page: (query: TransitionApplyPageQuery) =>
    get<PageData<TransitionApplyVO>>('/workflow/transitions', { params: query }),

  detail: (id: number) =>
    get<TransitionApplyVO>(`/workflow/transitions/${id}`),

  create: (data: TransitionApplyCreateRequest) =>
    post('/workflow/transitions', data),

  update: (id: number, data: TransitionApplyUpdateRequest) =>
    put(`/workflow/transitions/${id}`, data),

  submit: (id: number) =>
    post(`/workflow/transitions/${id}/submit`, {}),

  approve: (id: number, data: ApprovalOpinionRequest) =>
    post(`/workflow/transitions/${id}/approve`, data),

  reject: (id: number, data: ApprovalOpinionRequest) =>
    post(`/workflow/transitions/${id}/reject`, data),
}

export const salaryAdjustApi = {
  page: (query: SalaryAdjustApplyPageQuery) =>
    get<PageData<SalaryAdjustApplyVO>>('/workflow/salary-adjusts', { params: query }),

  detail: (id: number) =>
    get<SalaryAdjustApplyVO>(`/workflow/salary-adjusts/${id}`),

  create: (data: SalaryAdjustApplyCreateRequest) =>
    post('/workflow/salary-adjusts', data),

  update: (id: number, data: SalaryAdjustApplyUpdateRequest) =>
    put(`/workflow/salary-adjusts/${id}`, data),

  submit: (id: number) =>
    post(`/workflow/salary-adjusts/${id}/submit`, {}),

  approve: (id: number, data: ApprovalOpinionRequest) =>
    post(`/workflow/salary-adjusts/${id}/approve`, data),

  reject: (id: number, data: ApprovalOpinionRequest) =>
    post(`/workflow/salary-adjusts/${id}/reject`, data),
}

export const workflowApi = {
  getApprovalRecords: (bizType: string, bizId: number) =>
    get<ApprovalRecordVO[]>('/workflow/approval-records', { params: { bizType, bizId } }),

  getManagementRecords: (query: ManagementRecordPageQuery) =>
    get<PageData<ManagementRecordVO>>('/hr/management-records', { params: query }),
}

export interface MyApprovalPageQuery {
  pageNum?: number
  pageSize?: number
  bizType?: string
}

export const myApprovalApi = {
  pageTodos: (query: MyApprovalPageQuery) =>
    get<PageData<MyApprovalTaskVO>>('/workflow/my-approvals/todos', { params: query }),

  pageInitiated: (query: MyApprovalPageQuery) =>
    get<PageData<MyApprovalTaskVO>>('/workflow/my-approvals/initiated', { params: query }),

  pageProcessed: (query: MyApprovalPageQuery) =>
    get<PageData<MyApprovalTaskVO>>('/workflow/my-approvals/processed', { params: query }),

  approveTask: (taskId: number, opinion: string) =>
    post(`/workflow/tasks/${taskId}/approve`, { opinion, result: 'APPROVE' }),

  rejectTask: (taskId: number, opinion: string) =>
    post(`/workflow/tasks/${taskId}/reject`, { opinion, result: 'REJECT' }),

  getTimeline: (bizType: string, bizId: number) =>
    get<ActionLogVO[]>(`/workflow/processes/${bizType}/${bizId}/timeline`),
}
