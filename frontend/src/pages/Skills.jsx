import React, { useState } from 'react';

const allSkills = [
  { name: 'React.js', category: 'Frontend', level: 'Expert',   employees: 42, gap: 12, trending: true  },
  { name: 'Python',   category: 'Backend',  level: 'Advanced', employees: 78, gap: 28, trending: true  },
  { name: 'AWS Cloud',category: 'DevOps',   level: 'Mid',      employees: 31, gap: 42, trending: false },
  { name: 'Java Spring Boot', category: 'Backend', level: 'Advanced', employees: 55, gap: 18, trending: false },
  { name: 'Machine Learning', category: 'AI/ML', level: 'Mid', employees: 22, gap: 34, trending: true  },
  { name: 'Docker & Kubernetes', category: 'DevOps', level: 'Mid', employees: 28, gap: 38, trending: false },
  { name: 'Tableau / Power BI',  category: 'Analytics', level: 'Beginner', employees: 15, gap: 55, trending: true },
  { name: 'Node.js',  category: 'Backend',  level: 'Expert',   employees: 36, gap: 8,  trending: false },
  { name: 'UX Research', category: 'Design', level: 'Beginner', employees: 12, gap: 60, trending: false },
  { name: 'TypeScript', category: 'Frontend', level: 'Advanced', employees: 48, gap: 15, trending: true },
  { name: 'SQL & PostgreSQL', category: 'Data', level: 'Advanced', employees: 64, gap: 10, trending: false },
  { name: 'Generative AI', category: 'AI/ML', level: 'Beginner', employees: 8,  gap: 70, trending: true  },
];

const filters = ['All', 'Frontend', 'Backend', 'DevOps', 'AI/ML', 'Analytics', 'Data', 'Design'];

const levelColors = {
  Expert:   'expert',
  Advanced: 'advanced',
  Mid:      'mid',
  Beginner: 'beginner',
};

const Skills = () => {
  const [active, setActive] = useState('All');
  const [search, setSearch] = useState('');

  const filtered = allSkills.filter(s => {
    const matchCat = active === 'All' || s.category === active;
    const matchSearch = s.name.toLowerCase().includes(search.toLowerCase());
    return matchCat && matchSearch;
  });

  return (
    <div>
      <div className="page-header">
        <div>
          <div className="page-title">Skill Inventory</div>
          <div className="page-sub">Browse and manage all tracked competencies across your organization</div>
        </div>
        <button className="btn-primary" style={{ padding: '0.6rem 1.25rem', borderRadius: 8 }}>
          + Add Skill
        </button>
      </div>

      <div className="filters-row">
        {filters.map(f => (
          <button
            key={f}
            className={`filter-chip ${active === f ? 'active' : ''}`}
            onClick={() => setActive(f)}
          >
            {f}
          </button>
        ))}
        <div className="header-search" style={{ marginLeft: 'auto' }}>
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none">
            <circle cx="11" cy="11" r="8" stroke="currentColor" strokeWidth="2"/>
            <path d="m21 21-4.35-4.35" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
          </svg>
          <input
            type="text"
            placeholder="Search skills..."
            value={search}
            onChange={e => setSearch(e.target.value)}
            style={{ width: 160 }}
          />
        </div>
      </div>

      <div className="skills-grid">
        {filtered.map((skill, i) => (
          <div className="skill-card" key={i} style={{ animationDelay: `${i * 0.05}s` }}>
            <div className="skill-card-top">
              <div>
                <div className="skill-category">{skill.category}</div>
                <div className="skill-name">{skill.name}</div>
              </div>
              <span className={`skill-level ${levelColors[skill.level]}`}>{skill.level}</span>
            </div>

            <div style={{ display:'flex', justifyContent:'space-between', marginBottom:'6px' }}>
              <span style={{ fontSize:'0.75rem', color:'var(--text-muted)' }}>Coverage</span>
              <span style={{ fontSize:'0.75rem', fontWeight:700, color: skill.gap > 40 ? 'var(--danger)' : skill.gap > 20 ? 'var(--warning)' : 'var(--success)' }}>
                Gap: {skill.gap}%
              </span>
            </div>
            <div className="progress-bar">
              <div
                className="progress-fill"
                style={{
                  width: `${100 - skill.gap}%`,
                  background: skill.gap > 40
                    ? 'linear-gradient(90deg, #ef4444, #f87171)'
                    : skill.gap > 20
                    ? 'linear-gradient(90deg, #f59e0b, #fbbf24)'
                    : 'linear-gradient(90deg, #10b981, #34d399)',
                }}
              />
            </div>

            <div className="skill-meta">
              <span>👥 {skill.employees} employees</span>
              {skill.trending && <span style={{ color:'#ec4899' }}>🔥 Trending</span>}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Skills;
