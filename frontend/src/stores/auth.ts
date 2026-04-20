import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import {
  AUTH_TOKEN_KEY,
  AUTH_USER_KEY,
  fetchCurrentUserApi,
  loginApi,
  logoutApi,
  type CurrentUserInfo,
  type LoginPayload,
} from '../api/auth'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem(AUTH_TOKEN_KEY) ?? '')
  const initialUser = localStorage.getItem(AUTH_USER_KEY)
  const user = ref<CurrentUserInfo | null>(initialUser ? JSON.parse(initialUser) : null)

  const isLoggedIn = computed(() => Boolean(token.value))

  const persist = () => {
    if (token.value) {
      localStorage.setItem(AUTH_TOKEN_KEY, token.value)
    } else {
      localStorage.removeItem(AUTH_TOKEN_KEY)
    }

    if (user.value) {
      localStorage.setItem(AUTH_USER_KEY, JSON.stringify(user.value))
    } else {
      localStorage.removeItem(AUTH_USER_KEY)
    }
  }

  const login = async (payload: LoginPayload) => {
    const result = await loginApi(payload)
    token.value = result.token
    user.value = result.userInfo
    persist()
  }

  const refreshUser = async () => {
    if (!token.value) {
      return null
    }
    const currentUser = await fetchCurrentUserApi()
    user.value = currentUser
    persist()
    return currentUser
  }

  const logout = async () => {
    if (token.value) {
      try {
        await logoutApi()
      } catch {
        // 退出登录以清理本地状态为主，不阻断用户操作。
      }
    }
    token.value = ''
    user.value = null
    persist()
  }

  return {
    token,
    user,
    isLoggedIn,
    login,
    refreshUser,
    logout,
  }
})
