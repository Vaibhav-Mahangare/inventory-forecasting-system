import React, { useEffect, useState } from 'react';
import { Brain, RefreshCw, Zap } from 'lucide-react';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import { getProducts, getMLForecast, getMLForecastRetrain, getMLForecastBatch } from '../services/api';
const accClass = { Excellent:'accuracy-excellent', Good:'accuracy-good', Fair:'accuracy-fair', Poor:'accuracy-poor' };
export default function MLForecast() {
  const [products, setProducts] = useState([]); const [selected, setSelected] = useState(''); const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false); const [batchLoading, setBatchLoading] = useState(false); const [batchResults, setBatchResults] = useState([]);
  const [error, setError] = useState(''); const [tab, setTab] = useState('single');
  useEffect(() => { getProducts().then(r=>setProducts(r.data)).catch(()=>{}); }, []);
  const runForecast = async (retrain=false) => {
    if (!selected) { setError('Please select a product first'); return; }
    setLoading(true); setError(''); setResult(null);
    try { const r = retrain ? await getMLForecastRetrain(selected) : await getMLForecast(selected); setResult(r.data); }
    catch (e) { setError(e.response?.data?.message || 'Forecast failed. Make sure Python API is running on port 8000 and product has sales data.'); }
    finally { setLoading(false); }
  };
  const runBatch = async () => {
    setBatchLoading(true); setError('');
    try { const r = await getMLForecastBatch(); setBatchResults(r.data); }
    catch (e) { setError(e.response?.data?.message || 'Batch forecast failed.'); }
    finally { setBatchLoading(false); }
  };
  const chartData = result ? [
    { name: '7 Days', predicted: result.predicted7Days },
    { name: '15 Days', predicted: result.predicted15Days },
    { name: '30 Days', predicted: result.predicted30Days },
  ] : [];
  return (
    <div>
      <div className="page-header">
        <div><div className="page-title">AI Demand Prediction</div><div className="page-desc">Facebook Prophet ML model for demand forecasting</div></div>
        <div style={{display:'flex',gap:8}}>
          <button className={`btn ${tab==='single'?'btn-primary':'btn-secondary'}`} onClick={()=>setTab('single')}>Single Product</button>
          <button className={`btn ${tab==='batch'?'btn-primary':'btn-secondary'}`} onClick={()=>setTab('batch')}>Batch All</button>
        </div>
      </div>
      {error && <div className="alert-banner alert-banner-error">✕ {error}</div>}
      {tab==='single' && (
        <div>
          <div className="card" style={{marginBottom:20}}>
            <div className="card-title" style={{marginBottom:16}}>Run Prediction</div>
            <div style={{display:'flex',gap:12,alignItems:'flex-end'}}>
              <div style={{flex:1}}>
                <label className="form-label">Select Product</label>
                <select className="form-select" value={selected} onChange={e=>{setSelected(e.target.value);setResult(null);}}>
                  <option value="">Choose a product...</option>
                  {products.map(p=><option key={p.productId} value={p.productId}>{p.name} — {p.category}</option>)}
                </select>
              </div>
              <button className="btn btn-primary" onClick={()=>runForecast(false)} disabled={loading}><Brain size={15}/>{loading?'Running...':'Predict Demand'}</button>
              <button className="btn btn-secondary" onClick={()=>runForecast(true)} disabled={loading} title="Force retrain"><RefreshCw size={15}/>{loading?'...':'Retrain'}</button>
            </div>
          </div>
          {loading && <div className="card" style={{textAlign:'center',padding:48}}><Brain size={32} color="var(--accent-blue)" style={{margin:'0 auto 12px',display:'block'}}/><div style={{fontFamily:'Syne',fontSize:16,fontWeight:700,marginBottom:8}}>Prophet Model Running...</div><div style={{color:'var(--text-secondary)',fontSize:13}}>Analyzing sales patterns and generating forecast</div></div>}
          {result && !loading && (
            <div>
              <div className="card" style={{marginBottom:16,background:'var(--accent-blue-dim)',borderColor:'var(--accent-blue)'}}>
                <div style={{display:'flex',justifyContent:'space-between',flexWrap:'wrap',gap:16,alignItems:'center'}}>
                  <div style={{display:'flex',alignItems:'center',gap:12}}>
                    <div style={{width:44,height:44,borderRadius:12,background:'var(--accent-blue)',display:'flex',alignItems:'center',justifyContent:'center'}}><Brain size={22} color="white"/></div>
                    <div><div style={{fontFamily:'Syne',fontWeight:700,fontSize:15}}>Prophet Model Results</div><div style={{fontSize:12,color:'var(--text-secondary)'}}>Status: <span style={{color:'var(--accent-green)',fontWeight:600}}>{result.modelStatus?.replace(/_/g,' ')}</span></div></div>
                  </div>
                  <div style={{display:'flex',gap:24,flexWrap:'wrap'}}>
                    {[['MAE',result.mae?.toFixed(2)],['RMSE',result.rmse?.toFixed(2)],['MAPE',result.mape?.toFixed(2)+'%']].map(([k,v])=><div key={k} style={{textAlign:'center'}}><div style={{fontSize:11,color:'var(--text-secondary)'}}>{k}</div><div style={{fontFamily:'Syne',fontWeight:700,fontSize:16}}>{v}</div></div>)}
                    <div style={{textAlign:'center'}}><div style={{fontSize:11,color:'var(--text-secondary)'}}>Accuracy</div><div className={accClass[result.accuracyLabel]} style={{fontFamily:'Syne',fontSize:16}}>{result.accuracyLabel}</div></div>
                  </div>
                </div>
              </div>
              <div className="grid-3" style={{marginBottom:20}}>
                {[['Next 7 Days',result.predicted7Days,'var(--accent-blue)','📅'],['Next 15 Days',result.predicted15Days,'var(--accent-green)','📆'],['Next 30 Days',result.predicted30Days,'var(--accent-purple)','🗓️']].map(([label,val,color,icon])=>(
                  <div key={label} className="card" style={{textAlign:'center'}}>
                    <div style={{fontSize:24,marginBottom:8}}>{icon}</div>
                    <div style={{fontFamily:'Syne',fontSize:32,fontWeight:800,color}}>{val}</div>
                    <div style={{fontSize:12,color:'var(--text-secondary)',marginTop:4}}>units — {label}</div>
                  </div>
                ))}
              </div>
              <div className="card">
                <div className="card-header"><div className="card-title">Predicted Demand Comparison</div><span className="badge badge-blue">Prophet v2.0</span></div>
                <ResponsiveContainer width="100%" height={240}>
                  <BarChart data={chartData} barSize={60}>
                    <CartesianGrid strokeDasharray="3 3" stroke="var(--border)"/>
                    <XAxis dataKey="name" tick={{fill:'var(--text-secondary)',fontSize:13}}/>
                    <YAxis tick={{fill:'var(--text-secondary)',fontSize:12}}/>
                    <Tooltip formatter={val=>[`${val} units`,'Predicted']} contentStyle={{background:'var(--bg-card)',border:'1px solid var(--border)',borderRadius:8}}/>
                    <Bar dataKey="predicted" name="Predicted Units" fill="var(--accent-blue)" radius={[6,6,0,0]}/>
                  </BarChart>
                </ResponsiveContainer>
              </div>
            </div>
          )}
        </div>
      )}
      {tab==='batch' && (
        <div>
          <div className="card" style={{marginBottom:20,textAlign:'center',padding:32}}>
            <Zap size={32} color="var(--accent-orange)" style={{margin:'0 auto 12px',display:'block'}}/>
            <div style={{fontFamily:'Syne',fontWeight:700,fontSize:16,marginBottom:8}}>Batch Forecast All Products</div>
            <div style={{color:'var(--text-secondary)',fontSize:13,marginBottom:20}}>Run Prophet model for all products with sales data simultaneously</div>
            <button className="btn btn-primary" onClick={runBatch} disabled={batchLoading}><Zap size={15}/>{batchLoading?'Processing all products...':'Run Batch Forecast'}</button>
          </div>
          {batchResults.length > 0 && (
            <div className="card">
              <div className="card-header"><div className="card-title">Batch Results</div><span className="badge badge-green">{batchResults.length} products forecasted</span></div>
              <div className="table-wrapper"><table>
                <thead><tr><th>Product ID</th><th>7 Days</th><th>15 Days</th><th>30 Days</th><th>MAPE</th><th>Accuracy</th><th>Model</th></tr></thead>
                <tbody>{batchResults.map(r=><tr key={r.productId}><td style={{fontWeight:600}}>Product #{r.productId}</td><td><span className="badge badge-blue">{r.predicted7Days}</span></td><td><span className="badge badge-green">{r.predicted15Days}</span></td><td><span className="badge badge-purple">{r.predicted30Days}</span></td><td>{r.mape?.toFixed(2)}%</td><td><span className={accClass[r.accuracyLabel]}>{r.accuracyLabel}</span></td><td style={{color:'var(--text-secondary)',fontSize:11}}>{r.modelStatus}</td></tr>)}</tbody>
              </table></div>
            </div>
          )}
        </div>
      )}
    </div>
  );
}
