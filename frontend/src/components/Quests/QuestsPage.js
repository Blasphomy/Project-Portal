import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Navbar from '../Layout/Navbar';
import learningService from '../../services/learningService';
import './QuestsPage.css';

function QuestsPage() {
    const { topicId } = useParams();
    const navigate = useNavigate();
    const [quests, setQuests] = useState([]);
    const [topic, setTopic] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        fetchQuests();
    }, [topicId]);

    const fetchQuests = async () => {
        try {
            setLoading(true);
            const data = await learningService.getQuestsByTopic(topicId);
            setQuests(data);

            // Get topic name from first quest or use topicId
            if (data.length > 0) {
                setTopic({ id: topicId, name: getTopicName(topicId) });
            }

            setError(null);
        } catch (err) {
            setError('Failed to load quests. Please try again.');
            console.error('Error fetching quests:', err);
        } finally {
            setLoading(false);
        }
    };

    const getTopicName = (id) => {
        const names = {
            'topic-java-101': 'Java 101',
            'topic-python': 'Python Basics',
            'topic-javascript': 'JavaScript Fundamentals',
        };
        return names[id] || id;
    };

    const handleQuestClick = (questId) => {
        navigate(`/quests/${questId}`);
    };

    const handleBackClick = () => {
        navigate('/topics');
    };

    if (loading) {
        return (
            <div>
                <Navbar />
                <div className="loading-container">
                    <div className="spinner"></div>
                    <p>Loading quests...</p>
                </div>
            </div>
        );
    }

    if (error) {
        return (
            <div>
                <Navbar />
                <div className="error-container">
                    <p className="error-text">{error}</p>
                    <button onClick={fetchQuests} className="btn-retry">Try Again</button>
                    <button onClick={handleBackClick} className="btn-back">Back to Topics</button>
                </div>
            </div>
        );
    }

    return (
        <div className="quests-page">
            <Navbar />

            <div className="quests-container">
                <div className="quests-header">
                    <button onClick={handleBackClick} className="back-button">
                        ← Back to Topics
                    </button>
                    <h1>{topic?.name} Quests 🎮</h1>
                    <p>Choose a quest to begin your learning adventure</p>
                </div>

                <div className="quests-list">
                    {quests.map((quest, index) => (
                        <div
                            key={quest.id}
                            className="quest-card"
                            onClick={() => handleQuestClick(quest.id)}
                        >
                            <div className="quest-number">{index + 1}</div>
                            <div className="quest-content">
                                <h3 className="quest-name">{quest.name}</h3>
                                <p className="quest-description">{quest.description}</p>
                                <div className="quest-meta">
                                    <span className="quest-difficulty">
                                        {getDifficultyIcon(quest.difficulty)} {quest.difficulty}
                                    </span>
                                    <span className="quest-time">⏱️ {quest.estimatedMinutes || 30} min</span>
                                </div>
                            </div>
                            <div className="quest-arrow">→</div>
                        </div>
                    ))}
                </div>

                {quests.length === 0 && (
                    <div className="no-quests">
                        <p>No quests available for this topic yet.</p>
                        <button onClick={handleBackClick} className="btn-back">
                            Back to Topics
                        </button>
                    </div>
                )}
            </div>
        </div>
    );
}

function getDifficultyIcon(difficulty) {
    const icons = {
        'beginner': '🟢',
        'intermediate': '🟡',
        'advanced': '🔴',
    };
    return icons[difficulty?.toLowerCase()] || '⚪';
}

export default QuestsPage;
