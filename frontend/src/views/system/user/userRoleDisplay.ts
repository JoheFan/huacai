const roleNameMap: Record<string, string> = {
  ADMIN: '系统管理员',
  DEPT_ADMIN: '部门管理员',
  STAFF: '普通用户',
}

export const formatUserRoleNames = (roleCodes: string[] | null | undefined) => {
  if (!roleCodes || roleCodes.length === 0) {
    return '-'
  }
  return roleCodes.map((code) => roleNameMap[code] || code).join(' / ')
}

export const formatPrimaryRole = (
  primaryRoleName: string | null | undefined,
  primaryRoleCode: string | null | undefined,
) => {
  if (primaryRoleName) {
    return primaryRoleName
  }
  if (primaryRoleCode) {
    return roleNameMap[primaryRoleCode] || primaryRoleCode
  }
  return '未设置'
}
