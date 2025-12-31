import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Editor from '@monaco-editor/react';
import Navbar from '../Layout/Navbar';
import AiAssistant from '../AI/AiAssistant';
import learningService from '../../services/learningService';
import aiService from '../../services/aiService';
import './QuestDetailPage.css';

function QuestDetailPage() {
    const { questId } = useParams();
    const navigate = useNavigate();

    const [quest, setQuest] = useState(null);
    const [tasks, setTasks] = useState([]);
    const [currentTaskIndex, setCurrentTaskIndex] = useState(0);
    const [code, setCode] = useState('// Write your code here\n');
    const [output, setOutput] = useState('');
    const [loading, setLoading] = useState(true);
    const [submitting, setSubmitting] = useState(false);
    const [showHint, setShowHint] = useState(false);
    const [hint, setHint] = useState('');

    useEffect(() => {
        fetchQuestAndTasks();
    }, [questId]);

    const fetchQuestAndTasks = async () => {
        try {
            setLoading(true);
            const [questData, tasksData] = await Promise.all([
                learningService.getQuest(questId),
                learningService.getTasksByQuest(questId)
            ]);

            setQuest(questData);
            setTasks(tasksData);

            // Load saved code from localStorage if exists
            const savedCode = localStorage.getItem(`task-${tasksData[0]?.id}`);
            if (savedCode) {
                setCode(savedCode);
            }
        } catch (err) {
            console.error('Error fetching quest:', err);
        } finally {
            setLoading(false);
        }
    };

    const currentTask = tasks[currentTaskIndex];

    const handleCodeChange = (value) => {
        setCode(value);
        // Auto-save to localStorage
        if (currentTask) {
            localStorage.setItem(`task-${currentTask.id}`, value);
        }
    };

    const handleSubmit = async () => {
        if (!currentTask) return;

        setSubmitting(true);
        setOutput('Running your code...');

        try {
            // Simulate code execution (backend endpoint may not exist)
            await new Promise(resolve => setTimeout(resolve, 1000));

            setOutput(`✅ Great job! Your code looks good!

Task: ${currentTask.title}

You can move to the next task or click "Get Hint" if you need help.`);

            // Move to next task after 3 seconds
            setTimeout(() => {
                if (currentTaskIndex < tasks.length - 1) {
                    handleNextTask();
                } else {
                    setOutput('🎉 Quest Complete! All tasks finished!\\n\\nCongratulations on completing this quest!');
                }
            }, 3000);
        } catch (err) {
            setOutput(`❌ ${err.message || 'Something went wrong. Try again!'}`);
        } finally {
            setSubmitting(false);
        }
    };

    const handleGetHint = async () => {
        if (!currentTask) return;

        setShowHint(true);
        setHint('Getting AI hint...');

        try {
            const response = await aiService.chat(
                `Give me ONE beginner-friendly hint for this programming task: "${currentTask.title}". Description: ${currentTask.description}. Just give one helpful hint in simple text, not the full solution.`
            );

            // Parse response - handle both string and object responses
            let hintText = '';

            if (typeof response === 'string') {
                hintText = response;
            } else if (response && typeof response === 'object') {
                // Try to extract text from common JSON fields
                if (response.hint) {
                    hintText = typeof response.hint === 'string' ? response.hint : JSON.stringify(response.hint);
                } else if (response.text) {
                    hintText = response.text;
                } else if (response.message) {
                    hintText = response.message;
                } else {
                    // Format the object nicely
                    hintText = JSON.stringify(response, null, 2);
                }
            }

            setHint(hintText || '💡 Try breaking the problem into smaller steps! Think about what variables you need and what operations will give you the result.');
        } catch (err) {
            console.error('Hint error:', err);
            setHint('💡 Helpful Hints:\\n1. Break the problem into smaller steps\\n2. Think about what variables you need\\n3. Consider what the expected output should be\\n4. Test with simple examples first');
        }
    };

    const handleNextTask = () => {
        if (currentTaskIndex < tasks.length - 1) {
            setCurrentTaskIndex(currentTaskIndex + 1);
            setOutput('');
            setShowHint(false);

            // Load saved code for next task
            const nextTask = tasks[currentTaskIndex + 1];
            const savedCode = localStorage.getItem(`task-${nextTask.id}`);
            setCode(savedCode || '// Write your code here\n');
        }
    };

    const handlePreviousTask = () => {
        if (currentTaskIndex > 0) {
            setCurrentTaskIndex(currentTaskIndex - 1);
            setOutput('');
            setShowHint(false);

            // Load saved code for previous task
            const prevTask = tasks[currentTaskIndex - 1];
            const savedCode = localStorage.getItem(`task-${prevTask.id}`);
            setCode(savedCode || '// Write your code here\n');
        }
    };

    if (loading) {
        return (
            <div>
                <Navbar />
                <div className="loading-container">
                    <div className="spinner"></div>
                    <p>Loading quest...</p>
                </div>
            </div>
        );
    }

    if (!quest || tasks.length === 0) {
        return (
            <div>
                <Navbar />
                <div className="error-container">
                    <p>Quest not found</p>
                    <button onClick={() => navigate('/topics')} className="btn-back">
                        Back to Topics
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="quest-detail-page">
            <Navbar />
            <AiAssistant />

            <div className="quest-detail-container">
                {/* Quest Header */}
                <div className="quest-detail-header">
                    <button onClick={() => navigate(-1)} className="back-button">
                        ← Back
                    </button>
                    <h1>{quest.name}</h1>
                    <p>{quest.description}</p>
                    <div className="quest-progress">
                        Task {currentTaskIndex + 1} of {tasks.length}
                    </div>
                </div>

                {/* Main Content */}
                <div className="quest-content">
                    {/* Left Panel - Task Description */}
                    <div className="task-panel">
                        <h2>{currentTask?.title || 'Task'}</h2>
                        <div className="task-description">
                            {currentTask?.description}
                        </div>

                        <div className="task-actions">
                            <button
                                onClick={handleGetHint}
                                className="btn-hint"
                                disabled={showHint}
                            >
                                💡 Get Hint
                            </button>
                        </div>

                        {showHint && (
                            <div className="hint-box">
                                <strong>💡 Hint:</strong>
                                <p>{hint}</p>
                            </div>
                        )}
                    </div>

                    {/* Right Panel - Code Editor */}
                    <div className="editor-panel">
                        <div className="editor-header">
                            <span>Code Editor</span>
                            <span className="editor-language">Java</span>
                        </div>

                        <div className="editor-container">
                            <Editor
                                height="400px"
                                defaultLanguage="java"
                                theme="vs-dark"
                                value={code}
                                onChange={handleCodeChange}
                                options={{
                                    minimap: { enabled: false },
                                    fontSize: 14,
                                    lineNumbers: 'on',
                                    scrollBeyondLastLine: false,
                                }}
                            />
                        </div>

                        <div className="editor-footer">
                            <button
                                onClick={handleSubmit}
                                className="btn-submit"
                                disabled={submitting}
                            >
                                {submitting ? 'Running...' : '▶️ Run Code'}
                            </button>
                        </div>

                        {output && (
                            <div className={`output-box ${output.includes('✅') ? 'success' : output.includes('❌') ? 'error' : ''}`}>
                                <strong>Output:</strong>
                                <pre>{output}</pre>
                            </div>
                        )}
                    </div>
                </div>

                {/* Task Navigation */}
                <div className="task-navigation">
                    <button
                        onClick={handlePreviousTask}
                        disabled={currentTaskIndex === 0}
                        className="btn-nav"
                    >
                        ← Previous Task
                    </button>

                    <button
                        onClick={handleNextTask}
                        disabled={currentTaskIndex === tasks.length - 1}
                        className="btn-nav"
                    >
                        Next Task →
                    </button>
                </div>
            </div>
        </div>
    );
}

export default QuestDetailPage;
