import React, { useEffect, useState } from 'react';
import { Plus, Pencil, Trash2, Users } from 'lucide-react';
import { getSuppliers, createSupplier, updateSupplier, deleteSupplier } from '../services/api';
const emptyForm = { name: '', contactEmail: '', phone: '' };
export default function Suppliers() {
  const [suppliers, setSuppliers] = useState([]); const [loading, setLoading] = useState(true); const [modal, setModal] = useState(false);
  const [editing, setEditing] = useState(null); const [form, setForm] = useState(emptyForm); const [error, setError] = useState(''); const [success, setSuccess] = useState('');
  const load = async () => { try { const r = await getSuppliers(); setSuppliers(r.data); } catch { setError('Failed'); } finally { setLoading(false); } };
  useEffect(() => { load(); }, []);
  const openAdd = () => { setEditing(null); setForm(emptyForm); setError(''); setModal(true); };
  const openEdit = (s) => { setEditing(s); setForm({ name: s.name, contactEmail: s.contactEmail, phone: s.phone }); setError(''); setModal(true); };
  const handleSubmit = async () => {
    if (!form.name || !form.contactEmail || !form.phone) { setError('All fields required'); return; }
    try { if (editing) await updateSupplier(editing.supplierId, form); else await createSupplier(form); setModal(false); setSuccess(editing ? 'Updated!' : 'Added!'); setTimeout(() => setSuccess(''), 3000); load(); }
    catch { setError('Operation failed'); }
  };
  const handleDelete = async (id) => { if (!window.confirm('Delete?')) return; try { await deleteSupplier(id); load(); } catch { setError('Delete failed'); } };
  return (
    <div>
      <div className="page-header"><div><div className="page-title">Suppliers</div><div className="page-desc">Manage your supplier network</div></div><button className="btn btn-primary" onClick={openAdd}><Plus size={15}/>Add Supplier</button></div>
      {success && <div className="alert-banner alert-banner-success">✓ {success}</div>}
      {error && !modal && <div className="alert-banner alert-banner-error">✕ {error}</div>}
      <div className="card">
        {loading ? <div className="loading"><Users size={20}/>Loading...</div> : (
          <div className="table-wrapper"><table>
            <thead><tr><th>#</th><th>Name</th><th>Email</th><th>Phone</th><th>Actions</th></tr></thead>
            <tbody>{suppliers.length === 0
              ? <tr><td colSpan={5}><div className="empty-state"><Users size={28}/><p>No suppliers yet</p></div></td></tr>
              : suppliers.map((s, i) => <tr key={s.supplierId}><td style={{color:'var(--text-muted)'}}>{i+1}</td><td style={{fontWeight:600}}>{s.name}</td><td style={{color:'var(--text-secondary)'}}>{s.contactEmail}</td><td>{s.phone}</td><td><div style={{display:'flex',gap:6}}><button className="btn-icon" onClick={() => openEdit(s)}><Pencil size={13}/></button><button className="btn-icon" onClick={() => handleDelete(s.supplierId)} style={{color:'var(--accent-red)'}}><Trash2 size={13}/></button></div></td></tr>)}
            </tbody>
          </table></div>
        )}
      </div>
      {modal && <div className="modal-overlay" onClick={e => e.target===e.currentTarget && setModal(false)}><div className="modal">
        <div className="modal-header"><div className="modal-title">{editing ? 'Edit' : 'Add'} Supplier</div><button className="btn-icon" onClick={() => setModal(false)}>✕</button></div>
        <div className="modal-body">
          {error && <div className="alert-banner alert-banner-error">✕ {error}</div>}
          <div className="form-group"><label className="form-label">Company Name</label><input className="form-input" placeholder="e.g. ABC Traders" value={form.name} onChange={e => setForm({...form,name:e.target.value})}/></div>
          <div className="form-group"><label className="form-label">Contact Email</label><input className="form-input" type="email" placeholder="contact@supplier.com" value={form.contactEmail} onChange={e => setForm({...form,contactEmail:e.target.value})}/></div>
          <div className="form-group"><label className="form-label">Phone</label><input className="form-input" placeholder="+91 9876543210" value={form.phone} onChange={e => setForm({...form,phone:e.target.value})}/></div>
        </div>
        <div className="modal-footer"><button className="btn btn-secondary" onClick={() => setModal(false)}>Cancel</button><button className="btn btn-primary" onClick={handleSubmit}>{editing ? 'Update' : 'Add'}</button></div>
      </div></div>}
    </div>
  );
}
