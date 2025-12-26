import React, { useState } from 'react';
import './App.css';
import TopicSelectionPage from './components/TopicSelectionPage';
import QuestPage from './components/QuestPage';
import BadgeDisplay from './components/BadgeDisplay';
import RewardModal from './components/RewardModal';
import StudyMaterial from './components/StudyMaterial';

function App() {
  const [selectedTopicId, setSelectedTopicId] = useState(null);
  const [reward, setReward] = useState(null);
  const userId = 'user-1'; // Hardcoded user ID for now

  // Function to award a reward (for demonstration purposes)
  const awardReward = () => {
    setReward({
      type: 'Badge',
      name: 'First Step',
    });
  };

  const closeRewardModal = () => {
    setReward(null);
  };

  return (
    <div className="App">
      <header className="App-header">
        <h1>Lean Programming Guide</h1>
        <button onClick={awardReward}>Award Reward (Test)</button>
      </header>
      <main>
        <TopicSelectionPage onSelectTopic={setSelectedTopicId} />
        <QuestPage topicId={selectedTopicId} />
        <BadgeDisplay userId={userId} />
        <StudyMaterial />
        <RewardModal reward={reward} onClose={closeRewardModal} />
      </main>
    </div>
  );
}

export default App;
