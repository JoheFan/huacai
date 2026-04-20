import { get, post, put } from './http'

export const AUTH_TOKEN_KEY = 'huacai-token'
export const AUTH_USER_KEY = 'huacai-user'

export interface CurrentUserInfo {
  id: number
  username: string
  realName: string
  orgId: number | null
  orgName: string
  identityType: string
  roles: string[]
  primaryRoleCode: string
  primaryRoleName: string
  permissions: string[]
  pagePermissions: string[]
  buttonPermissions: string[]
  dataScopes: Record<string, string>
  permissionSummary: string
  visibleModuleKeys?: string[]
}

export interface LoginPayload {
  username: string
  password: string
}

export interface LoginResult {
  token: string
  tokenType: string
  expiresIn: number
  userInfo: CurrentUserInfo
}

export interface ChangePasswordPayload {
  oldPassword: string
  newPassword: string
}

export const loginApi = (payload: LoginPayload) => post<LoginResult>('/auth/login', payload)

export const fetchCurrentUserApi = () => get<CurrentUserInfo>('/auth/me')

export const logoutApi = () => post<void>('/auth/logout')

export const changePasswordApi = (payload: ChangePasswordPayload) => put<void>('/auth/password', payload)
