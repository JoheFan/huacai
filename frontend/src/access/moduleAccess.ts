import type { CurrentUserInfo } from '../api/auth.ts'
import { flatMenuItems, menuGroups } from '../router/menu.ts'
import type { MenuGroup, MenuItem } from '../types/navigation.ts'

const LEGACY_MODULE_PAGE_PERMISSIONS: Record<string, string> = {
  customers: '/customers',
  'customer-risks': '/customer-risks',
  'customer-debts': '/customer-debts',
  opportunities: '/opportunities',
  'loan-orders-self': '/loan-orders-self',
  'loan-orders-bank': '/loan-orders-bank',
  'increment-details': '/increment-details',
  'increment-overview': '/analysis/increment-overview',
  'employee-performance': '/analysis/performance',
  repayments: '/repayments',
}

const findMenuItem = (path: string) => flatMenuItems.find((item) => item.path === path)

const deriveLegacyPagePermissions = (user: CurrentUserInfo | null | undefined) => {
  if (!user?.visibleModuleKeys?.length) {
    return []
  }

  const pagePermissions = new Set<string>()
  if (!isAdminUser(user)) {
    pagePermissions.add('/welcome')
  }
  for (const moduleKey of user.visibleModuleKeys) {
    const pagePermission = LEGACY_MODULE_PAGE_PERMISSIONS[moduleKey]
    if (pagePermission) {
      pagePermissions.add(pagePermission)
    }
  }
  return [...pagePermissions]
}

const getEffectivePagePermissions = (user: CurrentUserInfo | null | undefined) => {
  if (!user) {
    return []
  }
  if (user.pagePermissions?.length) {
    return user.pagePermissions
  }
  return deriveLegacyPagePermissions(user)
}

export const isAdminUser = (user: CurrentUserInfo | null | undefined) =>
  Boolean(user?.identityType === 'SUPER_ADMIN' || user?.roles?.includes('ADMIN'))

export const getDefaultHomePath = (user: CurrentUserInfo | null | undefined) => {
  if (!user) {
    return '/dashboard'
  }
  return isAdminUser(user) ? '/dashboard' : '/welcome'
}

export const canAccessPagePermission = (
  pagePermission: string | undefined,
  user: CurrentUserInfo | null | undefined,
  options?: { requiresAdmin?: boolean; staffHome?: boolean },
) => {
  if (!user) {
    return false
  }
  if (isAdminUser(user)) {
    return !(options?.staffHome)
  }
  if (options?.staffHome) {
    return true
  }
  if (options?.requiresAdmin) {
    return false
  }
  if (!pagePermission) {
    return false
  }
  return getEffectivePagePermissions(user).includes(pagePermission)
}

export const canAccessMenuItem = (item: MenuItem, user: CurrentUserInfo | null | undefined) => {
  if (!user) {
    return false
  }
  if (isAdminUser(user)) {
    return !item.staffHome
  }
  const visibleChildren = item.children?.filter((child) => canAccessMenuItem(child, user)) ?? []
  if (visibleChildren.length > 0) {
    return true
  }
  return canAccessPagePermission(item.path, user, {
    requiresAdmin: item.requiresAdmin,
    staffHome: item.staffHome,
  })
}

export const canAccessModule = (
  moduleKey: string | undefined,
  user: CurrentUserInfo | null | undefined,
  options?: { requiresAdmin?: boolean; staffHome?: boolean },
) => {
  const pagePermission = moduleKey ? LEGACY_MODULE_PAGE_PERMISSIONS[moduleKey] : undefined
  return canAccessPagePermission(pagePermission, user, options)
}

const filterMenuItem = (item: MenuItem, user: CurrentUserInfo | null | undefined): MenuItem | null => {
  if (!canAccessMenuItem(item, user)) {
    return null
  }

  const children = item.children?.map((child) => filterMenuItem(child, user)).filter(Boolean) as MenuItem[] | undefined
  return children && children.length > 0 ? { ...item, children } : { ...item, children: undefined }
}

export const canAccessPath = (path: string, user: CurrentUserInfo | null | undefined) => {
  if (path === '/' || path === '/login') {
    return true
  }
  // Staff users can access /welcome regardless of menu configuration
  if (!isAdminUser(user) && getEffectivePagePermissions(user).includes(path)) {
    return true
  }
  const item = findMenuItem(path)
  if (!item) {
    return false
  }
  return canAccessMenuItem(item, user)
}

export const getVisibleMenuGroups = (user: CurrentUserInfo | null | undefined): MenuGroup[] =>
  menuGroups
    .map((group) => ({
      ...group,
      items: group.items.map((item) => filterMenuItem(item, user)).filter(Boolean) as MenuItem[],
    }))
    .filter((group) => group.items.length > 0)

export const getAccessibleBusinessItems = (user: CurrentUserInfo | null | undefined) =>
  flatMenuItems.filter((item) => item.moduleKey && canAccessMenuItem(item, user))
