import React, { useEffect, useMemo, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { JOB_ROLES } from '../data/examData';
import { courseFetch, getStoredUser, roleFamily, saveSession, apiFetch } from '../services/platformApi';
import { fetchEnrollments } from '../services/enrollments';

const Profile = () => {
  const navigate = useNavigate();
  const [user, setUser] = useState(getStoredUser());
  const [recommendations, setRecommendations] = useState([]);
  const [loading, setLoading] = useState(false);
  const [planMessage, setPlanMessage] = useState('Your AI plan is ready to generate when your profile and assessment are up to date.');
  const [selectedRole, setSelectedRole] = useState(user.targetRole || user.examRole || '');
  const [enrollments, setEnrollments] = useState([]);
  const [loadingEnrollments, setLoadingEnrollments] = useState(false);

  const gapSkills = Object.entries(user.examResults || {})
    .filter(([, value]) => value.level !== 'strong')
    .map(([skill, value]) => ({ skill, ...value }));

  const scoreSnapshot = useMemo(() => {
    const values = Object.values(user.examResults || {}).map((value) => value.score || 0);
    return values.length ? Math.round(values.reduce((sum, score) => sum + score, 0) / values.length) : null;
  }, [user.examResults]);

  const loadAiPlan = async () => {
    setLoading(true);
    setPlanMessage('Generating your AI development plan…');

    try {
      const res = await courseFetch(
        `/learning-paths?role=${encodeURIComponent(user.role || user.accountType || 'Employee')}&email=${encodeURIComponent(user.email || '')}&limit=6`
      );
      if (!res.ok) {
        throw new Error('Failed to fetch AI recommendations');
      }
      const data = await res.json();
      const courses = Array.isArray(data.recommendedCourses) ? data.recommendedCourses : [];
      setRecommendations(courses);
      setPlanMessage(courses.length > 0 ? 'AI-generated plan refreshed successfully.' : 'No recommendations were returned.');
    } catch {
      setRecommendations([]);
      setPlanMessage('Unable to generate AI plan right now. Please try again later.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadAiPlan();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [user.email, user.role, user.accountType]);

  useEffect(() => {
    const loadProfile = async () => {
      if (!user?.email) return;
      try {
        const res = await apiFetch(`/profile?email=${encodeURIComponent(user.email)}`);
        if (res.ok) {
          const serverUser = await res.json();
          const updatedUser = { ...user, ...serverUser };
          setUser(updatedUser);
          saveSession({ token: localStorage.getItem('token'), user: updatedUser });
        }
      } catch (err) {
        console.error('Failed to load profile', err);
      }
    };

    loadProfile();
    const load = async () => {
      if (!user?.email) return;
      setLoadingEnrollments(true);
      const list = await fetchEnrollments(user.email);
      setEnrollments(list || []);
      setLoadingEnrollments(false);
    };
    load();
  }, [user?.email]);

  const handleRoleUpdate = (roleId) => {
    const updatedUser = { ...user, targetRole: roleId };
    // Persist locally and server-side
    saveSession({ token: localStorage.getItem('token'), user: updatedUser });
    setUser(updatedUser);
    setSelectedRole(roleId);
    setPlanMessage('Target role updated. Refresh the AI plan to update recommendations.');

    // API update
    (async () => {
      try {
        await apiFetch('/profile', { method: 'PUT', body: JSON.stringify({ email: updatedUser.email, targetRole: roleId, name: updatedUser.name, department: updatedUser.department }) });
      } catch (err) {
        console.error('Failed to save profile to server', err);
      }
    })();
  };

  const topGapsText = gapSkills.length
    ? gapSkills.slice(0, 3).map((item) => item.skill).join(', ')
    : 'Complete the assessment to uncover your top gaps.';

  const statusLabel = user.examResults ? 'Completed' : 'Pending assessment';
  const targetRoleLabel = selectedRole
    ? JOB_ROLES.find((r) => r.id === selectedRole)?.label || selectedRole
    : 'Not selected yet';

  return (
    <div>
      <div className="page-header">
        <div>
          <div className="page-title">Profile & AI Action Plan</div>
          <div className="page-sub">Manage your profile, target role, and AI-generated development plan.</div>
        </div>
        <button className="btn-primary" onClick={loadAiPlan} disabled={loading}>
          {loading ? 'Refreshing AI Plan…' : 'Refresh AI Plan'}
        </button>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1.2fr 0.8fr', gap: '1.25rem', marginBottom: '1.5rem' }}>
        <div className="card">
          <div className="card-header">
            <div className="card-title">👤 Your Profile</div>
            <span style={{ color: 'var(--text-muted)' }}>{user.role || user.accountType || 'Employee'}</span>
          </div>
          <div style={{ display: 'grid', gap: '1rem', marginTop: '1rem' }}>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
              <div style={{ padding: '1rem', background: 'rgba(99,102,241,0.08)', borderRadius: 12 }}>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Name</div>
                <div style={{ fontSize: '1rem', fontWeight: 700 }}>{user.name || 'Your name'}</div>
              </div>
              <div style={{ padding: '1rem', background: 'rgba(99,102,241,0.08)', borderRadius: 12 }}>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Email</div>
                <div style={{ fontSize: '1rem', fontWeight: 700 }}>{user.email || 'you@company.com'}</div>
              </div>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '1rem' }}>
              <div style={{ padding: '1rem', borderRadius: 12, background: 'rgba(16,185,129,0.08)' }}>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Target role</div>
                <div style={{ fontSize: '1rem', fontWeight: 700 }}>{targetRoleLabel}</div>
              </div>
              <div style={{ padding: '1rem', borderRadius: 12, background: 'rgba(236,72,153,0.08)' }}>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Assessment</div>
                <div style={{ fontSize: '1rem', fontWeight: 700 }}>{statusLabel}</div>
              </div>
              <div style={{ padding: '1rem', borderRadius: 12, background: 'rgba(245,158,11,0.08)' }}>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>AI Plan</div>
                <div style={{ fontSize: '1rem', fontWeight: 700 }}>{recommendations.length} recommendations</div>
              </div>
            </div>

            <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.75rem' }}>
              <button className="btn-primary" onClick={() => navigate('/app/exam')}>Take Assessment</button>
              <button className="btn-secondary" onClick={() => navigate('/app/gap-analysis')}>View Gap Analysis</button>
              <button className="btn-secondary" onClick={() => navigate('/app/trainings')}>Browse Courses</button>
            </div>
          </div>
        </div>

        <div className="card" style={{ position: 'relative', overflow: 'hidden' }}>
          <div style={{ position: 'absolute', top: 0, right: 0, width: 140, height: 140, background: 'radial-gradient(circle, rgba(99,102,241,0.25) 0%, transparent 65%)', pointerEvents: 'none' }} />
          <div className="card-header">
            <div className="card-title">🎯 AI Plan Summary</div>
            <span style={{ color: 'var(--text-muted)' }}>Based on your profile and current gaps</span>
          </div>
          <div style={{ marginTop: '1rem', display: 'grid', gap: '1rem' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
              <span style={{ fontSize: '1.7rem' }}>🤖</span>
              <div>
                <div style={{ fontWeight: 700 }}>Focus areas</div>
                <div style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>{topGapsText}</div>
              </div>
            </div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
              <span style={{ fontSize: '1.7rem' }}>📈</span>
              <div>
                <div style={{ fontWeight: 700 }}>Personalized outcome</div>
                <div style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
                  {scoreSnapshot !== null
                    ? `Aim to lift your average competency score from ${scoreSnapshot}% to 90%+ with role-specific training.`
                    : 'Complete the assessment to generate a fully personalized AI plan.'}
                </div>
              </div>
            </div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
              <span style={{ fontSize: '1.7rem' }}>🧭</span>
              <div>
                <div style={{ fontWeight: 700 }}>Next step</div>
                <div style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
                  {user.examResults
                    ? 'Review the recommended courses below and enroll in the highest-impact gaps first.'
                    : 'Take your role-specific assessment so AI can generate a targeted plan.'}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 0.95fr', gap: '1.25rem' }}>
        <div className="card">
          <div className="card-header">
            <div className="card-title">🛠️ Update Target Role</div>
            <span style={{ color: 'var(--text-muted)' }}>Change your focus and refresh your AI plan.</span>
          </div>
          <div style={{ marginTop: '1rem', display: 'grid', gap: '0.85rem' }}>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, minmax(0, 1fr))', gap: '0.9rem' }}>
              {JOB_ROLES.map((role) => (
                <button
                  key={role.id}
                  type="button"
                  className={`role-picker-card ${selectedRole === role.id ? 'selected' : ''}`}
                  style={{ minHeight: 100, justifyContent: 'center' }}
                  onClick={() => handleRoleUpdate(role.id)}
                >
                  <span style={{ fontSize: '1.5rem' }}>{role.icon}</span>
                  <span style={{ marginTop: '0.5rem', textAlign: 'center', fontSize: '0.8rem', fontWeight: 700, color: selectedRole === role.id ? '#a5b4fc' : 'var(--text-secondary)' }}>
                    {role.label}
                  </span>
                </button>
              ))}
            </div>
            <div style={{ color: 'var(--text-secondary)', fontSize: '0.88rem' }}>{planMessage}</div>
          </div>
        </div>

        <div className="card">
          <div className="card-header">
            <div className="card-title">📚 AI-generated Learning Path</div>
            <span style={{ color: 'var(--text-muted)' }}>Courses matched to your current role and gap profile.</span>
          </div>
          <div style={{ marginTop: '1rem', display: 'grid', gap: '0.75rem' }}>
            {recommendations.length > 0 ? (
              recommendations.map((course, idx) => (
                <div key={`${course.title || 'course'}-${idx}`} style={{ padding: '1rem', borderRadius: 12, border: '1px solid rgba(255,255,255,0.08)', background: 'rgba(255,255,255,0.02)' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', gap: '1rem', flexWrap: 'wrap' }}>
                    <div>
                      <div style={{ fontWeight: 700, marginBottom: '0.35rem' }}>{course.title}</div>
                      <div style={{ fontSize: '0.82rem', color: 'var(--text-muted)' }}>{course.provider || course.source || 'Recommended Provider'}</div>
                    </div>
                    <span style={{ color: '#a5b4fc', fontWeight: 700 }}>{course.matchScore ? `${course.matchScore}% match` : ''}</span>
                  </div>
                  <div style={{ display: 'flex', gap: '0.75rem', flexWrap: 'wrap', marginTop: '0.9rem', alignItems: 'center' }}>
                    <span style={{ fontSize: '0.83rem', color: 'var(--text-secondary)' }}>Category: {course.category || course.skill || 'General'}</span>
                    <span style={{ fontSize: '0.83rem', color: 'var(--text-secondary)' }}>Duration: {course.duration || `${course.durationHours || 0}h`}</span>
                    {course.url && (
                      <a href={course.url} target="_blank" rel="noopener noreferrer" style={{ fontSize: '0.83rem', color: '#a5b4fc' }}>
                        Launch
                      </a>
                    )}
                  </div>
                </div>
              ))
            ) : (
              <div style={{ padding: '1.5rem', textAlign: 'center', color: 'var(--text-muted)' }}>
                <div style={{ fontSize: '2rem', marginBottom: '0.75rem' }}>🤖</div>
                <div>No AI recommendations available yet.</div>
                <div style={{ marginTop: '0.75rem' }}>Try refreshing the plan or complete your assessment first.</div>
              </div>
            )}
            <div style={{ marginTop: '1rem' }}>
              <div style={{ fontWeight: 700, marginBottom: '0.5rem' }}>Your Enrollments</div>
              {loadingEnrollments ? (
                <div style={{ color: 'var(--text-muted)' }}>Loading enrollments…</div>
              ) : enrollments.length ? (
                enrollments.map((en) => (
                  <div key={en.id} style={{ padding: '0.7rem 0', borderBottom: '1px dashed rgba(255,255,255,0.04)' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                      <div style={{ fontWeight: 700 }}>{en.programTitle}</div>
                      <div style={{ color: 'var(--text-muted)' }}>{en.status}</div>
                    </div>
                    <div style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', marginTop: '0.25rem' }}>
                      Progress: {en.progressPercent || 0}% • Provider: {en.provider || '—'}
                    </div>
                  </div>
                ))
              ) : (
                <div style={{ color: 'var(--text-muted)' }}>You have no active enrollments.</div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Profile;
