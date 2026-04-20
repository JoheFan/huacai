import { get } from './http'

export interface WorkbenchMetric {
  title: string
  value: string
  helper: string
  note: string
  emphasized: boolean
}

export interface WorkbenchRecord {
  id: number
  customerName: string
  recordType: string
  relationInfo: string
  actionDate: string
  status: string
  priority: string
  routePath: string
}

export interface WorkbenchTodo {
  title: string
  level: string
  deadline: string
}

export interface WorkbenchReminder {
  title: string
  tag: string
  tone: string
}

export interface WorkbenchOverview {
  metricCards: WorkbenchMetric[]
  focusRows: WorkbenchRecord[]
  todoItems: WorkbenchTodo[]
  reminderItems: WorkbenchReminder[]
}

export const fetchWorkbenchOverview = (params?: { keyword?: string }) =>
  get<WorkbenchOverview>('/workbench/overview', { params })
