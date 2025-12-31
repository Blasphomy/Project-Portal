import React, { useState, useRef } from 'react';
import Editor from '@monaco-editor/react';
import './CodeEditor.css';

const CodeEditor = ({ taskId, taskTitle, language = 'java', starterCode = '' }) => {
    const [code, setCode] = useState(starterCode);
    const [hints, setHints] = useState([]);
    const [review, setReview] = useState(null);
    const [loading, setLoading] = useState(false);
    const [showHints, setShowHints] = useState(false);
    const editorRef = useRef(null);

    const handleEditorChange = (value) => {
        setCode(value || '');
    };

    const handleEditorDidMount = (editor, monaco) => {
        editorRef.current = editor;
    };

    const getHints = async () => {
        if (!taskId || !code) return;

        setLoading(true);
        try {
            const response = await fetch('/api/ai/hints', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    taskId,
                    userCode: code,
                    language,
                }),
            });

            if (!response.ok) {
                throw new Error('Failed to get hints');
            }

            const data = await response.json();
            setHints(data.hints || []);
            setShowHints(true);
        } catch (error) {
            console.error('Error getting hints:', error);
            alert('Failed to get hints. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    const submitCode = async () => {
        if (!taskId || !code) return;

        setLoading(true);
        setReview(null);
        try {
            const response = await fetch('/api/ai/review-code', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    taskId,
                    userCode: code,
                    language,
                }),
            });

            if (!response.ok) {
                throw new Error('Failed to review code');
            }

            const data = await response.json();
            setReview(data);
        } catch (error) {
            console.error('Error submitting code:', error);
            alert('Failed to submit code. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    const resetCode = () => {
        setCode(starterCode);
        setHints([]);
        setReview(null);
        setShowHints(false);
    };

    return (
        <div className="code-editor-container glass-card">
            <div className="editor-header">
                <h2>💻 Code Challenge: {taskTitle}</h2>
                <div className="editor-actions">
                    <button
                        className="btn btn-secondary"
                        onClick={resetCode}
                        disabled={loading}
                    >
                        🔄 Reset
                    </button>
                    <button
                        className="btn btn-primary"
                        onClick={getHints}
                        disabled={loading || !code}
                    >
                        {loading ? '⏳' : '💡'} Get Hints
                    </button>
                    <button
                        className="btn btn-success"
                        onClick={submitCode}
                        disabled={loading || !code}
                    >
                        {loading ? '⏳ Reviewing...' : '✅ Submit'}
                    </button>
                </div>
            </div>

            <div className="editor-body">
                {/* Monaco Editor */}
                <div className="editor-panel">
                    <div className="panel-header">
                        <span>📝 Your Code ({language})</span>
                        <span className="char-count">{code.length} characters</span>
                    </div>
                    <Editor
                        height="500px"
                        language={language}
                        value={code}
                        onChange={handleEditorChange}
                        onMount={handleEditorDidMount}
                        theme="vs-dark"
                        options={{
                            minimap: { enabled: false },
                            fontSize: 14,
                            lineNumbers: 'on',
                            roundedSelection: true,
                            scrollBeyondLastLine: false,
                            automaticLayout: true,
                            tabSize: 2,
                            wordWrap: 'on',
                        }}
                    />
                </div>

                {/* Hints Panel */}
                {showHints && hints.length > 0 && (
                    <div className="hints-panel">
                        <div className="panel-header">
                            <span>💡 Hints</span>
                            <button
                                className="close-btn"
                                onClick={() => setShowHints(false)}
                            >
                                ✕
                            </button>
                        </div>
                        <div className="hints-list">
                            {hints.map((hint, index) => (
                                <div key={index} className={`hint-item level-${hint.level}`}>
                                    <div className="hint-header">
                                        <span className="hint-badge">Hint {hint.level}</span>
                                        <span className="hint-type">{hint.type}</span>
                                    </div>
                                    <p className="hint-text">{hint.hintText}</p>
                                </div>
                            ))}
                        </div>
                    </div>
                )}
            </div>

            {/* Code Review Results */}
            {review && (
                <div className="review-panel">
                    <div className="review-header">
                        <h3>📊 Code Review Results</h3>
                        <span className={`assessment-badge ${review.overallAssessment}`}>
                            {review.overallAssessment}
                        </span>
                        <span className="score-badge">
                            Score: {review.score}/100
                        </span>
                    </div>

                    <div className="review-content">
                        {/* Strengths */}
                        {review.strengths && review.strengths.length > 0 && (
                            <div className="review-section strengths">
                                <h4>✅ Strengths</h4>
                                <ul>
                                    {review.strengths.map((strength, index) => (
                                        <li key={index}>{strength}</li>
                                    ))}
                                </ul>
                            </div>
                        )}

                        {/* Improvements */}
                        {review.improvements && review.improvements.length > 0 && (
                            <div className="review-section improvements">
                                <h4>💪 Areas for Improvement</h4>
                                <ul>
                                    {review.improvements.map((improvement, index) => (
                                        <li key={index}>{improvement}</li>
                                    ))}
                                </ul>
                            </div>
                        )}

                        {/* Detailed Feedback */}
                        {review.feedback && review.feedback.length > 0 && (
                            <div className="review-section feedback">
                                <h4>📝 Detailed Feedback</h4>
                                {review.feedback.map((item, index) => (
                                    <div key={index} className={`feedback-item ${item.severity}`}>
                                        <div className="feedback-header">
                                            <span className="category-badge">{item.category}</span>
                                            <span className="severity-badge">{item.severity}</span>
                                            {item.lineNumber && (
                                                <span className="line-badge">Line {item.lineNumber}</span>
                                            )}
                                        </div>
                                        <p>{item.message}</p>
                                    </div>
                                ))}
                            </div>
                        )}

                        {/* Test Results */}
                        <div className="test-result">
                            {review.passesTests ? (
                                <div className="test-pass">
                                    <span className="test-icon">✅</span>
                                    <span>All tests passed!</span>
                                </div>
                            ) : (
                                <div className="test-fail">
                                    <span className="test-icon">❌</span>
                                    <span>Some tests failed. Review the feedback above.</span>
                                </div>
                            )}
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default CodeEditor;
