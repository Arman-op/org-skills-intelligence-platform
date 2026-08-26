import React, { useState } from 'react';

const mentors = [
  {
    emoji: '👩‍💻', bg: 'linear-gradient(135deg,#312e81,#4c1d95)',
    name: 'Sarah Donovan', title: 'Principal Cloud Architect',
    dept: 'Engineering', skills: ['AWS', 'Azure', 'Kubernetes', 'Terraform'],
    rating: 4.9, sessions: 48, available: true,
  },
  {
    emoji: '👨‍🔬', bg: 'linear-gradient(135deg,#064e3b,#065f46)',
    name: 'Dr. Rajan Mehta', title: 'Head of Data Science',
    dept: 'Data Science', skills: ['Python', 'ML', 'TensorFlow', 'Spark'],
    rating: 4.8, sessions: 62, available: true,
  },
  {
    emoji: '👩‍🎨', bg: 'linear-gradient(135deg,#7f1d1d,#991b1b)',
    name: 'Priya Sharma', title: 'Sr. UX Research Lead',
    dept: 'Product', skills: ['UX', 'Figma', 'User Research', 'A/B Testing'],
    rating: 4.7, sessions: 35, available: false,
  },
  {
    emoji: '👨‍💼', bg: 'linear-gradient(135deg,#1c1917,#44403c)',
    name: 'Michael Torres', title: 'VP of Product Strategy',
    dept: 'Product', skills: ['Product Vision', 'OKRs', 'Roadmapping', 'SQL'],
    rating: 4.9, sessions: 91, available: true,
  },
  {
    emoji: '👩‍🏫', bg: 'linear-gradient(135deg,#1e3a5f,#1d4ed8)',
    name: 'Aisha Williams', title: 'Learning & Development Lead',
    dept: 'HR & Ops', skills: ['L&D', 'Instructional Design', 'Coaching', 'LMS'],
    rating: 4.6, sessions: 27, available: true,
  },
  {
    emoji: '👨‍🔧', bg: 'linear-gradient(135deg,#1a1f2e,#374151)',
    name: 'Chris Park', title: 'DevOps & SRE Engineer',
    dept: 'Engineering', skills: ['Docker', 'CI/CD', 'Prometheus', 'Golang'],
    rating: 4.8, sessions: 53, available: false,
  },
];

const Mentorship = () => {
  const [filter, setFilter] = useState('All');
  const depts = ['All', 'Engineering', 'Data Science', 'Product', 'HR & Ops'];
  const filtered = filter === 'All' ? mentors : mentors.filter(m => m.dept === filter);

  return (
    <div>
      <div className="page-header">
        <div>
          <div className="page-title">Mentorship Network</div>
          <div className="page-sub">Connect with expert mentors to accelerate skill development</div>
        </div>
        <button className="btn-primary" style={{ padding:'0.6rem 1.25rem', borderRadius:8 }}>
          Become a Mentor
        </button>
      </div>

      {/* Stats */}
      <div style={{ display:'flex', gap:'1rem', marginBottom:'1.75rem' }}>
        {[
          { label:'Active Mentors',    value: mentors.length, icon:'🧑‍🏫' },
          { label:'Mentee Connections',value:'234',            icon:'🔗' },
          { label:'Sessions This Month',value:'89',           icon:'📅' },
          { label:'Avg. Rating',       value:'4.8 ⭐',        icon:'🏆' },
        ].map((s,i) => (
          <div key={i} className="card" style={{ flex:1, textAlign:'center', padding:'1.25rem' }}>
            <div style={{ fontSize:'1.75rem', marginBottom:'0.4rem' }}>{s.icon}</div>
            <div className="stat-value" style={{ fontSize:'1.5rem' }}>{s.value}</div>
            <div className="stat-title" style={{ marginTop:4 }}>{s.label}</div>
          </div>
        ))}
      </div>

      <div className="filters-row">
        {depts.map(d => (
          <button key={d} className={`filter-chip ${filter===d?'active':''}`} onClick={() => setFilter(d)}>{d}</button>
        ))}
      </div>

      <div className="mentor-grid">
        {filtered.map((m, i) => (
          <div className="mentor-card" key={i} style={{ animationDelay:`${i*0.07}s` }}>
            <div className="mentor-avatar" style={{ background: m.bg, border:'none' }}>
              <span style={{ fontSize:'2rem' }}>{m.emoji}</span>
            </div>
            <div style={{ display:'flex', alignItems:'center', justifyContent:'center', gap:'0.5rem', marginBottom:'0.25rem' }}>
              <div className="mentor-name">{m.name}</div>
              {m.available
                ? <span style={{ width:8, height:8, borderRadius:'50%', background:'var(--success)', display:'inline-block' }} title="Available" />
                : <span style={{ width:8, height:8, borderRadius:'50%', background:'var(--text-muted)', display:'inline-block' }} title="Unavailable" />
              }
            </div>
            <div className="mentor-title">{m.title}</div>
            <div style={{ fontSize:'0.72rem', color:'var(--accent)', marginTop:'4px' }}>{m.dept}</div>

            <div className="mentor-skills">
              {m.skills.map(s => (
                <span key={s} className="skill-pill">{s}</span>
              ))}
            </div>

            <div className="mentor-rating">
              {'⭐'.repeat(Math.round(m.rating))} {m.rating} · {m.sessions} sessions
            </div>

            <button
              className="btn-connect"
              disabled={!m.available}
              style={!m.available ? { opacity:0.5, cursor:'not-allowed' } : {}}
            >
              {m.available ? '🔗 Request Session' : '⏳ Currently Unavailable'}
            </button>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Mentorship;
