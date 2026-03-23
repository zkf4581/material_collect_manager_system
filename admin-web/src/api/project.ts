import { httpGet, httpRequest } from '@/api/http'

export interface ProjectItem {
  id: number
  name: string
  location?: string
  status: string
}

export interface TeamItem {
  id: number
  projectId: number
  name: string
  status: string
}

export interface WorkerItem {
  id: number
  name: string
  phone?: string
  status: string
}

export interface SaveProjectPayload {
  name: string
  location: string
  status: string
}

export interface SaveTeamPayload {
  projectId: number
  name: string
  status: string
}

export interface SaveWorkerPayload {
  name: string
  phone: string
  status: string
}

export function getProjects() {
  return httpGet<ProjectItem[]>('/projects')
}

export function createProject(payload: SaveProjectPayload) {
  return httpRequest<ProjectItem>('/projects', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export function updateProject(id: number, payload: SaveProjectPayload) {
  return httpRequest<ProjectItem>(`/projects/${id}`, {
    method: 'PUT',
    body: JSON.stringify(payload),
  })
}

export function getTeams(projectId?: number) {
  const query = projectId ? `?projectId=${projectId}` : ''
  return httpGet<TeamItem[]>(`/teams${query}`)
}

export function createTeam(payload: SaveTeamPayload) {
  return httpRequest<TeamItem>('/teams', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export function updateTeam(id: number, payload: SaveTeamPayload) {
  return httpRequest<TeamItem>(`/teams/${id}`, {
    method: 'PUT',
    body: JSON.stringify(payload),
  })
}

export function getWorkers() {
  return httpGet<WorkerItem[]>('/workers')
}

export function createWorker(payload: SaveWorkerPayload) {
  return httpRequest<WorkerItem>('/workers', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export function updateWorker(id: number, payload: SaveWorkerPayload) {
  return httpRequest<WorkerItem>(`/workers/${id}`, {
    method: 'PUT',
    body: JSON.stringify(payload),
  })
}
