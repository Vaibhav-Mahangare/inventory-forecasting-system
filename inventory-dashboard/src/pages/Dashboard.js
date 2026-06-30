import React, { useEffect, useState } from 'react';
import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, BarChart, Bar } from 'recharts';
import { Package, ShoppingCart, Bell, Truck, TrendingUp, AlertTriangle } from 'lucide-react';
import { getProducts, getSales, getAlerts, getPurchaseOrders, getLowStockInventories, getInventories } from '../services/api';

const CustomTooltip = ({ active, payload, label }) => {
  if (active && payload && payload.length) {
    return (
      <div style={{ background: 'var(--bg-card)', border: '1px solid var(--border)', borderRadius: 8, padding: '10px 14px' }}>
        <p style={{ color: 'var(--text-secondary)', fontSize: 12, marginBottom: 4 }}>{label}</p>
        {payload.map((p, i) => <p key={i} style={{ color: p.color, fontSize: 13, fontWeight: 600 }}>{p.name}: {p.value}</p>)}
      </div>
    );
  }
  return null;
};

export default function Dashboard() {
  const [stats, setStats] = useState({ products: 0, sales: 0, alerts: 0, orders: 0 });
  const [lowStock, setLowStock] = useState([]);
  const [recentAlerts, setRecentAlerts] = useState([]);
  const [salesData, setSalesData] = useState([]);
  const [inventoryData, setInventoryData] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchAll = async () => {
      try {
        const [products, sales, alerts, orders, lowStockRes, inventories] = await Promise.allSettled([
          getProducts(), getSales(), getAlerts(), getPurchaseOrders(), getLowStockInventories(), getInventories()
        ]);
        const p = products.status === 'fulfilled' ? products.value.data : [];
        const s = sales.status === 'fulfilled' ? sales.value.data : [];
        const a = alerts.status === 'fulfilled' ? alerts.value.data : [];
        const o = orders.status === 'fulfilled' ? orders.value.data : [];
        const ls = lowStockRes.status === 'fulfilled' ? lowStockRes.value.data : [];
        const inv = inventories.status === 'fulfilled' ? inventories.value.data : [];
        setStats({ products: p.length, sales: s.length, alerts: a.length, orders: o.length });
        setLowStock(ls.slice(0, 5));
        setRecentAlerts(a.slice(0, 5));
        const salesByDate = {};
        s.forEach(sale => { const d = sale.saleDate; salesByDate[d] = (salesByDate[d] || 0) + (sale.quantitySold || 0); });
        setSalesData(Object.entries(salesByDate).sort((a, b) => new Date(a[0]) - new Date(b[0])).slice(-10).map(([date, qty]) => ({ date: date.slice(5), qty })));
        setInventoryData(inv.slice(0, 6).map(i => ({ name: (i.productName || 'Product').slice(0, 10), stock: i.quantity, reorder: i.reorderPoint })));
      } catch (e) { console.error(e); }
      finally { setLoading(false); }
    };
    fetchAll();
  }, []);

  const statCards = [
    { label: 'Total Products', value: stats.products, icon: Package, color: 'var(--accent-blue)', bg: 'var(--accent-blue-dim)', accent: 'var(--accent-blue)' },
    { label: 'Total Sales', value: stats.sales, icon: ShoppingCart, color: 'var(--accent-green)', bg: 'var(--accent-green-dim)', accent: 'var(--accent-green)' },
    { label: 'Active Alerts', value: stats.alerts, icon: Bell, color: 'var(--accent-red)', bg: 'var(--accent-red-dim)', accent: 'var(--accent-red)' },
    { label: 'Purchase Orders', value: stats.orders, icon: Truck, color: 'var(--accent-orange)', bg: 'var(--accent-orange-dim)', accent: 'var(--accent-orange)' },
  ];

  if (loading) return <div className="loading"><TrendingUp size={20} />Loading dashboard...</div>;

  return (
    <div>
      <div className="page-header">
        <div>
          <div className="page-title">Dashboard</div>
          <div className="page-desc">Welcome to your Inventory Forecasting System</div>
        </div>
        <div style={{ fontSize: 12, color: 'var(--text-secondary)' }}>{new Date().toLocaleDateString('en-IN', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })}</div>
      </div>
      <div className="stat-grid">
        {statCards.map(card => (
          <div key={card.label} className="stat-card">
            <div className="stat-card-accent" style={{ background: card.accent }} />
            <div className="stat-card-icon" style={{ background: card.bg }}><card.icon size={18} color={card.color} /></div>
            <div className="stat-card-value" style={{ color: card.color }}>{card.value}</div>
            <div className="stat-card-label">{card.label}</div>
          </div>
        ))}
      </div>
      <div className="grid-2" style={{ marginBottom: 20 }}>
        <div className="card">
          <div className="card-header"><div><div className="card-title">Sales Activity</div><div className="card-subtitle">Units sold over time</div></div></div>
          {salesData.length > 0 ? (
            <ResponsiveContainer width="100%" height={200}>
              <AreaChart data={salesData}>
                <defs><linearGradient id="sg" x1="0" y1="0" x2="0" y2="1"><stop offset="5%" stopColor="#388bfd" stopOpacity={0.3}/><stop offset="95%" stopColor="#388bfd" stopOpacity={0}/></linearGradient></defs>
                <CartesianGrid strokeDasharray="3 3" stroke="var(--border)" />
                <XAxis dataKey="date" tick={{ fill: 'var(--text-secondary)', fontSize: 11 }} />
                <YAxis tick={{ fill: 'var(--text-secondary)', fontSize: 11 }} />
                <Tooltip content={<CustomTooltip />} />
                <Area type="monotone" dataKey="qty" name="Units Sold" stroke="#388bfd" fill="url(#sg)" strokeWidth={2} />
              </AreaChart>
            </ResponsiveContainer>
          ) : <div className="empty-state"><p>No sales data yet</p></div>}
        </div>
        <div className="card">
          <div className="card-header"><div><div className="card-title">Inventory Levels</div><div className="card-subtitle">Stock vs reorder point</div></div></div>
          {inventoryData.length > 0 ? (
            <ResponsiveContainer width="100%" height={200}>
              <BarChart data={inventoryData}>
                <CartesianGrid strokeDasharray="3 3" stroke="var(--border)" />
                <XAxis dataKey="name" tick={{ fill: 'var(--text-secondary)', fontSize: 11 }} />
                <YAxis tick={{ fill: 'var(--text-secondary)', fontSize: 11 }} />
                <Tooltip content={<CustomTooltip />} />
                <Bar dataKey="stock" name="Stock" fill="#388bfd" radius={[4,4,0,0]} />
                <Bar dataKey="reorder" name="Reorder Point" fill="#f85149" radius={[4,4,0,0]} />
              </BarChart>
            </ResponsiveContainer>
          ) : <div className="empty-state"><p>No inventory data yet</p></div>}
        </div>
      </div>
      <div className="grid-2">
        <div className="card">
          <div className="card-header"><div className="card-title">⚠️ Low Stock Items</div><span className="badge badge-red">{lowStock.length} items</span></div>
          {lowStock.length > 0 ? (
            <div className="table-wrapper">
              <table>
                <thead><tr><th>Product</th><th>Warehouse</th><th>Qty</th><th>Reorder At</th></tr></thead>
                <tbody>
                  {lowStock.map(item => (
                    <tr key={item.inventoryId}>
                      <td style={{ fontWeight: 500 }}>{item.productName}</td>
                      <td style={{ color: 'var(--text-secondary)' }}>{item.warehouseName}</td>
                      <td><span className="badge badge-red">{item.quantity}</span></td>
                      <td style={{ color: 'var(--text-secondary)' }}>{item.reorderPoint}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          ) : <div className="empty-state"><AlertTriangle size={28} /><p>All stock levels healthy!</p></div>}
        </div>
        <div className="card">
          <div className="card-header"><div className="card-title">🔔 Recent Alerts</div><span className="badge badge-orange">{recentAlerts.length}</span></div>
          {recentAlerts.length > 0 ? (
            <div>
              {recentAlerts.map(alert => (
                <div key={alert.alertId} style={{ display: 'flex', alignItems: 'flex-start', gap: 10, padding: '10px 0', borderBottom: '1px solid var(--border-light)' }}>
                  <div style={{ width: 8, height: 8, borderRadius: '50%', background: alert.alertType === 'LOW_STOCK' ? 'var(--accent-red)' : 'var(--accent-orange)', marginTop: 5, flexShrink: 0 }} />
                  <div style={{ flex: 1 }}>
                    <div style={{ fontSize: 13, fontWeight: 500 }}>{alert.message}</div>
                    <div style={{ fontSize: 11, color: 'var(--text-secondary)', marginTop: 2 }}>{alert.alertType} · {alert.productName}</div>
                  </div>
                </div>
              ))}
            </div>
          ) : <div className="empty-state"><Bell size={28} /><p>No alerts at the moment</p></div>}
        </div>
      </div>
    </div>
  );
}
