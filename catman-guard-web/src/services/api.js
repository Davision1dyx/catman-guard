import axios from 'axios'

const apiClient = axios.create({
  baseURL: '/processing/catman',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

const longRunningApiClient = axios.create({
  baseURL: '/processing/catman',
  timeout: 0,
  headers: {
    'Content-Type': 'application/json'
  }
})

apiClient.interceptors.response.use(
  response => response.data,
  error => {
    console.error('API Error:', error)
    return Promise.reject(error)
  }
)

export const dashboardApi = {
  getState: (params) => apiClient.get('/admin/dashboard/state', { params }),
  getTrend: (params) => apiClient.get('/admin/dashboard/trend', { params }),
  getHotQuestions: (params) => apiClient.get('/admin/dashboard/hot-questions', { params })
}

export const groupApi = {
  list: () => apiClient.get('/admin/group/list'),
  create: (data) => apiClient.post('/admin/group/create', data),
  update: (id, data) => apiClient.put(`/admin/group/modify/${id}`, data),
  delete: (id) => apiClient.delete(`/admin/group/delete/${id}`)
}

export const issueApi = {
  list: (data) => apiClient.post('/admin/issue/list', data),
  create: (data) => apiClient.post('/admin/issue/create', data),
  getDetail: (issueId) => apiClient.get(`/admin/issue/${issueId}`),
  update: (issueId, data) => apiClient.put(`/admin/issue/${issueId}`, data),
  delete: (issueId) => apiClient.delete(`/admin/issue/${issueId}`),
  assign: (issueId, data) => apiClient.post(`/admin/issue/assign/${issueId}`, data),
  updateStatus: (issueId, data) => apiClient.post(`/admin/issue/status/${issueId}`, data)
}

export const scheduleApi = {
  list: (params) => apiClient.get('/admin/schedule/list', { params }),
  create: (data) => apiClient.post('/admin/schedule/create', data)
}

export const staffApi = {
  list: (params) => apiClient.get('/admin/staff/list', { params }),
  create: (data) => apiClient.post('/admin/staff/create', data),
  update: (id, data) => apiClient.put(`/admin/staff/modify/${id}`, data),
  delete: (id) => apiClient.delete(`/admin/staff/delete/${id}`)
}

export const chatApi = {
  chat: (params) => axios.get('/processing/catman/conversation/chat/chat', {
    params,
    responseType: 'stream'
  }),
  websearch: (params) => axios.get('/processing/catman/conversation/chat/websearch', {
    params,
    responseType: 'stream'
  }),
  retrieval: (params) => axios.get('/processing/catman/conversation/chat/retrieval', {
    params,
    responseType: 'stream'
  })
}

export const knowledgeApi = {
  list: (data) => apiClient.post('/storage/knowledge/list', data),
  create: (data) => apiClient.post('/storage/knowledge/create', data),
  update: (id, data) => apiClient.put(`/storage/knowledge/${id}`, data),
  delete: (id) => apiClient.delete(`/storage/knowledge/${id}`)
}

export const fileApi = {
  upload: (formData) => apiClient.post('/storage/file/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  }),
  list: (knowledgeId, params) => apiClient.get(`/storage/file/list/${knowledgeId}`, { params }),
  delete: (fileId) => apiClient.delete(`/storage/file/${fileId}`),
  queryChunk: (params) => apiClient.get('/storage/file/queryChunk', { params }),
  chunk: (fileId, data) => longRunningApiClient.post(`/storage/chunk/chunk/${fileId}`, data),
  listChunks: (fileId) => apiClient.get(`/storage/chunk/list/${fileId}`),
  download: (fileId) => axios.get('/processing/catman/storage/file/download/' + fileId, {
    responseType: 'blob'
  })
}

export default apiClient