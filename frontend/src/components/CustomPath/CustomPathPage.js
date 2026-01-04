import React, { useState, useContext, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './CustomPathPage.css';
import { AuthContext } from '../../context/AuthContext';
import aiService from '../../services/aiService';
import skillTreeService from '../../services/skillTreeService';

const CustomPathPage = () => {
    const [goal, setGoal] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [typedText, setTypedText] = useState('');
    const { user } = useContext(AuthContext);
    const navigate = useNavigate();

    const greeting = "Greetings, Adventurer! I am your AI Game Master.";
    const subtext = "Tell me your ultimate goal, and I'll craft a personalized quest map tailored just for you...";

    const exampleGoals = [
        "Build a REST API with Spring Boot",
        "Create a blog with user authentication",
        "Master React and modern frontend development",
        "Learn Docker and Kubernetes deployment"
    ];

    // Typewriter effect for greeting
    useEffect(() => {
        let currentIndex = 0;
        const fullText = greeting;
        const interval = setInterval(() => {
            if (currentIndex <= fullText.length) {
                setTypedText(fullText.substring(0, currentIndex));
                currentIndex++;
            } else {
                clearInterval(interval);
            }
        }, 50);
        return () => clearInterval(interval);
    }, []);

    const handleGenerate = async () => {
        if (!goal.trim()) {
            setError('Please enter your goal.');
            return;
        }

        setLoading(true);
        setError(null);

        try {
            // Step 1: Generate skill tree using AI
            const aiResponse = await aiService.generateSkillTree(goal, user.id);

            // Step 2: Save the AI-generated skill tree to the database
            const skillTreeToSave = {
                title: aiResponse.data.title,
                description: aiResponse.data.description,
                userGoal: goal,
                userId: user.id,
                nodes: aiResponse.data.nodes,
                edges: aiResponse.data.edges
            };

            const saveResponse = await skillTreeService.createSkillTree(skillTreeToSave, user.id);

            // Step 3: Navigate with the saved skill tree (which now has an ID)
            setTimeout(() => {
                navigate('/skill-tree', { state: { skillTree: saveResponse.data } });
            }, 500);
        } catch (error) {
            console.error('Failed to generate skill tree:', error);
            setError('The AI Game Master is currently resting. Please try again in a moment.');
        } finally {
            setLoading(false);
        }
    };

    const fillExampleGoal = (example) => {
        setGoal(example);
        setError(null);
    };

    return (
        <div className="custom-path-page">
            {/* Animated background */}
            <div className="constellation-bg" />

            <div className="cosmic-container">
                <div className="ai-greeting">
                    <div className="ai-avatar">
                        <div className="ai-orb"></div>
                    </div>
                    <h1 className="greeting-text">{typedText}<span className="cursor">|</span></h1>
                    <p className="subtext">{subtext}</p>
                </div>

                <div className="cosmic-panel goal-input-panel">
                    <label className="goal-label">What is your ultimate goal?</label>
                    <textarea
                        className="cosmic-input cosmic-textarea"
                        value={goal}
                        onChange={(e) => setGoal(e.target.value)}
                        placeholder="e.g., I want to build a full-stack web application with authentication and real-time features..."
                        rows={4}
                        disabled={loading}
                    />

                    <div className="example-goals">
                        <p className="example-label">Or try one of these:</p>
                        <div className="example-pills">
                            {exampleGoals.map((example, index) => (
                                <button
                                    key={index}
                                    className="example-pill"
                                    onClick={() => fillExampleGoal(example)}
                                    disabled={loading}
                                >
                                    {example}
                                </button>
                            ))}
                        </div>
                    </div>

                    {error && <p className="error-message">{error}</p>}

                    <button
                        className="cosmic-button generate-button"
                        onClick={handleGenerate}
                        disabled={loading || !goal.trim()}
                    >
                        {loading ? (
                            <>
                                <span className="loading-spinner"></span>
                                Generating Your Quest Map<span className="loading-dots">...</span>
                            </>
                        ) : (
                            <>
                                <span className="button-icon">✨</span>
                                Generate My Skill Tree
                            </>
                        )}
                    </button>
                </div>

                {loading && (
                    <div className="generation-overlay">
                        <div className="cosmic-spinner-container">
                            <div className="pulsing-stars">
                                <div className="star star-1"></div>
                                <div className="star star-2"></div>
                                <div className="star star-3"></div>
                            </div>
                            <p className="generation-text">The AI Game Master is crafting your personalized adventure...</p>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
};

export default CustomPathPage;
