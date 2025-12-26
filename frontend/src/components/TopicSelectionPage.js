import React, { useState, useEffect } from 'react';
import './TopicSelectionPage.css';

const TopicSelectionPage = ({ onSelectTopic }) => {
  const [topics, setTopics] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchTopics = async () => {
      try {
        const response = await fetch('/api/topics');
        if (!response.ok) {
          throw new Error('Network response was not ok');
        }
        const data = await response.json();
        setTopics(data);
      } catch (error) {
        setError(error.message);
      } finally {
        setLoading(false);
      }
    };

    fetchTopics();
  }, []);

  if (loading) {
    return <div>Loading...</div>;
  }

  if (error) {
    return <div>Error: {error}</div>;
  }

  return (
    <div>
      <h1>Select a Topic</h1>
      <div className="topic-selection-page">
        {topics.map((topic) => (
          <div key={topic.id} className="topic-card" onClick={() => onSelectTopic(topic.id)}>
            <h2>{topic.name}</h2>
            <p>{topic.description}</p>
          </div>
        ))}
      </div>
    </div>
  );
};

export default TopicSelectionPage;
