import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../Layout/Navbar';
import { useAuth } from '../../context/AuthContext';
import './LeaderboardPage.css';

function LeaderboardPage() {
    const { user } = useAuth();
    const navigate = useNavigate();
    const [timeframe, setTimeframe] = useState('all-time'); // all-time, weekly, monthly
    const [leaderboard, setLeaderboard] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchLeaderboard();
    }, [timeframe]);

    const fetchLeaderboard = async () => {
        try {
            setLoading(true);

            // Simulated leaderboard data (in production, fetch from backend)
            const mockLeaderboard = [
                { id: 1, username: 'CodeMaster', fullName: 'Alex Johnson', xp: 5420, level: 12, questsCompleted: 45, avatar: '🏆' },
                { id: 2, username: 'DevNinja', fullName: 'Sarah Chen', xp: 4850, level: 11, questsCompleted: 38, avatar: '🥷' },
                { id: 3, username: 'ByteWizard', fullName: 'Mike Davis', xp: 4200, level: 10, questsCompleted: 35, avatar: '🧙' },
                { id: 4, username: user?.username || 'You', fullName: user?.fullName || 'You', xp: 1250, level: 3, questsCompleted: 5, avatar: '👨‍💻', isCurrentUser: true },
                { id: 5, username: 'ReactPro', fullName: 'Emma Wilson', xp: 1100, level: 3, questsCompleted: 4, avatar: '⚛️' },
                { id: 6, username: 'JavaGuru', fullName: 'Tom Brown', xp: 980, level: 2, questsCompleted: 3, avatar: '☕' },
                { id: 7, username: 'PythonSnake', fullName: 'Lisa Garcia', xp: 750, level: 2, questsCompleted: 2, avatar: '🐍' },
                { id: 8, username: 'JSExplorer', fullName: 'Chris Lee', xp: 620, level: 1, questsCompleted: 2, avatar: '⚡' },
            ];

            setLeaderboard(mockLeaderboard);
        } catch (err) {
            console.error('Error fetching leaderboard:', err);
        } finally {
            setLoading(false);
        }
    };

    const getRankMedal = (index) => {
        if (index === 0) return '🥇';
        if (index === 1) return '🥈';
        if (index === 2) return '🥉';
        return `#${index + 1}`;
    };

    const getRankClass = (index) => {
        if (index === 0) return 'rank-gold';
        if (index === 1) return 'rank-silver';
        if (index === 2) return 'rank-bronze';
        return 'rank-normal';
    };

    if (loading) {
        return (
            <div>
                <Navbar />
                <div className="loading-container">
                    <div className="spinner"></div>
                    <p>Loading leaderboard...</p>
                </div>
            </div>
        );
    }

    return (
        <div className="leaderboard-page">
            <Navbar />

            <div className="leaderboard-container">
                <div className="leaderboard-header">
                    <h1>🏆 Leaderboard</h1>
                    <p>Compete with learners worldwide!</p>
                </div>

                {/* Timeframe Selector */}
                <div className="timeframe-selector">
                    <button
                        className={`timeframe-btn ${timeframe === 'all-time' ? 'active' : ''}`}
                        onClick={() => setTimeframe('all-time')}
                    >
                        All Time
                    </button>
                    <button
                        className={`timeframe-btn ${timeframe === 'monthly' ? 'active' : ''}`}
                        onClick={() => setTimeframe('monthly')}
                    >
                        This Month
                    </button>
                    <button
                        className={`timeframe-btn ${timeframe === 'weekly' ? 'active' : ''}`}
                        onClick={() => setTimeframe('weekly')}
                    >
                        This Week
                    </button>
                </div>

                {/* Top 3 Podium */}
                {leaderboard.length >= 3 && (
                    <div className="podium">
                        {/* 2nd Place */}
                        <div className="podium-place second">
                            <div className="podium-avatar">{leaderboard[1].avatar}</div>
                            <div className="podium-medal">🥈</div>
                            <h3>{leaderboard[1].fullName}</h3>
                            <p className="podium-username">@{leaderboard[1].username}</p>
                            <p className="podium-xp">{leaderboard[1].xp} XP</p>
                            <div className="podium-stand">2</div>
                        </div>

                        {/* 1st Place */}
                        <div className="podium-place first">
                            <div className="podium-avatar">{leaderboard[0].avatar}</div>
                            <div className="podium-medal">🥇</div>
                            <h3>{leaderboard[0].fullName}</h3>
                            <p className="podium-username">@{leaderboard[0].username}</p>
                            <p className="podium-xp">{leaderboard[0].xp} XP</p>
                            <div className="podium-stand">1</div>
                        </div>

                        {/* 3rd Place */}
                        <div className="podium-place third">
                            <div className="podium-avatar">{leaderboard[2].avatar}</div>
                            <div className="podium-medal">🥉</div>
                            <h3>{leaderboard[2].fullName}</h3>
                            <p className="podium-username">@{leaderboard[2].username}</p>
                            <p className="podium-xp">{leaderboard[2].xp} XP</p>
                            <div className="podium-stand">3</div>
                        </div>
                    </div>
                )}

                {/* Full Leaderboard Table */}
                <div className="leaderboard-table">
                    <h2>Full Rankings</h2>
                    <div className="table-header">
                        <span>Rank</span>
                        <span>User</span>
                        <span className="hide-mobile">Level</span>
                        <span>XP</span>
                        <span className="hide-mobile">Quests</span>
                    </div>

                    {leaderboard.map((entry, index) => (
                        <div
                            key={entry.id}
                            className={`table-row ${getRankClass(index)} ${entry.isCurrentUser ? 'current-user' : ''}`}
                        >
                            <span className="rank-cell">
                                <span className="rank-badge">{getRankMedal(index)}</span>
                            </span>
                            <span className="user-cell">
                                <span className="user-avatar">{entry.avatar}</span>
                                <div className="user-info">
                                    <strong>{entry.fullName}</strong>
                                    <small>@{entry.username}</small>
                                </div>
                            </span>
                            <span className="level-cell hide-mobile">
                                Level {entry.level}
                            </span>
                            <span className="xp-cell">
                                <strong>{entry.xp}</strong> XP
                            </span>
                            <span className="quests-cell hide-mobile">
                                {entry.questsCompleted} quests
                            </span>
                        </div>
                    ))}
                </div>

                {/* Call to Action */}
                <div className="leaderboard-cta">
                    <h3>Want to climb the ranks?</h3>
                    <p>Complete more quests and earn XP to reach the top!</p>
                    <button onClick={() => navigate('/topics')} className="btn-start-learning">
                        Start Learning
                    </button>
                </div>
            </div>
        </div>
    );
}

export default LeaderboardPage;
