export interface CustomerAttachment {
  id: number
  fileName: string
}

export interface CustomerRiskRecordForm {
  id?: number
  testDate: string
  testLimit: number | null
  trafficValue: number | null
  compositeScore: number | null
  thirdPartyScore: number | null
  remark: string
}

export interface CustomerDebtRecordForm {
  id?: number
  debtType: string
  debtAmount: number | null
  advancePaidAmount: number | null
  installmentAmount: number | null
  repaymentDay: number | null
  remark: string
}

export interface CustomerContractRecordForm {
  id?: number
  customerName: string
  companyName: string
  creditCode: string
  contractNo: string
  contractName: string
  remark: string
  fileIds: number[]
}

export interface CustomerArchiveForm {
  customerNo: string
  customerName: string
  gender: string
  idCard: string
  birthday: string
  mobile: string
  companyName: string
  creditCode: string
  establishedDate: string
  industry: string
  businessAddress: string
  bankName: string
  bankAccount: string
  recommenderName: string
  recommenderRate: number | null
  serviceFee: number | null
  auditStatus: string
  bizStatus: string
  taxRegistrationNormal: boolean | null
  archiveAttachments: CustomerAttachment[]
  riskRecords: CustomerRiskRecordForm[]
  debtRecords: CustomerDebtRecordForm[]
  contractRecords: CustomerContractRecordForm[]
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
    testDate?: string | null
    testLimit?: number | null
    trafficValue?: number | null
    compositeScore?: number | null
    thirdPartyScore?: number | null
    remark?: string
  }>
  debtRecords: Array<{
    id?: number
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
    customerName: string
    companyName: string
    creditCode: string
    contractNo?: string
    contractName: string
    remark?: string
    fileIds: number[]
  }>
}

export const normalizeCustomerAttachments = (attachments: CustomerAttachment[]) =>
  attachments.filter((item) => Number(item.id) > 0 && item.fileName.trim())

const emptyRiskRecord = (): CustomerRiskRecordForm => ({
  testDate: '',
  testLimit: null,
  trafficValue: null,
  compositeScore: null,
  thirdPartyScore: null,
  remark: '',
})

const emptyDebtRecord = (): CustomerDebtRecordForm => ({
  debtType: '',
  debtAmount: null,
  advancePaidAmount: null,
  installmentAmount: null,
  repaymentDay: null,
  remark: '',
})

const emptyContractRecord = (): CustomerContractRecordForm => ({
  customerName: '',
  companyName: '',
  creditCode: '',
  contractNo: '',
  contractName: '',
  remark: '',
  fileIds: [],
})

export const createEmptyCustomerArchiveForm = (): CustomerArchiveForm => ({
  customerNo: '',
  customerName: '',
  gender: '',
  idCard: '',
  birthday: '',
  mobile: '',
  companyName: '',
  creditCode: '',
  establishedDate: '',
  industry: '',
  businessAddress: '',
  bankName: '',
  bankAccount: '',
  recommenderName: '',
  recommenderRate: null,
  serviceFee: null,
  auditStatus: '',
  bizStatus: '',
  taxRegistrationNormal: null,
  archiveAttachments: [],
  riskRecords: [emptyRiskRecord()],
  debtRecords: [emptyDebtRecord()],
  contractRecords: [emptyContractRecord()],
})

export const buildCustomerArchivePayload = (form: CustomerArchiveForm): CustomerArchivePayload => ({
  customerNo: form.customerNo.trim() || undefined,
  customerName: form.customerName.trim(),
  gender: form.gender || undefined,
  idCard: form.idCard.trim() || undefined,
  birthday: form.birthday || null,
  mobile: form.mobile.trim() || undefined,
  companyName: form.companyName.trim() || undefined,
  creditCode: form.creditCode.trim() || undefined,
  establishedDate: form.establishedDate || null,
  industry: form.industry.trim() || undefined,
  businessAddress: form.businessAddress.trim() || undefined,
  bankName: form.bankName.trim() || undefined,
  bankAccount: form.bankAccount.trim() || undefined,
  recommenderName: form.recommenderName.trim() || undefined,
  recommenderRate: form.recommenderRate,
  serviceFee: form.serviceFee,
  bizStatus: form.bizStatus || undefined,
  taxRegistrationNormal: form.taxRegistrationNormal,
  archiveFileIds: normalizeCustomerAttachments(form.archiveAttachments).map((item) => item.id),
  riskRecords: form.riskRecords
    .filter((item) => item.testDate || item.testLimit != null || item.trafficValue != null || item.compositeScore != null || item.thirdPartyScore != null || item.remark.trim())
    .map((item) => ({
      id: item.id,
      testDate: item.testDate || null,
      testLimit: item.testLimit,
      trafficValue: item.trafficValue,
      compositeScore: item.compositeScore,
      thirdPartyScore: item.thirdPartyScore,
      remark: item.remark.trim() || undefined,
    })),
  debtRecords: form.debtRecords
    .filter((item) => item.debtType.trim() || item.debtAmount != null || item.advancePaidAmount != null || item.installmentAmount != null || item.repaymentDay != null || item.remark.trim())
    .map((item) => {
      const debtAmount = item.debtAmount ?? 0
      const advancePaidAmount = item.advancePaidAmount ?? 0
      return {
        id: item.id,
        debtType: item.debtType.trim(),
        debtAmount: item.debtAmount,
        totalRepaymentAmount: debtAmount,
        advancePaidAmount: item.advancePaidAmount,
        pendingAmount: Math.max(debtAmount - advancePaidAmount, 0),
        installmentAmount: item.installmentAmount,
        repaymentDay: item.repaymentDay,
        remark: item.remark.trim() || undefined,
      }
    }),
  contractRecords: form.contractRecords
    .filter((item) => item.customerName.trim() || item.companyName.trim() || item.creditCode.trim() || item.contractNo.trim() || item.contractName.trim() || item.remark.trim() || item.fileIds.length > 0)
    .map((item) => ({
      id: item.id,
      customerName: item.customerName.trim(),
      companyName: item.companyName.trim(),
      creditCode: item.creditCode.trim(),
      contractNo: item.contractNo.trim() || undefined,
      contractName: item.contractName.trim(),
      remark: item.remark.trim() || undefined,
      fileIds: item.fileIds.filter((id) => Number(id) > 0),
    })),
})
