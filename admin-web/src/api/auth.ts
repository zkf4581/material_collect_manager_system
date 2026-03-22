import { httpGet, httpRequest } from '@/api/http'
import { clearAdminToken, setAdminToken } from '@/utils/storage'

export interface AdminProfile {
  id: number
  username: string
  roleCode: string
  projectId: number | null
}

export interface LoginPayload {
  username: string
  password: string
}

export interface LoginResponse {
  token: string
  user: AdminProfile
}

export function getAdminProfile() {
  return httpGet<AdminProfile>('/auth/me')
}

export async function login(payload: LoginPayload) {
  const response = await httpRequest<LoginResponse>('/auth/login', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
  if (response.code === 0 && response.data?.token) {
    setAdminToken(response.data.token)
  }
  return response
}

export async function logout() {
  try {
    await httpRequest<void>('/auth/logout', {
      method: 'POST',
    })
  } finally {
    clearAdminToken()
  }
}
