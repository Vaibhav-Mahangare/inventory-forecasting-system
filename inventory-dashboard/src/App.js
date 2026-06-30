import React, { useState } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import Sidebar from './components/Sidebar';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import Products from './pages/Products';
import Warehouses from './pages/Warehouses';
import Inventory from './pages/Inventory';
import Sales from './pages/Sales';
import Suppliers from './pages/Suppliers';
import PurchaseOrders from './pages/PurchaseOrders';
import Forecasts from './pages/Forecasts';
import MLForecast from './pages/MLForecast';
import Alerts from './pages/Alerts';
import Users from './pages/Users';

// Layout wrapper with sidebar and topbar
function Layout({ title, children, adminOnly = false }) {
  const { isAdmin, isAuthenticated } = useAuth();

  if (!isAuthenticated()) return <Navigate to="/login" />;
  if (adminOnly && !isAdmin()) return (
    <div style={{ minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center', background: 'var(--bg-primary)' }}>
      <div style={{ textAlign: 'center' }}>
        <div style={{ fontSize: 48, marginBottom: 16 }}>🔐</div>
        <div style={{ fontFamily: 'Syne', fontSize: 20, fontWeight: 700, marginBottom: 8 }}>Access Denied</div>
        <div style={{ color: 'var(--text-secondary)' }}>This page is restricted to Admins only.</div>
      </div>
    </div>
  );

  return (
    <div className="main-content">
      <header className="topbar">
        <div className="topbar-title">{title}</div>
        <div className="topbar-right">
          <span style={{ fontSize: 12, color: 'var(--text-secondary)' }}>🟢 localhost:8080</span>
          <span style={{ fontSize: 12, color: 'var(--text-secondary)', marginLeft: 12 }}>🟢 localhost:8000</span>
        </div>
      </header>
      <main className="page-content">{children}</main>
    </div>
  );
}

// Auth pages wrapper (login/register toggle)
function AuthPages() {
  const [page, setPage] = useState('login');
  return page === 'login'
    ? <Login onSwitchToRegister={() => setPage('register')} />
    : <Register onSwitchToLogin={() => setPage('login')} />;
}

// Protected app with sidebar
function AppRoutes() {
  const { isAuthenticated, loading } = useAuth();

  if (loading) return (
    <div style={{ minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center', background: 'var(--bg-primary)', color: 'var(--text-secondary)' }}>
      Loading...
    </div>
  );

  if (!isAuthenticated()) return <AuthPages />;

  const routes = [
    { path: '/', element: <Dashboard />, title: 'Dashboard' },
    { path: '/products', element: <Products />, title: 'Products' },
    { path: '/warehouses', element: <Warehouses />, title: 'Warehouses' },
    { path: '/inventory', element: <Inventory />, title: 'Inventory' },
    { path: '/sales', element: <Sales />, title: 'Sales' },
    { path: '/suppliers', element: <Suppliers />, title: 'Suppliers' },
    { path: '/purchase-orders', element: <PurchaseOrders />, title: 'Purchase Orders' },
    { path: '/forecasts', element: <Forecasts />, title: 'Forecasts' },
    { path: '/ml-forecast', element: <MLForecast />, title: 'AI Prediction' },
    { path: '/alerts', element: <Alerts />, title: 'Alerts' },
    { path: '/users', element: <Users />, title: 'User Management', adminOnly: true },
  ];

  return (
    <div className="app-layout">
      <Sidebar />
      <Routes>
        {routes.map(r => (
          <Route key={r.path} path={r.path}
            element={<Layout title={r.title} adminOnly={r.adminOnly || false}>{r.element}</Layout>} />
        ))}
        <Route path="*" element={<Navigate to="/" />} />
      </Routes>
    </div>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <AppRoutes />
      </BrowserRouter>
    </AuthProvider>
  );
}
