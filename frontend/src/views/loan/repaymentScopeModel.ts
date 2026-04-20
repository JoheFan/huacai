export interface RepaymentScopeInput {
  customerId?: number | null
  customerName?: string | null
  loanOrderId?: number | null
  capitalSourceType?: string | null
}

export interface RepaymentScopeState {
  isScoped: boolean
  title: string
  summaryLabel: string
}

const getSourceLabel = (capitalSourceType?: string | null) => {
  if (capitalSourceType === 'BANK') {
    return '银行'
  }
  if (capitalSourceType === 'SELF') {
    return '我方'
  }
  return ''
}

export const buildRepaymentScopeState = (input: RepaymentScopeInput): RepaymentScopeState => {
  const sourceLabel = getSourceLabel(input.capitalSourceType)

  if (input.loanOrderId) {
    return {
      isScoped: true,
      title: `借贷单 #${input.loanOrderId} ${sourceLabel ? `还款（${sourceLabel}）` : '还款'}详情`,
      summaryLabel: '总计',
    }
  }

  if (input.customerId) {
    const customerName = input.customerName?.trim() || `客户 #${input.customerId}`
    return {
      isScoped: true,
      title: `${customerName} ${sourceLabel ? `还款（${sourceLabel}）` : '还款'}详情`,
      summaryLabel: '总计',
    }
  }

  return {
    isScoped: false,
    title: '还款明细',
    summaryLabel: '',
  }
}
