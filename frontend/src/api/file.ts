import http, { del, get, request } from './http'

export interface UploadedFileRecord {
  id: number
  fileName: string
  fileExt?: string
  fileSize?: number
  bizType: string
  bizId: number
  uploaderId?: number
  uploaderName?: string
}

export const uploadFile = async (file: File, bizType?: string, bizId?: number) => {
  const formData = new FormData()
  formData.append('file', file)
  if (bizType) {
    formData.append('bizType', bizType)
  }
  if (typeof bizId === 'number') {
    formData.append('bizId', String(bizId))
  }
  const response = await http.post('/files/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
  return response.data.data as UploadedFileRecord
}

export const fetchFileDetail = (id: number) => get<UploadedFileRecord>(`/files/${id}`)

export const deleteFile = (id: number) => del<void>(`/files/${id}`)

// Workflow-specific attachment APIs — these go through HR service with business permission checks
export const listWorkflowAttachments = (bizType: string, bizId: number) =>
  get<UploadedFileRecord[]>('/hr/workflow-attachments', { params: { bizType, bizId } })

export const uploadWorkflowAttachment = async (file: File, bizType: string, bizId: number) => {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('bizType', bizType)
  formData.append('bizId', String(bizId))
  const response = await http.post('/hr/workflow-attachments/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
  return response.data.data as UploadedFileRecord
}

export const deleteWorkflowAttachment = (id: number, bizType: string, bizId: number) =>
  request<void>({ method: 'DELETE', url: `/hr/workflow-attachments/${id}`, params: { bizType, bizId } })
