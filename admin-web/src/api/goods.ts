import { httpGet, httpRequest } from '@/api/http'

export interface RewardItem {
  id: number
  name: string
  pointsCost: number
  stock: number
  status: string
}

export interface SaveRewardItemPayload {
  name: string
  pointsCost: number
  stock: number
  status: string
}

export function getRewardItems() {
  return httpGet<RewardItem[]>('/reward-items')
}

export function createRewardItem(payload: SaveRewardItemPayload) {
  return httpRequest<RewardItem>('/reward-items', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export function updateRewardItem(id: number, payload: SaveRewardItemPayload) {
  return httpRequest<RewardItem>(`/reward-items/${id}`, {
    method: 'PUT',
    body: JSON.stringify(payload),
  })
}
