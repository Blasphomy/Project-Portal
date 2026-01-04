import React, { useState, useMemo, useCallback, useContext } from 'react';
import { useLocation, Link } from 'react-router-dom';
import ReactFlow, { Background, Controls, MiniMap, useNodesState, useEdgesState } from 'reactflow';
import 'reactflow/dist/style.css';
import dagre from 'dagre';
import CosmicQuestNode from './CosmicQuestNode';
import QuestDetailModal from './QuestDetailModal';
import { AuthContext } from '../../context/AuthContext';
import skillTreeService from '../../services/skillTreeService';
import './SkillTreePage.css';

// Custom node types
const nodeTypes = {
    cosmicQuest: CosmicQuestNode,
};

// Hierarchical layout using Dagre
const getLayoutedElements = (nodes, edges) => {
    const dagreGraph = new dagre.graphlib.Graph();
    dagreGraph.setDefaultEdgeLabel(() => ({}));
    dagreGraph.setGraph({ rankdir: 'TB', ranksep: 100, nodesep: 80 });

    nodes.forEach((node) => {
        dagreGraph.setNode(node.id, { width: 200, height: 80 });
    });

    edges.forEach((edge) => {
        dagreGraph.setEdge(edge.source, edge.target);
    });

    dagre.layout(dagreGraph);

    const layoutedNodes = nodes.map((node) => {
        const nodeWithPosition = dagreGraph.node(node.id);
        return {
            ...node,
            position: {
                x: nodeWithPosition.x - 100,
                y: nodeWithPosition.y - 40,
            },
        };
    });

    return { nodes: layoutedNodes, edges };
};

const SkillTreePage = () => {
    const location = useLocation();
    const { user } = useContext(AuthContext);
    const skillTree = location.state?.skillTree;

    const [selectedQuest, setSelectedQuest] = useState(null);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [questStatuses, setQuestStatuses] = useState({});

    // Convert skill tree data to React Flow format
    const { nodes: initialNodes, edges: initialEdges } = useMemo(() => {
        if (!skillTree || !skillTree.nodes || skillTree.nodes.length === 0) {
            return { nodes: [], edges: [] };
        }

        // Create nodes from quests
        const nodes = skillTree.nodes.map((quest) => ({
            id: quest.id,
            type: 'cosmicQuest',
            data: {
                title: quest.title,
                description: quest.description,
                category: quest.category,
                status: questStatuses[quest.id] || 'unlocked', // Default to unlocked for demo
                onClick: () => handleQuestClick(quest),
            },
            position: { x: 0, y: 0 }, // Will be set by layout algorithm
        }));

        // Create edges from dependencies
        const edges = (skillTree.edges || []).map((dep, index) => ({
            id: `e${index}-${dep.source}-${dep.target}`,
            source: dep.source,
            target: dep.target,
            type: 'smoothstep',
            animated: true,
            style: {
                stroke: '#667eea',
                strokeWidth: 2,
            },
            markerEnd: {
                type: 'arrowclosed',
                color: '#667eea',
            },
        }));

        const layouted = getLayoutedElements(nodes, edges);
        console.log('🌟 React Flow Initial Nodes:', nodes);
        console.log('🌟 React Flow Initial Edges:', edges);
        console.log('🌟 Layouted Nodes:', layouted.nodes);
        console.log('🌟 Layouted Edges:', layouted.edges);
        return layouted;
    }, [skillTree, questStatuses]);

    const [nodes, setNodes, onNodesChange] = useNodesState(initialNodes);
    const [edges, setEdges, onEdgesChange] = useEdgesState(initialEdges);

    // Handle quest click
    const handleQuestClick = useCallback((quest) => {
        setSelectedQuest(quest);
        setIsModalOpen(true);
    }, []);

    // Handle start quest
    const handleStartQuest = useCallback((quest) => {
        // Update quest status to in-progress
        setQuestStatuses(prev => ({
            ...prev,
            [quest.id]: 'in-progress'
        }));

        // Update node data
        setNodes(nds =>
            nds.map(node =>
                node.id === quest.id
                    ? { ...node, data: { ...node.data, status: 'in-progress' } }
                    : node
            )
        );
    }, [setNodes]);

    // Handle complete quest
    const handleCompleteQuest = useCallback(async (quest) => {
        try {
            // Call API to complete quest
            if (skillTree.id && user?.id) {
                await skillTreeService.completeQuest(skillTree.id, quest.id, user.id);
            }

            // Update quest status to completed
            setQuestStatuses(prev => ({
                ...prev,
                [quest.id]: 'completed'
            }));

            // Update node data
            setNodes(nds =>
                nds.map(node =>
                    node.id === quest.id
                        ? { ...node, data: { ...node.data, status: 'completed' } }
                        : node
                )
            );

            // TODO: Check and unlock dependent quests
            // This would require fetching updated skill tree from backend

        } catch (error) {
            console.error('Failed to complete quest:', error);
        }
    }, [skillTree, user, setNodes]);

    if (!skillTree) {
        return (
            <div className="skill-tree-page">
                <div className="constellation-bg" />
                <div className="no-skill-tree-message cosmic-panel">
                    <h2>No Cosmic Map Data Available</h2>
                    <p>Your personalized quest map is waiting to be discovered.</p>
                    <Link to="/custom-path" className="cosmic-button">
                        <span className="button-icon">✨</span>
                        Generate Your Quest Map
                    </Link>
                </div>
            </div>
        );
    }

    const { title, description } = skillTree;
    const completedCount = Object.values(questStatuses).filter(s => s === 'completed').length;
    const totalCount = skillTree.nodes?.length || 0;

    return (
        <div className="skill-tree-page">
            {/* Background */}
            <div className="constellation-bg" />

            {/* Header */}
            <div className="skill-tree-header cosmic-panel">
                <div className="header-content">
                    <div>
                        <h1 className="cosmic-panel-header">{title}</h1>
                        <p className="header-description">{description}</p>
                        {skillTree.userGoal && (
                            <p className="user-goal">
                                <span className="goal-label">Your Goal:</span> {skillTree.userGoal}
                            </p>
                        )}
                    </div>
                    <div className="progress-section">
                        <div className="progress-stats">
                            <span className="stat-number">{completedCount}/{totalCount}</span>
                            <span className="stat-label">Quests Completed</span>
                        </div>
                        <div className="progress-bar">
                            <div
                                className="progress-bar-fill"
                                style={{ width: `${(completedCount / totalCount) * 100}%` }}
                            />
                        </div>
                    </div>
                </div>
            </div>

            {/* Quest Map */}
            <div className="react-flow-container">
                <ReactFlow
                    nodes={nodes}
                    edges={edges}
                    onNodesChange={onNodesChange}
                    onEdgesChange={onEdgesChange}
                    nodeTypes={nodeTypes}
                    fitView
                    proOptions={{ hideAttribution: true }}
                    minZoom={0.5}
                    maxZoom={1.5}
                >
                    <Background
                        color="rgba(102, 126, 234, 0.3)"
                        variant="dots"
                        gap={16}
                        size={1}
                    />
                    <Controls className="cosmic-controls" />
                    <MiniMap
                        nodeColor={(node) => {
                            switch (node.data.status) {
                                case 'completed': return '#10b981';
                                case 'in-progress': return '#f7d060';
                                case 'unlocked': return '#667eea';
                                default: return '#4b5563';
                            }
                        }}
                        nodeStrokeWidth={3}
                        className="cosmic-minimap"
                        zoomable
                        pannable
                    />
                </ReactFlow>
            </div>

            {/* Quest Detail Modal */}
            <QuestDetailModal
                quest={selectedQuest}
                isOpen={isModalOpen}
                onClose={() => setIsModalOpen(false)}
                onStartQuest={handleStartQuest}
                onCompleteQuest={handleCompleteQuest}
            />
        </div>
    );
};

export default SkillTreePage;
