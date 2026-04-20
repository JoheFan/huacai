import axios, { type AxiosRequestConfig } from 'axios'
import { handleUnauthorizedResponse } from './http-auth'

export interface ApiEnvelope<T> {
  code: number
  message: string
  data: T
  traceId: string
}

export interface PageData<T> {
  records: T[]
  total: number
  pageNum: number
  pageSize: number
}

const http = axios.create({
  baseURL: '/api/v1',
  timeout: 15000,
})

http.interceptors.request.use((config) => {
  const token = localStorage.getItem('huacai-token')

  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }

  return config
})

http.interceptors.response.use(
  (response) => response,
  (error) => {
    if (axios.isAxiosError(error)) {
      handleUnauthorizedResponse(error.response?.status, {
        currentPath: typeof window === 'undefined' ? '/login' : window.location.pathname,
        removeItem: (key) => localStorage.removeItem(key),
        redirectTo: (path) => window.location.assign(path),
      })
    }
    return Promise.reject(error)
  },
)

const unwrap = <T>(payload: ApiEnvelope<T>): T => {
  if (payload.code !== 0) {
    throw new Error(payload.message || '请求失败')
  }
  return payload.data
}

const mapError = (error: unknown) => {
  if (axios.isAxiosError(error)) {
    const message =
      (error.response?.data as Partial<ApiEnvelope<unknown>> | undefined)?.message ?? error.message ?? '请求失败'
    return new Error(message)
  }
  return error instanceof Error ? error : new Error('请求失败')
}

export const request = async <T>(config: AxiosRequestConfig) => {
  try {
    const response = await http.request<ApiEnvelope<T>>(config)
    return unwrap(response.data)
  } catch (error) {
    throw mapError(error)
  }
}

export const get = <T>(url: string, config?: AxiosRequestConfig) => request<T>({ ...config, method: 'GET', url })

export const post = <T>(url: string, data?: unknown, config?: AxiosRequestConfig) =>
  request<T>({ ...config, method: 'POST', url, data })

export const put = <T>(url: string, data?: unknown, config?: AxiosRequestConfig) =>
  request<T>({ ...config, method: 'PUT', url, data })

export const del = <T>(url: string, config?: AxiosRequestConfig) =>
  request<T>({ ...config, method: 'DELETE', url })

export default http
