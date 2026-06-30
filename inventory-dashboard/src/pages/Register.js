import React, { useState } from 'react';
import axios from 'axios';
import { useAuth } from '../context/AuthContext';

export default function Register({ onSwitchToLogin }) {
  const { login } = useAuth();
  const [form, setForm] = useState({ username: '', email: '', password: '', role: 'INVENTORY_MANAGER' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.username || !form.email || !form.password) { setError('All fields are required'); return; }
    if (form.password.length < 6) { setError('Password must be at least 6 characters'); return; }
    setLoading(true); setError('');
    try {
      const res = await axios.post('http://localhost:8080/api/auth/register', form);
      login(res.data);
    } catch (err) {
      setError(err.response?.data?.message || 'Registration failed. Try again.');
    } finally { setLoading(false); }
  };

  return (
    <div style={{ minHeight: '100vh', background: 'var(--bg-primary)', display: 'flex', alignItems: 'center', justifyContent: 'center', padding: 20 }}>
      <div style={{ width: '100%', maxWidth: 420 }}>
        <div style={{ textAlign: 'center', marginBottom: 32 }}>
          <div style={{ width: 56, height: 56, background: 'linear-gradient(135deg, var(--accent-blue), var(--accent-purple))', borderRadius: 16, display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 26, margin: '0 auto 16px' }}>📦</div>
          <div style={{ fontFamily: 'Syne', fontSize: 24, fontWeight: 800 }}>InvenAI</div>
          <div style={{ color: 'var(--text-secondary)', fontSize: 13, marginTop: 4 }}>Create a new account</div>
        </div>

        <div className="card">
          <div style={{ fontFamily: 'Syne', fontSize: 18, fontWeight: 700, marginBottom: 4 }}>Create Account</div>
          <div style={{ color: 'var(--text-secondary)', fontSize: 13, marginBottom: 24 }}>Fill in your details below</div>

          {error && <div className="alert-banner alert-banner-error" style={{ marginBottom: 16 }}>✕ {error}</div>}

          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label className="form-label">Username</label>
              <input className="form-input" placeholder="Choose a username" value={form.username}
                onChange={e => setForm({ ...form, username: e.target.value })} />
            </div>
            <div className="form-group">
              <label className="form-label">Email</label>
              <input className="form-input" type="email" placeholder="your@email.com" value={form.email}
                onChange={e => setForm({ ...form, email: e.target.value })} />
            </div>
            <div className="form-group">
              <label className="form-label">Password</label>
              <input className="form-input" type="password" placeholder="Min 6 characters" value={form.password}
                onChange={e => setForm({ ...form, password: e.target.value })} />
            </div>
            <div className="form-group">
              <label className="form-label">Role</label>
              <select className="form-select" value={form.role} onChange={e => setForm({ ...form, role: e.target.value })}>
                <option value="INVENTORY_MANAGER">Inventory Manager</option>
                <option value="ADMIN">Admin</option>
              </select>
            </div>
            <button type="submit" className="btn btn-primary" style={{ width: '100%', justifyContent: 'center', padding: '10px', marginTop: 8 }} disabled={loading}>
              {loading ? 'Creating account...' : 'Create Account'}
            </button>
          </form>

          <div style={{ marginTop: 20, textAlign: 'center', fontSize: 13, color: 'var(--text-secondary)' }}>
            Already have an account?{' '}
            <span style={{ color: 'var(--accent-blue)', cursor: 'pointer', fontWeight: 600 }} onClick={onSwitchToLogin}>
              Sign in here
            </span>
          </div>
        </div>
      </div>
    </div>
  );
}
