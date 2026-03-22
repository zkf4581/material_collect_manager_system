import { request } from './http'

export interface RewardItem {
  id: number
  name: string
  pointsCost: number
  stock: number
  status: string
}

export interface ExchangeOrder {
  id: number
  orderNo: string
  projectId: number
  workerId: number
  rewardItemId: number
  quantity: number
  totalPoints: number
  status: string
}

export function getRewardItems() {
  return request<RewardItem[]>('/reward-items')
}

export function getMyExchangeOrders() {
  return request<ExchangeOrder[]>('/exchange-orders/me')
}

export function createExchangeOrder(payload: { rewardItemId: number; quantity: number }) {
  return request<ExchangeOrder>('/exchange-orders', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}
