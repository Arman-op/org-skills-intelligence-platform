import React from 'react';

const departments = [
  { name: 'Engineering', gap: 22, color: '#ef4444', skills: ['Cloud', 'Security', 'DevOps'] },
  { name: 'Marketing',   gap: 35, color: '#f59e0b', skills: ['SEO', 'Analytics', 'AI Tools'] },
  { name: 'Data Science',gap: 45, color: '#ef4444', skills: ['ML', 'Python', 'Tableau'] },
  { name: 'HR & Ops',    gap: 12, color: '#10b981', skills: ['Policy', 'Data Literacy', 'Tools'] },
  { name: 'Product',     gap: 28, color: '#f59e0b', skills: ['UX', 'Agile', 'SQL'] },
];

const heatmapData = [
  { skill:'React',       eng:90, mkt:30, ds:20, hr:15, prod:60 },
  { skill:'Python',      eng:70, mkt:25, ds:80, hr:10, prod:35 },
  { skill:'AWS',         eng:55, mkt:15, ds:40, hr:8,  prod:20 },
  { skill:'SQL',         eng:80, mkt:50, ds:85, hr:60, prod:75 },
  { skill:'ML / AI',    eng:45, mkt:20, ds:70, hr:5,  prod:30 },
  { skill:'Agile',       eng:75, mkt:65, ds:55, hr:70, prod:90 },
  { skill:'DataViz',     eng:40, mkt:55, ds:80, hr:30, prod:50 },
];
const depts = ['Eng', 'Mkt', 'DS', 'HR', 'Prod'];
const deptKeys = ['eng', 'mkt', 'ds', 'hr', 'prod'];

const cellColor = (v) => {
  if (v >= 75) return { bg:'rgba(16,185,129,0.25)', color:'#10b981' };
  if (v >= 50) return { bg:'rgba(245,158,11,0.2)',  color:'#f59e0b' };
  return           { bg:'rgba(239,68,68,0.2)',       color:'#ef4444' };
};

const Analytics = () => {
  return (
    <div>
      <div className="page-header">
        <div>
          <div className="page-title">Gap Analytics</div>
          <div className="page-sub">Visualize skill deficiencies across departments and roles</div>
        </div>
        <button className="btn-primary" style={{ padding:'0.6rem 1.25rem', borderRadius:8 }}>
          Export Report
        </button>
      </div>

      {/* Stats Row */}
      <div className="stats-row" style={{ marginBottom:'1.75rem' }}>
        {[
          { label:'Avg. Gap Score', value:'28%', color:'#ef4444', icon:'📊' },
          { label:'Departments Assessed', value:'5',   color:'#6366f1', icon:'🏢' },
          { label:'High-Risk Skills',     value:'12',  color:'#f59e0b', icon:'⚠️' },
          { label:'Skills Improving',     value:'34',  color:'#10b981', icon:'📈' },
        ].map((s, i) => (
          <div className="stat-card" key={i}>
            <div className="stat-top">
              <div className="stat-icon" style={{ background:`${s.color}22`, color:s.color }}>
                <span style={{ fontSize:'1.35rem' }}>{s.icon}</span>
              </div>
            </div>
            <div className="stat-value" style={{ color:s.color }}>{s.value}</div>
            <div className="stat-title">{s.label}</div>
          </div>
        ))}
      </div>

      <div className="analytics-grid">
        {/* Skill Heatmap */}
        <div className="card" style={{ gridColumn:'1/-1' }}>
          <div className="card-header">
            <div className="card-title">
              <span>🔥</span> Skill Coverage Heatmap
            </div>
            <span style={{ fontSize:'0.78rem', color:'var(--text-muted)' }}>
              Green = Strong  |  Yellow = Moderate  |  Red = Critical Gap
            </span>
          </div>
          <div style={{ overflowX:'auto' }}>
            <table style={{ width:'100%', borderCollapse:'separate', borderSpacing:4 }}>
              <thead>
                <tr>
                  <th style={{ textAlign:'left', fontSize:'0.78rem', color:'var(--text-muted)', paddingBottom:'0.5rem', paddingRight:'1rem' }}>Skill</th>
                  {depts.map(d => (
                    <th key={d} style={{ textAlign:'center', fontSize:'0.78rem', color:'var(--text-secondary)', paddingBottom:'0.5rem', width:80 }}>{d}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {heatmapData.map((row, ri) => (
                  <tr key={ri}>
                    <td style={{ fontSize:'0.85rem', paddingRight:'1rem', paddingBottom:'0.4rem', color:'var(--text-secondary)', whiteSpace:'nowrap' }}>{row.skill}</td>
                    {deptKeys.map(dk => {
                      const v = row[dk];
                      const { bg, color } = cellColor(v);
                      return (
                        <td key={dk} style={{ paddingBottom:'0.4rem' }}>
                          <div style={{
                            background: bg,
                            color,
                            borderRadius:6,
                            display:'flex',
                            alignItems:'center',
                            justifyContent:'center',
                            height:38,
                            fontWeight:700,
                            fontSize:'0.78rem',
                          }}>
                            {v}%
                          </div>
                        </td>
                      );
                    })}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        {/* Department Gap Bars */}
        <div className="card">
          <div className="card-header">
            <div className="card-title"><span>🏢</span> Department Gap Overview</div>
          </div>
          {departments.map((d, i) => (
            <div className="department-row" key={i} style={{ marginBottom:'1.25rem' }}>
              <span className="dept-name" style={{ width:110 }}>{d.name}</span>
              <div className="dept-bar">
                <div style={{ display:'flex', justifyContent:'space-between', marginBottom:'4px' }}>
                  <span style={{ fontSize:'0.72rem', color:'var(--text-muted)' }}>{d.skills.join(', ')}</span>
                  <span style={{ fontSize:'0.75rem', fontWeight:700, color:d.color }}>Gap: {d.gap}%</span>
                </div>
                <div className="progress-bar" style={{ height:8 }}>
                  <div className="progress-fill" style={{ width:`${d.gap}%`, background:`linear-gradient(90deg, ${d.color}, ${d.color}88)` }} />
                </div>
              </div>
            </div>
          ))}
        </div>

        {/* Top Gaps Table */}
        <div className="card">
          <div className="card-header">
            <div className="card-title"><span>📋</span> Top 5 Critical Gaps</div>
            <button className="card-action">Full Report →</button>
          </div>
          <table style={{ width:'100%', borderCollapse:'collapse', fontSize:'0.875rem' }}>
            <thead>
              <tr style={{ borderBottom:'1px solid var(--glass-border)' }}>
                {['Skill', 'Dept', 'Gap', 'Priority'].map(h => (
                  <th key={h} style={{ textAlign:'left', padding:'0.5rem 0', color:'var(--text-muted)', fontSize:'0.75rem', fontWeight:600 }}>{h}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {[
                { skill:'Generative AI',  dept:'All',      gap:70, pri:'Critical' },
                { skill:'UX Research',    dept:'Product',  gap:60, pri:'Critical' },
                { skill:'Tableau/BI',     dept:'Marketing',gap:55, pri:'High'     },
                { skill:'ML Modeling',    dept:'Data Sci', gap:45, pri:'High'     },
                { skill:'AWS/Azure',      dept:'Eng',      gap:42, pri:'High'     },
              ].map((r, i) => (
                <tr key={i} style={{ borderBottom:'1px solid rgba(255,255,255,0.04)' }}>
                  <td style={{ padding:'0.75rem 0', fontWeight:600 }}>{r.skill}</td>
                  <td style={{ padding:'0.75rem 0', color:'var(--text-secondary)' }}>{r.dept}</td>
                  <td style={{ padding:'0.75rem 0', fontWeight:700, color: r.gap > 60 ? 'var(--danger)' : 'var(--warning)' }}>{r.gap}%</td>
                  <td style={{ padding:'0.75rem 0' }}>
                    <span className={`tag ${r.pri === 'Critical' ? 'urgent' : 'hot'}`}>{r.pri}</span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default Analytics;
