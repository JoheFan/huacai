import { get, post, put, del, type PageData } from './http'

// ========== Organization ==========

export interface OrgRecord {
  id: number
  parentId: number
  parentName: string
  orgName: string
  orgType: string
  sortNo: number
  status: string
  remark: string
  createdAt: string
  updatedAt: string
  children?: OrgRecord[]
}

export interface OrgSavePayload {
  parentId?: number
  orgName: string
  orgType: string
  sortNo?: number
  status: string
  remark?: string
}

export const fetchOrgTree = () => get<OrgRecord[]>('/system/orgs/tree')

export const fetchOrgDetail = (id: number) => get<OrgRecord>(`/system/orgs/${id}`)

export const createOrg = (payload: OrgSavePayload) => post<void>('/system/orgs', payload)

export const updateOrg = (id: number, payload: Partial<OrgSavePayload>) => 
  put<void>(`/system/orgs/${id}`, payload)

export const deleteOrg = (id: number) => del<void>(`/system/orgs/${id}`)

export const updateOrgStatus = (id: number, status: string) => 
  put<void>(`/system/orgs/${id}/status`, { status })

// ========== User ==========

export interface UserRecord {
  id: number
  username: string
  employeeCode: string
  realName: string
  phone: string
  email: string
  orgId: number
  orgName: string
  identityType: string
  primaryRoleId: number | null
  primaryRoleCode: string
  primaryRoleName: string
  permissionSummary: string
  jobTitle: string
  employmentStatus: string
  accountStatus: string
  lastLoginAt: string
  remark: string
  createdAt: string
  updatedAt: string
}

export interface UserQuery {
  pageNum: number
  pageSize: number
  keyword?: string
  orgId?: number
  accountStatus?: string
  employmentStatus?: string
}

export interface UserSavePayload {
  username: string
  password: string
  employeeCode?: string
  realName: string
  phone?: string
  email?: string
  orgId?: number
  jobTitle?: string
  employmentStatus?: string
  accountStatus?: string
  remark?: string
  primaryRoleId: number
}

export interface UserUpdatePayload {
  username?: string
  employeeCode?: string
  realName?: string
  phone?: string
  email?: string
  orgId?: number
  jobTitle?: string
  employmentStatus?: string
  accountStatus?: string
  remark?: string
  primaryRoleId?: number
}

export const fetchUserPage = (params: UserQuery) => get<PageData<UserRecord>>('/system/users', { params })

export const fetchUserDetail = (id: number) => get<UserRecord>(`/system/users/${id}`)

export const createUser = (payload: UserSavePayload) => post<void>('/system/users', payload)

export const updateUser = (id: number, payload: UserUpdatePayload) => 
  put<void>(`/system/users/${id}`, payload)

export const deleteUser = (id: number) => del<void>(`/system/users/${id}`)

export const updateUserStatus = (id: number, status: string) => 
  put<void>(`/system/users/${id}/status`, { status })

export const resetUserPassword = (id: number, newPassword: string) => 
  put<void>(`/system/users/${id}/reset-password`, { newPassword })

export const fetchUserRoles = (id: number) => get<number[]>(`/system/users/${id}/roles`)

export const assignUserRoles = (id: number, roleIds: number[]) => 
  put<void>(`/system/users/${id}/roles`, { roleIds })

// ========== Role ==========

export interface RoleRecord {
  id: number
  roleCode: string
  roleName: string
  identityType: string
  dataScope: string
  permissionSummary: string
  status: string
  remark: string
  createdAt: string
  updatedAt: string
}

export interface PermissionItemRecord {
  id: number
  permissionCode: string
  permissionName: string
  permissionType: string
  moduleCode: string
  routePath?: string
  buttonCode?: string
}

export interface PermissionCatalogRecord {
  pageItems: PermissionItemRecord[]
  buttonItems: PermissionItemRecord[]
}

export interface UserPermissionProfile {
  userId: number
  identityType: string
  primaryRoleId: number | null
  primaryRoleCode: string
  primaryRoleName: string
  rolePagePermissions: string[]
  roleButtonPermissions: string[]
  roleDataScopes: Record<string, string>
  effectivePagePermissions: string[]
  effectiveButtonPermissions: string[]
  dataScopes: Record<string, string>
  permissionSummary: string
}

export interface UserPermissionProfileUpdatePayload {
  pagePermissions: string[]
  buttonPermissions: string[]
  dataScopes: Record<string, string>
}

export interface RoleQuery {
  pageNum: number
  pageSize: number
  keyword?: string
  status?: string
}

export interface RoleSavePayload {
  roleCode: string
  roleName: string
  identityType: string
  dataScope?: string
  status?: string
  remark?: string
}

export interface RoleUpdatePayload {
  roleCode?: string
  roleName?: string
  identityType?: string
  dataScope?: string
  status?: string
  remark?: string
}

export interface RolePermissionProfile {
  roleId: number
  roleCode: string
  roleName: string
  identityType: string
  pagePermissions: string[]
  buttonPermissions: string[]
  dataScopes: Record<string, string>
  permissionSummary: string
}

export interface RolePermissionProfileUpdatePayload {
  pagePermissions: string[]
  buttonPermissions: string[]
  dataScopes: Record<string, string>
}

export const fetchRolePage = (params: RoleQuery) => get<PageData<RoleRecord>>('/system/roles', { params })

export const fetchRoleOptions = () => get<RoleRecord[]>('/system/roles/options')

export const fetchRoleDetail = (id: number) => get<RoleRecord>(`/system/roles/${id}`)

export const createRole = (payload: RoleSavePayload) => post<void>('/system/roles', payload)

export const updateRole = (id: number, payload: RoleUpdatePayload) => 
  put<void>(`/system/roles/${id}`, payload)

export const deleteRole = (id: number) => del<void>(`/system/roles/${id}`)

export const updateRoleStatus = (id: number, status: string) => 
  put<void>(`/system/roles/${id}/status`, { status })

export const fetchRolePermissionProfile = (id: number) =>
  get<RolePermissionProfile>(`/system/roles/${id}/permissions`)

export const updateRolePermissionProfile = (id: number, payload: RolePermissionProfileUpdatePayload) =>
  put<void>(`/system/roles/${id}/permissions`, payload)

// ========== Permission ==========

export const fetchPermissionCatalog = () => get<PermissionCatalogRecord>('/system/permissions/catalog')

export const fetchUserPermissionProfile = (id: number) =>
  get<UserPermissionProfile>(`/system/users/${id}/permissions`)

export const updateUserPermissionProfile = (id: number, payload: UserPermissionProfileUpdatePayload) =>
  put<void>(`/system/users/${id}/permissions`, payload)

// ========== Operation Log ==========

export interface OperationLogRecord {
  id: number
  userId: number | null
  userName: string | null
  moduleCode: string | null
  bizType: string | null
  actionCode: string | null
  actionDesc: string | null
  targetType: string | null
  targetId: number | null
  requestUri: string | null
  requestMethod: string | null
  resultCode: string | null
  resultMsg: string | null
  ip: string | null
  createdAt: string
}

export interface OperationLogQuery {
  pageNum: number
  pageSize: number
  keyword?: string
  moduleCode?: string
  bizType?: string
  userId?: number | null
  startDate?: string
  endDate?: string
}

export const fetchOperationLogPage = (params: OperationLogQuery) =>
  get<PageData<OperationLogRecord>>('/system/operation-logs', { params })

export const fetchOperationLogDetail = (id: number) =>
  get<OperationLogRecord>(`/system/operation-logs/${id}`)
