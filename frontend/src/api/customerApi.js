import api, { unwrap } from './axiosClient';
export const customerApi = {
  list: async (params) => unwrap(await api.get('/api/customers', { params })),
  get: async (id) => unwrap(await api.get(`/api/customers/${id}`)),
  profile: async () => unwrap(await api.get('/api/customers/profile')),
  update: async (id, body) => unwrap(await api.put(`/api/customers/${id}`, body)),
  updateProfile: async (body) => unwrap(await api.put('/api/customers/profile', body)),
  remove: async (id) => unwrap(await api.delete(`/api/customers/${id}`)),
};
