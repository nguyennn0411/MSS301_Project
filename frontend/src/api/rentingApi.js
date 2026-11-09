import api, { unwrap } from './axiosClient';
export const rentingApi = {
  create: async (details) => unwrap(await api.post('/api/rentings', { details })),
  createForCustomer: async (customerId, details) => unwrap(await api.post('/api/rentings', { customerId, details })),
  history: async () => unwrap(await api.get('/api/rentings/my-history')),
  all: async () => unwrap(await api.get('/api/rentings')),
  get: async (id) => unwrap(await api.get(`/api/rentings/${id}`)),
  confirm: async (id) => unwrap(await api.patch(`/api/rentings/${id}/confirm`)),
  reject: async (id, note) => unwrap(await api.patch(`/api/rentings/${id}/reject`, { note })),
  deliver: async (id) => unwrap(await api.patch(`/api/rentings/${id}/deliver`)),
  complete: async (id, fees) => unwrap(await api.patch(`/api/rentings/${id}/complete`, fees)),
  cancel: async (id, note) => unwrap(await api.patch(`/api/rentings/${id}/cancel`, { note })),
  report: async (startDate, endDate) => unwrap(await api.get('/api/rentings/report', { params: { startDate, endDate } })),
};
