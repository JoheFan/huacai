export interface LoanOverviewColumn {
  key: string
  label: string
}

export interface LoanOverviewMode {
  scopeLabel: string
  actionLabel: string
  columns: LoanOverviewColumn[]
}

const selfColumns: LoanOverviewColumn[] = [
  { key: 'customerNo', label: '客户ID' },
  { key: 'customerName', label: '客户名称' },
  { key: 'mobile', label: '联系电话' },
  { key: 'companyName', label: '企业名称' },
  { key: 'depositGoldAmount', label: '理财/定存/黄金' },
  { key: 'creditCardRepaymentAmount', label: '垫还信用卡网贷' },
  { key: 'balanceAmount', label: '余额（含手续费）' },
  { key: 'loanCount', label: '笔数' },
  { key: 'monthlyInterestAmount', label: '每月利息' },
  { key: 'totalLoanAmount', label: '总贷款金额(我方)' },
  { key: 'firstLoanDate', label: '借款日期（第一笔）' },
  { key: 'repaidAmount', label: '已还款（我方）' },
  { key: 'pendingAmount', label: '待还款（我方）' },
  { key: 'remark', label: '备注' },
]

const bankColumns: LoanOverviewColumn[] = [
  { key: 'customerNo', label: '客户ID' },
  { key: 'customerName', label: '客户名称' },
  { key: 'mobile', label: '联系电话' },
  { key: 'companyName', label: '企业名称' },
  { key: 'totalLoanAmount', label: '总贷款金额（银行）' },
  { key: 'firstLoanDate', label: '出款日期（银行）' },
  { key: 'repaidAmount', label: '已还款（银行）' },
  { key: 'pendingAmount', label: '待还款（银行）' },
  { key: 'remark', label: '备注' },
]

export const getLoanOverviewMode = (capitalSourceType: string | null | undefined): LoanOverviewMode => {
  if (capitalSourceType === 'BANK') {
    return {
      scopeLabel: '银行',
      actionLabel: '查看',
      columns: bankColumns,
    }
  }

  return {
    scopeLabel: '我方',
    actionLabel: '查看',
    columns: selfColumns,
  }
}
