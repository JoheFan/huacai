import type { CurrentUserInfo } from '../api/auth.ts'
import { canAccessPath, getDefaultHomePath } from '../access/moduleAccess.ts'

export interface StartupAuthStore {
  token: string
  refreshUser: () => Promise<CurrentUserInfo | null | void>
  logout: () => Promise<unknown>
}

export interface StartupRouter {
  currentRoute: {
    value: {
      path: string
    }
  }
  replace: (path: string) => Promise<unknown> | unknown
}

export const restoreAuthSession = async (authStore: StartupAuthStore, router: StartupRouter) => {
  if (!authStore.token) {
    return false
  }

  try {
    const currentUser = (await authStore.refreshUser()) ?? null
    const currentPath = router.currentRoute.value.path
    const fallbackPath = getDefaultHomePath(currentUser)

    if (currentPath === '/login' || !canAccessPath(currentPath, currentUser)) {
      await router.replace(fallbackPath)
    }
    return true
  } catch {
    await authStore.logout()
    if (router.currentRoute.value.path !== '/login') {
      await router.replace('/login')
    }
    return false
  }
}
