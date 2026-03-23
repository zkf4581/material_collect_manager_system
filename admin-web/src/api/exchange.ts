import { httpGet, httpRequest } from '@/api/http'

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
  return httpGet<RewardItem[]>('/reward-items')
}

export function getExchangeOrders() {
  return httpGet<ExchangeOrder[]>('/exchange-orders')
}

export function approveExchangeOrder(id: number) {
  return httpRequest<ExchangeOrder>(`/exchange-orders/${id}/approve`, {
    method: 'POST',
  })
}

export function rejectExchangeOrder(id: number) {
  return httpRequest<ExchangeOrder>(`/exchange-orders/${id}/reject`, {
    method: 'POST',
  })
}

export function deliverExchangeOrder(id: number) {
  return httpRequest<ExchangeOrder>(`/exchange-orders/${id}/deliver`, {
    method: 'POST',
  })
}
