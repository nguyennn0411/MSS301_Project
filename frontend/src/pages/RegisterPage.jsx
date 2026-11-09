import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import { authApi } from '../api/authApi';
import { apiError } from '../api/axiosClient';
import { today } from '../utils/format';

const initialForm = {
  customerName: '', email: '', telephone: '', customerBirthday: '',
  address: '', identityCard: '', password: '', confirm: '',
};

export default function RegisterPage() {
  const [form, setForm] = useState(initialForm);
  const [busy, setBusy] = useState(false);
  const navigate = useNavigate();
  const change = event => setForm({ ...form, [event.target.name]: event.target.value });

  const submit = async event => {
    event.preventDefault();
    if (form.password.length < 6) return toast.error('Mật khẩu tối thiểu 6 ký tự');
    if (form.password !== form.confirm) return toast.error('Mật khẩu xác nhận không khớp');
    if (!/^[0-9+ ().-]{8,15}$/.test(form.telephone)) return toast.error('Số điện thoại không hợp lệ');
    if (!/^[A-Za-z0-9-]{6,20}$/.test(form.identityCard)) return toast.error('Số giấy tờ tùy thân không hợp lệ');
    setBusy(true);
    try {
      const { confirm, ...request } = form;
      await authApi.register(request);
      toast.success('Đăng ký thành công. Hãy đăng nhập!');
      navigate('/login');
    } catch (error) {
      toast.error(apiError(error));
    } finally {
      setBusy(false);
    }
  };

  const fields = [
    ['customerName', 'Họ và tên', 'text'], ['email', 'Email', 'email'],
    ['telephone', 'Số điện thoại', 'tel'], ['customerBirthday', 'Ngày sinh', 'date'],
    ['identityCard', 'CCCD/Hộ chiếu', 'text'], ['address', 'Địa chỉ', 'text'],
    ['password', 'Mật khẩu', 'password'], ['confirm', 'Xác nhận mật khẩu', 'password'],
  ];

  return <div className="simple-auth"><div className="form-box wide">
    <Link className="brand" to="/"><i className="bi bi-car-front-fill"/> FUCar</Link>
    <h2>Tạo tài khoản</h2><p>Bắt đầu hành trình của bạn cùng FUCar.</p>
    <form className="form-grid" onSubmit={submit}>
      {fields.map(([name, label, type]) => <div key={name}>
        <label>{label}</label><input name={name} type={type} required max={type === 'date' ? today() : undefined} value={form[name]} onChange={change}/>
      </div>)}
      <button disabled={busy} className="btn btn-brand btn-lg full">{busy ? 'Đang tạo tài khoản...' : 'Đăng ký'}</button>
    </form>
    <p className="switch">Đã có tài khoản? <Link to="/login">Đăng nhập</Link></p>
  </div></div>;
}
