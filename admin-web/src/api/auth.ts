import { httpGet } from '@/api/http'

export interface AdminProfile {
  id: number
  name: string
  role: string
}

export function getAdminProfile() {
  return httpGet<AdminProfile>('/api/auth/me')
}
