import React, { useState } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import { toast } from 'react-toastify';
import { useAuth } from '../context/AuthContext';
import { apiError } from '../api/axiosClient';

export default function LoginPage() {
  const [form, setForm] = useState({ email: '', password: '', remember: true });
  const [busy, setBusy] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();
  const [params] = useSearchParams();

  const submit = async (event) => {
    event.preventDefault();
    if (!form.email.trim()) return toast.error('Vui lòng nhập tài khoản hoặc email');
    setBusy(true);
    try {
      const user = await login({ email: form.email.trim(), password: form.password }, form.remember);
      toast.success('Đăng nhập thành công');
      navigate(user.role === 'ADMIN' ? '/admin/dashboard' : '/customer/dashboard');
    } catch (error) {
      toast.error(apiError(error));
    } finally {
      setBusy(false);
    }
  };

  return <div className="auth-page">
    <div className="auth-art">
      <Link className="brand light" to="/"><i className="bi bi-car-front-fill"/> FUCar</Link>
      <div><span className="eyebrow">CHÀO MỪNG TRỞ LẠI</span><h1>Mỗi hành trình<br/>đều bắt đầu<br/>từ đây.</h1><p>Đăng nhập để quản lý chuyến đi và khám phá những chiếc xe tuyệt vời.</p></div>
    </div>
    <div className="auth-form"><div className="form-box">
      <Link className="back" to="/"><i className="bi bi-arrow-left"/> Về trang chủ</Link>
      <h2>Đăng nhập</h2><p>Chào mừng bạn trở lại FUCar.</p>
      {params.has('expired') && <div className="alert alert-warning">Phiên đăng nhập đã hết hạn.</div>}
      <form onSubmit={submit}>
        <label>Tài khoản hoặc email</label>
        <div className="input-icon"><i className="bi bi-person"/><input type="text" required placeholder="admin hoặc you@example.com" value={form.email} onChange={e => setForm({ ...form, email: e.target.value })}/></div>
        <label>Mật khẩu</label>
        <div className="input-icon"><i className="bi bi-lock"/><input type="password" required placeholder="••••••••" value={form.password} onChange={e => setForm({ ...form, password: e.target.value })}/></div>
        <label className="check"><input type="checkbox" checked={form.remember} onChange={e => setForm({ ...form, remember: e.target.checked })}/> Ghi nhớ đăng nhập</label>
        <button className="btn btn-brand btn-lg w-100" disabled={busy}>{busy ? <><span className="spinner-border spinner-border-sm"/> Đang đăng nhập...</> : 'Đăng nhập'}</button>
      </form>
      <p className="switch">Chưa có tài khoản? <Link to="/register">Đăng ký ngay</Link></p>
    </div></div>
  </div>;
}
