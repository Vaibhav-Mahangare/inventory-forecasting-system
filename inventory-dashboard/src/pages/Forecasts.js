import React, { useEffect, useState } from 'react';
import { TrendingUp } from 'lucide-react';
import { getProducts, getForecastsByProduct } from '../services/api';
export default function Forecasts() {
  const [products, setProducts] = useState([]); const [selected, setSelected] = useState(''); const [forecasts, setForecasts] = useState([]); const [loading, setLoading] = useState(false); const [error, setError] = useState('');
  useEffect(() => { getProducts().then(r => setProducts(r.data)).catch(()=>{}); }, []);
  const loadForecasts = async (pid) => { if (!pid) return; setLoading(true); setError(''); try { const r = await getForecastsByProduct(pid); setForecasts(r.data); } catch { setError('No forecasts found. Run AI Prediction first.'); setForecasts([]); } finally { setLoading(false); } };
  return (
    <div>
      <div className="page-header"><div><div className="page-title">Forecasts</div><div className="page-desc">View saved demand forecasts from the AI model</div></div></div>
      {error && <div className="alert-banner alert-banner-error">✕ {error}</div>}
      <div className="card" style={{marginBottom:20}}>
        <div className="form-group" style={{marginBottom:0}}>
          <label className="form-label">Select Product</label>
          <select className="form-select" value={selected} onChange={e=>{setSelected(e.target.value);loadForecasts(e.target.value);}}>
            <option value="">Choose a product...</option>
            {products.map(p=><option key={p.productId} value={p.productId}>{p.name} — {p.category}</option>)}
          </select>
        </div>
      </div>
      <div className="card">
        {loading ? <div className="loading"><TrendingUp size={20}/>Loading forecasts...</div>
          : forecasts.length === 0 ? <div className="empty-state"><TrendingUp size={28}/><p>{selected ? 'No forecasts found. Run AI Prediction first.' : 'Select a product to view forecasts'}</p></div>
          : <div className="table-wrapper"><table>
            <thead><tr><th>#</th><th>Forecast Date</th><th>Predicted Qty</th><th>Model Version</th></tr></thead>
            <tbody>{forecasts.map((f,i)=><tr key={f.forecastId}><td style={{color:'var(--text-muted)'}}>{i+1}</td><td>{f.forecastDate}</td><td><span className="badge badge-purple" style={{fontSize:13}}>{f.predictedQuantity} units</span></td><td><span className="badge badge-grey">{f.modelVersion}</span></td></tr>)}</tbody>
          </table></div>}
      </div>
    </div>
  );
}
