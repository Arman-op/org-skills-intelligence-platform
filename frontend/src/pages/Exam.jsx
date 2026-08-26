import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { ROLE_EXAMS, JOB_ROLES } from '../data/examData';
import { buildImprovementPayload, saveEmployeeImprovement } from '../services/employeeImprovement';

const QUESTION_TIME = 45; // seconds per question

const Exam = () => {
  const navigate = useNavigate();
  const user = JSON.parse(localStorage.getItem('user') || '{}');
  const [targetRole, setTargetRole] = useState(user.targetRole || user.examRole || '');
  const exam = ROLE_EXAMS[targetRole];

  const [phase, setPhase] = useState('intro'); // intro | question | result
  const [currentQ, setCurrentQ] = useState(0);
  const [selected, setSelected] = useState(null);
  const [answers, setAnswers] = useState([]);
  const [timeLeft, setTimeLeft] = useState(QUESTION_TIME);
  const [showFeedback, setShowFeedback] = useState(false);

  const handleAnswer = useCallback((optIdx) => {
    if (showFeedback) return;
    const q = exam.questions[currentQ];
    const isCorrect = optIdx === q.correct;
    setSelected(optIdx);
    setShowFeedback(true);

    const newAnswer = { questionId: q.id, skill: q.skill, correct: isCorrect, selected: optIdx };

    setTimeout(() => {
      const updatedAnswers = [...answers, newAnswer];
      if (currentQ + 1 >= exam.questions.length) {
        // Compute results and save
        const skillScores = {};
        exam.skills.forEach(s => { skillScores[s] = { total: 0, correct: 0 }; });
        updatedAnswers.forEach(a => {
          skillScores[a.skill].total += 1;
          if (a.correct) skillScores[a.skill].correct += 1;
        });
        const gapResults = {};
        Object.entries(skillScores).forEach(([skill, { total, correct }]) => {
          const pct = total > 0 ? Math.round((correct / total) * 100) : 0;
          gapResults[skill] = { score: pct, gap: 100 - pct, level: pct >= 80 ? 'strong' : pct >= 50 ? 'moderate' : 'critical' };
        });
        const existingUser = JSON.parse(localStorage.getItem('user') || '{}');
        const updatedUser = { ...existingUser, examResults: gapResults, examDate: new Date().toISOString(), examRole: targetRole };
        localStorage.setItem('user', JSON.stringify(updatedUser));
        saveEmployeeImprovement(buildImprovementPayload({ user: updatedUser, examResults: gapResults }));
        setAnswers(updatedAnswers);
        setPhase('result');
      } else {
        setAnswers(updatedAnswers);
        setCurrentQ(c => c + 1);
        setSelected(null);
        setShowFeedback(false);
        setTimeLeft(QUESTION_TIME);
      }
    }, 1000);
  }, [showFeedback, currentQ, answers, exam, targetRole]);

  // Timer
  useEffect(() => {
    if (phase !== 'question') return;
    if (showFeedback) return;
    if (timeLeft <= 0) {
      handleAnswer(-1); // time's up → wrong
      return;
    }
    const t = setTimeout(() => setTimeLeft(t => t - 1), 1000);
    return () => clearTimeout(t);
  }, [phase, timeLeft, showFeedback, handleAnswer]);

  const startExam = () => {
    if (!targetRole) {
      return;
    }
    setPhase('question');
    setTimeLeft(QUESTION_TIME);
  };

  if (!exam) {
    return (
      <div className="exam-page">
        <div className="exam-intro-card">
          <div className="exam-intro-icon">🎯</div>
          <h1>Choose Your Assessment Role</h1>
          <p>Select the role you want to be assessed against. This will unlock the matching exam and learning path.</p>

          <div className="role-picker-grid" style={{ marginTop: '1.5rem' }}>
            {JOB_ROLES.map((role) => (
              <button
                key={role.id}
                type="button"
                className={`role-picker-card ${targetRole === role.id ? 'selected' : ''}`}
                onClick={() => {
                  setTargetRole(role.id);
                  const storedUser = JSON.parse(localStorage.getItem('user') || '{}');
                  localStorage.setItem('user', JSON.stringify({ ...storedUser, targetRole: role.id }));
                }}
              >
                <span style={{ fontSize: '1.6rem' }}>{role.icon}</span>
                <span style={{ fontSize: '0.82rem', fontWeight: 600, color: targetRole === role.id ? '#a5b4fc' : 'var(--text-secondary)', textAlign: 'center', lineHeight: 1.3 }}>{role.label}</span>
                {targetRole === role.id && (
                  <div style={{ position: 'absolute', top: 8, right: 8, width: 18, height: 18, borderRadius: '50%', background: '#6366f1', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '0.55rem', color: '#fff', fontWeight: 900 }}>✓</div>
                )}
              </button>
            ))}
          </div>

          <button className="btn-start-exam" onClick={startExam} disabled={!targetRole} style={{ marginTop: '1.5rem', opacity: targetRole ? 1 : 0.6, cursor: targetRole ? 'pointer' : 'not-allowed' }}>
            🚀 Start Assessment
          </button>
        </div>
      </div>
    );
  }

  /* -------- INTRO -------- */
  if (phase === 'intro') {
    const roleInfo = JOB_ROLES.find(r => r.id === targetRole);
    return (
      <div className="exam-page">
        <div className="exam-intro-card">
          <div className="exam-intro-icon">{roleInfo?.icon || '📝'}</div>
          <h1>{exam.title}</h1>
          <p>Test your knowledge across <strong>{exam.skills.length} skill domains</strong> with <strong>{exam.questions.length} questions</strong>. Your results will identify knowledge gaps and suggest personalized learning paths.</p>

          <div className="exam-meta-grid">
            <div className="exam-meta-item">
              <span className="exam-meta-icon">❓</span>
              <div>
                <div className="exam-meta-val">{exam.questions.length}</div>
                <div className="exam-meta-label">Questions</div>
              </div>
            </div>
            <div className="exam-meta-item">
              <span className="exam-meta-icon">⏱</span>
              <div>
                <div className="exam-meta-val">{QUESTION_TIME}s</div>
                <div className="exam-meta-label">Per Question</div>
              </div>
            </div>
            <div className="exam-meta-item">
              <span className="exam-meta-icon">🎯</span>
              <div>
                <div className="exam-meta-val">{exam.skills.length}</div>
                <div className="exam-meta-label">Skill Areas</div>
              </div>
            </div>
            <div className="exam-meta-item">
              <span className="exam-meta-icon">📊</span>
              <div>
                <div className="exam-meta-val">AI</div>
                <div className="exam-meta-label">Gap Analysis</div>
              </div>
            </div>
          </div>

          <div className="exam-skills-preview">
            <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', marginBottom: '0.75rem', textTransform: 'uppercase', letterSpacing: '0.1em' }}>Skills Assessed</div>
            <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap', justifyContent: 'center' }}>
              {exam.skills.map(s => (
                <span key={s} style={{ background: 'rgba(99,102,241,0.12)', border: '1px solid rgba(99,102,241,0.25)', borderRadius: 50, padding: '0.3rem 0.85rem', fontSize: '0.82rem', color: '#a5b4fc' }}>{s}</span>
              ))}
            </div>
          </div>

          <button className="btn-start-exam" onClick={startExam}>
            🚀 Start Assessment
          </button>
          <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)', marginTop: '0.75rem' }}>Results are private and used only to personalize your learning path.</p>
        </div>
      </div>
    );
  }

  /* -------- QUESTION -------- */
  if (phase === 'question') {
    const q = exam.questions[currentQ];
    const progress = ((currentQ) / exam.questions.length) * 100;
    const timerPct = (timeLeft / QUESTION_TIME) * 100;
    const timerColor = timeLeft > 20 ? '#10b981' : timeLeft > 10 ? '#f59e0b' : '#ef4444';

    return (
      <div className="exam-page">
        <div className="exam-q-card">
          {/* Header */}
          <div className="exam-q-header">
            <div className="exam-q-progress-info">
              <span style={{ fontSize: '0.82rem', color: 'var(--text-muted)' }}>Question {currentQ + 1} of {exam.questions.length}</span>
              <span style={{ fontSize: '0.82rem', fontWeight: 600, color: '#a5b4fc' }}>Skill: {q.skill}</span>
            </div>
            <div className="exam-q-timer" style={{ borderColor: timerColor, color: timerColor }}>
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke={timerColor} strokeWidth="2">
                <circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/>
              </svg>
              {timeLeft}s
            </div>
          </div>

          {/* Progress Bar */}
          <div style={{ height: 4, background: 'rgba(255,255,255,0.06)', borderRadius: 4, marginBottom: '1.75rem', overflow: 'hidden' }}>
            <div style={{ height: '100%', width: `${progress}%`, background: 'linear-gradient(90deg, #6366f1, #8b5cf6)', borderRadius: 4, transition: 'width 0.4s ease' }} />
          </div>

          {/* Timer bar */}
          <div style={{ height: 3, background: 'rgba(255,255,255,0.04)', borderRadius: 3, marginBottom: '2rem', overflow: 'hidden' }}>
            <div style={{ height: '100%', width: `${timerPct}%`, background: timerColor, borderRadius: 3, transition: 'width 1s linear' }} />
          </div>

          <div className="exam-question-text">{q.question}</div>

          <div className="exam-options">
            {q.options.map((opt, idx) => {
              let cls = 'exam-option';
              if (showFeedback) {
                if (idx === q.correct) cls += ' correct';
                else if (idx === selected) cls += ' wrong';
                else cls += ' dim';
              } else if (selected === idx) {
                cls += ' selected';
              }
              return (
                <button key={idx} className={cls} onClick={() => handleAnswer(idx)} disabled={showFeedback}>
                  <span className="exam-option-letter">{String.fromCharCode(65 + idx)}</span>
                  <span>{opt}</span>
                  {showFeedback && idx === q.correct && <span className="exam-option-badge correct">✓</span>}
                  {showFeedback && idx === selected && idx !== q.correct && <span className="exam-option-badge wrong">✗</span>}
                </button>
              );
            })}
          </div>
        </div>
      </div>
    );
  }

  /* -------- RESULT -------- */
  if (phase === 'result') {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    const results = user.examResults || {};
    const totalCorrect = answers.filter(a => a.correct).length;
    const totalScore = Math.round((totalCorrect / exam.questions.length) * 100);
    const gaps = Object.entries(results).filter(([, v]) => v.level !== 'strong').sort((a, b) => a[1].score - b[1].score);
    const strengths = Object.entries(results).filter(([, v]) => v.level === 'strong');

    const getGradeLabel = (score) => {
      if (score >= 80) return { label: 'Excellent', color: '#10b981', emoji: '🏆' };
      if (score >= 60) return { label: 'Good', color: '#6366f1', emoji: '👍' };
      if (score >= 40) return { label: 'Developing', color: '#f59e0b', emoji: '📈' };
      return { label: 'Needs Work', color: '#ef4444', emoji: '🎯' };
    };
    const grade = getGradeLabel(totalScore);

    return (
      <div className="exam-page" style={{ overflowY: 'auto', padding: '2rem 1rem' }}>
        <div className="exam-result-card">
          {/* Score Hero */}
          <div className="exam-score-hero">
            <div className="exam-score-ring" style={{ '--score-color': grade.color }}>
              <svg viewBox="0 0 120 120" width="140" height="140">
                <circle cx="60" cy="60" r="52" fill="none" stroke="rgba(255,255,255,0.06)" strokeWidth="8"/>
                <circle
                  cx="60" cy="60" r="52" fill="none"
                  stroke={grade.color} strokeWidth="8"
                  strokeDasharray={`${2 * Math.PI * 52}`}
                  strokeDashoffset={`${2 * Math.PI * 52 * (1 - totalScore / 100)}`}
                  strokeLinecap="round"
                  style={{ transform: 'rotate(-90deg)', transformOrigin: '60px 60px', transition: 'stroke-dashoffset 1s ease' }}
                />
                <text x="60" y="58" textAnchor="middle" fill="white" fontSize="20" fontWeight="700" fontFamily="Outfit">{totalScore}%</text>
                <text x="60" y="74" textAnchor="middle" fill={grade.color} fontSize="10" fontFamily="Inter">{grade.emoji} {grade.label}</text>
              </svg>
            </div>
            <h1>{exam.title} — Completed!</h1>
            <p style={{ color: 'var(--text-secondary)' }}>You answered {totalCorrect} of {exam.questions.length} questions correctly. Here's your personalized gap analysis.</p>
          </div>

          {/* Skill Breakdown */}
          <div className="exam-result-section">
            <h3>📊 Skill-by-Skill Breakdown</h3>
            <div className="exam-skill-bars">
              {Object.entries(results).map(([skill, { score, level }]) => {
                const c = level === 'strong' ? '#10b981' : level === 'moderate' ? '#f59e0b' : '#ef4444';
                const lbl = level === 'strong' ? 'Strong' : level === 'moderate' ? 'Moderate' : 'Gap Detected';
                return (
                  <div key={skill} className="exam-skill-row">
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 6 }}>
                      <span style={{ fontWeight: 600, fontSize: '0.9rem' }}>{skill}</span>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                        <span style={{ fontSize: '0.75rem', background: `${c}20`, color: c, padding: '2px 8px', borderRadius: 50, fontWeight: 600 }}>{lbl}</span>
                        <span style={{ fontWeight: 700, color: c, fontSize: '0.9rem' }}>{score}%</span>
                      </div>
                    </div>
                    <div style={{ height: 8, background: 'rgba(255,255,255,0.06)', borderRadius: 4, overflow: 'hidden' }}>
                      <div style={{ height: '100%', width: `${score}%`, background: `linear-gradient(90deg, ${c}, ${c}99)`, borderRadius: 4, transition: 'width 1s ease' }} />
                    </div>
                  </div>
                );
              })}
            </div>
          </div>

          {/* Gaps */}
          {gaps.length > 0 && (
            <div className="exam-result-section">
              <h3>⚠️ Identified Knowledge Gaps</h3>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                {gaps.map(([skill, { gap, level }]) => (
                  <div key={skill} style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '0.85rem 1rem', background: 'rgba(239,68,68,0.06)', border: `1px solid ${level === 'critical' ? 'rgba(239,68,68,0.25)' : 'rgba(245,158,11,0.25)'}`, borderRadius: 10 }}>
                    <div style={{ display: 'flex', gap: '0.75rem', alignItems: 'center' }}>
                      <span style={{ fontSize: '1.2rem' }}>{level === 'critical' ? '🔴' : '🟡'}</span>
                      <div>
                        <div style={{ fontWeight: 600 }}>{skill}</div>
                        <div style={{ fontSize: '0.78rem', color: 'var(--text-muted)' }}>{level === 'critical' ? 'Critical gap — priority learning needed' : 'Moderate gap — improvement recommended'}</div>
                      </div>
                    </div>
                    <div style={{ textAlign: 'right' }}>
                      <div style={{ fontWeight: 700, color: level === 'critical' ? '#ef4444' : '#f59e0b', fontSize: '1.1rem' }}>{gap}%</div>
                      <div style={{ fontSize: '0.72rem', color: 'var(--text-muted)' }}>gap</div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Strengths */}
          {strengths.length > 0 && (
            <div className="exam-result-section">
              <h3>✅ Your Strengths</h3>
              <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap' }}>
                {strengths.map(([skill, { score }]) => (
                  <div key={skill} style={{ background: 'rgba(16,185,129,0.1)', border: '1px solid rgba(16,185,129,0.3)', borderRadius: 50, padding: '0.4rem 1rem', fontSize: '0.85rem', color: '#10b981', fontWeight: 600 }}>
                    ✓ {skill} ({score}%)
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* CTAs */}
          <div style={{ display: 'flex', gap: '1rem', flexWrap: 'wrap', justifyContent: 'center', marginTop: '1.5rem' }}>
            <button className="btn-primary" style={{ padding: '0.75rem 2rem', borderRadius: 10, fontSize: '1rem' }} onClick={() => navigate('/app/gap-analysis')}>
              📊 View Full Gap Analysis
            </button>
            <button className="btn-primary" style={{ padding: '0.75rem 2rem', borderRadius: 10, fontSize: '1rem', background: 'linear-gradient(135deg, #10b981, #059669)' }} onClick={() => navigate('/app/trainings')}>
              📚 Start Learning →
            </button>
            <button style={{ padding: '0.75rem 2rem', borderRadius: 10, background: 'transparent', border: '1px solid var(--glass-border)', color: 'var(--text-secondary)', cursor: 'pointer' }} onClick={() => navigate('/app')}>
              Go to Dashboard
            </button>
          </div>
        </div>
      </div>
    );
  }

  return null;
};

export default Exam;
