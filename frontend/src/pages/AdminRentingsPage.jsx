import { useEffect, useMemo, useState } from 'react';
import { toast } from 'react-toastify';
import { rentingApi } from '../api/rentingApi';
import { customerApi } from '../api/customerApi';
import { carApi } from '../api/carApi';
import { apiError } from '../api/axiosClient';
import { EmptyState, Loading } from '../components/Common';
import { date, listOf, money, today } from '../utils/format';
import { RentalStatus } from './MyRentingsPage';
import './RentalPages.css';
import './MaintenanceOptions.css';

const initialFees = { lateFee: 0, damageFee: 0, otherFee: 0, adminNote: '', maintenanceCarIds: [] };

export default function AdminRentingsPage() {
  const [rows, setRows] = useState([]);
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selected, setSelected] = useState(null);
  const [filter, setFilter] = useState('ALL');
  const [busy, setBusy] = useState(false);
  const [note, setNote] = useState('');
  const [fees, setFees] = useState(initialFees);
  const [showCreate, setShowCreate] = useState(false);

  const load = () => Promise.all([rentingApi.all(), customerApi.list()])
    .then(([rentings, customerRows]) => { setRows(listOf(rentings)); setCustomers(listOf(customerRows)); })
    .catch(e => toast.error(apiError(e))).finally(() => setLoading(false));
  useEffect(() => { load(); }, []);

  const shown = useMemo(() => filter === 'ALL' ? rows : rows.filter(x => x.rentingStatus === filter), [rows, filter]);
  const customerName = id => customers.find(x => String(x.customerId) === String(id))?.customerName || `Khách hàng #${id}`;
  const updateRow = value => { setRows(current => current.map(x => x.rentingTransactionId === value.rentingTransactionId ? value : x)); setSelected(value); };
  const run = async (action, success) => {
    setBusy(true);
    try { updateRow(await action()); toast.success(success); }
    catch (e) { toast.error(apiError(e)); }
    finally { setBusy(false); }
  };
  const openDetail = row => { setSelected(row); setNote(row.adminNote || ''); setFees({ ...initialFees, adminNote: row.adminNote || '' }); };

  return <>
    <div className="admin-title">
      <div>
        <h1>Quản lý giao dịch thuê xe</h1>
        <p>Xác nhận, giao xe, nhận xe trả và xử lý chi phí phát sinh.</p>
      </div>
      <button className="btn btn-brand" onClick={() => setShowCreate(x => !x)}>
        <i className="bi bi-plus-lg"/> Tạo giao dịch
      </button>
    </div>
    
    {showCreate && (
      <AdminCreateRental 
        customers={customers} 
        onCreated={item => { 
          setRows(x => [item, ...x]); 
          setShowCreate(false); 
          openDetail(item); 
        }}
      />
    )} 
    
    <div className="rental-toolbar">
      <select value={filter} onChange={e => setFilter(e.target.value)}>
        <option value="ALL">Tất cả trạng thái</option>
        {['PENDING','CONFIRMED','RENTING','COMPLETED','CANCELLED','REJECTED'].map(x => <option key={x}>{x}</option>)}
      </select>
      <span>{shown.length} giao dịch</span>
    </div>
    
    {loading ? <Loading/> : shown.length ? (
      <div className="table-card">
        <table>
          <thead>
            <tr>
              <th>Mã GD</th>
              <th>Khách hàng</th>
              <th>Ngày đặt</th>
              <th>Thời gian thuê</th>
              <th>Tổng tiền</th>
              <th>Trạng thái</th>
              <th/>
            </tr>
          </thead>
          <tbody>
            {shown.map(row => (
              <tr key={row.rentingTransactionId}>
                <td><b>#{row.rentingTransactionId}</b></td>
                <td>{customerName(row.customerId)}</td>
                <td>{date(row.rentingDate)}</td>
                <td>{date(row.details?.[0]?.startDate)} – {date(row.details?.[0]?.endDate)}</td>
                <td><b>{money(row.totalPrice)}</b></td>
                <td><RentalStatus value={row.rentingStatus}/></td>
                <td>
                  <button className="table-action" onClick={() => openDetail(row)} title="Xử lý giao dịch">
                    <i className="bi bi-eye"/>
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    ) : <EmptyState title="Không có giao dịch phù hợp"/>}
    
    {/* POPUP CHI TIẾT GIAO DỊCH (MODAL) */}
    {selected && (
      <div className="modal-overlay" onClick={() => setSelected(null)}>
        <div className="rental-admin-detail panel" onClick={e => e.stopPropagation()}>
          <div className="rental-admin-head">
            <div>
              <small>GIAO DỊCH #{selected.rentingTransactionId}</small>
              <h3>{customerName(selected.customerId)}</h3>
            </div>
            <RentalStatus value={selected.rentingStatus}/>
            <button className="icon-btn" onClick={() => setSelected(null)}>
              <i className="bi bi-x-lg"/>
            </button>
          </div>

          <div className="rental-admin-grid">
            <section>
              <h5>Xe đã đặt</h5>
              {selected.details?.map(item => (
                <div className="rental-car-row" key={item.carId}>
                  <i className="bi bi-car-front"/>
                  <span>
                    <b>Xe #{item.carId}</b>
                    <small>{date(item.startDate)} – {date(item.endDate)}</small>
                  </span>
                  <strong>{money(item.price)}</strong>
                </div>
              ))}
            </section>
            
            <section className="rental-cost">
              <h5>Chi phí</h5>
              <p><span>Tiền thuê</span><b>{money(selected.basePrice || selected.totalPrice)}</b></p>
              <p><span>Phí trả muộn</span><b>{money(selected.lateFee)}</b></p>
              <p><span>Phí hư hỏng</span><b>{money(selected.damageFee)}</b></p>
              <p><span>Phí khác</span><b>{money(selected.otherFee)}</b></p>
              <div><span>Tổng cộng</span><strong>{money(selected.totalPrice)}</strong></div>
            </section>
          </div>

          {selected.rentingStatus === 'RENTING' && (
            <div className="fee-form">
              <label>Phí trả muộn<input type="number" min="0" value={fees.lateFee} onChange={e => setFees({ ...fees, lateFee: Number(e.target.value) })}/></label>
              <label>Phí hư hỏng<input type="number" min="0" value={fees.damageFee} onChange={e => setFees({ ...fees, damageFee: Number(e.target.value) })}/></label>
              <label>Phí khác<input type="number" min="0" value={fees.otherFee} onChange={e => setFees({ ...fees, otherFee: Number(e.target.value) })}/></label>
              <div className="full maintenance-options">
                <b>Tình trạng xe sau khi trả</b>
                {selected.details?.map(item => (
                  <label key={item.carId}>
                    <input 
                      type="checkbox" 
                      checked={fees.maintenanceCarIds.includes(item.carId)} 
                      onChange={e => setFees(current => ({ 
                        ...current, 
                        maintenanceCarIds: e.target.checked 
                          ? [...current.maintenanceCarIds, item.carId] 
                          : current.maintenanceCarIds.filter(id => id !== item.carId) 
                      }))}
                    /> 
                    Xe #{item.carId} cần bảo trì
                  </label>
                ))}
              </div>
              <label className="full">Ghi chú<textarea value={fees.adminNote} onChange={e => setFees({ ...fees, adminNote: e.target.value })}/></label>
            </div>
          )}

          {['PENDING','CONFIRMED'].includes(selected.rentingStatus) && (
            <textarea className="admin-note" placeholder="Ghi chú xử lý (không bắt buộc)" value={note} onChange={e => setNote(e.target.value)}/>
          )} 

          <div className="rental-actions">
            {selected.rentingStatus === 'PENDING' && (
              <>
                <button disabled={busy} className="btn btn-success" onClick={() => run(() => rentingApi.confirm(selected.rentingTransactionId), 'Đã xác nhận yêu cầu thuê xe')}>Xác nhận</button>
                <button disabled={busy} className="btn btn-outline-danger" onClick={() => run(() => rentingApi.reject(selected.rentingTransactionId, note), 'Đã từ chối yêu cầu')}>Từ chối</button>
              </>
            )}
            {selected.rentingStatus === 'CONFIRMED' && (
              <button disabled={busy} className="btn btn-brand" onClick={() => run(() => rentingApi.deliver(selected.rentingTransactionId), 'Đã xác nhận giao xe')}>Xác nhận giao xe</button>
            )}
            {selected.rentingStatus === 'RENTING' && (
              <button disabled={busy} className="btn btn-success" onClick={() => run(() => rentingApi.complete(selected.rentingTransactionId, fees), 'Đã nhận xe và hoàn tất giao dịch')}>Xác nhận trả xe & hoàn tất</button>
            )}
            {['PENDING','CONFIRMED'].includes(selected.rentingStatus) && (
              <button disabled={busy} className="btn btn-outline-secondary" onClick={() => run(() => rentingApi.cancel(selected.rentingTransactionId, note), 'Đã hủy giao dịch')}>Hủy giao dịch</button>
            )}
          </div>
        </div>
      </div>
    )}
  </>;
}

function AdminCreateRental({ customers, onCreated }) {
  const [form, setForm] = useState({ customerId: '', carId: '', startDate: today(), endDate: today() });
  const [cars, setCars] = useState([]);
  const [busy, setBusy] = useState(false);
  
  const findCars = async () => {
    if (form.endDate < form.startDate) return toast.error('Ngày trả phải sau ngày nhận');
    try { setCars(listOf(await carApi.available(form.startDate, form.endDate))); }
    catch (e) { toast.error(apiError(e)); }
  };
  
  useEffect(() => { findCars(); }, []);
  
  const submit = async e => {
    e.preventDefault(); 
    setBusy(true);
    try { 
      const item = await rentingApi.createForCustomer(Number(form.customerId), [{ carId: Number(form.carId), startDate: form.startDate, endDate: form.endDate }]); 
      toast.success('Đã tạo yêu cầu thuê xe ở trạng thái PENDING'); 
      onCreated(item); 
    }
    catch (error) { 
      toast.error(apiError(error)); 
    }
    finally { 
      setBusy(false); 
    }
  };
  
  return (
    <form className="panel admin-create-rental" onSubmit={submit}>
      <label>
        Khách hàng
        <select required value={form.customerId} onChange={e => setForm({ ...form, customerId: e.target.value })}>
          <option value="">Chọn khách hàng</option>
          {customers.filter(x => x.customerStatus).map(x => (
            <option key={x.customerId} value={x.customerId}>{x.customerName} — {x.email}</option>
          ))}
        </select>
      </label>
      <label>
        Ngày nhận
        <input required type="date" min={today()} value={form.startDate} onChange={e => setForm({ ...form, startDate: e.target.value })}/>
      </label>
      <label>
        Ngày trả
        <input required type="date" min={form.startDate} value={form.endDate} onChange={e => setForm({ ...form, endDate: e.target.value })}/>
      </label>
      <button type="button" className="btn btn-outline-primary" onClick={findCars}>Kiểm tra xe trống</button>
      <label className="car-select">
        Xe
        <select required value={form.carId} onChange={e => setForm({ ...form, carId: e.target.value })}>
          <option value="">Chọn xe còn trống</option>
          {cars.map(x => (
            <option key={x.carId} value={x.carId}>{x.carName} — {money(x.carRentingPricePerDay)}/ngày</option>
          ))}
        </select>
      </label>
      <button disabled={busy} className="btn btn-brand">Tạo yêu cầu PENDING</button>
    </form>
  );
}