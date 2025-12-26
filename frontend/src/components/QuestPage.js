import React, { useState, useEffect } from 'react';
import './QuestPage.css';

const QuestPage = ({ topicId }) => {
  const [quests, setQuests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (!topicId) {
      setLoading(false);
      return;
    }

    const fetchQuestsAndTasks = async () => {
      try {
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
      } catch (error) {
        setError(error.message);
      } finally {
        setLoading(false);
      }
    };

    fetchQuestsAndTasks();
  }, [topicId]);

  if (loading) {
    return <div>Loading quests...</div>;
  }

  if (error) {
    return <div>Error: {error}</div>;
  }

  if (!topicId) {
    return <div>Select a topic to see the quests.</div>
  }

  return (
    <div className="quest-page">
      <h2>Quests</h2>
      <ul className="quest-list">
        {quests.map((quest) => (
          <li key={quest.id} className="quest-item">
            <h3>{quest.name}</h3>
            <p>{quest.description}</p>
            <ul className="task-list">
              {quest.tasks && quest.tasks.map((task) => (
                <li key={task.id} className="task-item">
                  <strong>{task.title}</strong> - {task.xpReward} XP
                </li>
              ))}
            </ul>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default QuestPage;
