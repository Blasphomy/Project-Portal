import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../Layout/Navbar';
import learningService from '../../services/learningService';
import './TopicsPage.css';

function TopicsPage() {
    const [topics, setTopics] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        fetchTopics();
    }, []);

    const fetchTopics = async () => {
        try {
            setLoading(true);
            const data = await learningService.getTopics();
            setTopics(data);
            setError(null);
        } catch (err) {
            setError('Failed to load topics. Please try again.');
            console.error('Error fetching topics:', err);
        } finally {
            setLoading(false);
        }
    };

    const handleTopicClick = (topicId) => {
        navigate(`/topics/${topicId}/quests`);
    };

    if (loading) {
        return (
            <div>
                <Navbar />
                <div className="loading-container">
                    <div className="spinner"></div>
                    <p>Loading topics...</p>
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
                    <button onClick={fetchTopics} className="btn-retry">Try Again</button>
                </div>
            </div>
        );
    }

    return (
        <div className="topics-page">
            <Navbar />

            <div className="topics-container">
                <div className="topics-header">
                    <h1>Choose Your Learning Path 🎯</h1>
                    <p>Select a topic to start your coding journey</p>
                </div>

                <div className="topics-grid">
                    {topics.map((topic) => (
                        <div
                            key={topic.id}
                            className="topic-card"
                            onClick={() => handleTopicClick(topic.id)}
                        >
                            <div className="topic-icon">{getTopicIcon(topic.id)}</div>
                            <h3 className="topic-name">{topic.name}</h3>
                            <p className="topic-description">{topic.description}</p>
                            <div className="topic-footer">
                                <span className="topic-difficulty">{topic.difficulty || 'All Levels'}</span>
                                <span className="topic-arrow">→</span>
                            </div>
                        </div>
                    ))}
                </div>

                {topics.length === 0 && (
                    <div className="no-topics">
                        <p>No topics available yet. Check back soon!</p>
                    </div>
                )}
            </div>
        </div>
    );
}

// Helper function to get emoji icons for topics
function getTopicIcon(topicId) {
    const icons = {
        'topic-java-101': '☕',
        'topic-python': '🐍',
        'topic-javascript': '⚡',
        'topic-react': '⚛️',
        'topic-algorithms': '🧮',
        'topic-databases': '🗄️',
    };
    return icons[topicId] || '📚';
}

export default TopicsPage;
