import React from 'react';
import { useNavigate } from 'react-router-dom';
import './DailyChallenges.css';

function DailyChallenges() {
    const navigate = useNavigate();

    // Simulated daily challenges
    const challenges = [
        {
            id: 1,
            title: 'Code Sprint',
            description: 'Complete 3 tasks in a row',
            progress: 2,
            target: 3,
            reward: 150,
            icon: '⚡',
            completed: false,
        },
        {
            id: 2,
            title: 'Quick Learner',
            description: 'Finish a quest in under 10 minutes',
            progress: 0,
            target: 1,
            reward: 200,
            icon: '🏃‍♂️',
            completed: false,
        },
        {
            id: 3,
            title: 'Perfect Score',
            description: 'Submit code with no errors',
            progress: 1,
            target: 1,
            reward: 100,
            icon: '✨',
            completed: true,
        },
    ];

    const getProgressPercentage = (progress, target) => {
        return Math.min((progress / target) * 100, 100);
    };

    return (
        <div className="daily-challenges">
            <div className="challenges-header">
                <h2>🎯 Daily Challenges</h2>
                <span className="reset-timer">Resets in 8h 24m</span>
            </div>

            <div className="challenges-list">
                {challenges.map((challenge) => (
                    <div
                        key={challenge.id}
                        className={`challenge-card ${challenge.completed ? 'completed' : ''}`}
                    >
                        <div className="challenge-icon">{challenge.icon}</div>
                        <div className="challenge-content">
                            <h3>{challenge.title}</h3>
                            <p>{challenge.description}</p>

                            <div className="challenge-progress">
                                <div className="progress-bar-small">
                                    <div
                                        className="progress-fill-small"
                                        style={{ width: `${getProgressPercentage(challenge.progress, challenge.target)}%` }}
                                    ></div>
                                </div>
                                <span className="progress-text-small">
                                    {challenge.progress}/{challenge.target}
                                </span>
                            </div>
                        </div>

                        <div className="challenge-reward">
                            {challenge.completed ? (
                                <span className="completed-badge">✓ Done</span>
                            ) : (
                                <>
                                    <span className="reward-amount">+{challenge.reward}</span>
                                    <span className="reward-label">XP</span>
                                </>
                            )}
                        </div>
                    </div>
                ))}
            </div>

            <button
                className="view-all-challenges"
                onClick={() => navigate('/topics')}
            >
                Start Challenges →
            </button>
        </div>
    );
}

export default DailyChallenges;
