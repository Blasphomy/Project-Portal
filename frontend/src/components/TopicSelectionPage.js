import React, { useState, useEffect } from 'react';
import './TopicSelectionPage.css';

const TopicSelectionPage = ({ onSelectTopic }) => {
  const [topics, setTopics] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    fetchTopics();
  }, []);

  const fetchTopics = async () => {
    try {
      const response = await fetch('/api/topics');
      if (!response.ok) {
        throw new Error('Failed to fetch topics');
      }
      const data = await response.json();
      setTopics(data);
      setError(null);
    } catch (error) {
      console.error('Error fetching topics:', error);
      setError(error.message);
    } finally {
      setLoading(false);
    }
  };

  const filteredTopics = topics.filter(topic =>
    topic.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    (topic.description && topic.description.toLowerCase().includes(searchTerm.toLowerCase()))
  );

  const getDifficultyColor = (difficulty) => {
    switch (difficulty?.toLowerCase()) {
      case 'beginner': return '#4ade80';
      case 'intermediate': return '#fb923c';
      case 'advanced': return '#ef4444';
      default: return '#6366f1';
    }
  };

  const getCategoryIcon = (category) => {
    switch (category?.toLowerCase()) {
      case 'programming': return '💻';
      case 'database': return '🗄️';
      case 'devops': return '⚙️';
      case 'tools': return '🔧';
      default: return '📚';
    }
  };

  if (loading) {
    return (
      <div className="topic-selection-page">
        <div className="page-header">
          <h1 className="page-title text-gradient">Choose Your Learning Path</h1>
          <p className="page-subtitle">Master programming from fundamentals to advanced concepts</p>
        </div>
        <div className="topics-grid">
          {[1, 2, 3].map(i => (
            <div key={i} className="skeleton topic-card-skeleton"></div>
          ))}
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="topic-selection-page">
        <div className="glass-card error-container">
          <h2>⚠️ Failed to load topics</h2>
          <p>{error}</p>
          <button className="btn btn-primary" onClick={fetchTopics}>
            Retry
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="topic-selection-page">
      <div className="page-header">
        <h1 className="page-title text-gradient">Choose Your Learning Path</h1>
        <p className="page-subtitle">Master programming from fundamentals to advanced concepts</p>

        {/* Search Bar */}
        <div className="search-container">
          <div className="search-input-wrapper">
            <span className="search-icon">🔍</span>
            <input
              type="text"
              className="search-input"
              placeholder="Search topics..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
            {searchTerm && (
              <button
                className="clear-search"
                onClick={() => setSearchTerm('')}
              >
                ✕
              </button>
            )}
          </div>
        </div>
      </div>

      {/* Topics Grid */}
      <div className="topics-grid">
        {filteredTopics.length === 0 ? (
          <div className="no-results">
            <h3>No topics found</h3>
            <p>Try adjusting your search terms</p>
          </div>
        ) : (
          filteredTopics.map((topic) => (
            <div
              key={topic.id}
              className="topic-card glass-card"
              onClick={() => onSelectTopic(topic.id)}
              style={{
                '--difficulty-color': getDifficultyColor(topic.difficultyLevel)
              }}
            >
              {/* Topic Icon */}
              <div className="topic-icon">
                {topic.iconUrl ? (
                  <img src={topic.iconUrl} alt={topic.name} />
                ) : (
                  <span className="default-icon">
                    {getCategoryIcon(topic.category)}
                  </span>
                )}
              </div>

              {/* Topic Content */}
              <div className="topic-content">
                <h2 className="topic-name">{topic.name}</h2>
                <p className="topic-description">{topic.description}</p>

                {/* Topic Meta */}
                <div className="topic-meta">
                  {topic.difficultyLevel && (
                    <span className={`difficulty-badge ${topic.difficultyLevel}`}>
                      {topic.difficultyLevel}
                    </span>
                  )}
                  {topic.estimatedHours > 0 && (
                    <span className="meta-badge">
                      ⏱️ {topic.estimatedHours}h
                    </span>
                  )}
                  {topic.category && (
                    <span className="meta-badge category">
                      {getCategoryIcon(topic.category)} {topic.category}
                    </span>
                  )}
                </div>

                {/* Prerequisites */}
                {topic.prerequisites && topic.prerequisites.length > 0 && (
                  <div className="prerequisites">
                    <span className="prereq-label">Prerequisites:</span>
                    <span className="prereq-text">
                      {JSON.parse(topic.prerequisites).join(', ')}
                    </span>
                  </div>
                )}
              </div>

              {/* Hover Indicator */}
              <div className="card-overlay">
                <span className="start-text">Start Learning →</span>
              </div>
            </div>
          ))
        )}
      </div>

      {/* Stats Footer */}
      {filteredTopics.length > 0 && (
        <div className="stats-footer">
          <div className="stat-item">
            <span className="stat-number">{filteredTopics.length}</span>
            <span className="stat-label">Learning Paths</span>
          </div>
          <div className="stat-item">
            <span className="stat-number">
              {filteredTopics.reduce((acc, t) => acc + (t.estimatedHours || 0), 0)}h
            </span>
            <span className="stat-label">Total Content</span>
          </div>
          <div className="stat-item">
            <span className="stat-number">∞</span>
            <span className="stat-label">Possibilities</span>
          </div>
        </div>
      )}
    </div>
  );
};

export default TopicSelectionPage;
