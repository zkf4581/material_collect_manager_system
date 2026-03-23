import { httpGet, httpRequest } from '@/api/http'

export interface MaterialItem {
  id: number
  name: string
  unitCode: string
  status: string
}

export interface PointRule {
  id: number
  materialItemId: number
  unitCode: string
  basePoint: number
  conditionCode: string
  conditionFactor: number
  status: string
}

export interface DictOption {
  code: string
  name: string
  factor?: string
}

export interface SaveMaterialPayload {
  name: string
  unitCode: string
  status: string
}

export interface SavePointRulePayload {
  materialItemId: number
  unitCode: string
  basePoint: number
  conditionCode: string
  conditionFactor: number
  status: string
}

export function getMaterialItems() {
  return httpGet<MaterialItem[]>('/material-items')
}

export function createMaterialItem(payload: SaveMaterialPayload) {
  return httpRequest<MaterialItem>('/material-items', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export function updateMaterialItem(id: number, payload: SaveMaterialPayload) {
  return httpRequest<MaterialItem>(`/material-items/${id}`, {
    method: 'PUT',
    body: JSON.stringify(payload),
  })
}

export function getPointRules() {
  return httpGet<PointRule[]>('/point-rules')
}

export function createPointRule(payload: SavePointRulePayload) {
  return httpRequest<PointRule>('/point-rules', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export function updatePointRule(id: number, payload: SavePointRulePayload) {
  return httpRequest<PointRule>(`/point-rules/${id}`, {
    method: 'PUT',
    body: JSON.stringify(payload),
  })
}

export function getUnitDict() {
  return httpGet<DictOption[]>('/unit-dict')
}

export function getConditionFactors() {
  return httpGet<DictOption[]>('/condition-factors')
}
