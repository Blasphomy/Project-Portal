import React from 'react';
import { Handle, Position } from 'reactflow';
import './CosmicQuestNode.css';

/**
 * Custom React Flow node component for rendering quests as cosmic entities
 * Supports 4 states: locked, unlocked, in-progress, completed
 */
const CosmicQuestNode = ({ data }) => {
    const { title, description, category, status, onClick } = data;

    // Determine node appearance based on status
    const getNodeClass = () => {
        const baseClass = 'cosmic-quest-node';
        return `${baseClass} ${status || 'locked'}`;
    };

    // Get category badge color
    const getCategoryClass = () => {
        return `cosmic-badge ${category || 'fundamentals'}`;
    };

    // Get status icon
    const getStatusIcon = () => {
        switch (status) {
            case 'completed':
                return '✓';
            case 'in-progress':
                return '⚡';
            case 'unlocked':
                return '🌟';
            case 'locked':
            default:
                return '🔒';
        }
    };

    return (
        <div className={getNodeClass()} onClick={onClick}>
            {/* Connection handles for edges */}
            <Handle
                type="target"
                position={Position.Top}
                className="node-handle node-handle-target"
            />

            {/* Status icon badge */}
            <div className="status-icon">{getStatusIcon()}</div>

            {/* Quest content */}
            <div className="node-content">
                <div className="node-title">{title}</div>
                {category && (
                    <span className={getCategoryClass()}>
                        {category}
                    </span>
                )}
            </div>

            {/* Tooltip on hover */}
            {description && (
                <div className="node-tooltip">
                    <p>{description}</p>
                </div>
            )}

            <Handle
                type="source"
                position={Position.Bottom}
                className="node-handle node-handle-source"
            />
        </div>
    );
};

export default CosmicQuestNode;
