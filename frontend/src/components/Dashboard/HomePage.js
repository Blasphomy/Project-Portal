import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import Navbar from '../Layout/Navbar';
import DailyChallenges from './DailyChallenges';
import learningService from '../../services/learningService';
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
    const [topics, setTopics] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchDashboardData();
    }, []);

    const fetchDashboardData = async () => {
        try {
            setLoading(true);

            // Fetch topics
            const topicsData = await learningService.getTopics();
            setTopics(topicsData);

            // Simulate user stats (in production, fetch from backend)
            const simulatedStats = {
                totalXP: 1250,
                questsCompleted: 5,
                currentLevel: 3,
                streak: 7,
            };
            setStats(simulatedStats);

        } catch (err) {
            console.error('Error fetching dashboard data:', err);
        } finally {
            setLoading(false);
        }
    };

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

    if (loading) {
        return (
            <div>
                <Navbar />
                <div className="loading-container">
                    <div className="spinner"></div>
                    <p>Loading your dashboard...</p>
                </div>
            </div>
        );
    }

    return (
        <div className="home-page">
            <Navbar />

            <div className="home-container">
                {/* Welcome Banner */}
                <div className="welcome-banner">
                    <div className="welcome-content">
                        <h1>{getGreeting()}, {user?.fullName?.split(' ')[0] || user?.username}! 👋</h1>
                        <p>Ready to continue your learning journey?</p>
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
                        <div className="stat-icon">📚</div>
                        <div className="stat-info">
                            <h3>{topics.length}</h3>
                            <p>Topics Available</p>
                        </div>
                    </div>
                </div>

                {/* Level Progress */}
                <div className="progress-section">
                    <h2>Level Progress</h2>
                    <div className="level-progress-bar">
                        <div className="progress-info">
                            <span>Level {stats.currentLevel}</span>
                            <span>{stats.totalXP} XP</span>
                        </div>
                        <div className="progress-bar-container">
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

                {/* Daily Challenges */}
                <DailyChallenges />

                {/* Quick Actions */}
                <div className="quick-actions">
                    <h2>Quick Actions</h2>
                    <div className="action-cards">
                        <button
                            className="action-card"
                            onClick={() => navigate('/topics')}
                        >
                            <span className="action-icon">🎮</span>
                            <h3>Browse Topics</h3>
                            <p>Explore all learning paths</p>
                        </button>

                        <button
                            className="action-card"
                            onClick={() => navigate('/profile')}
                        >
                            <span className="action-icon">👤</span>
                            <h3>View Profile</h3>
                            <p>See your achievements</p>
                        </button>

                        <button
                            className="action-card"
                            onClick={() => {
                                // Get first topic and navigate to it
                                if (topics.length > 0) {
                                    navigate(`/topics/${topics[0].id}/quests`);
                                }
                            }}
                        >
                            <span className="action-icon">🚀</span>
                            <h3>Start Learning</h3>
                            <p>Jump into a quest</p>
                        </button>
                    </div>
                </div>

                {/* Recommended Topics */}
                <div className="recommended-section">
                    <h2>Recommended for You</h2>
                    <div className="topics-carousel">
                        {topics.slice(0, 3).map((topic) => (
                            <div
                                key={topic.id}
                                className="topic-recommendation-card"
                                onClick={() => navigate(`/topics/${topic.id}/quests`)}
                            >
                                <div className="topic-rec-icon">📚</div>
                                <h3>{topic.name}</h3>
                                <p>{topic.description}</p>
                                <button className="btn-start">Start →</button>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        </div>
    );
}

export default HomePage;
