import React, { createContext, useContext, useMemo, useState } from 'react';
import { authApi } from '../api/authApi';
import { decodeJwt } from '../utils/format';

const AuthContext = createContext(null);
const readUser = () => { try { return JSON.parse(localStorage.getItem('user') || sessionStorage.getItem('user')) || null; } catch { return null; } };
export function AuthProvider({ children }) {
  const [user, setUser] = useState(readUser);
  const login = async (credentials, remember) => {
    const result = await authApi.login(credentials); const token = result.token || result.accessToken;
    const claims = decodeJwt(token); const profile = { id: claims.userId ?? claims.sub, email: claims.email || credentials.email, name: claims.name || credentials.email.split('@')[0], role: result.role || claims.role };
    const storage = remember ? localStorage : sessionStorage; storage.setItem('access_token', token); storage.setItem('user', JSON.stringify(profile)); setUser(profile); return profile;
  };
  const logout = () => { ['access_token','user'].forEach(k => { localStorage.removeItem(k); sessionStorage.removeItem(k); }); setUser(null); };
  const value = useMemo(() => ({ user, login, logout, authenticated: Boolean(user) }), [user]);
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
export const useAuth = () => useContext(AuthContext);
