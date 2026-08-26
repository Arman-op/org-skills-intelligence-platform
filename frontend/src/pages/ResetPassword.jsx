import React, { useState, useEffect } from 'react';
import { resetPassword } from '../services/platformApi';
import { useNavigate, useLocation } from 'react-router-dom';

function useQuery() {
  return new URLSearchParams(useLocation().search);
}

const ResetPassword = () => {
  const query = useQuery();
  const tokenFromQuery = query.get('token') || '';
  const [token, setToken] = useState(tokenFromQuery);
  const [password, setPassword] = useState('');
  const [status, setStatus] = useState(null);
  const navigate = useNavigate();

  useEffect(() => { if (tokenFromQuery) setToken(tokenFromQuery); }, [tokenFromQuery]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setStatus('saving');
    const res = await resetPassword(token, password);
    if (res) {
      setStatus('done');
      setTimeout(() => navigate('/login'), 1200);
    } else {
      setStatus('error');
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-form-box">
        <h2>Reset Password</h2>
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Token</label>
            <input value={token} onChange={(e) => setToken(e.target.value)} required />
          </div>
          <div className="form-group">
            <label>New Password</label>
            <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
          </div>
          <button className="btn-primary" type="submit">Reset Password</button>
        </form>
        {status === 'saving' && <div>Saving…</div>}
        {status === 'done' && <div>Password updated. Redirecting to login…</div>}
        {status === 'error' && <div style={{ color: 'red' }}>Failed to reset password.</div>}
      </div>
    </div>
  );
};

export default ResetPassword;
