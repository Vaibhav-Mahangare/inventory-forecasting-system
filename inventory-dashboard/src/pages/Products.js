import React, { useEffect, useState } from 'react';
import { Plus, Pencil, Trash2, Package } from 'lucide-react';
import { getProducts, createProduct, updateProduct, deleteProduct } from '../services/api';

const emptyForm = { name: '', category: '', price: '', leadTimeDays: '' };

export default function Products() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [modal, setModal] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form, setForm] = useState(emptyForm);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const load = async () => {
    try { const res = await getProducts(); setProducts(res.data); }
    catch { setError('Failed to load products'); }
    finally { setLoading(false); }
  };
  useEffect(() => { load(); }, []);

  const openAdd = () => { setEditing(null); setForm(emptyForm); setError(''); setModal(true); };
  const openEdit = (p) => { setEditing(p); setForm({ name: p.name, category: p.category, price: p.price, leadTimeDays: p.leadTimeDays }); setError(''); setModal(true); };

  const handleSubmit = async () => {
    if (!form.name || !form.category || !form.price || !form.leadTimeDays) { setError('All fields are required'); return; }
    try {
      if (editing) await updateProduct(editing.productId, form);
      else await createProduct(form);
      setModal(false); setSuccess(editing ? 'Product updated!' : 'Product created!');
      setTimeout(() => setSuccess(''), 3000); load();
    } catch { setError('Operation failed. Please try again.'); }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this product?')) return;
    try { await deleteProduct(id); load(); setSuccess('Product deleted!'); setTimeout(() => setSuccess(''), 3000); }
    catch { setError('Delete failed'); }
  };

  return (
    <div>
      <div className="page-header">
        <div><div className="page-title">Products</div><div className="page-desc">Manage your product catalog</div></div>
        <button className="btn btn-primary" onClick={openAdd}><Plus size={15} />Add Product</button>
      </div>
      {success && <div className="alert-banner alert-banner-success">✓ {success}</div>}
      {error && !modal && <div className="alert-banner alert-banner-error">✕ {error}</div>}
      <div className="card">
        {loading ? <div className="loading"><Package size={20} />Loading...</div> : (
          <div className="table-wrapper">
            <table>
              <thead><tr><th>#</th><th>Name</th><th>Category</th><th>Price (₹)</th><th>Lead Time</th><th>Created</th><th>Actions</th></tr></thead>
              <tbody>
                {products.length === 0
                  ? <tr><td colSpan={7}><div className="empty-state"><Package size={28} /><p>No products yet. Add one!</p></div></td></tr>
                  : products.map((p, i) => (
                    <tr key={p.productId}>
                      <td style={{ color: 'var(--text-muted)' }}>{i + 1}</td>
                      <td style={{ fontWeight: 600 }}>{p.name}</td>
                      <td><span className="badge badge-blue">{p.category}</span></td>
                      <td style={{ fontWeight: 500 }}>₹{parseFloat(p.price).toLocaleString()}</td>
                      <td><span className="badge badge-grey">{p.leadTimeDays}d</span></td>
                      <td style={{ color: 'var(--text-secondary)' }}>{p.createdAt ? new Date(p.createdAt).toLocaleDateString() : '—'}</td>
                      <td>
                        <div style={{ display: 'flex', gap: 6 }}>
                          <button className="btn-icon" onClick={() => openEdit(p)}><Pencil size={13} /></button>
                          <button className="btn-icon" onClick={() => handleDelete(p.productId)} style={{ color: 'var(--accent-red)' }}><Trash2 size={13} /></button>
                        </div>
                      </td>
                    </tr>
                  ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
      {modal && (
        <div className="modal-overlay" onClick={e => e.target === e.currentTarget && setModal(false)}>
          <div className="modal">
            <div className="modal-header"><div className="modal-title">{editing ? 'Edit Product' : 'Add Product'}</div><button className="btn-icon" onClick={() => setModal(false)}>✕</button></div>
            <div className="modal-body">
              {error && <div className="alert-banner alert-banner-error">✕ {error}</div>}
              <div className="form-group"><label className="form-label">Product Name</label><input className="form-input" placeholder="e.g. Rice 5kg" value={form.name} onChange={e => setForm({ ...form, name: e.target.value })} /></div>
              <div className="form-group"><label className="form-label">Category</label><input className="form-input" placeholder="e.g. Food, Electronics" value={form.category} onChange={e => setForm({ ...form, category: e.target.value })} /></div>
              <div className="form-row">
                <div className="form-group"><label className="form-label">Price (₹)</label><input className="form-input" type="number" placeholder="0.00" value={form.price} onChange={e => setForm({ ...form, price: e.target.value })} /></div>
                <div className="form-group"><label className="form-label">Lead Time (Days)</label><input className="form-input" type="number" placeholder="e.g. 5" value={form.leadTimeDays} onChange={e => setForm({ ...form, leadTimeDays: e.target.value })} /></div>
              </div>
            </div>
            <div className="modal-footer">
              <button className="btn btn-secondary" onClick={() => setModal(false)}>Cancel</button>
              <button className="btn btn-primary" onClick={handleSubmit}>{editing ? 'Update' : 'Create'}</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
