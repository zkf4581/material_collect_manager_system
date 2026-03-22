import { httpGet, httpRequest } from '@/api/http'

export interface RecycleRecordItem {
  id: number
  projectId: number
  teamId: number
  workerId: number
  materialItemId: number
  quantity: number
  unitCode: string
  conditionCode: string
  calculatedPoints: number
  status: string
  remark?: string
}

export function getRecycleRecords() {
  return httpGet<RecycleRecordItem[]>('/recycle-records')
}

export function approveRecycleRecord(id: number) {
  return httpRequest<RecycleRecordItem>(`/recycle-records/${id}/approve`, {
    method: 'POST',
  })
}
