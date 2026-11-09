import api, { unwrap } from './axiosClient';
export const authApi = {
  login: async (body) => unwrap(await api.post('/api/auth/login', body)),
  register: async (body) => unwrap(await api.post('/api/customers/register', body)),
  me: async () => unwrap(await api.get('/api/customers/profile')),
};
