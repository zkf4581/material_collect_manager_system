import { request, uploadRequest } from './http'

export interface ProjectOption {
  id: number
  name: string
  location?: string
  status: string
}

export interface TeamOption {
  id: number
  projectId: number
  name: string
  status: string
}

export interface WorkerOption {
  id: number
  name: string
  phone?: string
  status: string
}

export interface MaterialItemOption {
  id: number
  name: string
  unitCode: string
  status: string
}

export interface UnitOption {
  code: string
  name: string
}

export interface ConditionOption {
  code: string
  name: string
  factor: string
}

export interface UploadedFileInfo {
  fileId: number
  url: string
  storageType: string
  size: number
  mime: string
}

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

export interface CreateRecyclePayload {
  projectId: number
  teamId: number
  workerId: number
  materialItemId: number
  quantity: number
  unitCode: string
  conditionCode: string
  remark: string
  photoIds: number[]
}

export function getProjects() {
  return request<ProjectOption[]>('/projects')
}

export function getTeams(projectId?: number) {
  const query = projectId ? `?projectId=${projectId}` : ''
  return request<TeamOption[]>(`/teams${query}`)
}

export function getWorkers() {
  return request<WorkerOption[]>('/workers')
}

export function getMaterialItems() {
  return request<MaterialItemOption[]>('/material-items')
}

export function getUnitOptions() {
  return request<UnitOption[]>('/unit-dict')
}

export function getConditionOptions() {
  return request<ConditionOption[]>('/condition-factors')
}

export function uploadImage(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return uploadRequest<UploadedFileInfo>('/files/upload', formData)
}

export function createRecycleRecord(payload: CreateRecyclePayload) {
  return request<RecycleRecordItem>('/recycle-records', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export function getRecycleRecords() {
  return request<RecycleRecordItem[]>('/recycle-records')
}
