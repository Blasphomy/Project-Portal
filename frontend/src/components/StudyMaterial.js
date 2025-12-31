import React, { useState, useEffect } from 'react';
import ReactMarkdown from 'react-markdown';
import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter';
import { vscDarkPlus } from 'react-syntax-highlighter/dist/esm/styles/prism';
import remarkGfm from 'remark-gfm';
import './StudyMaterial.css';

const StudyMaterial = ({ questId, taskId, topicId }) => {
  const [material, setMaterial] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [generating, setGenerating] = useState(false);

  useEffect(() => {
    if (questId || taskId || topicId) {
      fetchStudyMaterial();
    }
  }, [questId, taskId, topicId]);

  const fetchStudyMaterial = async () => {
    setLoading(true);
    setError(null);

    try {
      // Try to get cached material first
      const params = new URLSearchParams();
      if (questId) params.append('questId', questId);
      if (taskId) params.append('taskId', taskId);
      if (topicId) params.append('topicId', topicId);

      const cachedResponse = await fetch(`/api/study-materials?${params}`);
      
      if (cachedResponse.ok) {
        const cachedData = await cachedResponse.json();
        if (cachedData && cachedData.length > 0) {
          // Parse the cached JSON content
          const parsedContent = JSON.parse(cachedData[0].content);
          setMaterial(parsedContent);
          setLoading(false);
          return;
        }
      }

      // If no cached material and we have a questId, generate new material
      if (questId) {
        await generateMaterial();
      } else {
        setMaterial(null);
        setLoading(false);
      }
    } catch (err) {
      console.error('Error fetching study material:', err);
      setError('Failed to load study material');
      setLoading(false);
    }
  };

  const generateMaterial = async () => {
    if (!questId) return;

    setGenerating(true);
    setError(null);

    try {
      const response = await fetch(`/api/ai/generate-material?questId=${questId}`, {
        method: 'POST',
      });

      if (!response.ok) {
        throw new Error('Failed to generate material');
      }

      const data = await response.json();
      setMaterial(data);
    } catch (err) {
      console.error('Error generating study material:', err);
      setError('Failed to generate study material. Please try again.');
    } finally {
      setGenerating(false);
      setLoading(false);
    }
  };

  if (loading && !generating) {
    return (
      <div className="study-material glass-card">
        <div className="skeleton" style={{ height: '300px' }}></div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="study-material glass-card">
        <div className="error-message">
          <h3>⚠️ {error}</h3>
          <button className="btn btn-primary" onClick={fetchStudyMaterial}>
            Try Again
          </button>
        </div>
      </div>
    );
  }

  if (!material && !questId && !taskId && !topicId) {
    return (
      <div className="study-material glass-card">
        <p className="text-muted">Select a quest to view study materials</p>
      </div>
    );
  }

  if (generating) {
    return (
      <div className="study-material glass-card">
        <div className="generating-indicator">
          <div className="spinner"></div>
          <h3>🤖 AI is generating your study material...</h3>
          <p className="text-muted">This may take a few seconds</p>
        </div>
      </div>
    );
  }

  if (!material) {
    return (
      <div className="study-material glass-card">
        <div className="no-material">
          <h3>📚 No Study Material Available</h3>
          {questId && (
            <button className="btn btn-primary" onClick={generateMaterial}>
              Generate with AI
            </button>
          )}
        </div>
      </div>
    );
  }

  return (
    <div className="study-material glass-card">
      <div className="material-header">
        <h2>📖 Study Material</h2>
        {questId && (
          <button className="btn btn-secondary" onClick={generateMaterial}>
            🔄 Regenerate
          </button>
        )}
      </div>

      {/* Introduction */}
      {material.introduction && (
        <section className="material-section intro-section">
          <ReactMarkdown remarkPlugins={[remarkGfm]}>
            {material.introduction}
          </ReactMarkdown>
        </section>
      )}

      {/* Concepts */}
      {material.concepts && material.concepts.length > 0 && (
        <section className="material-section">
          <h3 className="section-title">💡 Key Concepts</h3>
          {material.concepts.map((concept, index) => (
            <div key={index} className="concept-card">
              <h4>{concept.title}</h4>
              <ReactMarkdown remarkPlugins={[remarkGfm]}>
                {concept.explanation}
              </ReactMarkdown>
              {concept.keyPoints && concept.keyPoints.length > 0 && (
                <ul className="key-points">
                  {concept.keyPoints.map((point, i) => (
                    <li key={i}>{point}</li>
                  ))}
                </ul>
              )}
            </div>
          ))}
        </section>
      )}

      {/* Code Examples */}
      {material.examples && material.examples.length > 0 && (
        <section className="material-section">
          <h3 className="section-title">💻 Code Examples</h3>
          {material.examples.map((example, index) => (
            <div key={index} className="example-card">
              <h4>{example.title}</h4>
              <SyntaxHighlighter
                language={example.language || 'java'}
                style={vscDarkPlus}
                customStyle={{
                  borderRadius: '8px',
                  padding: '1rem',
                  fontSize: '0.9rem',
                }}
              >
                {example.code}
              </SyntaxHighlighter>
              <ReactMarkdown remarkPlugins={[remarkGfm]}>
                {example.explanation}
              </ReactMarkdown>
            </div>
          ))}
        </section>
      )}

      {/* Common Mistakes */}
      {material.commonMistakes && material.commonMistakes.length > 0 && (
        <section className="material-section">
          <h3 className="section-title">⚠️ Common Mistakes to Avoid</h3>
          <div className="mistakes-list">
            {material.commonMistakes.map((mistake, index) => (
              <div key={index} className="mistake-item">
                <span className="mistake-icon">❌</span>
                <p>{mistake}</p>
              </div>
            ))}
          </div>
        </section>
      )}

      {/* Exercises */}
      {material.exercises && material.exercises.length > 0 && (
        <section className="material-section">
          <h3 className="section-title">✏️ Practice Exercises</h3>
          <ol className="exercises-list">
            {material.exercises.map((exercise, index) => (
              <li key={index}>{exercise}</li>
            ))}
          </ol>
        </section>
      )}

      {/* Resources */}
      {material.resources && material.resources.length > 0 && (
        <section className="material-section">
          <h3 className="section-title">🔗 Additional Resources</h3>
          <div className="resources-grid">
            {material.resources.map((resource, index) => (
              <a
                key={index}
                href={resource.url}
                target="_blank"
                rel="noopener noreferrer"
                className="resource-card"
              >
                <span className="resource-icon">
                  {resource.linkType === 'video' ? '🎥' : 
                   resource.linkType === 'documentation' ? '📄' : 
                   resource.linkType === 'interactive' ? '🎮' : '📰'}
                </span>
                <div className="resource-info">
                  <h5>{resource.title}</h5>
                  <p className="text-muted">{resource.description}</p>
                </div>
              </a>
            ))}
          </div>
        </section>
      )}
    </div>
  );
};

export default StudyMaterial;
