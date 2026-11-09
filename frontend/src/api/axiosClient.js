import axios from 'axios';

export const unwrap = (response) => response?.data?.data ?? response?.data;
export const apiError = (error) => {
  if (!error.response) return 'Không thể kết nối API Gateway. Vui lòng kiểm tra backend.';
  const data = error.response.data;
  return data?.message || data?.error || (typeof data === 'string' ? data : null) || `Yêu cầu thất bại (${error.response.status})`;
};

// Use the current frontend origin by default. Nginx (Docker) and Vite (dev)
// proxy /api to the Gateway, so browser requests never collide with local apps
// that may occupy 127.0.0.1:8080.
const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || '';

const axiosClient = axios.create({ baseURL: apiBaseUrl, headers: { 'Content-Type': 'application/json' }, timeout: 15000 });
axiosClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('access_token') || sessionStorage.getItem('access_token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});
axiosClient.interceptors.response.use((response) => response, (error) => {
  if (error.response?.status === 401) {
    localStorage.removeItem('access_token'); localStorage.removeItem('user');
    sessionStorage.removeItem('access_token'); sessionStorage.removeItem('user');
    if (!location.pathname.includes('/login')) location.assign('/login?expired=1');
  } else if (error.response?.status === 403 && location.pathname !== '/forbidden') location.assign('/forbidden');
  return Promise.reject(error);
});
export default axiosClient;
