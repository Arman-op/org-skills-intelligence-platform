import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { courseFetch, getStoredUser, roleFamily } from '../services/platformApi';

const AIPlan = () => {
  const navigate = useNavigate();
  const user = getStoredUser();
  const [recommendations, setRecommendations] = useState([]);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('Building your AI development plan now.');

  const gapSkills = Object.entries(user.examResults || {})
    .filter(([, value]) => value.level !== 'strong')
    .map(([skill, value]) => ({ skill, ...value }));

  const loadPlan = async () => {
    setLoading(true);
    setMessage('Generating your AI learning path...');

    try {
      const response = await courseFetch(
        `/learning-paths?role=${encodeURIComponent(user.role || user.accountType || 'Employee')}&email=${encodeURIComponent(user.email || '')}&limit=8`
      );
      if (!response.ok) throw new Error('No plan returned');
      const data = await response.json();
      const items = Array.isArray(data.recommendedCourses) ? data.recommendedCourses : [];
      setRecommendations(items);
      setMessage(items.length > 0 ? 'AI plan generated successfully.' : 'No plan items available yet.');
    } catch (error) {
      console.error(error);
      setRecommendations([]);
      setMessage('Unable to generate AI plan at this time. Please try again later.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadPlan();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [user.email, user.role, user.accountType]);

  return (
    <div>
      <div className="page-header">
        <div>
          <div className="page-title">AI Development Plan</div>
          <div className="page-sub">An intelligent learning path tailored to your role and assessment gaps.</div>
        </div>
        <div style={{ display: 'flex', gap: '0.75rem', flexWrap: 'wrap' }}>
          <button className="btn-secondary" onClick={() => navigate('/app/profile')}>
            View Profile
          </button>
          <button className="btn-primary" onClick={loadPlan} disabled={loading}>
            {loading ? 'Refreshing…' : 'Refresh Plan'}
          </button>
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 0.95fr', gap: '1.25rem', marginBottom: '1.5rem' }}>
        <div className="card">
          <div className="card-header">
            <div className="card-title">📌 Plan Overview</div>
            <span style={{ color: 'var(--text-muted)' }}>Your current assessment and target role define this plan.</span>
          </div>
          <div style={{ display: 'grid', gap: '1rem', marginTop: '1rem' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', gap: '1rem', flexWrap: 'wrap' }}>
              <div>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Current role</div>
                <div style={{ fontWeight: 700 }}>{user.role || user.accountType || 'Employee'}</div>
              </div>
              <div>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Target role</div>
                <div style={{ fontWeight: 700 }}>{user.targetRole || user.examRole || 'Not selected'}</div>
              </div>
              <div>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Assessment status</div>
                <div style={{ fontWeight: 700 }}>{user.examResults ? 'Completed' : 'Pending'}</div>
              </div>
            </div>
            <div style={{ padding: '1rem', borderRadius: 12, background: 'rgba(99,102,241,0.08)', border: '1px solid rgba(99,102,241,0.2)' }}>
              <div style={{ fontWeight: 700, marginBottom: '0.5rem' }}>What this plan includes</div>
              <ul style={{ display: 'grid', gap: '0.5rem', paddingLeft: '1rem', color: 'var(--text-secondary)' }}>
                <li>Role-aligned course recommendations</li>
                <li>Gap-focused skill improvement actions</li>
                <li>Priority learning items for rapid competency lift</li>
                <li>Suggested next steps and training focus</li>
              </ul>
            </div>
          </div>
        </div>

        <div className="card">
          <div className="card-header">
            <div className="card-title">🧭 AI Plan Status</div>
            <span style={{ color: 'var(--text-muted)' }}>{message}</span>
          </div>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem', marginTop: '1rem' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', gap: '1rem', flexWrap: 'wrap' }}>
              <div style={{ background: 'rgba(16,185,129,0.08)', borderRadius: 12, padding: '1rem', flex: 1 }}>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Strongest area</div>
                <div style={{ fontWeight: 700 }}>{gapSkills.length ? gapSkills[0].skill : 'Not yet available'}</div>
              </div>
              <div style={{ background: 'rgba(245,158,11,0.08)', borderRadius: 12, padding: '1rem', flex: 1 }}>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Biggest gap</div>
                <div style={{ fontWeight: 700 }}>{gapSkills.length ? gapSkills[gapSkills.length - 1].skill : 'Complete assessment first'}</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="card">
        <div className="card-header">
          <div className="card-title">Recommended Learning Items</div>
          <span style={{ color: 'var(--text-muted)' }}>Courses selected by AI for your improvement plan.</span>
        </div>

        {recommendations.length > 0 ? (
          <div style={{ display: 'grid', gap: '1rem', marginTop: '1rem' }}>
            {recommendations.map((item, idx) => (
              <div key={`${item.title || 'course'}-${idx}`} style={{ padding: '1rem', borderRadius: 14, background: 'rgba(255,255,255,0.04)', border: '1px solid rgba(255,255,255,0.08)' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', gap: '1rem', flexWrap: 'wrap' }}>
                  <div>
                    <div style={{ fontWeight: 700, marginBottom: '0.35rem' }}>{item.title}</div>
                    <div style={{ color: 'var(--text-secondary)', fontSize: '0.85rem' }}>{item.provider || item.source || 'Recommended Provider'}</div>
                  </div>
                  <div style={{ textAlign: 'right' }}>
                    <div style={{ fontWeight: 700 }}>{item.matchScore ? `${item.matchScore}% match` : 'Recommended'}</div>
                    <div style={{ color: 'var(--text-muted)', fontSize: '0.82rem' }}>{item.duration || `${item.durationHours || 0}h`}</div>
                  </div>
                </div>
                <div style={{ display: 'flex', gap: '0.75rem', flexWrap: 'wrap', marginTop: '0.9rem' }}>
                  <span style={{ fontSize: '0.82rem', color: 'var(--text-muted)' }}>Category: {item.category || item.skill || 'General'}</span>
                  {item.url && (
                    <a href={item.url} target="_blank" rel="noopener noreferrer" style={{ color: '#a5b4fc', fontWeight: 700 }}>
                      Open Resource →
                    </a>
                  )}
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div style={{ padding: '2rem', textAlign: 'center', color: 'var(--text-muted)' }}>
            <div style={{ fontSize: '2rem', marginBottom: '0.8rem' }}>🤖</div>
            <div>No AI recommendations loaded yet.</div>
            <div style={{ marginTop: '0.75rem' }}>Refresh the plan or complete your assessment to generate a tailored learning path.</div>
          </div>
        )}
      </div>
    </div>
  );
};

export default AIPlan;
