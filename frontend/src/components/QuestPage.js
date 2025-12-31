import React, { useState, useEffect } from 'react';
import StudyMaterial from './StudyMaterial';
import './QuestPage.css';

const QuestPage = ({ topicId }) => {
  const [quests, setQuests] = useState([]);
  const [selectedQuest, setSelectedQuest] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [expandedTasks, setExpandedTasks] = useState({});

  useEffect(() => {
    if (!topicId) {
      setLoading(false);
      return;
    }

    fetchQuestsAndTasks();
  }, [topicId]);

  const fetchQuestsAndTasks = async () => {
    try {
      setLoading(true);
      // Fetch quests for the selected topic
      const questsResponse = await fetch(`/api/topics/${topicId}/quests`);
      if (!questsResponse.ok) {
        throw new Error('Failed to fetch quests');
      }
      const questsData = await questsResponse.json();

      // For each quest, fetch its tasks
      const questsWithTasks = await Promise.all(
        questsData.map(async (quest) => {
          const tasksResponse = await fetch(`/api/quests/${quest.id}/tasks`);
          if (!tasksResponse.ok) {
            throw new Error(`Failed to fetch tasks for quest ${quest.id}`);
          }
          const tasksData = await tasksResponse.json();
          return { ...quest, tasks: tasksData };
        })
      );

      setQuests(questsWithTasks);
      // Auto-select first quest
      if (questsWithTasks.length > 0) {
        setSelectedQuest(questsWithTasks[0]);
      }
      setError(null);
    } catch (error) {
      console.error('Error fetching quests:', error);
      setError(error.message);
    } finally {
      setLoading(false);
    }
  };

  const toggleTask = (taskId) => {
    setExpandedTasks(prev => ({
      ...prev,
      [taskId]: !prev[taskId]
    }));
  };

  const handleQuestSelect = (quest) => {
    setSelectedQuest(quest);
    setExpandedTasks({}); // Reset expanded tasks
  };

  if (loading) {
    return (
      <div className="quest-page">
        <div className="skeleton" style={{ height: '400px', borderRadius: '16px' }}></div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="quest-page">
        <div className="glass-card error-state">
          <h3>⚠️ Error loading quests</h3>
          <p>{error}</p>
          <button className="btn btn-primary" onClick={fetchQuestsAndTasks}>
            Retry
          </button>
        </div>
      </div>
    );
  }

  if (!topicId) {
    return (
      <div className="quest-page">
        <div className="glass-card empty-state">
          <h2>🎯 Ready to Start Learning?</h2>
          <p>Select a topic from above to begin your learning journey!</p>
        </div>
      </div>
    );
  }

  if (quests.length === 0) {
    return (
      <div className="quest-page">
        <div className="glass-card empty-state">
          <h3>📭 No quests available yet</h3>
          <p>This topic is still being prepared.</p>
        </div>
      </div>
    );
  }

  return (
    <div className="quest-page-container">
      {/* Left Panel - Quest List */}
      <aside className="quest-sidebar">
        <div className="sidebar-header">
          <h2>🎮 Quests</h2>
          <span className="quest-count">{quests.length} available</span>
        </div>

        <div className="quest-list">
          {quests.map((quest) => (
            <div
              key={quest.id}
              className={`quest-card ${selectedQuest?.id === quest.id ? 'active' : ''}`}
              onClick={() => handleQuestSelect(quest)}
            >
              <div className="quest-card-header">
                <h3>{quest.name}</h3>
                {quest.difficulty && (
                  <span className={`difficulty-badge ${quest.difficulty}`}>
                    {quest.difficulty}
                  </span>
                )}
              </div>
              <p className="quest-description">{quest.description}</p>

              {quest.tasks && (
                <div className="quest-stats">
                  <span className="task-count">
                    📝 {quest.tasks.length} tasks
                  </span>
                  {quest.estimatedMinutes && (
                    <span className="time-estimate">
                      ⏱️ {quest.estimatedMinutes} min
                    </span>
                  )}
                </div>
              )}
            </div>
          ))}
        </div>
      </aside>

      {/* Right Panel - Quest Details & Study Material */}
      <main className="quest-content">
        {selectedQuest && (
          <>
            {/* Quest Header */}
            <div className="glass-card quest-header">
              <div className="quest-title-section">
                <h1>{selectedQuest.name}</h1>
                {selectedQuest.difficulty && (
                  <span className={`difficulty-badge large ${selectedQuest.difficulty}`}>
                    {selectedQuest.difficulty}
                  </span>
                )}
              </div>
              <p className="quest-description-full">{selectedQuest.description}</p>

              {selectedQuest.estimatedMinutes && (
                <div className="quest-meta">
                  <span className="meta-item">
                    ⏱️ Estimated Time: <strong>{selectedQuest.estimatedMinutes} minutes</strong>
                  </span>
                </div>
              )}
            </div>

            {/* Study Material */}
            <StudyMaterial questId={selectedQuest.id} />

            {/* Tasks Section */}
            <div className="glass-card tasks-section">
              <h2 className="section-title">📋 Quest Tasks</h2>
              <div className="tasks-list">
                {selectedQuest.tasks && selectedQuest.tasks.map((task, index) => (
                  <div key={task.id} className="task-item">
                    <div className="task-header" onClick={() => toggleTask(task.id)}>
                      <div className="task-title-row">
                        <span className="task-number">{index + 1}</span>
                        <h3>{task.title}</h3>
                        <span className={`expand-icon ${expandedTasks[task.id] ? 'expanded' : ''}`}>
                          ▼
                        </span>
                      </div>
                      <div className="task-meta">
                        <span className="xp-badge">
                          ⭐ {task.xpReward} XP
                        </span>
                        {task.difficulty && (
                          <span className={`difficulty-badge small ${task.difficulty}`}>
                            {task.difficulty}
                          </span>
                        )}
                      </div>
                    </div>

                    {expandedTasks[task.id] && (
                      <div className="task-details">
                        <p className="task-description">{task.description}</p>

                        {task.hasCodeChallenge && (
                          <div className="code-challenge-indicator">
                            <span>💻 This task includes a coding challenge</span>
                          </div>
                        )}

                        <div className="task-actions">
                          <button className="btn btn-primary">
                            Start Task
                          </button>
                          {task.hasCodeChallenge && (
                            <button className="btn btn-secondary">
                              Open Code Editor
                            </button>
                          )}
                        </div>
                      </div>
                    )}
                  </div>
                ))}
              </div>
            </div>
          </>
        )}
      </main>
    </div>
  );
};

export default QuestPage;
