import React from 'react';
import { Outlet, Link, useLocation, useNavigate } from 'react-router-dom';
import { clearSession, defaultPermissionsForRole, getStoredUser, roleFamily, apiFetch } from '../services/platformApi';

const icons = {
  dashboard: (
    <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
      <rect x="3" y="3" width="7" height="7" rx="1" stroke="currentColor" strokeWidth="2"/>
      <rect x="14" y="3" width="7" height="7" rx="1" stroke="currentColor" strokeWidth="2"/>
      <rect x="3" y="14" width="7" height="7" rx="1" stroke="currentColor" strokeWidth="2"/>
      <rect x="14" y="14" width="7" height="7" rx="1" stroke="currentColor" strokeWidth="2"/>
    </svg>
  ),
  skills: (
    <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
      <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
    </svg>
  ),
  analytics: (
    <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
      <line x1="18" y1="20" x2="18" y2="10" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
      <line x1="12" y1="20" x2="12" y2="4"  stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
      <line x1="6"  y1="20" x2="6"  y2="14" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
    </svg>
  ),
  trainings: (
    <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
      <path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z" stroke="currentColor" strokeWidth="2"/>
      <path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z" stroke="currentColor" strokeWidth="2"/>
    </svg>
  ),
  mentorship: (
    <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
      <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" stroke="currentColor" strokeWidth="2"/>
      <circle cx="9" cy="7" r="4" stroke="currentColor" strokeWidth="2"/>
      <path d="M23 21v-2a4 4 0 0 0-3-3.87" stroke="currentColor" strokeWidth="2"/>
      <path d="M16 3.13a4 4 0 0 1 0 7.75" stroke="currentColor" strokeWidth="2"/>
    </svg>
  ),
  profile: (
    <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
      <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4z" stroke="currentColor" strokeWidth="2"/>
      <path d="M4 20c0-4 4-6 8-6s8 2 8 6" stroke="currentColor" strokeWidth="2"/>
    </svg>
  ),
  aiPlan: (
    <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
      <path d="M12 3v4" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
      <path d="M16.24 7.76l-2.83 2.83" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
      <path d="M21 12h-4" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
      <path d="M16.24 16.24l-2.83-2.83" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
      <path d="M12 21v-4" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
      <path d="M7.76 16.24l2.83-2.83" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
      <path d="M3 12h4" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
      <path d="M7.76 7.76l2.83 2.83" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
    </svg>
  ),
  exam: (
    <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
      <path d="M9 5H7a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2h-2" stroke="currentColor" strokeWidth="2"/>
      <rect x="9" y="3" width="6" height="4" rx="2" stroke="currentColor" strokeWidth="2"/>
      <line x1="9" y1="12" x2="15" y2="12" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
      <line x1="9" y1="16" x2="12" y2="16" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
    </svg>
  ),
  gap: (
    <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
      <polygon points="12,2 22,20 2,20" stroke="currentColor" strokeWidth="2" strokeLinejoin="round"/>
      <line x1="12" y1="9" x2="12" y2="13" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
      <circle cx="12" cy="17" r="0.5" fill="currentColor" stroke="currentColor" strokeWidth="1.5"/>
    </svg>
  ),
  bell: (
    <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
      <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9" stroke="currentColor" strokeWidth="2"/>
      <path d="M13.73 21a2 2 0 0 1-3.46 0" stroke="currentColor" strokeWidth="2"/>
    </svg>
  ),
  settings: (
    <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
      <circle cx="12" cy="12" r="3" stroke="currentColor" strokeWidth="2"/>
      <path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-4 0v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83-2.83l.06-.06A1.65 1.65 0 0 0 4.68 15a1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 2.83-2.83l.06.06A1.65 1.65 0 0 0 9 4.68a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 2.83l-.06.06A1.65 1.65 0 0 0 19.4 9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1z" stroke="currentColor" strokeWidth="2"/>
    </svg>
  ),
  logout: (
    <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
      <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" stroke="currentColor" strokeWidth="2"/>
      <polyline points="16,17 21,12 16,7" stroke="currentColor" strokeWidth="2"/>
      <line x1="21" y1="12" x2="9" y2="12" stroke="currentColor" strokeWidth="2"/>
    </svg>
  ),
};

const Layout = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const user = getStoredUser();
  const role = user.role || user.accountType || 'Employee';
  const family = roleFamily(role);
  const permissions = new Set(user.permissions || defaultPermissionsForRole(role));
  const initials = (user.name || 'U').split(' ').map(n => n[0]).join('').toUpperCase();
  const isAdmin = family !== 'employee';
  const hasExamResults = !!user.examResults;

  const navItems = [
    { name: 'Dashboard',       path: '/app',               icon: icons.dashboard,  badge: null, show: true },
    { name: 'Profile',         path: '/app/profile',       icon: icons.profile,    badge: null, show: true },
    { name: 'AI Plan',         path: '/app/ai-plan',       icon: icons.aiPlan,     badge: null, show: true },
    { name: 'Skill Inventory', path: '/app/skills',        icon: icons.skills,     badge: null, show: true },
    { name: 'Gap Analytics',   path: '/app/analytics',     icon: icons.analytics,  badge: null, show: permissions.has('view_team_skill_coverage') || permissions.has('organization_gap_intelligence') || permissions.has('system_monitoring') },
    { name: 'Trainings',       path: '/app/trainings',     icon: icons.trainings,  badge: '3',  show: true },
    { name: 'Mentorship',      path: '/app/mentorship',    icon: icons.mentorship, badge: null, show: true },
    { name: 'My Assessment',   path: '/app/exam',          icon: icons.exam,       badge: null, show: family === 'employee' },
    { name: 'My Gap Analysis', path: '/app/gap-analysis',  icon: icons.gap,        badge: hasExamResults ? null : '!', show: family === 'employee' },
    { name: 'Admin Tracker',   path: '/app/admin',         icon: icons.settings,   badge: null, show: permissions.has('user_management') || permissions.has('role_management') || permissions.has('training_catalog_management') },
  ].filter(n => n.show);

  const currentPage = navItems.find(n =>
    n.path === location.pathname || (n.path !== '/app' && location.pathname.startsWith(n.path))
  )?.name || 'Dashboard';

  const handleLogout = () => {
    try {
      const email = getStoredUser().email;
      if (email) {
        apiFetch('/auth/logout', { method: 'POST', body: JSON.stringify({ email }) });
      }
    } catch (e) {
      // ignore
    }
    clearSession();
    navigate('/');
  };

  return (
    <div className="app-layout">
      {/* SIDEBAR */}
      <aside className="sidebar">
        <div className="sidebar-brand">
          <div className="sidebar-brand-icon">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
              <path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5" stroke="white" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
            </svg>
          </div>
          <div>
            <h2>KnowledgeIQ</h2>
            <span>Intelligence Platform</span>
          </div>
        </div>

        {/* Role badge */}
        <div style={{ margin: '0.75rem 1.25rem', padding: '0.5rem 0.85rem', borderRadius: 8, background: isAdmin ? 'rgba(236,72,153,0.1)' : 'rgba(99,102,241,0.1)', border: `1px solid ${isAdmin ? 'rgba(236,72,153,0.25)' : 'rgba(99,102,241,0.25)'}`, display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <span style={{ fontSize: '1rem' }}>{isAdmin ? '🛡️' : '👤'}</span>
          <div>
            <div style={{ fontSize: '0.72rem', fontWeight: 700, color: isAdmin ? '#ec4899' : '#a5b4fc' }}>{role}</div>
            {!isAdmin && user.targetRole && <div style={{ fontSize: '0.65rem', color: 'var(--text-muted)' }}>Targeting: {user.targetRole.replace('_', ' ')}</div>}
          </div>
        </div>

        <div className="sidebar-section-label">Main Menu</div>
        <nav className="sidebar-nav">
          {navItems.map((item) => {
            const isActive = location.pathname === item.path ||
              (item.path !== '/app' && location.pathname.startsWith(item.path));
            return (
              <Link
                key={item.path}
                to={item.path}
                className={`nav-item ${isActive ? 'active' : ''}`}
              >
                <span className="nav-icon">{item.icon}</span>
                <span>{item.name}</span>
                {item.badge && <span className="nav-badge" style={item.badge === '!' ? { background: '#ef4444' } : {}}>{item.badge}</span>}
              </Link>
            );
          })}
        </nav>

        <div className="sidebar-section-label">Account</div>
        <nav className="sidebar-nav">
          <button className="nav-item">
            <span className="nav-icon">{icons.settings}</span>
            <span>Settings</span>
          </button>
          <button className="nav-item" onClick={handleLogout} style={{ color: 'var(--danger)' }}>
            <span className="nav-icon">{icons.logout}</span>
            <span>Sign Out</span>
          </button>
        </nav>

        <div className="sidebar-footer">
          <div className="user-card">
            <div className="user-avatar">{initials}</div>
            <div>
              <div className="user-info-name">{user.name || 'User'}</div>
              <div className="user-info-role">{role}</div>
            </div>
          </div>
        </div>
      </aside>

      {/* MAIN */}
      <main className="main-content">
        <header className="top-header">
          <div className="header-left">
            <h3>{currentPage}</h3>
            <p>Welcome back, {(user.name || 'User').split(' ')[0]} 👋</p>
          </div>
          <div className="header-actions">
            <div className="header-search">
              <span className="header-search-icon">
                <svg width="15" height="15" viewBox="0 0 24 24" fill="none">
                  <circle cx="11" cy="11" r="8" stroke="currentColor" strokeWidth="2"/>
                  <path d="m21 21-4.35-4.35" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
                </svg>
              </span>
              <input type="text" placeholder="Search..." />
            </div>
            <div className="icon-btn notification-btn">
              {icons.bell}
              <span className="notif-dot" />
            </div>
            <div className="icon-btn">
              {icons.settings}
            </div>
            <div className="user-avatar" style={{ width: 38, height: 38, borderRadius: 10, cursor: 'pointer' }}>
              {initials}
            </div>
          </div>
        </header>

        <div className="page-body">
          <Outlet />
        </div>
      </main>
    </div>
  );
};

export default Layout;
