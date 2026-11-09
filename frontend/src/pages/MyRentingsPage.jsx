import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { toast } from 'react-toastify';
import { rentingApi } from '../api/rentingApi';
import { apiError } from '../api/axiosClient';
import { EmptyState, Loading } from '../components/Common';
import { date, listOf, money } from '../utils/format';
import './RentalPages.css';

export const RentalStatus = ({ value }) => <span className={`rental-status ${String(value).toLowerCase()}`}>{value}</span>;

export default function MyRentingsPage() {
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(true);
  useEffect(() => {
    rentingApi.history().then(x => setRows(listOf(x))).catch(e => toast.error(apiError(e))).finally(() => setLoading(false));
  }, []);
  return <div className="container-xl page">
    <div className="page-title left"><span className="eyebrow">TÀI KHOẢN CỦA TÔI</span><h1>Lịch sử thuê xe</h1><p>Theo dõi tất cả hành trình và giao dịch của bạn.</p></div>
    {loading ? <Loading/> : !rows.length ? <EmptyState title="Bạn chưa có chuyến đi nào" text="Hãy chọn một chiếc xe và bắt đầu hành trình đầu tiên."/> : <div className="table-card"><table><thead><tr><th>Mã giao dịch</th><th>Ngày đặt</th><th>Thời gian thuê</th><th>Số xe</th><th>Tổng tiền</th><th>Trạng thái</th><th/></tr></thead><tbody>{rows.map(r => <tr key={r.rentingTransactionId}><td><b>#{r.rentingTransactionId}</b></td><td>{date(r.rentingDate)}</td><td>{date(r.details?.[0]?.startDate)} – {date(r.details?.[0]?.endDate)}</td><td>{r.details?.length || 0}</td><td><b>{money(r.totalPrice)}</b></td><td><RentalStatus value={r.rentingStatus}/></td><td><Link className="btn btn-sm btn-outline-primary" to={`/customer/rentings/${r.rentingTransactionId}`}>Chi tiết</Link></td></tr>)}</tbody></table></div>}
  </div>;
}
