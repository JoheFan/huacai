import { get, post, put, del } from './http'
import type { PageData } from './http'

export interface EmployeeVO {
  id: number
  employeeCode: string
  realName: string
  gender: string
  birthday: string
  idCardNo: string
  age: number
  nation: string
  politicalStatus: string
  hometown: string
  maritalStatus: string
  phone: string
  email: string
  graduateSchool: string
  highestEducation: string
  workStartDate: string
  homeAddress: string
  emergencyContact: string
  emergencyContactPhone: string
  bankCardNo: string
  idPhotoUrl: string
  employmentStatus: string
  talentFlag: string
  createSystemAccount: number
  systemUsername: string
  orgId: number
  orgName: string
  jobTitle: string
  remark: string
  createdAt: string
  updatedAt: string
}

export interface JobInfoVO {
  id: number
  employeeId: number
  employeeCode: string
  joinDate: string
  formalDate: string
  workUnit: string
  workMode: string
  borrowDispatchDate: string
  department: string
  rankLevel: string
  jobCategory: string
  position: string
  sortNo: number
  is编制: number
}

export interface SalaryStandardVO {
  id: number
  salaryName: string
  amount: number
  jobTitle: string
  orgId: number
  orgName: string
  description: string
  sortNo: number
  status: string
  effectiveDate: string
  expireDate: string
  versionNo: string
  applicableScope: string
  createdAt: string
  updatedAt: string
}

export interface EmployeeDetailVO extends EmployeeVO {
  jobInfo: JobInfoVO | null
  removal: RemovalVO | null
  certificates: CertificateVO[]
  assessments: AssessmentVO[]
  growthRecords: GrowthVO[]
  familyMembers: FamilyVO[]
  changeRecords: ChangeVO[]
  contracts: ContractVO[]
  salaryInfo: EmployeeSalaryVO[]
  leaveRecords: LeaveRecordVO[]
}

export interface RemovalVO {
  id: number
  employeeId: number
  removalType: string
  removalDate: string
  expectedRetireDate: string
  reason: string
}

export interface CertificateVO {
  id: number
  employeeId: number
  certificateName: string
  certificateNo: string
  issueDate: string
  certificateType: string
  issueOrg: string
  isPermanent: number
  expireDate: string
  certificateFileUrl: string
}

export interface AssessmentVO {
  id: number
  employeeId: number
  assessmentMonth: string
  assessmentScore: number
  assessmentGrade: string
  remark: string
}

export interface GrowthVO {
  id: number
  employeeId: number
  startDate: string
  endDate: string
  workName: string
}

export interface FamilyVO {
  id: number
  employeeId: number
  relation: string
  name: string
  birthday: string
  idCardNo: string
  politicalStatus: string
  workUnitPosition: string
}

export interface ChangeVO {
  id: number
  employeeId: number
  currentDepartment: string
  currentPosition: string
  currentRankLevel: string
  originalDepartment: string
  originalPosition: string
  originalRankLevel: string
  reportDate: string
  changeType: string
  changeReason: string
}

export interface ContractVO {
  id: number
  employeeId: number
  contractName: string
  contractNo: string
  contractStartDate: string
  contractEndDate: string
  contractFileUrl: string
  remark: string
}

export interface EmployeeSalaryVO {
  id: number
  employeeId: number
  salaryStandardId: number
  salaryName: string
  amount: number
  effectiveDate: string
  expireDate: string
  changeReason: string
}

export interface LeaveRecordVO {
  id: number
  employeeId: number
  leaveType: string
  startDate: string
  endDate: string
  days: number
  reason: string
  status: string
  applicantId: number
  applicantName: string
  applyTime: string
}

export interface EmployeePageQuery {
  pageNum?: number
  pageSize?: number
  keyword?: string
  employmentStatus?: string
  orgId?: number
}

export interface SalaryStandardPageQuery {
  pageNum?: number
  pageSize?: number
  keyword?: string
  status?: string
  orgId?: number
}

export interface EmployeeCreateRequest {
  employeeCode: string
  realName: string
  gender?: string
  idCardNo?: string
  birthday?: string
  age?: number
  nation?: string
  politicalStatus?: string
  hometown?: string
  maritalStatus?: string
  phone: string
  email?: string
  graduateSchool?: string
  highestEducation?: string
  workStartDate?: string
  homeAddress?: string
  emergencyContact?: string
  emergencyContactPhone?: string
  bankCardNo?: string
  idPhotoUrl?: string
  employmentStatus: string
  talentFlag?: string
  createSystemAccount: number
  systemUsername?: string
  systemPasswordPlain?: string
  orgId?: number
  jobTitle?: string
  remark?: string
}

export interface EmployeeUpdateRequest {
  realName?: string
  gender?: string
  idCardNo?: string
  birthday?: string
  age?: number
  nation?: string
  politicalStatus?: string
  hometown?: string
  maritalStatus?: string
  phone?: string
  email?: string
  graduateSchool?: string
  highestEducation?: string
  workStartDate?: string
  homeAddress?: string
  emergencyContact?: string
  emergencyContactPhone?: string
  bankCardNo?: string
  idPhotoUrl?: string
  employmentStatus?: string
  talentFlag?: string
  createSystemAccount?: number
  systemUsername?: string
  systemPasswordPlain?: string
  orgId?: number
  jobTitle?: string
  remark?: string
}

export interface MyProfileUpdateRequest {
  realName?: string
  gender?: string
  idCardNo?: string
  birthday?: string
  age?: number
  nation?: string
  politicalStatus?: string
  hometown?: string
  maritalStatus?: string
  phone?: string
  email?: string
  graduateSchool?: string
  highestEducation?: string
  homeAddress?: string
  emergencyContact?: string
  emergencyContactPhone?: string
}

export interface JobInfoSaveRequest {
  employeeId: number
  employeeCode?: string
  joinDate?: string
  formalDate?: string
  workUnit?: string
  workMode?: string
  borrowDispatchDate?: string
  department?: string
  rankLevel?: string
  jobCategory?: string
  position?: string
  sortNo?: number
  is编制?: number
}

export interface SalaryStandardCreateRequest {
  salaryName: string
  amount: number
  jobTitle?: string
  orgId?: number
  orgName?: string
  description?: string
  sortNo?: number
  status: string
  effectiveDate?: string
  expireDate?: string
  versionNo?: string
  applicableScope?: string
}

export interface SalaryStandardUpdateRequest {
  salaryName?: string
  amount?: number
  jobTitle?: string
  orgId?: number
  orgName?: string
  description?: string
  sortNo?: number
  status?: string
  effectiveDate?: string
  expireDate?: string
  versionNo?: string
  applicableScope?: string
}

export interface CertificateSaveRequest {
  id?: number
  employeeId: number
  certificateName: string
  certificateNo?: string
  issueDate?: string
  certificateType?: string
  issueOrg?: string
  isPermanent?: number
  expireDate?: string
  certificateFileUrl?: string
}

export interface AssessmentSaveRequest {
  id?: number
  employeeId: number
  assessmentMonth?: string
  assessmentScore?: number
  assessmentGrade?: string
  remark?: string
}

export interface GrowthSaveRequest {
  id?: number
  employeeId: number
  startDate?: string
  endDate?: string
  workName?: string
}

export interface FamilySaveRequest {
  id?: number
  employeeId: number
  relation?: string
  name?: string
  birthday?: string
  idCardNo?: string
  politicalStatus?: string
  workUnitPosition?: string
}

export interface ChangeSaveRequest {
  id?: number
  employeeId: number
  currentDepartment?: string
  currentPosition?: string
  currentRankLevel?: string
  originalDepartment?: string
  originalPosition?: string
  originalRankLevel?: string
  reportDate?: string
  changeType?: string
  changeReason?: string
}

export interface ContractSaveRequest {
  id?: number
  employeeId: number
  contractName: string
  contractNo?: string
  contractStartDate?: string
  contractEndDate?: string
  contractFileUrl?: string
  remark?: string
}

export const employeeApi = {
  page: (query: EmployeePageQuery) =>
    get<PageData<EmployeeVO>>('/hr/employees', { params: query }),

  detail: (id: number) =>
    get<EmployeeDetailVO>(`/hr/employees/${id}`),

  create: (data: EmployeeCreateRequest) =>
    post('/hr/employees', data),

  update: (id: number, data: EmployeeUpdateRequest) =>
    put(`/hr/employees/${id}`, data),

  delete: (id: number) =>
    del(`/hr/employees/${id}`),

  list: (keyword?: string) =>
    get<EmployeeVO[]>('/hr/employees/list', { params: { keyword } }),

  getJobInfo: (employeeId: number) =>
    get<JobInfoVO>(`/hr/employees/${employeeId}/job-info`),

  saveJobInfo: (data: JobInfoSaveRequest) =>
    post('/hr/job-info', data),

  getCurrentSalary: (employeeId: number) =>
    get<number>(`/hr/employees/${employeeId}/current-salary`),

  // Sub-table APIs
  saveCertificate: (data: CertificateSaveRequest) =>
    post('/hr/certificates', data),

  deleteCertificate: (id: number) =>
    del(`/hr/certificates/${id}`),

  saveAssessment: (data: AssessmentSaveRequest) =>
    post('/hr/assessments', data),

  deleteAssessment: (id: number) =>
    del(`/hr/assessments/${id}`),

  saveGrowth: (data: GrowthSaveRequest) =>
    post('/hr/growth', data),

  deleteGrowth: (id: number) =>
    del(`/hr/growth/${id}`),

  saveFamily: (data: FamilySaveRequest) =>
    post('/hr/family', data),

  deleteFamily: (id: number) =>
    del(`/hr/family/${id}`),

  saveChange: (data: ChangeSaveRequest) =>
    post('/hr/changes', data),

  saveContract: (data: ContractSaveRequest) =>
    post('/hr/contracts', data),

  deleteContract: (id: number) =>
    del(`/hr/contracts/${id}`),
}

export const myProfileApi = {
  get: () => get<EmployeeDetailVO>('/hr/my-profile'),

  update: (data: MyProfileUpdateRequest) =>
    put('/hr/my-profile', data),
}

export const salaryApi = {
  page: (query: SalaryStandardPageQuery) =>
    get<PageData<SalaryStandardVO>>('/hr/salaries', { params: query }),

  detail: (id: number) =>
    get<SalaryStandardVO>(`/hr/salaries/${id}`),

  create: (data: SalaryStandardCreateRequest) =>
    post('/hr/salaries', data),

  update: (id: number, data: SalaryStandardUpdateRequest) =>
    put(`/hr/salaries/${id}`, data),

  delete: (id: number) =>
    del(`/hr/salaries/${id}`),
}
