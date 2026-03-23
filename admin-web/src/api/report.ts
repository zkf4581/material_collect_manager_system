import { httpGet } from '@/api/http'

export interface OverviewReport {
  recycleRecordCount: number
  approvedRecycleCount: number
  totalAwardedPoints: number
  exchangeOrderCount: number
  approvedExchangePoints: number
  rewardItemCount: number
}

export interface RankingItem {
  workerId: number
  projectId: number
  balance: number
}

export interface MaterialRankingItem {
  materialItemId: number
  materialName: string
  unitCode: string
  totalQuantity: number
  totalPoints: number
}

export interface RewardRankingItem {
  rewardItemId: number
  rewardName: string
  totalQuantity: number
  totalPoints: number
}

export function getOverviewReport() {
  return httpGet<OverviewReport>('/reports/overview')
}

export function getRankingReport() {
  return httpGet<RankingItem[]>('/reports/rankings')
}

export function getMaterialRankingReport() {
  return httpGet<MaterialRankingItem[]>('/reports/materials')
}

export function getRewardRankingReport() {
  return httpGet<RewardRankingItem[]>('/reports/rewards')
}
