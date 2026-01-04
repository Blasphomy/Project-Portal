import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import LoginPage from './components/Auth/LoginPage';
import RegisterPage from './components/Auth/RegisterPage';
import HomePage from './components/Dashboard/HomePage';
import ProfilePage from './components/Profile/ProfilePage';
import CustomPathPage from './components/CustomPath/CustomPathPage';
import SkillTreePage from './components/SkillTree/SkillTreePage';
import AiAssistant from './components/AI/AiAssistant';
import './App.css';

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="App">
          <Routes>
            {/* Public routes */}
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />

            {/* Protected routes - AI Game Master */}
            <Route
              path="/dashboard"
              element={
                <ProtectedRoute>
                  <HomePage />
                </ProtectedRoute>
              }
            />

            <Route
              path="/profile"
              element={
                <ProtectedRoute>
                  <ProfilePage />
                </ProtectedRoute>
              }
            />

            <Route
              path="/custom-path"
              element={
                <ProtectedRoute>
                  <CustomPathPage />
                </ProtectedRoute>
              }
            />

            <Route
              path="/skill-tree"
              element={
                <ProtectedRoute>
                  <SkillTreePage />
                </ProtectedRoute>
              }
            />

            {/* Default redirect */}
            <Route path="/" element={<Navigate to="/dashboard" replace />} />
            <Route path="*" element={<Navigate to="/dashboard" replace />} />
          </Routes>

          {/* AI Assistant - available on all pages */}
          <AiAssistant />
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;
