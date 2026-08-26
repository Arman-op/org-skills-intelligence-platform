import React, { useState } from 'react';
import { forgotPassword } from '../services/platformApi';
import { useNavigate } from 'react-router-dom';

const ForgotPassword = () => {
  const [email, setEmail] = useState('');
  const [status, setStatus] = useState(null);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setStatus('sending');
    const res = await forgotPassword(email);
    if (res) {
      setStatus('sent');
      // for demo, we navigate to reset page with token in query
      if (res.token) {
        navigate(`/reset?token=${encodeURIComponent(res.token)}`);
      }
    } else {
      setStatus('error');
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-form-box">
        <h2>Forgot Password</h2>
        <p>Enter your email and we'll send a password reset link.</p>
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Email</label>
            <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
          </div>
          <button className="btn-primary" type="submit">Send Reset Link</button>
        </form>
        {status === 'sending' && <div>Sending…</div>}
        {status === 'sent' && <div>Reset link created. Check the browser redirect (demo).</div>}
        {status === 'error' && <div style={{ color: 'red' }}>Failed to create reset token.</div>}
      </div>
    </div>
  );
};

export default ForgotPassword;
