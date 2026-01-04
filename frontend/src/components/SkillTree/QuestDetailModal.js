import React from 'react';
import './QuestDetailModal.css';

/**
 * Modal for displaying quest details and allowing user to start/complete quests
 */
const QuestDetailModal = ({ quest, onClose, onStartQuest, onCompleteQuest, isOpen }) => {
    if (!isOpen || !quest) return null;

    const { title, description, category, status } = quest;

    const canStart = status === 'unlocked';
    const canComplete = status === 'in-progress';
    const isCompleted = status === 'completed';
    const isLocked = status === 'locked';

    const handleBackdropClick = (e) => {
        if (e.target.className === 'modal-backdrop') {
            onClose();
        }
    };

    return (
        <div className="modal-backdrop" onClick={handleBackdropClick}>
            <div className="cosmic-modal">
                {/* Header */}
                <div className="modal-header">
                    <div className="modal-title-section">
                        <h2 className="modal-title">{title}</h2>
                        <span className={`cosmic-badge ${category}`}>
                            {category}
                        </span>
                    </div>
                    <button className="modal-close" onClick={onClose}>✕</button>
                </div>

                {/* Status badge */}
                <div className="modal-status">
                    {isCompleted && <span className="status-badge completed">✓ Completed</span>}
                    {status === 'in-progress' && <span className="status-badge in-progress">⚡ In Progress</span>}
                    {canStart && <span className="status-badge unlocked">🌟 Ready to Start</span>}
                    {isLocked && <span className="status-badge locked">🔒 Locked</span>}
                </div>

                {/* Description */}
                <div className="modal-body">
                    <h3>Quest Description</h3>
                    <p className="quest-description">{description}</p>

                    {isLocked && (
                        <div className="locked-message">
                            <p>⚠️ Complete prerequisite quests to unlock this adventure!</p>
                        </div>
                    )}

                    {isCompleted && (
                        <div className="completed-message">
                            <p>🎉 Congratulations! You've mastered this quest!</p>
                        </div>
                    )}
                </div>

                {/* Actions */}
                <div className="modal-actions">
                    {canStart && (
                        <button
                            className="cosmic-button"
                            onClick={() => {
                                onStartQuest(quest);
                                onClose();
                            }}
                        >
                            <span className="button-icon">🚀</span>
                            Begin This Quest
                        </button>
                    )}

                    {canComplete && (
                        <button
                            className="cosmic-button"
                            onClick={() => {
                                onCompleteQuest(quest);
                                onClose();
                            }}
                        >
                            <span className="button-icon">✓</span>
                            Mark as Complete
                        </button>
                    )}

                    <button
                        className="cosmic-button-secondary"
                        onClick={onClose}
                    >
                        Close
                    </button>
                </div>
            </div>
        </div>
    );
};

export default QuestDetailModal;
