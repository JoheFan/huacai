import {
  createEmptyCustomerArchiveForm,
  type CustomerArchiveForm,
  type CustomerContractRecordForm,
  type CustomerDebtRecordForm,
  type CustomerRiskRecordForm,
} from './customerModels.ts'

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

export { createEmptyCustomerArchiveForm }

export const appendRiskRecord = (form: CustomerArchiveForm) => {
  form.riskRecords.push(emptyRiskRecord())
}

export const appendDebtRecord = (form: CustomerArchiveForm) => {
  form.debtRecords.push(emptyDebtRecord())
}

export const appendContractRecord = (form: CustomerArchiveForm) => {
  form.contractRecords.push(emptyContractRecord())
}

export const calculateCustomerAge = (birthday: string, now: Date = new Date()) => {
  if (!birthday) {
    return null
  }
  const birthdayDate = new Date(`${birthday}T00:00:00`)
  if (Number.isNaN(birthdayDate.getTime())) {
    return null
  }

  let age = now.getFullYear() - birthdayDate.getFullYear()
  const monthGap = now.getMonth() - birthdayDate.getMonth()
  if (monthGap < 0 || (monthGap === 0 && now.getDate() < birthdayDate.getDate())) {
    age -= 1
  }
  return age >= 0 ? age : null
}

export const toTotalRepaymentAmount = (debtAmount: number | null | undefined) => debtAmount ?? 0

export const derivePendingDebtAmount = (
  debtAmount: number | null | undefined,
  advancePaidAmount: number | null | undefined,
) => Math.max((debtAmount ?? 0) - (advancePaidAmount ?? 0), 0)
