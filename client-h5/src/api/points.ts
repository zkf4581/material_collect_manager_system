import { request } from './http'

export interface PointSummary {
  balance: number
  projectId: number
  workerId: number
}

export interface PointLedgerItem {
  id: number
  pointAccountId: number
  workerId: number
  projectId: number
  bizType: string
  bizId: number
  changeAmount: number
  balanceAfter: number
  remark?: string
}

export function getPointSummary() {
  return request<PointSummary>('/points/summary')
}

export function getPointLedger() {
  return request<PointLedgerItem[]>('/points/ledger')
}
