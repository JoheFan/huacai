import type {
  PermissionCatalogRecord,
  PermissionItemRecord,
  RolePermissionProfile,
  RolePermissionProfileUpdatePayload,
} from '../../../api/system'

const BUSINESS_MODULE_LABELS: Record<string, string> = {
  customers: '客户管理',
  'customer-risks': '风险评估',
  'customer-debts': '负债登记',
  opportunities: '商机管理',
  'loan-orders': '借贷管理',
  repayments: '还款明细',
}

const DATA_SCOPE_ORDER = ['ALL', 'ORG_AND_SUB', 'ORG', 'SELF'] as const

const normalizePermissionCodes = (
  codes: string[] | null | undefined,
  items: PermissionItemRecord[],
) => {
  const allowed = new Set(items.map((item) => item.permissionCode))
  const seen = new Set<string>()
  const normalized: string[] = []
  for (const code of codes ?? []) {
    if (!allowed.has(code) || seen.has(code)) {
      continue
    }
    seen.add(code)
    normalized.push(code)
  }
  return normalized
}

export const buildRoleScopedModuleItems = (
  pagePermissions: string[],
  pageItems: PermissionItemRecord[],
  dataScopes: Record<string, string>,
) =>
  pageItems
    .filter((item) => pagePermissions.includes(item.permissionCode))
    .map((item) => item.moduleCode)
    .filter((moduleCode): moduleCode is string => Boolean(moduleCode) && Boolean(BUSINESS_MODULE_LABELS[moduleCode]))
    .filter((moduleCode, index, all) => all.indexOf(moduleCode) === index)
    .map((moduleCode) => ({
      moduleCode,
      moduleName: BUSINESS_MODULE_LABELS[moduleCode],
      scopeType: DATA_SCOPE_ORDER.includes((dataScopes[moduleCode] ?? 'SELF') as (typeof DATA_SCOPE_ORDER)[number])
        ? dataScopes[moduleCode] ?? 'SELF'
        : 'SELF',
    }))

export const buildRolePermissionPayload = (
  pagePermissions: string[],
  buttonPermissions: string[],
  dataScopes: Record<string, string>,
  catalog: PermissionCatalogRecord,
): RolePermissionProfileUpdatePayload => {
  const normalizedPages = normalizePermissionCodes(pagePermissions, catalog.pageItems)
  const normalizedButtons = normalizePermissionCodes(buttonPermissions, catalog.buttonItems)
  const scopedModules = buildRoleScopedModuleItems(normalizedPages, catalog.pageItems, dataScopes)
  return {
    pagePermissions: normalizedPages,
    buttonPermissions: normalizedButtons,
    dataScopes: Object.fromEntries(scopedModules.map((item) => [item.moduleCode, item.scopeType])),
  }
}

export const createRolePermissionDraft = (profile: RolePermissionProfile) => ({
  pagePermissions: [...profile.pagePermissions],
  buttonPermissions: [...profile.buttonPermissions],
  dataScopes: { ...profile.dataScopes },
})

export const summarizeRolePermission = (profile: RolePermissionProfile) =>
  `${profile.roleName} · ${profile.permissionSummary}`
