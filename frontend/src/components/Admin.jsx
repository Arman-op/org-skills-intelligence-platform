import React, { useEffect, useMemo, useState } from 'react';
import { fetchEmployeeImprovements } from '../services/employeeImprovement';

function Admin() {
  const [records, setRecords] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadRecords = async () => {
      setLoading(true);
      const data = await fetchEmployeeImprovements();
      setRecords(data);
      setLoading(false);
    };

    loadRecords();
  }, []);

  const summary = useMemo(() => {
    const total = records.length;
    const avgScore = total
      ? Math.round(records.reduce((sum, record) => sum + (record.overallScore || 0), 0) / total)
      : 0;
    const withEnrollments = records.filter((record) => (record.enrolledCourses || '').trim().length > 0).length;
    return { total, avgScore, withEnrollments };
  }, [records]);

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
      <div className="page-header">
        <div>
          <div className="page-title">Admin Improvement Tracker</div>
          <div className="page-sub">Stored employee assessments, learning progress, and improvement notes</div>
        </div>
        <button className="btn-primary" onClick={() => window.location.reload()} style={{ padding: '0.6rem 1.25rem', borderRadius: 8 }}>
          Refresh Records
        </button>
      </div>

      <div className="stats-row">
        {[
          { label: 'Employees Stored', value: summary.total, color: '#6366f1' },
          { label: 'Avg. Score', value: `${summary.avgScore}%`, color: '#10b981' },
          { label: 'With Learning Updates', value: summary.withEnrollments, color: '#f59e0b' },
        ].map((item) => (
          <div key={item.label} className="stat-card" style={{ flex: 1, textAlign: 'center' }}>
            <div className="stat-value" style={{ color: item.color }}>{item.value}</div>
            <div className="stat-title">{item.label}</div>
          </div>
        ))}
      </div>

      <div className="card">
        <div className="card-header">
          <div className="card-title">Employee Records</div>
        </div>
        {loading ? (
          <div style={{ padding: '2rem', color: 'var(--text-secondary)' }}>Loading stored employee improvements...</div>
        ) : records.length === 0 ? (
          <div style={{ padding: '2rem', color: 'var(--text-secondary)' }}>
            No employee improvement records found yet. When employees complete an assessment or enroll in training, their progress will appear here.
          </div>
        ) : (
          <div style={{ display: 'grid', gap: '1rem' }}>
            {records.map((record) => (
              <div key={record.id} style={{ padding: '1rem 1.1rem', borderRadius: 12, background: 'rgba(255,255,255,0.03)', border: '1px solid rgba(255,255,255,0.06)' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', gap: '1rem', flexWrap: 'wrap' }}>
                  <div>
                    <div style={{ fontWeight: 700, fontSize: '1rem' }}>{record.employeeName}</div>
                    <div style={{ color: 'var(--text-muted)', fontSize: '0.84rem' }}>{record.employeeEmail} • {record.role || 'Employee'}{record.targetRole ? ` • Target: ${record.targetRole}` : ''}</div>
                  </div>
                  <div style={{ textAlign: 'right' }}>
                    <div style={{ fontWeight: 800, color: '#10b981', fontSize: '1.1rem' }}>{record.overallScore || 0}%</div>
                    <div style={{ color: 'var(--text-muted)', fontSize: '0.75rem' }}>{record.lastUpdated ? `Updated ${new Date(record.lastUpdated).toLocaleString()}` : 'Recently updated'}</div>
                  </div>
                </div>

                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '0.85rem', marginTop: '0.85rem' }}>
                  <div>
                    <div style={{ fontSize: '0.72rem', textTransform: 'uppercase', letterSpacing: '0.08em', color: 'var(--text-muted)', marginBottom: '0.35rem' }}>Gaps</div>
                    <div style={{ fontSize: '0.88rem' }}>{record.gapSummary || 'No gaps recorded'}</div>
                  </div>
                  <div>
                    <div style={{ fontSize: '0.72rem', textTransform: 'uppercase', letterSpacing: '0.08em', color: 'var(--text-muted)', marginBottom: '0.35rem' }}>Enrolled Courses</div>
                    <div style={{ fontSize: '0.88rem' }}>{record.enrolledCourses || 'No courses enrolled yet'}</div>
                  </div>
                  <div>
                    <div style={{ fontSize: '0.72rem', textTransform: 'uppercase', letterSpacing: '0.08em', color: 'var(--text-muted)', marginBottom: '0.35rem' }}>Improvement Notes</div>
                    <div style={{ fontSize: '0.88rem' }}>{record.improvementSummary || 'Assessment captured'}</div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}

export default Admin;