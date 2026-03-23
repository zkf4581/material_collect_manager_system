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

export function getOverviewReport() {
  return httpGet<OverviewReport>('/reports/overview')
}

export function getRankingReport() {
  return httpGet<RankingItem[]>('/reports/rankings')
}
