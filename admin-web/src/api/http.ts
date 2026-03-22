export interface HttpResult<T> {
  code: number
  message: string
  data: T
}

export async function httpGet<T>(_url: string): Promise<HttpResult<T>> {
  return {
    code: 0,
    message: 'success',
    data: {} as T,
  }
}
