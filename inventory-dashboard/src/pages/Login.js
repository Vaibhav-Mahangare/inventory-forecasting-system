import React, { useState } from 'react';
import axios from 'axios';
import { useAuth } from '../context/AuthContext';

export default function Login({ onSwitchToRegister }) {
  const { login } = useAuth();
  const [form, setForm] = useState({ username: '', password: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.username || !form.password) { setError('Both fields are required'); return; }
    setLoading(true); setError('');
    try {
      const res = await axios.post('http://localhost:8080/api/auth/login', form);
      login(res.data);
    } catch (err) {
      setError(err.response?.data?.message || 'Invalid username or password');
    } finally { setLoading(false); }
  };

  return (
    <div style={{ minHeight: '100vh', background: 'var(--bg-primary)', display: 'flex', alignItems: 'center', justifyContent: 'center', padding: 20 }}>
      <div style={{ width: '100%', maxWidth: 420 }}>

        {/* Logo */}
        <div style={{ textAlign: 'center', marginBottom: 32 }}>
          <div style={{ width: 56, height: 56, background: 'linear-gradient(135deg, var(--accent-blue), var(--accent-purple))', borderRadius: 16, display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 26, margin: '0 auto 16px' }}>📦</div>
          <div style={{ fontFamily: 'Syne', fontSize: 24, fontWeight: 800 }}>InvenAI</div>
          <div style={{ color: 'var(--text-secondary)', fontSize: 13, marginTop: 4 }}>Inventory Forecasting System</div>
        </div>

        {/* Card */}
        <div className="card">
          <div style={{ fontFamily: 'Syne', fontSize: 18, fontWeight: 700, marginBottom: 4 }}>Welcome back</div>
          <div style={{ color: 'var(--text-secondary)', fontSize: 13, marginBottom: 24 }}>Sign in to your account</div>

          {error && <div className="alert-banner alert-banner-error" style={{ marginBottom: 16 }}>✕ {error}</div>}

          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label className="form-label">Username</label>
              <input className="form-input" placeholder="Enter your username" value={form.username}
                onChange={e => setForm({ ...form, username: e.target.value })} autoFocus />
            </div>
            <div className="form-group">
              <label className="form-label">Password</label>
              <input className="form-input" type="password" placeholder="Enter your password" value={form.password}
                onChange={e => setForm({ ...form, password: e.target.value })} />
            </div>
            <button type="submit" className="btn btn-primary" style={{ width: '100%', justifyContent: 'center', padding: '10px', marginTop: 8 }} disabled={loading}>
              {loading ? 'Signing in...' : 'Sign In'}
            </button>
          </form>

          <div style={{ marginTop: 20, textAlign: 'center', fontSize: 13, color: 'var(--text-secondary)' }}>
            Don't have an account?{' '}
            <span style={{ color: 'var(--accent-blue)', cursor: 'pointer', fontWeight: 600 }} onClick={onSwitchToRegister}>
              Register here
            </span>
          </div>
        </div>

        {/* Default credentials hint */}
        <div style={{ marginTop: 16, background: 'var(--bg-card)', border: '1px solid var(--border)', borderRadius: 8, padding: '12px 16px' }}>
          <div style={{ fontSize: 12, color: 'var(--text-secondary)', marginBottom: 8, fontWeight: 600 }}>DEFAULT CREDENTIALS</div>
          <div style={{ fontSize: 12, display: 'flex', gap: 24 }}>
            <div>
              <div style={{ color: 'var(--accent-blue)', fontWeight: 600 }}>Admin</div>
              <div style={{ color: 'var(--text-secondary)' }}>admin / admin123</div>
            </div>
            <div>
              <div style={{ color: 'var(--accent-green)', fontWeight: 600 }}>Manager</div>
              <div style={{ color: 'var(--text-secondary)' }}>manager / manager123</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
