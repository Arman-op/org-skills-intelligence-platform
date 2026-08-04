import React from 'react';
import { Link } from 'react-router-dom';

const features = [
  {
    icon: '🧠',
    color: 'rgba(99,102,241,0.15)',
    title: 'AI-Powered Gap Detection',
    desc: 'Our intelligent engine scans your organization in real-time, identifying critical skill deficiencies before they impact deliverables.',
  },
  {
    icon: '📊',
    color: 'rgba(236,72,153,0.12)',
    title: 'Visual Analytics Dashboard',
    desc: 'Intuitive heatmaps, trend charts, and department breakdowns give leadership a 360° view of workforce readiness.',
  },
  {
    icon: '🎯',
    color: 'rgba(16,185,129,0.12)',
    title: 'Smart Training Paths',
    desc: 'Personalized learning journeys are auto-generated and matched to individual competency scores and business goals.',
  },
  {
    icon: '🤝',
    color: 'rgba(139,92,246,0.12)',
    title: 'Mentorship Network',
    desc: 'Connect high-potential employees with seasoned mentors across departments for accelerated skills transfer.',
  },
  {
    icon: '⚡',
    color: 'rgba(245,158,11,0.12)',
    title: 'Real-Time Alerts',
    desc: 'Instant notifications for emerging gaps, upcoming deadlines, and milestone achievements keep your team proactive.',
  },
  {
    icon: '🔒',
    color: 'rgba(239,68,68,0.12)',
    title: 'Enterprise Security',
    desc: 'Role-based access, SSO support, and end-to-end encryption ensure your workforce data is always protected.',
  },
];

const Landing = () => {
  return (
    <div style={{ background: 'var(--bg-primary)', minHeight: '100vh' }}>

      {/* NAVBAR */}
      <nav className="landing-navbar">
        <div className="nav-brand">
          <div className="nav-brand-icon">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
              <path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5" stroke="white" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
            </svg>
          </div>
          <span>Knowledge<span style={{color:'var(--accent)'}}>IQ</span></span>
        </div>

        <div className="nav-links">
          <a href="#features">Features</a>
          <a href="#stats">Impact</a>
          <a href="#cta">Pricing</a>
          <a href="#cta">About</a>
        </div>

        <div className="nav-auth">
          <Link to="/login">
            <button className="btn-ghost">Sign In</button>
          </Link>
          <Link to="/register">
            <button className="btn-primary">Get Started Free</button>
          </Link>
        </div>
      </nav>

      {/* HERO */}
      <section className="hero-section" id="hero">
        <div className="hero-orbs">
          <div className="orb orb-1" />
          <div className="orb orb-2" />
          <div className="orb orb-3" />
        </div>

        <div className="hero-content">
          <div className="hero-badge">
            <div className="badge-dot" />
            🚀 &nbsp;Now with AI-Powered Skill Intelligence
          </div>

          <h1 className="hero-title">
            Bridge Your Organization's&nbsp;
            <span className="gradient-text">Knowledge Gaps</span>
            &nbsp;Intelligently
          </h1>

          <p className="hero-sub">
            The all-in-one platform to identify critical skill gaps, personalize
            learning paths, and build a future-ready workforce — powered by AI.
          </p>

          <div className="hero-actions">
            <Link to="/register">
              <button className="btn-hero primary">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
                  <path d="M13 10V3L4 14h7v7l9-11h-7z" stroke="white" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                </svg>
                Start for Free
              </button>
            </Link>
            <Link to="/login">
              <button className="btn-hero secondary">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
                  <circle cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="2"/>
                  <polygon points="10,8 16,12 10,16 10,8" fill="currentColor"/>
                </svg>
                See Live Demo
              </button>
            </Link>
          </div>

          <div className="hero-stats" id="stats">
            {[
              { value: '3,450+', label: 'Employees Tracked' },
              { value: '142',    label: 'Skills Monitored' },
              { value: '94%',    label: 'Participation Rate' },
              { value: '18%',    label: 'Gap Reduction' },
            ].map((s, i) => (
              <div className="hero-stat" key={i}>
                <div className="hero-stat-value gradient-text-blue">{s.value}</div>
                <div className="hero-stat-label">{s.label}</div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* FEATURES */}
      <section className="section" id="features" style={{ background: 'linear-gradient(180deg, var(--bg-primary) 0%, var(--bg-secondary) 100%)' }}>
        <div className="section-header">
          <div className="section-label">Platform Features</div>
          <h2 className="section-title">
            Everything you need to&nbsp;
            <span className="gradient-text">close skill gaps</span>
          </h2>
          <p className="section-sub">
            From automated detection to guided learning, KnowledgeIQ gives HR teams
            and managers complete insight into workforce readiness.
          </p>
        </div>

        <div className="features-grid">
          {features.map((f, i) => (
            <div className="feature-card" key={i} style={{ animationDelay: `${i * 0.08}s` }}>
              <div className="feature-icon" style={{ background: f.color }}>
                <span role="img" aria-label={f.title}>{f.icon}</span>
              </div>
              <h3>{f.title}</h3>
              <p>{f.desc}</p>
            </div>
          ))}
        </div>
      </section>

      {/* CTA SECTION */}
      <section className="section" id="cta" style={{ textAlign: 'center', background: 'var(--bg-secondary)' }}>
        <div style={{
          background: 'linear-gradient(135deg, rgba(99,102,241,0.12), rgba(139,92,246,0.06))',
          border: '1px solid rgba(99,102,241,0.2)',
          borderRadius: 'var(--radius-xl)',
          padding: '5rem 2rem',
          position: 'relative',
          overflow: 'hidden',
        }}>
          <div className="orb" style={{ position:'absolute', width:'350px', height:'350px', top:'-100px', right:'-100px', background: 'radial-gradient(circle, rgba(236,72,153,0.1), transparent 70%)', animation:'orb-drift 10s infinite', filter:'blur(60px)' }} />
          <div className="section-label">Get Started Today</div>
          <h2 className="section-title" style={{ marginTop:'0.5rem' }}>
            Ready to transform your&nbsp;
            <span className="gradient-text">workforce future?</span>
          </h2>
          <p className="section-sub" style={{ margin: '1rem auto 2.5rem' }}>
            Join hundreds of organizations already using KnowledgeIQ to make smarter,
            data-driven talent decisions.
          </p>
          <div style={{ display: 'flex', gap: '1rem', justifyContent: 'center' }}>
            <Link to="/register">
              <button className="btn-hero primary">Create Free Account</button>
            </Link>
            <Link to="/login">
              <button className="btn-hero secondary">Request a Demo</button>
            </Link>
          </div>
        </div>
      </section>

      {/* FOOTER */}
      <footer style={{
        borderTop: '1px solid var(--glass-border)',
        padding: '2rem 5rem',
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        color: 'var(--text-muted)',
        fontSize: '0.85rem',
        background: 'var(--bg-primary)',
      }}>
        <div className="nav-brand" style={{ fontSize: '1rem' }}>
          <div className="nav-brand-icon" style={{ width: 28, height: 28 }}>
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none">
              <path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5" stroke="white" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round"/>
            </svg>
          </div>
          Knowledge<span style={{color:'var(--accent)'}}>IQ</span>
        </div>
        <span>© 2026 KnowledgeIQ – Built by Infosys Team</span>
        <div style={{ display: 'flex', gap: '1.5rem' }}>
          <a href="#" style={{ color: 'var(--text-muted)' }}>Privacy</a>
          <a href="#" style={{ color: 'var(--text-muted)' }}>Terms</a>
          <a href="#" style={{ color: 'var(--text-muted)' }}>Contact</a>
        </div>
      </footer>
    </div>
  );
};

export default Landing;
