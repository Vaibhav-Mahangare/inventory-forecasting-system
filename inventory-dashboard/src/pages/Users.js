import React, { useEffect, useState } from 'react';
import { Users as UsersIcon, Trash2 } from 'lucide-react';
import axios from 'axios';

export default function Users() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const load = async () => {
    try { const r = await axios.get('http://localhost:8080/api/auth/users'); setUsers(r.data); }
    catch { setError('Failed to load users'); }
    finally { setLoading(false); }
  };
  useEffect(() => { load(); }, []);

  const toggleStatus = async (userId) => {
    try { await axios.patch(`http://localhost:8080/api/auth/users/${userId}/toggle`); load(); setSuccess('User status updated!'); setTimeout(() => setSuccess(''), 2000); }
    catch { setError('Failed to update status'); }
  };

  const handleDelete = async (userId) => {
    if (!window.confirm('Delete this user permanently?')) return;
    try { await axios.delete(`http://localhost:8080/api/auth/users/${userId}`); load(); setSuccess('User deleted!'); setTimeout(() => setSuccess(''), 2000); }
    catch { setError('Delete failed'); }
  };

  const roleColor = { ADMIN: 'badge-purple', INVENTORY_MANAGER: 'badge-blue' };

  return (
    <div>
      <div className="page-header">
        <div><div className="page-title">User Management</div><div className="page-desc">Manage system users — Admin only</div></div>
        <span className="badge badge-purple" style={{ padding: '8px 12px' }}>🔐 Admin Only</span>
      </div>
      {success && <div className="alert-banner alert-banner-success">✓ {success}</div>}
      {error && <div className="alert-banner alert-banner-error">✕ {error}</div>}
      <div className="card">
        {loading ? <div className="loading"><UsersIcon size={20} />Loading users...</div> : (
          <div className="table-wrapper">
            <table>
              <thead><tr><th>#</th><th>Username</th><th>Email</th><th>Role</th><th>Status</th><th>Created</th><th>Actions</th></tr></thead>
              <tbody>
                {users.length === 0
                  ? <tr><td colSpan={7}><div className="empty-state"><UsersIcon size={28} /><p>No users found</p></div></td></tr>
                  : users.map((u, i) => (
                    <tr key={u.userId}>
                      <td style={{ color: 'var(--text-muted)' }}>{i + 1}</td>
                      <td style={{ fontWeight: 600 }}>{u.username}</td>
                      <td style={{ color: 'var(--text-secondary)' }}>{u.email}</td>
                      <td><span className={`badge ${roleColor[u.role] || 'badge-grey'}`}>{u.role}</span></td>
                      <td>
                        <button onClick={() => toggleStatus(u.userId)}
                          className={`badge ${u.enabled ? 'badge-green' : 'badge-red'}`}
                          style={{ border: 'none', cursor: 'pointer' }}>
                          {u.enabled ? '✓ Active' : '✕ Disabled'}
                        </button>
                      </td>
                      <td style={{ color: 'var(--text-secondary)', fontSize: 12 }}>{u.createdAt ? new Date(u.createdAt).toLocaleDateString() : '—'}</td>
                      <td>
                        <button className="btn-icon" onClick={() => handleDelete(u.userId)} style={{ color: 'var(--accent-red)' }}><Trash2 size={13} /></button>
                      </td>
                    </tr>
                  ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}
