import React, { useEffect, useMemo, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { toast } from 'react-toastify';
import { customerApi } from '../api/customerApi';
import { rentingApi } from '../api/rentingApi';
import { apiError } from '../api/axiosClient';
import { EmptyState, Loading, Status } from '../components/Common';
import { date, listOf, money } from '../utils/format';
import './CustomerDetailPage.css';

export default function CustomerDetailPage() {
  const { id } = useParams();
  const [customer, setCustomer] = useState(null);
  const [rentings, setRentings] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let active = true;
    Promise.all([customerApi.get(id), rentingApi.all()])
      .then(([customerData, rentingData]) => {
        if (!active) return;
        setCustomer(customerData);
        setRentings(listOf(rentingData).filter(item => String(item.customerId) === String(id)));
      })
      .catch(error => toast.error(apiError(error)))
      .finally(() => active && setLoading(false));
    return () => { active = false; };
  }, [id]);

  const summary = useMemo(() => ({
    transactions: rentings.length,
    cars: rentings.reduce((total, item) => total + (item.details?.length || 0), 0),
    revenue: rentings.filter(item => item.rentingStatus === 'COMPLETED')
      .reduce((total, item) => total + Number(item.totalPrice || 0), 0),
  }), [rentings]);

  if (loading) return <Loading text="Đang tải hồ sơ khách hàng..."/>;
  if (!customer) return <EmptyState title="Không tìm thấy khách hàng"/>;

  const fields = [
    ['Mã khách hàng', `#${customer.customerId}`, 'hash'],
    ['Họ và tên', customer.customerName, 'person'],
    ['Email', customer.email, 'envelope'],
    ['Số điện thoại', customer.telephone || '—', 'telephone'],
    ['Ngày sinh', date(customer.customerBirthday), 'calendar3'],
    ['Địa chỉ', customer.address || 'Chưa cập nhật', 'geo-alt'],
    ['CCCD/Hộ chiếu', customer.identityCard || 'Chưa cập nhật', 'person-vcard'],
  ];

  return <>
    <div className="admin-title customer-detail-heading">
      <div><Link className="back" to="/admin/customers"><i className="bi bi-arrow-left"/> Quay lại danh sách</Link><h1>Chi tiết khách hàng</h1><p>Hồ sơ và lịch sử sử dụng dịch vụ của khách hàng.</p></div>
      <Status active={customer.customerStatus}/>
    </div>
    <div className="customer-detail-grid">
      <section className="panel customer-profile-card">
        <div className="customer-avatar">{customer.customerName?.charAt(0)?.toUpperCase()}</div>
        <h3>{customer.customerName}</h3><p>{customer.email}</p>
        <div className="customer-fields">{fields.map(([label, value, icon]) => <div key={label}><i className={`bi bi-${icon}`}/><span><small>{label}</small><b>{value}</b></span></div>)}</div>
      </section>
      <section>
        <div className="customer-summary">
          <div><i className="bi bi-receipt"/><span>Giao dịch<b>{summary.transactions}</b></span></div>
          <div><i className="bi bi-car-front"/><span>Lượt xe thuê<b>{summary.cars}</b></span></div>
          <div><i className="bi bi-cash-stack"/><span>Tổng chi tiêu<b>{money(summary.revenue)}</b></span></div>
        </div>
        <div className="table-card customer-renting-table">
          <div className="table-title"><h4>Lịch sử thuê xe</h4><span>{rentings.length} giao dịch</span></div>
          {rentings.length ? <table><thead><tr><th>Mã GD</th><th>Ngày đặt</th><th>Thời gian thuê</th><th>Số xe</th><th>Tổng tiền</th><th>Trạng thái</th><th/></tr></thead><tbody>{rentings.map(item => <tr key={item.rentingTransactionId}><td><b>#{item.rentingTransactionId}</b></td><td>{date(item.rentingDate)}</td><td>{date(item.details?.[0]?.startDate)} – {date(item.details?.[0]?.endDate)}</td><td>{item.details?.length || 0}</td><td><b>{money(item.totalPrice)}</b></td><td><span className="status available">{item.rentingStatus}</span></td><td><Link className="table-action" to="/admin/rentings"><i className="bi bi-arrow-right"/></Link></td></tr>)}</tbody></table> : <EmptyState title="Chưa có lịch sử thuê xe" text="Khách hàng chưa thực hiện giao dịch nào."/>}
        </div>
      </section>
    </div>
  </>;
}
