import React from 'react';
import Confetti from 'react-confetti';
import './RewardModal.css';

const RewardModal = ({ reward, onClose }) => {
  if (!reward) {
    return null;
  }

  return (
    <div className="reward-modal">
      <Confetti
        width={window.innerWidth}
        height={window.innerHeight}
        recycle={false}
        numberOfPieces={200}
      />
      <div className="reward-modal-content">
        <h2>Congratulations!</h2>
        <p>You've earned a new {reward.type}!</p>
        <h3>{reward.name}</h3>
        <button onClick={onClose}>Claim</button>
      </div>
    </div>
  );
};

export default RewardModal;
