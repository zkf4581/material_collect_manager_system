export interface HttpResult<T> {
  code: number
  message: string
  data: T
}

const BASE_URL = import.meta.env.VITE_API_BASE_URL ?? '/api'

export async function httpRequest<T>(path: string, init?: RequestInit): Promise<HttpResult<T>> {
  const token = localStorage.getItem('admin_token')
  const response = await fetch(`${BASE_URL}${path}`, {
    ...init,
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...init?.headers,
    },
  })

  if (!response.ok) {
    throw new Error(`请求失败：${response.status}`)
  }

  return (await response.json()) as HttpResult<T>
}

export function httpGet<T>(path: string) {
  return httpRequest<T>(path)
}
