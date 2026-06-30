import React, { useEffect, useState } from 'react';
import { Plus, Trash2, ShoppingCart } from 'lucide-react';
import { getSales, createSale, deleteSale, getProducts, getWarehouses } from '../services/api';
const emptyForm = { quantitySold: '', saleDate: new Date().toISOString().split('T')[0], productId: '', warehouseId: '' };
export default function Sales() {
  const [sales, setSales] = useState([]); const [products, setProducts] = useState([]); const [warehouses, setWarehouses] = useState([]);
  const [loading, setLoading] = useState(true); const [modal, setModal] = useState(false); const [form, setForm] = useState(emptyForm);
  const [error, setError] = useState(''); const [success, setSuccess] = useState('');
  const load = async () => {
    try { const [s, p, w] = await Promise.allSettled([getSales(), getProducts(), getWarehouses()]); if (s.status==='fulfilled') setSales(s.value.data); if (p.status==='fulfilled') setProducts(p.value.data); if (w.status==='fulfilled') setWarehouses(w.value.data); }
    catch { setError('Failed to load'); } finally { setLoading(false); }
  };
  useEffect(() => { load(); }, []);
  const handleSubmit = async () => {
    if (!form.quantitySold || !form.saleDate || !form.productId || !form.warehouseId) { setError('All fields required'); return; }
    try { await createSale(form); setModal(false); setSuccess('Sale recorded! Stock deducted automatically.'); setTimeout(() => setSuccess(''), 4000); load(); }
    catch (e) { setError(e.response?.data?.message || 'Failed. Check stock availability.'); }
  };
  const handleDelete = async (id) => { if (!window.confirm('Delete?')) return; try { await deleteSale(id); load(); } catch { setError('Delete failed'); } };
  const totalSold = sales.reduce((acc, s) => acc + (s.quantitySold || 0), 0);
  return (
    <div>
      <div className="page-header">
        <div><div className="page-title">Sales</div><div className="page-desc">Record and track all sales transactions</div></div>
        <button className="btn btn-primary" onClick={() => { setForm(emptyForm); setError(''); setModal(true); }}><Plus size={15}/>Record Sale</button>
      </div>
      {success && <div className="alert-banner alert-banner-success">✓ {success}</div>}
      {error && !modal && <div className="alert-banner alert-banner-error">✕ {error}</div>}
      <div style={{display:'flex',gap:16,marginBottom:20}}>
        <div className="card" style={{flex:1,padding:'14px 20px'}}><div style={{fontSize:12,color:'var(--text-secondary)'}}>Total Transactions</div><div style={{fontSize:24,fontWeight:800,fontFamily:'Syne',color:'var(--accent-blue)'}}>{sales.length}</div></div>
        <div className="card" style={{flex:1,padding:'14px 20px'}}><div style={{fontSize:12,color:'var(--text-secondary)'}}>Total Units Sold</div><div style={{fontSize:24,fontWeight:800,fontFamily:'Syne',color:'var(--accent-green)'}}>{totalSold}</div></div>
      </div>
      <div className="card">
        {loading ? <div className="loading"><ShoppingCart size={20}/>Loading...</div> : (
          <div className="table-wrapper"><table>
            <thead><tr><th>#</th><th>Product</th><th>Warehouse</th><th>Qty Sold</th><th>Sale Date</th><th>Actions</th></tr></thead>
            <tbody>{sales.length === 0
              ? <tr><td colSpan={6}><div className="empty-state"><ShoppingCart size={28}/><p>No sales recorded yet</p></div></td></tr>
              : [...sales].reverse().map((s, i) => <tr key={s.saleId}><td style={{color:'var(--text-muted)'}}>{sales.length-i}</td><td style={{fontWeight:600}}>{s.productName}</td><td style={{color:'var(--text-secondary)'}}>{s.warehouseName}</td><td><span className="badge badge-green">+{s.quantitySold} units</span></td><td style={{color:'var(--text-secondary)'}}>{s.saleDate}</td><td><button className="btn-icon" onClick={() => handleDelete(s.saleId)} style={{color:'var(--accent-red)'}}><Trash2 size={13}/></button></td></tr>)}
            </tbody>
          </table></div>
        )}
      </div>
      {modal && <div className="modal-overlay" onClick={e => e.target===e.currentTarget && setModal(false)}><div className="modal">
        <div className="modal-header"><div className="modal-title">Record Sale</div><button className="btn-icon" onClick={() => setModal(false)}>✕</button></div>
        <div className="modal-body">
          {error && <div className="alert-banner alert-banner-error">✕ {error}</div>}
          <div className="alert-banner alert-banner-info" style={{marginBottom:16}}>ℹ️ Recording a sale deducts stock and auto-triggers alerts if low.</div>
          <div className="form-group"><label className="form-label">Product</label><select className="form-select" value={form.productId} onChange={e => setForm({...form,productId:e.target.value})}><option value="">Select product...</option>{products.map(p => <option key={p.productId} value={p.productId}>{p.name}</option>)}</select></div>
          <div className="form-group"><label className="form-label">Warehouse</label><select className="form-select" value={form.warehouseId} onChange={e => setForm({...form,warehouseId:e.target.value})}><option value="">Select warehouse...</option>{warehouses.map(w => <option key={w.warehouseId} value={w.warehouseId}>{w.name}</option>)}</select></div>
          <div className="form-row">
            <div className="form-group"><label className="form-label">Quantity Sold</label><input className="form-input" type="number" placeholder="e.g. 10" value={form.quantitySold} onChange={e => setForm({...form,quantitySold:e.target.value})}/></div>
            <div className="form-group"><label className="form-label">Sale Date</label><input className="form-input" type="date" value={form.saleDate} onChange={e => setForm({...form,saleDate:e.target.value})}/></div>
          </div>
        </div>
        <div className="modal-footer"><button className="btn btn-secondary" onClick={() => setModal(false)}>Cancel</button><button className="btn btn-primary" onClick={handleSubmit}>Record Sale</button></div>
      </div></div>}
    </div>
  );
}
