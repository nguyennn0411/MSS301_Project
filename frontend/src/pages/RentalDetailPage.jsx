import { useEffect, useMemo, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { toast } from 'react-toastify';
import { rentingApi } from '../api/rentingApi';
import { apiError } from '../api/axiosClient';
import { ConfirmDialog, EmptyState, Loading } from '../components/Common';
import { date, money, today } from '../utils/format';
import { RentalStatus } from './MyRentingsPage';
import './RentalPages.css';

export default function RentalDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [rental, setRental] = useState(null);
  const [loading, setLoading] = useState(true);
  const [confirmCancel, setConfirmCancel] = useState(false);
  const [busy, setBusy] = useState(false);
  const load = () => rentingApi.get(id).then(setRental).catch(e => toast.error(apiError(e))).finally(() => setLoading(false));
  useEffect(() => { load(); }, [id]);
  const canCancel = useMemo(() => {
    if (!rental) return false;
    if (rental.rentingStatus === 'PENDING') return true;
    return rental.rentingStatus === 'CONFIRMED' && rental.details?.every(item => item.startDate > today());
  }, [rental]);
  const cancel = async () => {
    setBusy(true);
    try { setRental(await rentingApi.cancel(id, 'Khách hàng yêu cầu hủy')); toast.success('Đã hủy giao dịch và giải phóng lịch xe'); setConfirmCancel(false); }
    catch (e) { toast.error(apiError(e)); }
    finally { setBusy(false); }
  };
  if (loading) return <Loading/>;
  if (!rental) return <EmptyState title="Không tìm thấy giao dịch"/>;
  return <div className="container-xl page rental-detail-page">
    <Link className="back" to="/customer/rentings"><i className="bi bi-arrow-left"/> Quay lại lịch sử</Link>
    <div className="rental-heading"><div><span className="eyebrow">GIAO DỊCH #{rental.rentingTransactionId}</span><h1>Chi tiết thuê xe</h1><p>Tạo ngày {date(rental.rentingDate)}</p></div><RentalStatus value={rental.rentingStatus}/></div>
    <div className="rental-detail-grid"><section className="panel"><h4>Danh sách xe và thời gian thuê</h4>{rental.details?.map(item => <div className="rental-car-row" key={item.carId}><i className="bi bi-car-front-fill"/><span><b>Xe #{item.carId}</b><small>{date(item.startDate)} – {date(item.endDate)}</small></span><strong>{money(item.price)}</strong></div>)}</section><aside className="panel rental-cost"><h4>Chi phí</h4><p><span>Tiền thuê xe</span><b>{money(rental.basePrice)}</b></p><p><span>Phí trả muộn</span><b>{money(rental.lateFee)}</b></p><p><span>Phí hư hỏng</span><b>{money(rental.damageFee)}</b></p><p><span>Phí phát sinh</span><b>{money(rental.otherFee)}</b></p><div><span>Tổng thanh toán</span><strong>{money(rental.totalPrice)}</strong></div>{rental.adminNote && <small className="rental-note"><b>Ghi chú Admin:</b> {rental.adminNote}</small>}{canCancel && <button className="btn btn-outline-danger w-100" onClick={() => setConfirmCancel(true)}>Hủy giao dịch</button>}</aside></div>
    <ConfirmDialog show={confirmCancel} busy={busy} title="Hủy giao dịch?" message="Lịch giữ xe sẽ được giải phóng. Thao tác này không thể hoàn tác." onCancel={() => setConfirmCancel(false)} onConfirm={cancel}/>
  </div>;
}
