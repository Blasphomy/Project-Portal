import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import Navbar from '../Layout/Navbar';
import learningService from '../../services/learningService';
import './ProfilePage.css';

function ProfilePage() {
    const { user } = useAuth();
    const navigate = useNavigate();
    const [stats, setStats] = useState({
        questsCompleted: 5,
        tasksCompleted: 15,
        totalXP: 1250,
        currentLevel: 3,
        streak: 7,
        studyTime: '12h 30m',
    });
    const [completedQuests, setCompletedQuests] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchProfileData();
    }, []);

    const fetchProfileData = async () => {
        try {
            setLoading(true);

            // Simulate completed quests data
            const mockCompletedQuests = [
                {
                    id: 'quest-1',
                    name: 'Variables and Data Types',
                    topic: 'Java 101',
                    completedAt: '2024-12-28',
                    xpEarned: 250,
                },
                {
                    id: 'quest-2',
                    name: 'Control Flow Basics',
                    topic: 'Java 101',
                    completedAt: '2024-12-29',
                    xpEarned: 300,
                },
                {
                    id: 'quest-3',
                    name: 'Loop Mastery',
                    topic: 'Java 101',
                    completedAt: '2024-12-30',
                    xpEarned: 350,
                },
            ];

            setCompletedQuests(mockCompletedQuests);

        } catch (err) {
            console.error('Error fetching profile data:', err);
        } finally {
            setLoading(false);
        }
    };

    const getJoinDate = () => {
        // Simulate join date
        return 'December 2024';
    };

    const getAvatar = () => {
        const avatars = ['👨‍💻', '👩‍💻', '🧑‍💻', '🦸', '🧙', '🥷'];
        return avatars[Math.floor(Math.random() * avatars.length)];
    };

    if (loading) {
        return (
            <div>
                <Navbar />
                <div className="loading-container">
                    <div className="spinner"></div>
                    <p>Loading profile...</p>
                </div>
            </div>
        );
    }

    return (
        <div className="profile-page">
            <Navbar />

            <div className="profile-container">
                {/* Profile Header */}
                <div className="profile-header">
                    <div className="profile-avatar-section">
                        <div className="profile-avatar">{getAvatar()}</div>
                        <div className="profile-info">
                            <h1>{user?.fullName || user?.username}</h1>
                            <p className="username">@{user?.username}</p>
                            <p className="join-date">Joined {getJoinDate()}</p>
                        </div>
                    </div>

                    <div className="profile-level">
                        <div className="level-circle-large">
                            <span className="level-number">{stats.currentLevel}</span>
                        </div>
                        <p className="level-text">Level {stats.currentLevel}</p>
                        <p className="xp-text">{stats.totalXP} XP</p>
                    </div>
                </div>

                {/* Stats Grid */}
                <div className="profile-stats-section">
                    <h2>Your Statistics</h2>
                    <div className="profile-stats-grid">
                        <div className="profile-stat-card">
                            <div className="stat-icon">🎯</div>
                            <div className="stat-content">
                                <h3>{stats.questsCompleted}</h3>
                                <p>Quests Completed</p>
                            </div>
                        </div>

                        <div className="profile-stat-card">
                            <div className="stat-icon">✅</div>
                            <div className="stat-content">
                                <h3>{stats.tasksCompleted}</h3>
                                <p>Tasks Completed</p>
                            </div>
                        </div>

                        <div className="profile-stat-card">
                            <div className="stat-icon">⚡</div>
                            <div className="stat-content">
                                <h3>{stats.totalXP}</h3>
                                <p>Total XP</p>
                            </div>
                        </div>

                        <div className="profile-stat-card">
                            <div className="stat-icon">🔥</div>
                            <div className="stat-content">
                                <h3>{stats.streak} Days</h3>
                                <p>Current Streak</p>
                            </div>
                        </div>

                        <div className="profile-stat-card">
                            <div className="stat-icon">⏱️</div>
                            <div className="stat-content">
                                <h3>{stats.studyTime}</h3>
                                <p>Study Time</p>
                            </div>
                        </div>

                        <div className="profile-stat-card">
                            <div className="stat-icon">📊</div>
                            <div className="stat-content">
                                <h3>Level {stats.currentLevel}</h3>
                                <p>Current Rank</p>
                            </div>
                        </div>
                    </div>
                </div>

                {/* Achievements */}
                <div className="achievements-section">
                    <h2>Achievements 🏆</h2>
                    <div className="achievements-grid">
                        <div className="achievement-badge earned">
                            <span className="badge-icon">🌟</span>
                            <p>First Quest</p>
                        </div>
                        <div className="achievement-badge earned">
                            <span className="badge-icon">🔥</span>
                            <p>7 Day Streak</p>
                        </div>
                        <div className="achievement-badge earned">
                            <span className="badge-icon">⚡</span>
                            <p>1000 XP</p>
                        </div>
                        <div className="achievement-badge locked">
                            <span className="badge-icon">💎</span>
                            <p>Legendary</p>
                        </div>
                        <div className="achievement-badge locked">
                            <span className="badge-icon">🎖️</span>
                            <p>Master</p>
                        </div>
                        <div className="achievement-badge locked">
                            <span className="badge-icon">👑</span>
                            <p>Champion</p>
                        </div>
                    </div>
                </div>

                {/* Completed Quests */}
                <div className="completed-quests-section">
                    <h2>Completed Quests ({completedQuests.length})</h2>
                    <div className="quests-list">
                        {completedQuests.map((quest) => (
                            <div key={quest.id} className="completed-quest-card">
                                <div className="quest-info">
                                    <h3>{quest.name}</h3>
                                    <p className="quest-topic">{quest.topic}</p>
                                </div>
                                <div className="quest-meta">
                                    <span className="xp-badge">+{quest.xpEarned} XP</span>
                                    <span className="completion-date">{quest.completedAt}</span>
                                </div>
                            </div>
                        ))}
                    </div>

                    {completedQuests.length === 0 && (
                        <div className="no-quests">
                            <p>No completed quests yet. Start your learning journey!</p>
                            <button
                                className="btn-start-learning"
                                onClick={() => navigate('/topics')}
                            >
                                Browse Topics
                            </button>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}

export default ProfilePage;
