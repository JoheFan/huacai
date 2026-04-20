interface UnauthorizedHandlerOptions {
  currentPath: string
  removeItem: (key: string) => void
  redirectTo: (path: string) => void
}

const TOKEN_KEY = 'huacai-token'
const USER_KEY = 'huacai-user'

export const handleUnauthorizedResponse = (status: number | undefined, options: UnauthorizedHandlerOptions) => {
  if (status !== 401) {
    return false
  }

  options.removeItem(TOKEN_KEY)
  options.removeItem(USER_KEY)

  if (options.currentPath !== '/login') {
    options.redirectTo('/login')
  }

  return true
}
