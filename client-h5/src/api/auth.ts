import { request } from './http'
import { clearToken, setToken } from '@/utils/storage'

export interface LoginPayload {
  username: string
  password: string
}

export async function login(payload: LoginPayload) {
  const res = await request<{ token: string }>('/auth/login', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
  if (res.code === 0 && res.data?.token) {
    setToken(res.data.token)
  }
  return res
}

export function logout() {
  clearToken()
}
