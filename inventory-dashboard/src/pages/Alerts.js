import React, { useEffect, useState } from 'react';
import { Bell } from 'lucide-react';
import { getAlerts, deleteAlert } from '../services/api';
const typeColor = { LOW_STOCK:'badge-red', OVERSTOCK:'badge-orange', EXPIRY_WARNING:'badge-orange', DELAYED_DELIVERY:'badge-purple' };
export default function Alerts() {
  const [alerts, setAlerts] = useState([]); const [loading, setLoading] = useState(true); const [error, setError] = useState('');
  const load = async () => { try { const r = await getAlerts(); setAlerts(r.data); } catch { setError('Failed to load alerts'); } finally { setLoading(false); } };
  useEffect(() => { load(); }, []);
  const handleDelete = async (id) => { if(!window.confirm('Dismiss alert?')) return; try { await deleteAlert(id); load(); } catch { setError('Failed'); } };
  return (
    <div>
      <div className="page-header"><div><div className="page-title">Alerts</div><div className="page-desc">System generated alerts and notifications</div></div>{alerts.length>0&&<span className="badge badge-red" style={{padding:'8px 12px'}}>{alerts.length} active</span>}</div>
      {error && <div className="alert-banner alert-banner-error">✕ {error}</div>}
      <div className="card">
        {loading ? <div className="loading"><Bell size={20}/>Loading...</div> : (
          <div className="table-wrapper"><table>
            <thead><tr><th>#</th><th>Type</th><th>Product</th><th>Warehouse</th><th>Message</th><th>Created</th><th>Action</th></tr></thead>
            <tbody>{alerts.length===0
              ? <tr><td colSpan={7}><div className="empty-state"><Bell size={28}/><p>No alerts — everything looks healthy!</p></div></td></tr>
              : [...alerts].reverse().map((a,i)=><tr key={a.alertId}><td style={{color:'var(--text-muted)'}}>{i+1}</td><td><span className={`badge ${typeColor[a.alertType]||'badge-grey'}`}>{a.alertType}</span></td><td style={{fontWeight:500}}>{a.productName}</td><td style={{color:'var(--text-secondary)'}}>{a.warehouseName}</td><td style={{maxWidth:260,fontSize:12}}>{a.message}</td><td style={{color:'var(--text-secondary)',fontSize:12}}>{a.createdAt?new Date(a.createdAt).toLocaleString():'—'}</td><td><button className="btn btn-sm btn-secondary" onClick={()=>handleDelete(a.alertId)}>Dismiss</button></td></tr>)}
            </tbody>
          </table></div>
        )}
      </div>
    </div>
  );
}
