import React, { useEffect, useState } from 'react';
import { Plus, Pencil, Trash2, Warehouse as WIcon } from 'lucide-react';
import { getWarehouses, createWarehouse, updateWarehouse, deleteWarehouse } from '../services/api';
const emptyForm = { name: '', location: '' };
export default function Warehouses() {
  const [warehouses, setWarehouses] = useState([]); const [loading, setLoading] = useState(true);
  const [modal, setModal] = useState(false); const [editing, setEditing] = useState(null);
  const [form, setForm] = useState(emptyForm); const [error, setError] = useState(''); const [success, setSuccess] = useState('');
  const load = async () => { try { const r = await getWarehouses(); setWarehouses(r.data); } catch { setError('Failed to load'); } finally { setLoading(false); } };
  useEffect(() => { load(); }, []);
  const openAdd = () => { setEditing(null); setForm(emptyForm); setError(''); setModal(true); };
  const openEdit = (w) => { setEditing(w); setForm({ name: w.name, location: w.location }); setError(''); setModal(true); };
  const handleSubmit = async () => {
    if (!form.name || !form.location) { setError('All fields required'); return; }
    try { if (editing) await updateWarehouse(editing.warehouseId, form); else await createWarehouse(form); setModal(false); setSuccess(editing ? 'Updated!' : 'Created!'); setTimeout(() => setSuccess(''), 3000); load(); }
    catch { setError('Operation failed'); }
  };
  const handleDelete = async (id) => { if (!window.confirm('Delete?')) return; try { await deleteWarehouse(id); load(); } catch { setError('Delete failed'); } };
  return (
    <div>
      <div className="page-header"><div><div className="page-title">Warehouses</div><div className="page-desc">Manage warehouse locations</div></div><button className="btn btn-primary" onClick={openAdd}><Plus size={15}/>Add Warehouse</button></div>
      {success && <div className="alert-banner alert-banner-success">✓ {success}</div>}
      {error && !modal && <div className="alert-banner alert-banner-error">✕ {error}</div>}
      <div className="card">
        {loading ? <div className="loading"><WIcon size={20}/>Loading...</div> : (
          <div className="table-wrapper"><table>
            <thead><tr><th>#</th><th>Name</th><th>Location</th><th>Actions</th></tr></thead>
            <tbody>{warehouses.length === 0 ? <tr><td colSpan={4}><div className="empty-state"><WIcon size={28}/><p>No warehouses yet</p></div></td></tr>
              : warehouses.map((w, i) => <tr key={w.warehouseId}><td style={{color:'var(--text-muted)'}}>{i+1}</td><td style={{fontWeight:600}}>{w.name}</td><td><span className="badge badge-blue">📍 {w.location}</span></td><td><div style={{display:'flex',gap:6}}><button className="btn-icon" onClick={() => openEdit(w)}><Pencil size={13}/></button><button className="btn-icon" onClick={() => handleDelete(w.warehouseId)} style={{color:'var(--accent-red)'}}><Trash2 size={13}/></button></div></td></tr>)}
            </tbody>
          </table></div>
        )}
      </div>
      {modal && <div className="modal-overlay" onClick={e => e.target===e.currentTarget && setModal(false)}><div className="modal">
        <div className="modal-header"><div className="modal-title">{editing ? 'Edit' : 'Add'} Warehouse</div><button className="btn-icon" onClick={() => setModal(false)}>✕</button></div>
        <div className="modal-body">
          {error && <div className="alert-banner alert-banner-error">✕ {error}</div>}
          <div className="form-group"><label className="form-label">Name</label><input className="form-input" placeholder="e.g. Main Warehouse" value={form.name} onChange={e => setForm({...form,name:e.target.value})}/></div>
          <div className="form-group"><label className="form-label">Location</label><input className="form-input" placeholder="e.g. Pune, Maharashtra" value={form.location} onChange={e => setForm({...form,location:e.target.value})}/></div>
        </div>
        <div className="modal-footer"><button className="btn btn-secondary" onClick={() => setModal(false)}>Cancel</button><button className="btn btn-primary" onClick={handleSubmit}>{editing ? 'Update' : 'Create'}</button></div>
      </div></div>}
    </div>
  );
}
