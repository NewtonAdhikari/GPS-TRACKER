import axios from 'axios'

const API_BASE = 'http://localhost:8080/api'

const api = axios.create({
  baseURL: API_BASE,
  timeout: 10000
})

export const locationService = {
  sendLocation: (data) => api.post('/location', data),
  getHistory: (userId) => api.get('/history', { params: { userId } })
}

export const predictionService = {
  getPrediction: (userId) => api.get('/predict', { params: { userId } }),
  getExplanation: (userId) => api.get('/explain', { params: { userId } })
}

export const simulationService = {
  simulate: (userId, hours = 2) => api.get('/simulate', { params: { userId, hours } })
}

export const alertService = {
  getAnomalies: (userId) => api.get('/anomalies', { params: { userId } }),
  getAlerts: (userId) => api.get('/alerts', { params: { userId } })
}

export const chatService = {
  sendMessage: (data) => api.post('/chat', data)
}

export default api
