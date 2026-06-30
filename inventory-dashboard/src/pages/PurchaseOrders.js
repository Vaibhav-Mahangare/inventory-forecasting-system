import React, { useEffect, useState } from 'react';
import { Plus, Truck, Trash2 } from 'lucide-react';
import { getPurchaseOrders, createPurchaseOrder, deletePurchaseOrder, updateOrderStatus, getProducts, getSuppliers } from '../services/api';
const emptyForm = { quantity: '', orderDate: new Date().toISOString().split('T')[0], expectedDelivery: '', status: 'PENDING', productId: '', supplierId: '' };
const STATUSES = ['PENDING','CONFIRMED','SHIPPED','DELIVERED','CANCELLED'];
export default function PurchaseOrders() {
  const [orders, setOrders] = useState([]); const [products, setProducts] = useState([]); const [suppliers, setSuppliers] = useState([]);
  const [loading, setLoading] = useState(true); const [modal, setModal] = useState(false); const [form, setForm] = useState(emptyForm);
  const [error, setError] = useState(''); const [success, setSuccess] = useState(''); const [filter, setFilter] = useState('ALL');
  const load = async () => {
    try { const [o,p,s] = await Promise.allSettled([getPurchaseOrders(),getProducts(),getSuppliers()]); if(o.status==='fulfilled') setOrders(o.value.data); if(p.status==='fulfilled') setProducts(p.value.data); if(s.status==='fulfilled') setSuppliers(s.value.data); }
    catch { setError('Failed to load'); } finally { setLoading(false); }
  };
  useEffect(() => { load(); }, []);
  const handleSubmit = async () => {
    if (!form.quantity||!form.orderDate||!form.expectedDelivery||!form.productId||!form.supplierId) { setError('All fields required'); return; }
    try { await createPurchaseOrder(form); setModal(false); setSuccess('Order created!'); setTimeout(()=>setSuccess(''),3000); load(); }
    catch { setError('Operation failed'); }
  };
  const handleStatusChange = async (id, status) => { try { await updateOrderStatus(id,status); load(); setSuccess('Status updated!'); setTimeout(()=>setSuccess(''),2000); } catch { setError('Update failed'); } };
  const handleDelete = async (id) => { if(!window.confirm('Delete?')) return; try { await deletePurchaseOrder(id); load(); } catch { setError('Delete failed'); } };
  const filtered = filter==='ALL' ? orders : orders.filter(o=>o.status===filter);
  return (
    <div>
      <div className="page-header"><div><div className="page-title">Purchase Orders</div><div className="page-desc">Track and manage supplier orders</div></div><button className="btn btn-primary" onClick={()=>{setForm(emptyForm);setError('');setModal(true);}}><Plus size={15}/>New Order</button></div>
      {success && <div className="alert-banner alert-banner-success">✓ {success}</div>}
      {error && !modal && <div className="alert-banner alert-banner-error">✕ {error}</div>}
      <div style={{display:'flex',gap:8,marginBottom:16,flexWrap:'wrap'}}>
        {['ALL',...STATUSES].map(s=><button key={s} className={`btn btn-sm ${filter===s?'btn-primary':'btn-secondary'}`} onClick={()=>setFilter(s)}>{s}</button>)}
      </div>
      <div className="card">
        {loading ? <div className="loading"><Truck size={20}/>Loading...</div> : (
          <div className="table-wrapper"><table>
            <thead><tr><th>#</th><th>Product</th><th>Supplier</th><th>Qty</th><th>Order Date</th><th>Expected</th><th>Status</th><th>Actions</th></tr></thead>
            <tbody>{filtered.length===0
              ? <tr><td colSpan={8}><div className="empty-state"><Truck size={28}/><p>No orders found</p></div></td></tr>
              : filtered.map((o,i)=><tr key={o.orderId}><td style={{color:'var(--text-muted)'}}>{i+1}</td><td style={{fontWeight:600}}>{o.productName}</td><td style={{color:'var(--text-secondary)'}}>{o.supplierName}</td><td>{o.quantity}</td><td style={{fontSize:12,color:'var(--text-secondary)'}}>{o.orderDate}</td><td style={{fontSize:12,color:'var(--text-secondary)'}}>{o.expectedDelivery}</td>
                <td><select value={o.status} onChange={e=>handleStatusChange(o.orderId,e.target.value)} style={{background:'var(--bg-hover)',border:'1px solid var(--border)',borderRadius:6,color:'var(--text-primary)',padding:'4px 8px',fontSize:12,cursor:'pointer'}}>{STATUSES.map(s=><option key={s} value={s}>{s}</option>)}</select></td>
                <td><button className="btn-icon" onClick={()=>handleDelete(o.orderId)} style={{color:'var(--accent-red)'}}><Trash2 size={13}/></button></td>
              </tr>)}
            </tbody>
          </table></div>
        )}
      </div>
      {modal && <div className="modal-overlay" onClick={e=>e.target===e.currentTarget&&setModal(false)}><div className="modal">
        <div className="modal-header"><div className="modal-title">New Purchase Order</div><button className="btn-icon" onClick={()=>setModal(false)}>✕</button></div>
        <div className="modal-body">
          {error && <div className="alert-banner alert-banner-error">✕ {error}</div>}
          <div className="form-row">
            <div className="form-group"><label className="form-label">Product</label><select className="form-select" value={form.productId} onChange={e=>setForm({...form,productId:e.target.value})}><option value="">Select...</option>{products.map(p=><option key={p.productId} value={p.productId}>{p.name}</option>)}</select></div>
            <div className="form-group"><label className="form-label">Supplier</label><select className="form-select" value={form.supplierId} onChange={e=>setForm({...form,supplierId:e.target.value})}><option value="">Select...</option>{suppliers.map(s=><option key={s.supplierId} value={s.supplierId}>{s.name}</option>)}</select></div>
          </div>
          <div className="form-row">
            <div className="form-group"><label className="form-label">Quantity</label><input className="form-input" type="number" value={form.quantity} onChange={e=>setForm({...form,quantity:e.target.value})}/></div>
            <div className="form-group"><label className="form-label">Status</label><select className="form-select" value={form.status} onChange={e=>setForm({...form,status:e.target.value})}>{STATUSES.map(s=><option key={s} value={s}>{s}</option>)}</select></div>
          </div>
          <div className="form-row">
            <div className="form-group"><label className="form-label">Order Date</label><input className="form-input" type="date" value={form.orderDate} onChange={e=>setForm({...form,orderDate:e.target.value})}/></div>
            <div className="form-group"><label className="form-label">Expected Delivery</label><input className="form-input" type="date" value={form.expectedDelivery} onChange={e=>setForm({...form,expectedDelivery:e.target.value})}/></div>
          </div>
        </div>
        <div className="modal-footer"><button className="btn btn-secondary" onClick={()=>setModal(false)}>Cancel</button><button className="btn btn-primary" onClick={handleSubmit}>Create Order</button></div>
      </div></div>}
    </div>
  );
}
