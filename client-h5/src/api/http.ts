export interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

const BASE_URL = import.meta.env.VITE_API_BASE_URL ?? '/api'

export async function request<T>(path: string, init?: RequestInit): Promise<ApiResponse<T>> {
  const token = localStorage.getItem('h5_token')
  const response = await fetch(`${BASE_URL}${path}`, {
    ...init,
    headers: {
      ...(init?.body instanceof FormData ? {} : { 'Content-Type': 'application/json' }),
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...init?.headers,
    },
  })

  const data = (await response.json()) as ApiResponse<T>

  if (!response.ok || data.code !== 0) {
    throw new Error(data.message || `请求失败：${response.status}`)
  }

  return data
}

export function uploadRequest<T>(path: string, formData: FormData) {
  return request<T>(path, {
    method: 'POST',
    body: formData,
  })
}
