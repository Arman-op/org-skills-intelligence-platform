import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Layout from './components/Layout';
import Landing from './pages/Landing';
import Login from './pages/Login';
import Register from './pages/Register';
import ForgotPassword from './pages/ForgotPassword';
import ResetPassword from './pages/ResetPassword';
import Dashboard from './pages/Dashboard';
import Profile from './pages/Profile';
import AIPlan from './pages/AIPlan';
import Skills from './pages/Skills';
import Analytics from './pages/Analytics';
import Trainings from './pages/Trainings';
import Mentorship from './pages/Mentorship';
import Exam from './pages/Exam';
import GapAnalysis from './pages/GapAnalysis';
import Admin from './components/Admin.jsx';
import { getStoredUser, roleFamily } from './services/platformApi';

// Simple auth check
const isAuthenticated = () => localStorage.getItem('user') !== null || localStorage.getItem('token') !== null;

const getCurrentRoleFamily = () => roleFamily(getStoredUser().role || getStoredUser().accountType || 'Employee');

const ProtectedRoute = ({ children }) => {
  return isAuthenticated() ? children : <Navigate to="/login" replace />;
};

const RoleProtectedRoute = ({ allowedFamilies, children }) => {
  const family = getCurrentRoleFamily();
  return isAuthenticated() && allowedFamilies.includes(family) ? children : <Navigate to="/app" replace />;
};

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Public Routes */}
        <Route path="/" element={<Landing />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/forgot" element={<ForgotPassword />} />
        <Route path="/reset" element={<ResetPassword />} />
        <Route path="/admin" element={
          <ProtectedRoute>
            <Navigate to="/app/admin" replace />
          </ProtectedRoute>
        } />

        {/* Protected App Routes */}
        <Route path="/app" element={
          <ProtectedRoute>
            <Layout />
          </ProtectedRoute>
        }>
          <Route index element={<Dashboard />} />
          <Route path="profile" element={<Profile />} />
          <Route path="ai-plan" element={<AIPlan />} />
          <Route path="skills" element={<Skills />} />
          <Route path="analytics" element={<RoleProtectedRoute allowedFamilies={['manager', 'hr', 'learning', 'system']}><Analytics /></RoleProtectedRoute>} />
          <Route path="trainings" element={<Trainings />} />
          <Route path="mentorship" element={<Mentorship />} />
          <Route path="exam" element={<Exam />} />
          <Route path="gap-analysis" element={<GapAnalysis />} />
          <Route path="admin" element={<RoleProtectedRoute allowedFamilies={['manager', 'hr', 'learning', 'system']}><Admin /></RoleProtectedRoute>} />
        </Route>

        {/* Fallback */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
