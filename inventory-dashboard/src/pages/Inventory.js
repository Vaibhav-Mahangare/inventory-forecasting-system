import React, { useEffect, useState } from 'react';
import { Plus, Pencil, Trash2, BarChart3, AlertTriangle } from 'lucide-react';
import { getInventories, createInventory, updateInventory, deleteInventory, getLowStockInventories, getProducts, getWarehouses } from '../services/api';
const emptyForm = { quantity: '', reorderPoint: '', productId: '', warehouseId: '' };
export default function Inventory() {
  const [inventories, setInventories] = useState([]); const [products, setProducts] = useState([]); const [warehouses, setWarehouses] = useState([]); const [lowStock, setLowStock] = useState([]);
  const [loading, setLoading] = useState(true); const [modal, setModal] = useState(false); const [editing, setEditing] = useState(null); const [form, setForm] = useState(emptyForm);
  const [error, setError] = useState(''); const [success, setSuccess] = useState(''); const [tab, setTab] = useState('all');
  const load = async () => {
    try {
      const [inv, prods, ware, ls] = await Promise.allSettled([getInventories(), getProducts(), getWarehouses(), getLowStockInventories()]);
      if (inv.status==='fulfilled') setInventories(inv.value.data); if (prods.status==='fulfilled') setProducts(prods.value.data);
      if (ware.status==='fulfilled') setWarehouses(ware.value.data); if (ls.status==='fulfilled') setLowStock(ls.value.data);
    } catch { setError('Failed to load'); } finally { setLoading(false); }
  };
  useEffect(() => { load(); }, []);
  const openAdd = () => { setEditing(null); setForm(emptyForm); setError(''); setModal(true); };
  const openEdit = (i) => { setEditing(i); setForm({ quantity: i.quantity, reorderPoint: i.reorderPoint, productId: i.productId, warehouseId: i.warehouseId }); setError(''); setModal(true); };
  const handleSubmit = async () => {
    if (!form.quantity || !form.reorderPoint || !form.productId || !form.warehouseId) { setError('All fields required'); return; }
    try { if (editing) await updateInventory(editing.inventoryId, form); else await createInventory(form); setModal(false); setSuccess(editing ? 'Updated!' : 'Created!'); setTimeout(() => setSuccess(''), 3000); load(); }
    catch (e) { setError(e.response?.data?.message || 'Operation failed'); }
  };
  const handleDelete = async (id) => { if (!window.confirm('Delete?')) return; try { await deleteInventory(id); load(); } catch { setError('Delete failed'); } };
  const displayed = tab === 'low' ? lowStock : inventories;
  return (
    <div>
      <div className="page-header">
        <div><div className="page-title">Inventory</div><div className="page-desc">Track stock levels across warehouses</div></div>
        <div style={{display:'flex',gap:10}}>{lowStock.length > 0 && <span className="badge badge-red" style={{padding:'8px 12px'}}><AlertTriangle size={12}/>{lowStock.length} Low Stock</span>}<button className="btn btn-primary" onClick={openAdd}><Plus size={15}/>Add Inventory</button></div>
      </div>
      {success && <div className="alert-banner alert-banner-success">✓ {success}</div>}
      {error && !modal && <div className="alert-banner alert-banner-error">✕ {error}</div>}
      <div style={{display:'flex',gap:8,marginBottom:16}}>
        <button className={`btn ${tab==='all'?'btn-primary':'btn-secondary'}`} onClick={() => setTab('all')}>All ({inventories.length})</button>
        <button className={`btn ${tab==='low'?'btn-danger':'btn-secondary'}`} onClick={() => setTab('low')}><AlertTriangle size={13}/>Low Stock ({lowStock.length})</button>
      </div>
      <div className="card">
        {loading ? <div className="loading"><BarChart3 size={20}/>Loading...</div> : (
          <div className="table-wrapper"><table>
            <thead><tr><th>#</th><th>Product</th><th>Warehouse</th><th>Quantity</th><th>Reorder Point</th><th>Status</th><th>Last Updated</th><th>Actions</th></tr></thead>
            <tbody>{displayed.length === 0
              ? <tr><td colSpan={8}><div className="empty-state"><BarChart3 size={28}/><p>No inventory records</p></div></td></tr>
              : displayed.map((item, i) => {
                const isLow = item.quantity <= item.reorderPoint;
                return <tr key={item.inventoryId}>
                  <td style={{color:'var(--text-muted)'}}>{i+1}</td>
                  <td style={{fontWeight:600}}>{item.productName}</td>
                  <td style={{color:'var(--text-secondary)'}}>{item.warehouseName}</td>
                  <td><span className={`badge ${isLow?'badge-red':'badge-green'}`}>{item.quantity}</span></td>
                  <td>{item.reorderPoint}</td>
                  <td>{isLow ? <span className="badge badge-red"><AlertTriangle size={10}/>Low Stock</span> : <span className="badge badge-green">✓ Healthy</span>}</td>
                  <td style={{color:'var(--text-secondary)'}}>{item.lastUpdated ? new Date(item.lastUpdated).toLocaleDateString() : '—'}</td>
                  <td><div style={{display:'flex',gap:6}}><button className="btn-icon" onClick={() => openEdit(item)}><Pencil size={13}/></button><button className="btn-icon" onClick={() => handleDelete(item.inventoryId)} style={{color:'var(--accent-red)'}}><Trash2 size={13}/></button></div></td>
                </tr>;
              })}
            </tbody>
          </table></div>
        )}
      </div>
      {modal && <div className="modal-overlay" onClick={e => e.target===e.currentTarget && setModal(false)}><div className="modal">
        <div className="modal-header"><div className="modal-title">{editing ? 'Update' : 'Add'} Inventory</div><button className="btn-icon" onClick={() => setModal(false)}>✕</button></div>
        <div className="modal-body">
          {error && <div className="alert-banner alert-banner-error">✕ {error}</div>}
          <div className="form-group"><label className="form-label">Product</label><select className="form-select" value={form.productId} onChange={e => setForm({...form,productId:e.target.value})}><option value="">Select product...</option>{products.map(p => <option key={p.productId} value={p.productId}>{p.name}</option>)}</select></div>
          <div className="form-group"><label className="form-label">Warehouse</label><select className="form-select" value={form.warehouseId} onChange={e => setForm({...form,warehouseId:e.target.value})}><option value="">Select warehouse...</option>{warehouses.map(w => <option key={w.warehouseId} value={w.warehouseId}>{w.name}</option>)}</select></div>
          <div className="form-row">
            <div className="form-group"><label className="form-label">Quantity</label><input className="form-input" type="number" placeholder="e.g. 100" value={form.quantity} onChange={e => setForm({...form,quantity:e.target.value})}/></div>
            <div className="form-group"><label className="form-label">Reorder Point</label><input className="form-input" type="number" placeholder="e.g. 20" value={form.reorderPoint} onChange={e => setForm({...form,reorderPoint:e.target.value})}/></div>
          </div>
        </div>
        <div className="modal-footer"><button className="btn btn-secondary" onClick={() => setModal(false)}>Cancel</button><button className="btn btn-primary" onClick={handleSubmit}>{editing ? 'Update' : 'Create'}</button></div>
      </div></div>}
    </div>
  );
}
