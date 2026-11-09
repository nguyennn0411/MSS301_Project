import api, { unwrap } from './axiosClient';
export const carApi = {
  list: async (params) => unwrap(await api.get('/api/cars', { params })),
  available: async (startDate, endDate) => unwrap(await api.get('/api/cars/available', { params: { startDate, endDate } })),
  get: async (id) => unwrap(await api.get(`/api/cars/${id}`)),
  schedule: async (id) => unwrap(await api.get(`/api/cars/${id}/schedule`)),
  create: async (body) => unwrap(await api.post('/api/cars', body)),
  update: async (id, body) => unwrap(await api.put(`/api/cars/${id}`, body)),
  remove: async (id) => unwrap(await api.delete(`/api/cars/${id}`)),
  manufacturers: async () => unwrap(await api.get('/api/manufacturers')),
  suppliers: async () => unwrap(await api.get('/api/suppliers')),
};
