import React, { useState, useEffect } from 'react';
import './BadgeDisplay.css';

const BadgeDisplay = ({ userId }) => {
  const [badges, setBadges] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (!userId) {
      setLoading(false);
      return;
    }

    const fetchBadges = async () => {
      try {
        const response = await fetch(`/api/users/${userId}/badges`);
        if (!response.ok) {
          throw new Error('Network response was not ok');
        }
        const data = await response.json();
        setBadges(data);
      } catch (error) {
        setError(error.message);
      } finally {
        setLoading(false);
      }
    };

    fetchBadges();
  }, [userId]);

  if (loading) {
    return <div>Loading badges...</div>;
  }

  if (error) {
    return <div>Error: {error}</div>;
  }

  if (!userId) {
    return null;
  }

  return (
    <div className="badge-display">
      <h2>Your Badges</h2>
      <ul className="badge-list">
        {badges.map((badge) => (
          <li key={badge.id} className="badge-item">
            <div className="badge-icon">🎉</div>
            <span>{badge.name}</span>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default BadgeDisplay;
