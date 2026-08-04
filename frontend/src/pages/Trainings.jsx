import React, { useState } from 'react';

const courses = [
  {
    emoji: '☁️', bg: 'linear-gradient(135deg,#1e1b4b,#312e81)',
    title: 'Advanced Cloud Architectures (AWS + Azure)',
    provider: 'Internal Engineering', duration: '12h', level: 'Advanced',
    tags: ['urgent','hot'], tagLabels: ['High Priority','Trending'],
    enrolled: 142, rating: 4.8, matchScore: 98,
  },
  {
    emoji: '🤖', bg: 'linear-gradient(135deg,#1a1033,#2d1b69)',
    title: 'Generative AI for Business Professionals',
    provider: 'Coursera Enterprise', duration: '4h', level: 'Beginner',
    tags: ['hot','new'], tagLabels: ['Trending','New'],
    enrolled: 389, rating: 4.9, matchScore: 92,
  },
  {
    emoji: '📊', bg: 'linear-gradient(135deg,#0f1f0f,#064e3b)',
    title: 'Advanced Data Visualization with Tableau',
    provider: 'Udemy Business', duration: '8h', level: 'Mid',
    tags: ['new'], tagLabels: ['New'],
    enrolled: 67, rating: 4.5, matchScore: 85,
  },
  {
    emoji: '🐍', bg: 'linear-gradient(135deg,#1a2c0f,#14532d)',
    title: 'Predictive Analytics with Python & Scikit-learn',
    provider: 'LinkedIn Learning', duration: '10h', level: 'Advanced',
    tags: ['urgent'], tagLabels: ['Critical Gap'],
    enrolled: 44, rating: 4.7, matchScore: 88,
  },
  {
    emoji: '🔐', bg: 'linear-gradient(135deg,#1f0f0f,#7f1d1d)',
    title: 'Cybersecurity & Compliance Fundamentals 2026',
    provider: 'Internal HR', duration: '6h', level: 'Beginner',
    tags: ['urgent'], tagLabels: ['Mandatory'],
    enrolled: 450, rating: 4.3, matchScore: 76,
  },
  {
    emoji: '🎨', bg: 'linear-gradient(135deg,#1a0f2e,#4c1d95)',
    title: 'UX Research Methods & Usability Testing',
    provider: 'Google via Coursera', duration: '15h', level: 'Mid',
    tags: ['new'], tagLabels: ['New'],
    enrolled: 23, rating: 4.6, matchScore: 79,
  },
];

const Trainings = () => {
  const [filter, setFilter] = useState('All');

  const levels = ['All', 'Beginner', 'Mid', 'Advanced'];

  const filtered = filter === 'All' ? courses : courses.filter(c => c.level === filter);

  return (
    <div>
      <div className="page-header">
        <div>
          <div className="page-title">Learning & Trainings</div>
          <div className="page-sub">AI-matched courses based on your organization's skill gaps</div>
        </div>
        <button className="btn-primary" style={{ padding:'0.6rem 1.25rem', borderRadius:8 }}>
          + Recommend Course
        </button>
      </div>

      {/* Stats */}
      <div style={{ display:'flex', gap:'1rem', marginBottom:'1.75rem' }}>
        {[
          { label:'Available Courses', value: courses.length, icon:'📚' },
          { label:'Active Learners',   value:'1,894',          icon:'🎓' },
          { label:'Avg. Completion',   value:'74%',            icon:'✅' },
          { label:'Hours Logged',      value:'12.4k',          icon:'⏱' },
        ].map((s,i) => (
          <div key={i} className="card" style={{ flex:1, textAlign:'center', padding:'1.25rem' }}>
            <div style={{ fontSize:'1.75rem', marginBottom:'0.4rem' }}>{s.icon}</div>
            <div className="stat-value" style={{ fontSize:'1.5rem' }}>{s.value}</div>
            <div className="stat-title" style={{ marginTop:4 }}>{s.label}</div>
          </div>
        ))}
      </div>

      <div className="filters-row">
        {levels.map(l => (
          <button key={l} className={`filter-chip ${filter===l?'active':''}`} onClick={() => setFilter(l)}>{l}</button>
        ))}
      </div>

      <div className="trainings-grid">
        {filtered.map((c, i) => (
          <div className="training-card" key={i} style={{ animationDelay:`${i*0.06}s` }}>
            <div className="training-thumb" style={{ background: c.bg }}>
              <span style={{ fontSize:'3rem' }}>{c.emoji}</span>
              <div style={{
                position:'absolute', top:10, right:10,
                background:'rgba(0,0,0,0.4)', backdropFilter:'blur(8px)',
                borderRadius:50, padding:'3px 10px',
                fontSize:'0.72rem', fontWeight:700, color:'#a5b4fc',
                border:'1px solid rgba(99,102,241,0.3)',
              }}>
                {c.matchScore}% match
              </div>
            </div>
            <div className="training-body">
              <div className="training-tags">
                {c.tags.map((t, ti) => (
                  <span key={ti} className={`tag ${t}`}>{c.tagLabels[ti]}</span>
                ))}
              </div>
              <div className="training-title">{c.title}</div>
              <div className="training-meta">
                <span>⏱ {c.duration}</span>
                <span>📶 {c.level}</span>
                <span>⭐ {c.rating}</span>
              </div>
              <div style={{ fontSize:'0.78rem', color:'var(--text-muted)', marginBottom:'0.75rem' }}>
                by {c.provider}
              </div>
              <div className="training-footer">
                <span style={{ fontSize:'0.78rem', color:'var(--text-secondary)' }}>
                  👥 {c.enrolled.toLocaleString()} enrolled
                </span>
                <button className="btn-enroll">Enroll Now →</button>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Trainings;
