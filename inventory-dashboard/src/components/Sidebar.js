import React from 'react';
import { NavLink } from 'react-router-dom';
import { LayoutDashboard, Package, Warehouse, BarChart3, ShoppingCart, TrendingUp, Bell, Truck, Users, Brain, LogOut, ShieldCheck } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

export default function Sidebar() {
  const { user, logout, isAdmin } = useAuth();

  const navItems = [
    { section: 'Overview', items: [{ to: '/', icon: LayoutDashboard, label: 'Dashboard' }] },
    { section: 'Inventory', items: [
      { to: '/products', icon: Package, label: 'Products' },
      { to: '/warehouses', icon: Warehouse, label: 'Warehouses' },
      { to: '/inventory', icon: BarChart3, label: 'Inventory' },
    ]},
    { section: 'Operations', items: [
      { to: '/sales', icon: ShoppingCart, label: 'Sales' },
      { to: '/suppliers', icon: Users, label: 'Suppliers' },
      { to: '/purchase-orders', icon: Truck, label: 'Purchase Orders' },
    ]},
    { section: 'Intelligence', items: [
      { to: '/forecasts', icon: TrendingUp, label: 'Forecasts' },
      { to: '/ml-forecast', icon: Brain, label: 'AI Prediction' },
      { to: '/alerts', icon: Bell, label: 'Alerts' },
    ]},
    // Admin only section
    ...(isAdmin() ? [{ section: 'Administration', items: [
      { to: '/users', icon: ShieldCheck, label: 'User Management' },
    ]}] : []),
  ];

  return (
    <aside className="sidebar">
      <div className="sidebar-logo">
        <div className="sidebar-logo-icon">📦</div>
        <div>
          <div className="sidebar-logo-text">InvenAI</div>
          <div className="sidebar-logo-sub">Forecasting System</div>
        </div>
      </div>

      <nav className="sidebar-nav">
        {navItems.map(section => (
          <div key={section.section}>
            <div className="nav-section-label">{section.section}</div>
            {section.items.map(item => (
              <NavLink key={item.to} to={item.to} end={item.to === '/'}
                className={({ isActive }) => `nav-item${isActive ? ' active' : ''}`}>
                <item.icon size={15} />{item.label}
              </NavLink>
            ))}
          </div>
        ))}
      </nav>

      <div className="sidebar-footer">
        {/* User info */}
        <div style={{ padding: '10px', marginBottom: 8, background: 'var(--bg-card)', borderRadius: 8, border: '1px solid var(--border)' }}>
          <div style={{ fontSize: 12, fontWeight: 600, color: 'var(--text-primary)' }}>{user?.username}</div>
          <div style={{ fontSize: 11, color: 'var(--text-secondary)', marginTop: 2 }}>
            <span style={{ color: user?.role === 'ADMIN' ? 'var(--accent-purple)' : 'var(--accent-blue)' }}>
              ● {user?.role === 'ADMIN' ? 'Admin' : 'Inventory Manager'}
            </span>
          </div>
        </div>
        {/* Logout */}
        <button onClick={logout} className="nav-item" style={{ width: '100%', border: 'none', background: 'none', cursor: 'pointer', color: 'var(--accent-red)' }}>
          <LogOut size={15} />Logout
        </button>
      </div>
    </aside>
  );
}
