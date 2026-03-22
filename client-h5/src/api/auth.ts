import { request } from './http'
import { clearToken, setToken } from '@/utils/storage'

export interface LoginPayload {
  username: string
  password: string
}

export interface CurrentUser {
  id: number
  username: string
  roleCode: string
  projectId: number | null
}

export interface LoginResponse {
  token: string
  user: CurrentUser
}

export async function login(payload: LoginPayload) {
  const res = await request<LoginResponse>('/auth/login', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
  if (res.code === 0 && res.data?.token) {
    setToken(res.data.token)
  }
  return res
}

export function getCurrentUser() {
  return request<CurrentUser>('/auth/me')
}

export function logout() {
  clearToken()
}
