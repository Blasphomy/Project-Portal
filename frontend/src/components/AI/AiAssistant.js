import React, { useState, useRef, useEffect } from 'react';
import aiService from '../../services/aiService';
import './AiAssistant.css';

function AiAssistant() {
    const [isOpen, setIsOpen] = useState(false);
    const [messages, setMessages] = useState([
        {
            role: 'assistant',
            content: 'Hi! 👋 I\'m your AI study assistant. Ask me anything about programming, and I\'ll help you learn!',
        },
    ]);
    const [input, setInput] = useState('');
    const [loading, setLoading] = useState(false);
    const messagesEndRef = useRef(null);

    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    };

    useEffect(() => {
        scrollToBottom();
    }, [messages]);

    const handleSend = async () => {
        if (!input.trim() || loading) return;

        const userMessage = {
            role: 'user',
            content: input,
        };

        setMessages((prev) => [...prev, userMessage]);
        setInput('');
        setLoading(true);

        try {
            const response = await aiService.chat(input);

            // Parse response whether it's string or object
            let aiContent = '';
            if (typeof response === 'string') {
                aiContent = response;
            } else if (response && typeof response === 'object') {
                aiContent = response.text || response.message || JSON.stringify(response, null, 2);
            }

            const aiMessage = {
                role: 'assistant',
                content: aiContent || 'I\'m having trouble understanding. Can you rephrase that?',
            };

            setMessages((prev) => [...prev, aiMessage]);
        } catch (err) {
            console.error('AI error:', err);
            const errorMessage = {
                role: 'assistant',
                content: 'Sorry, I encountered an error. Please try again!',
            };
            setMessages((prev) => [...prev, errorMessage]);
        } finally {
            setLoading(false);
        }
    };

    const handleKeyPress = (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            handleSend();
        }
    };

    const quickQuestions = [
        'Explain variables in simple terms',
        'What is a loop?',
        'How do I debug my code?',
        'Explain object-oriented programming',
    ];

    const handleQuickQuestion = (question) => {
        setInput(question);
    };

    return (
        <>
            {/* Chat Panel */}
            <div className={`ai-assistant-panel ${isOpen ? 'open' : ''}`}>
                <div className="ai-assistant-header">
                    <div className="ai-header-content">
                        <span className="ai-icon">🤖</span>
                        <div>
                            <h3>AI Study Assistant</h3>
                            <p>Ask me anything!</p>
                        </div>
                    </div>
                    <button className="ai-close-btn" onClick={() => setIsOpen(false)}>
                        ✕
                    </button>
                </div>

                <div className="ai-messages">
                    {messages.map((msg, index) => (
                        <div
                            key={index}
                            className={`ai-message ${msg.role === 'user' ? 'user' : 'assistant'}`}
                        >
                            <div className="ai-message-content">
                                {msg.content}
                            </div>
                        </div>
                    ))}

                    {loading && (
                        <div className="ai-message assistant">
                            <div className="ai-message-content">
                                <div className="typing-indicator">
                                    <span></span>
                                    <span></span>
                                    <span></span>
                                </div>
                            </div>
                        </div>
                    )}

                    <div ref={messagesEndRef} />
                </div>

                {/* Quick Questions */}
                {messages.length === 1 && (
                    <div className="quick-questions">
                        <p>Quick questions:</p>
                        {quickQuestions.map((q, idx) => (
                            <button
                                key={idx}
                                className="quick-question-btn"
                                onClick={() => handleQuickQuestion(q)}
                            >
                                {q}
                            </button>
                        ))}
                    </div>
                )}

                <div className="ai-input-area">
                    <textarea
                        value={input}
                        onChange={(e) => setInput(e.target.value)}
                        onKeyPress={handleKeyPress}
                        placeholder="Ask me anything about programming..."
                        rows="2"
                        disabled={loading}
                    />
                    <button
                        onClick={handleSend}
                        disabled={!input.trim() || loading}
                        className="ai-send-btn"
                    >
                        {loading ? '⏳' : '➤'}
                    </button>
                </div>
            </div>

            {/* Floating Button */}
            <button
                className="ai-assistant-toggle"
                onClick={() => setIsOpen(!isOpen)}
                title="AI Study Assistant"
            >
                🤖
            </button>
        </>
    );
}

export default AiAssistant;
