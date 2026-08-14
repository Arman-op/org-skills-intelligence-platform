import React, { useState, useEffect } from 'react';
import { fetchSkills, fetchEmployeeSkills, saveEmployeeSkill, updateEmployeeSkill } from '../services/employeeSkills';
import { buildImprovementPayload, saveEmployeeImprovement } from '../services/employeeImprovement';

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

const proficiencyLevels = [
  { label: 'Beginner', value: 1 },
  { label: 'Intermediate', value: 2 },
  { label: 'Advanced', value: 3 },
  { label: 'Expert', value: 4 },
];

const Skills = () => {
  const [active, setActive] = useState('All');
  const [search, setSearch] = useState('');
  const [catalog, setCatalog] = useState([]);
  const [employeeSkills, setEmployeeSkills] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const user = JSON.parse(localStorage.getItem('user') || '{}');

  useEffect(() => {
    const load = async () => {
      setLoading(true);
      const [skills, userSkills] = await Promise.all([fetchSkills(), fetchEmployeeSkills(user.email)]);
      setCatalog(skills);
      setEmployeeSkills(userSkills);
      setLoading(false);
    };
    load();
  }, [user.email]);

  const mergedSkills = catalog.map((skill) => {
    const match = employeeSkills.find((es) => es.skillId === skill.id || es.skillName === skill.name);
    return {
      ...skill,
      proficiency: match?.proficiency || 1,
      targetProficiency: match?.targetProficiency || Math.min((match?.proficiency || 1) + 1, 4),
      employeeSkillId: match?.id,
    };
  });

  const filtered = mergedSkills.filter((s) => {
    const matchCat = active === 'All' || s.category === active;
    const matchSearch = s.name.toLowerCase().includes(search.toLowerCase());
    return matchCat && matchSearch;
  });

  const saveSkillUpdate = async (skill, field, value) => {
    setSaving(true);
    try {
      const payload = {
        employeeEmail: user.email,
        skillId: skill.id,
        skillName: skill.name,
        proficiency: field === 'proficiency' ? value : skill.proficiency,
        targetProficiency: field === 'targetProficiency' ? value : skill.targetProficiency,
      };
      if (skill.employeeSkillId) {
        await updateEmployeeSkill(skill.employeeSkillId, payload);
      } else {
        await saveEmployeeSkill(payload);
      }
      const userSkills = await fetchEmployeeSkills(user.email);
      setEmployeeSkills(userSkills);
      saveEmployeeImprovement(buildImprovementPayload({ user, examResults: user.examResults || {}, enrolledCourses: user.enrolledCourses || [] }));
    } finally {
      setSaving(false);
    }
  };

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
                <div className="skill-category">{skill.category || 'General'}</div>
                <div className="skill-name">{skill.name}</div>
              </div>
              <span className={`skill-level ${levelColors[skill.level || 'Beginner']}`}>{skill.level || 'Beginner'}</span>
            </div>

            <div style={{ display:'grid', gap:'0.75rem', marginBottom:'1rem' }}>
              <div style={{ display:'flex', justifyContent:'space-between', gap:'1rem', fontSize:'0.85rem' }}>
                <div>
                  <div style={{ color:'var(--text-muted)', fontSize:'0.75rem' }}>Current proficiency</div>
                  <div style={{ fontWeight:700 }}>{proficiencyLevels.find((item) => item.value === skill.proficiency)?.label || 'Beginner'}</div>
                </div>
                <div>
                  <div style={{ color:'var(--text-muted)', fontSize:'0.75rem' }}>Target level</div>
                  <div style={{ fontWeight:700 }}>{proficiencyLevels.find((item) => item.value === skill.targetProficiency)?.label || 'Intermediate'}</div>
                </div>
              </div>
              <div style={{ display:'grid', gap:'0.75rem' }}>
                <label style={{ fontSize:'0.8rem', color:'var(--text-muted)' }}>
                  Improve proficiency:
                  <select
                    value={skill.proficiency}
                    onChange={(e) => saveSkillUpdate(skill, 'proficiency', Number(e.target.value))}
                    disabled={saving}
                    style={{ width:'100%', marginTop:'0.45rem', padding:'0.6rem 0.75rem', borderRadius: 10, background:'rgba(255,255,255,0.05)', border:'1px solid rgba(255,255,255,0.12)', color:'#fff' }}
                  >
                    {proficiencyLevels.map((level) => (
                      <option key={level.value} value={level.value}>{level.label}</option>
                    ))}
                  </select>
                </label>
                <label style={{ fontSize:'0.8rem', color:'var(--text-muted)' }}>
                  Set target level:
                  <select
                    value={skill.targetProficiency}
                    onChange={(e) => saveSkillUpdate(skill, 'targetProficiency', Number(e.target.value))}
                    disabled={saving}
                    style={{ width:'100%', marginTop:'0.45rem', padding:'0.6rem 0.75rem', borderRadius: 10, background:'rgba(255,255,255,0.05)', border:'1px solid rgba(255,255,255,0.12)', color:'#fff' }}
                  >
                    {proficiencyLevels.map((level) => (
                      <option key={level.value} value={level.value}>{level.label}</option>
                    ))}
                  </select>
                </label>
              </div>
            </div>

            <div className="skill-meta" style={{ justifyContent:'space-between', alignItems:'center' }}>
              <span>📈 {skill.employeeSkillId ? 'Tracked skill' : 'New skill'}</span>
              <span style={{ color: skill.proficiency >= skill.targetProficiency ? '#10b981' : '#f59e0b', fontWeight: 700 }}>
                {skill.proficiency >= skill.targetProficiency ? 'On track' : 'Learning gap'}
              </span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Skills;
