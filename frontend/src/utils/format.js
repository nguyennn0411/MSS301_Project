export const money = (value = 0) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
export const date = (value) => value ? new Intl.DateTimeFormat('vi-VN').format(new Date(`${value}T00:00:00`)) : '—';
export const today = () => new Date().toISOString().slice(0, 10);
export const days = (start, end) => start && end ? Math.floor((new Date(end) - new Date(start)) / 86400000) + 1 : 0;
export const listOf = (data) => Array.isArray(data) ? data : (data?.content || []);
export const decodeJwt = (token) => { try { return JSON.parse(atob(token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/'))); } catch { return {}; } };
