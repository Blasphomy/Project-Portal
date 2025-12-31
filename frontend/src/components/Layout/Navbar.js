import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import './Navbar.css';

function Navbar() {
    const { user, logout } = useAuth();
    const navigate = useNavigate();

    // Simulated stats (in production, fetch from backend)
    const stats = {
        totalXP: 1250,
        currentLevel: 3,
    };

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <nav className="navbar">
            <div className="navbar-content">
                <div className="navbar-brand" onClick={() => navigate('/dashboard')} style={{ cursor: 'pointer' }}>
                    <h2>🚀 Learning Platform</h2>
                </div>

                <div className="navbar-actions">
                    <div className="navbar-level">
                        <span className="level-badge">⭐ Level {stats.currentLevel}</span>
                        <span className="xp-text">{stats.totalXP} XP</span>
                    </div>
                    <button onClick={() => navigate('/custom-path')} className="btn-custom-path">
                        Create Path
                    </button>
                    <button onClick={() => navigate('/leaderboard')} className="btn-leaderboard">
                        Leaderboard
                    </button>
                    <button onClick={() => navigate('/profile')} className="btn-profile">
                        Profile
                    </button>
                    <span className="user-name">👋 {user?.fullName || user?.username}</span>
                    <button onClick={handleLogout} className="btn-logout">
                        Logout
                    </button>
                </div>
            </div>
        </nav>
    );
}

export default Navbar;
