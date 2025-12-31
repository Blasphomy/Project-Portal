import React, { useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import './CustomPathPage.css';
import { AuthContext } from '../../context/AuthContext';
import aiService from '../../services/aiService';

const CustomPathPage = () => {
    const [goal, setGoal] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const { user } = useContext(AuthContext);
    const navigate = useNavigate();

    const handleGenerate = async () => {
        if (!goal) {
            setError('Please enter a goal.');
            return;
        }

        setLoading(true);
        setError(null);

        try {
            const response = await aiService.generateSkillTree(goal, user.id);
            navigate('/skill-tree', { state: { skillTree: response.data } });
        } catch (error) {
            setError('Failed to generate skill tree. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="custom-path-page">
            <h1>Create Your Own Learning Path</h1>
            <p>Tell us your goal, and we'll generate a personalized skill tree for you.</p>
            <input
                type="text"
                value={goal}
                onChange={(e) => setGoal(e.target.value)}
                placeholder="e.g., Learn to build a web application with React"
            />
            <button onClick={handleGenerate} disabled={loading}>
                {loading ? 'Generating...' : 'Generate Skill Tree'}
            </button>
            {error && <p style={{ color: 'red' }}>{error}</p>}
        </div>
    );
};

export default CustomPathPage;
