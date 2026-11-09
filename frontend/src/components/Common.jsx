import { useEffect } from 'react';

export const Loading = ({ text = 'Đang tải dữ liệu...' }) => <div className="loading"><span className="spinner-border spinner-border-sm" /> {text}</div>;
export const EmptyState = ({ title = 'Chưa có dữ liệu', text = 'Không tìm thấy kết quả phù hợp.' }) => <div className="empty"><i className="bi bi-inbox"/><h5>{title}</h5><p>{text}</p></div>;
export function ConfirmDialog({ show, title = 'Xác nhận', message, onConfirm, onCancel, busy }) {
  useEffect(() => { const fn = e => e.key === 'Escape' && onCancel(); if (show) addEventListener('keydown', fn); return () => removeEventListener('keydown', fn); }, [show, onCancel]);
  if (!show) return null;
  return <div className="modal-backdrop-custom"><div className="confirm-card"><div className="confirm-icon"><i className="bi bi-exclamation-triangle"/></div><h4>{title}</h4><p>{message}</p><div className="d-flex gap-2 justify-content-center"><button className="btn btn-light" onClick={onCancel}>Hủy</button><button disabled={busy} className="btn btn-danger" onClick={onConfirm}>{busy ? 'Đang xử lý...' : 'Xác nhận'}</button></div></div></div>;
}
export const Status = ({ active, text }) => <span className={`status ${active ? 'available' : 'inactive'}`}><i className="bi bi-circle-fill"/> {text || (active ? 'Sẵn sàng' : 'Không khả dụng')}</span>;
export const Pagination = ({ page, total, onChange }) => total > 1 && <nav className="pagination-wrap"><button disabled={!page} onClick={() => onChange(page - 1)}><i className="bi bi-chevron-left"/></button><span>Trang {page + 1} / {total}</span><button disabled={page >= total - 1} onClick={() => onChange(page + 1)}><i className="bi bi-chevron-right"/></button></nav>;
