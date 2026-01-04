import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import Navbar from '../Layout/Navbar';
import './HomePage.css';

function HomePage() {
    const { user } = useAuth();
    const navigate = useNavigate();
    const [stats, setStats] = useState({
        totalXP: 0,
        questsCompleted: 0,
        currentLevel: 1,
        streak: 0,
    });

    useEffect(() => {
        // Get actual user data from localStorage (saved during login)
        if (user) {
            setStats({
                totalXP: user.totalXp || 0,
                questsCompleted: 0, // TODO: fetch from backend
                currentLevel: user.level || 1,
                streak: 0, // TODO: fetch from backend
            });
        }
    }, [user]);

    const getGreeting = () => {
        const hour = new Date().getHours();
        if (hour < 12) return 'Good Morning';
        if (hour < 18) return 'Good Afternoon';
        return 'Good Evening';
    };

    const calculateLevelProgress = () => {
        const baseXP = 500;
        const currentLevelXP = baseXP * stats.currentLevel;
        const progress = (stats.totalXP % currentLevelXP) / currentLevelXP * 100;
        return Math.min(progress, 100);
    };

    return (
        <div className="home-page">
            <Navbar />

            <div className="home-container">
                {/* Welcome Banner */}
                <div className="welcome-banner">
                    <div className="welcome-content">
                        <h1>{getGreeting()}, {user?.fullName?.split(' ')[0] || user?.username}! 👋</h1>
                        <p>Ready to embark on your personalized learning adventure?</p>
                    </div>
                    <div className="level-badge">
                        <div className="level-circle">
                            <span className="level-number">{stats.currentLevel}</span>
                        </div>
                        <p>Level {stats.currentLevel}</p>
                    </div>
                </div>

                {/* Stats Grid */}
                <div className="stats-grid">
                    <div className="stat-card">
                        <div className="stat-icon">⚡</div>
                        <div className="stat-info">
                            <h3>{stats.totalXP}</h3>
                            <p>Total XP</p>
                        </div>
                    </div>

                    <div className="stat-card">
                        <div className="stat-icon">🎯</div>
                        <div className="stat-info">
                            <h3>{stats.questsCompleted}</h3>
                            <p>Quests Completed</p>
                        </div>
                    </div>

                    <div className="stat-card">
                        <div className="stat-icon">🔥</div>
                        <div className="stat-info">
                            <h3>{stats.streak}</h3>
                            <p>Day Streak</p>
                        </div>
                    </div>

                    <div className="stat-card">
                        <div className="stat-icon">🌟</div>
                        <div className="stat-info">
                            <h3>{Math.round(calculateLevelProgress())}%</h3>
                            <p>To Next Level</p>
                        </div>
                    </div>
                </div>

                {/* Level Progress */}
                <div className="progress-section cosmic-panel">
                    <h2>Level Progress</h2>
                    <div className="level-progress-bar">
                        <div className="progress-info">
                            <span>Level {stats.currentLevel}</span>
                            <span>{stats.totalXP} XP</span>
                        </div>
                        <div className="progress-bar">
                            <div
                                className="progress-bar-fill"
                                style={{ width: `${calculateLevelProgress()}%` }}
                            ></div>
                        </div>
                        <p className="progress-text">
                            {Math.round(calculateLevelProgress())}% to Level {stats.currentLevel + 1}
                        </p>
                    </div>
                </div>

                {/* AI Game Master Call-to-Action */}
                <div className="ai-game-master-cta">
                    <div className="cta-content">
                        <div className="cta-icon">✨</div>
                        <h2>Your AI Game Master Awaits</h2>
                        <p>Tell me your goal, and I'll create a personalized quest map just for you</p>
                        <button
                            className="cosmic-button"
                            onClick={() => navigate('/custom-path')}
                        >
                            <span className="button-icon">🚀</span>
                            Generate My Quest Map
                        </button>
                    </div>
                </div>

                {/* Quick Actions */}
                <div className="quick-actions">
                    <h2>Quick Actions</h2>
                    <div className="action-cards">
                        <button
                            className="action-card cosmic-card"
                            onClick={() => navigate('/custom-path')}
                        >
                            <span className="action-icon">✨</span>
                            <h3>Create Custom Path</h3>
                            <p>AI-powered learning journey</p>
                        </button>

                        <button
                            className="action-card cosmic-card"
                            onClick={() => navigate('/profile')}
                        >
                            <span className="action-icon">👤</span>
                            <h3>View Profile</h3>
                            <p>See your achievements</p>
                        </button>

                        <button
                            className="action-card cosmic-card"
                            onClick={() => navigate('/skill-tree')}
                        >
                            <span className="action-icon">🗺️</span>
                            <h3>View Quest Map</h3>
                            <p>Continue your adventure</p>
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default HomePage;
