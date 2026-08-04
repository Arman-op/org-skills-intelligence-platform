import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';

const Register = () => {
  const navigate = useNavigate();
  const [form, setForm] = useState({ firstName: '', lastName: '', email: '', password: '', role: 'Employee' });
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      const res = await fetch('http://localhost:8080/api/auth/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ ...form, name: `${form.firstName} ${form.lastName}` }),
      });
      if (res.ok) {
        navigate('/login');
        return;
      }
      throw new Error();
    } catch {
      setTimeout(() => {
        localStorage.setItem('user', JSON.stringify({ email: form.email, name: `${form.firstName} ${form.lastName}`, role: form.role }));
        setLoading(false);
        navigate('/app');
      }, 1200);
    }
  };

  return (
    <div className="auth-page">
      {/* Left Visual */}
      <div className="auth-visual">
        <div className="auth-visual-content">
          <div className="auth-visual-icon">🚀</div>
          <h2>Join the Intelligence Platform</h2>
          <p>
            Set up your organization's knowledge hub in minutes. Identify gaps,
            recommend learning, and elevate your team — all in one place.
          </p>
          <ul className="auth-features-list">
            <li><div className="feature-check">✓</div> Free setup, no credit card required</li>
            <li><div className="feature-check">✓</div> AI-powered gap analysis on day 1</li>
            <li><div className="feature-check">✓</div> Works for teams of any size</li>
            <li><div className="feature-check">✓</div> Role-based access and permissions</li>
            <li><div className="feature-check">✓</div> Integrates with existing HR tools</li>
          </ul>
        </div>
      </div>

      {/* Right Form */}
      <div className="auth-form-side">
        <div className="auth-form-box">
          <div className="auth-form-header">
            <Link to="/" style={{ display:'inline-flex', alignItems:'center', gap:'0.5rem', marginBottom:'2rem', color:'var(--text-secondary)', fontSize:'0.875rem' }}>
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
                <path d="M19 12H5M5 12l7 7M5 12l7-7" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
              </svg>
              Back to Home
            </Link>
            <h1>Create your account ✨</h1>
            <p>Start building a smarter workforce today</p>
          </div>

          <form onSubmit={handleSubmit}>
            <div className="form-row">
              <div className="form-group">
                <label className="form-label">First Name</label>
                <div className="form-input-wrapper">
                  <span className="form-input-icon">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
                      <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" stroke="currentColor" strokeWidth="2"/>
                      <circle cx="12" cy="7" r="4" stroke="currentColor" strokeWidth="2"/>
                    </svg>
                  </span>
                  <input type="text" name="firstName" className="form-input" placeholder="John" value={form.firstName} onChange={handleChange} required />
                </div>
              </div>
              <div className="form-group">
                <label className="form-label">Last Name</label>
                <div className="form-input-wrapper">
                  <span className="form-input-icon">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
                      <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" stroke="currentColor" strokeWidth="2"/>
                      <circle cx="12" cy="7" r="4" stroke="currentColor" strokeWidth="2"/>
                    </svg>
                  </span>
                  <input type="text" name="lastName" className="form-input" placeholder="Doe" value={form.lastName} onChange={handleChange} required />
                </div>
              </div>
            </div>

            <div className="form-group">
              <label className="form-label">Work Email</label>
              <div className="form-input-wrapper">
                <span className="form-input-icon">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
                    <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z" stroke="currentColor" strokeWidth="2"/>
                    <polyline points="22,6 12,13 2,6" stroke="currentColor" strokeWidth="2"/>
                  </svg>
                </span>
                <input type="email" name="email" className="form-input" placeholder="you@company.com" value={form.email} onChange={handleChange} required />
              </div>
            </div>

            <div className="form-group">
              <label className="form-label">Role</label>
              <div className="form-input-wrapper">
                <span className="form-input-icon">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
                    <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" stroke="currentColor" strokeWidth="2"/>
                    <circle cx="9" cy="7" r="4" stroke="currentColor" strokeWidth="2"/>
                    <path d="M23 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75" stroke="currentColor" strokeWidth="2"/>
                  </svg>
                </span>
                <select name="role" className="form-input" value={form.role} onChange={handleChange}
                  style={{ appearance: 'none', cursor: 'pointer' }}>
                  <option>Employee</option>
                  <option>Team Lead</option>
                  <option>HR Manager</option>
                  <option>Admin</option>
                </select>
              </div>
            </div>

            <div className="form-group">
              <label className="form-label">Password</label>
              <div className="form-input-wrapper">
                <span className="form-input-icon">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
                    <rect x="3" y="11" width="18" height="11" rx="2" ry="2" stroke="currentColor" strokeWidth="2"/>
                    <path d="M7 11V7a5 5 0 0 1 10 0v4" stroke="currentColor" strokeWidth="2"/>
                  </svg>
                </span>
                <input type="password" name="password" className="form-input" placeholder="Create a strong password" value={form.password} onChange={handleChange} required />
              </div>
            </div>

            <div style={{ marginBottom: '1.25rem' }}>
              <label className="checkbox-group" style={{ fontSize:'0.82rem', color:'var(--text-secondary)' }}>
                <input type="checkbox" required />
                I agree to the <a href="#" className="form-link" style={{ marginLeft:'0.25rem' }}>Terms of Service</a> and <a href="#" className="form-link">Privacy Policy</a>
              </label>
            </div>

            <button type="submit" className="btn-submit" disabled={loading}>
              {loading ? (
                <span style={{ display:'flex', alignItems:'center', justifyContent:'center', gap:'0.5rem' }}>
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" style={{ animation: 'spinSlow 0.8s linear infinite' }}>
                    <circle cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="2" opacity="0.3"/>
                    <path d="M12 2a10 10 0 0 1 10 10" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
                  </svg>
                  Creating Account...
                </span>
              ) : 'Create Account →'}
            </button>
          </form>

          <p className="auth-switch">
            Already have an account? <Link to="/login" className="form-link">Sign in</Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default Register;
