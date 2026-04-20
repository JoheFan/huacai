export interface LoanOrderFormMode {
  isEditing: boolean
  showManagedFields: boolean
  createHint: string
}

const CREATE_HINT =
  '新增时“借贷状态”“当前余额”由系统根据借款金额和后续还款自动回算，保存后在列表回显。'

export const getLoanOrderFormMode = (editingId: number | null): LoanOrderFormMode => {
  const isEditing = editingId !== null

  return {
    isEditing,
    showManagedFields: isEditing,
    createHint: isEditing ? '' : CREATE_HINT,
  }
}
