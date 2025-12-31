import React, { useMemo } from 'react';
import { useLocation, Link } from 'react-router-dom';
import ReactFlow, { Background, Controls, MiniMap } from 'reactflow';
import 'reactflow/dist/style.css';
import './SkillTreePage.css';

// A simple layout function to arrange nodes in a circle for a "galaxy" feel.
const getCircularLayoutedElements = (quests, dependencies) => {
    const nodes = [];
    // Adjust radius based on the number of quests to prevent overlap
    const radius = 150 * Math.ceil(quests.length / 4);
    const centerX = window.innerWidth / 4; // Center based on viewport
    const centerY = window.innerHeight / 5;

    quests.forEach((quest, i) => {
        const angle = (i / quests.length) * 2 * Math.PI;
        nodes.push({
            id: quest.id.toString(), // Ensure IDs are strings for ReactFlow
            position: {
                x: centerX + radius * Math.cos(angle),
                y: centerY + radius * Math.sin(angle),
            },
            data: { label: quest.title },
            className: 'cosmic-node', // General class for all nodes
        });
    });

    const edges = dependencies.map((dep, index) => ({
        id: `e${index}-${dep.from}-${dep.to}`,
        source: dep.from.toString(),
        target: dep.to.toString(),
        animated: true,
        style: { strokeWidth: 2 },
    }));

    return { nodes, edges };
};


const SkillTreePage = () => {
    const location = useLocation();
    const skillTree = location.state?.skillTree;

    const { nodes, edges } = useMemo(() => {
        if (!skillTree || !skillTree.quests || skillTree.quests.length === 0) {
            return { nodes: [], edges: [] };
        }
        return getCircularLayoutedElements(skillTree.quests, skillTree.dependencies || []);
    }, [skillTree]);

    if (!skillTree) {
        return (
            <div className="skill-tree-page">
                <div className="no-skill-tree-message">
                    <h2>No Cosmic Map Data Available</h2>
                    <p>Your personalized quest map is waiting to be discovered.</p>
                    <Link to="/custom-path" className="btn-nav">Generate Your Quest Map</Link>
                </div>
            </div>
        );
    }

    const { title, description } = skillTree;

    return (
        <div className="skill-tree-page">
            <div className="skill-tree-header">
                <h1>{title}</h1>
                <p>{description}</p>
            </div>
            <div className="react-flow-container">
                <ReactFlow
                    nodes={nodes}
                    edges={edges}
                    fitView
                    proOptions={{ hideAttribution: true }} // Hide React Flow attribution
                >
                    <Background color="#667eea" variant="dots" gap={24} size={1} />
                    <Controls />
                    <MiniMap nodeColor="#667eea" nodeStrokeWidth={3} zoomable pannable />
                </ReactFlow>
            </div>
        </div>
    );
};

export default SkillTreePage;
