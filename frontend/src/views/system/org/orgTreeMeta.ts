export const getOrgManagementLabel = (orgType: string | null | undefined) => (orgType === 'COMPANY' ? '是' : '否')

export const getOrgParentLabel = (parentName: string | null | undefined) => {
  const trimmed = parentName?.trim()
  return trimmed ? trimmed : '顶层组织'
}
