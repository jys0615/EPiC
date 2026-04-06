// src/pages/CurriculumQ.jsx

import React, { useState, useEffect, useRef } from 'react';
import { useLocation } from 'react-router-dom';
import Header from '../components/Header';
import { addQuestionWithAI } from '../api';
import '../styles/curriculum-q.css';

const renderMessage = (text) => {
  return text.split('\n').map((line, i, arr) => {
    const parts = line.split(/(\*\*[^*]+\*\*)/g);
    return (
      <span key={i}>
        {parts.map((part, j) =>
          part.startsWith('**') && part.endsWith('**')
            ? <strong key={j}>{part.slice(2, -2)}</strong>
            : part
        )}
        {i < arr.length - 1 && <br />}
      </span>
    );
  });
};

const CurriculumQ = () => {
  const location = useLocation();
  const { keyword, addInfo, aiResponse } = location.state || {};

  const [messages, setMessages] = useState([]);
  const [question, setQuestion] = useState('');
  const [loading, setLoading] = useState(false);
  const bottomRef = useRef(null);
  const textareaRef = useRef(null);

  useEffect(() => {
    if (aiResponse) {
      setMessages([{ role: 'epic', content: aiResponse }]);
    }
  }, []);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages, loading]);

  const handleSubmit = async () => {
    if (!question.trim() || loading) return;

    const userMessage = question.trim();
    setQuestion('');
    if (textareaRef.current) textareaRef.current.style.height = 'auto';

    setMessages(prev => [...prev, { role: 'user', content: userMessage }]);
    setLoading(true);

    try {
      const response = await addQuestionWithAI(userMessage);
      setMessages(prev => [...prev, { role: 'epic', content: response.ai_add_response }]);
    } catch {
      setMessages(prev => [...prev, { role: 'epic', content: '오류가 발생했습니다. 다시 시도해 주세요.' }]);
    } finally {
      setLoading(false);
    }
  };

  const handleKeyDown = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSubmit();
    }
  };

  const handleTextareaChange = (e) => {
    setQuestion(e.target.value);
    e.target.style.height = 'auto';
    e.target.style.height = Math.min(e.target.scrollHeight, 120) + 'px';
  };

  return (
    <div>
      <Header />
      <div className="curriculum-intro">
        <h2 className="curriculum-title">커리큘럼 추천</h2>
        <p className="curriculum-description">
          관심 키워드를 입력하면 AI가 최적의 커리큘럼을 추천해 드립니다.
        </p>
      </div>

      <div className="curriculum-container">
        <div className="chat-meta">
          <span className="chat-keyword-badge">{keyword}</span>
          {addInfo && <span className="chat-addinfo">{addInfo}</span>}
        </div>

        <div className="chat-window">
          {messages.map((msg, i) => (
            <div key={i} className={`chat-row ${msg.role}`}>
              {msg.role === 'epic' && (
                <div className="chat-avatar">E</div>
              )}
              <div className={`chat-bubble ${msg.role}`}>
                {renderMessage(msg.content)}
              </div>
            </div>
          ))}

          {loading && (
            <div className="chat-row epic">
              <div className="chat-avatar">E</div>
              <div className="chat-bubble epic loading">
                <span className="dot" />
                <span className="dot" />
                <span className="dot" />
              </div>
            </div>
          )}

          <div ref={bottomRef} />
        </div>

        <div className="chat-input-area">
          <textarea
            ref={textareaRef}
            className="chat-input"
            placeholder="추가로 궁금한 점을 입력하세요  (Enter 전송 / Shift+Enter 줄바꿈)"
            value={question}
            onChange={handleTextareaChange}
            onKeyDown={handleKeyDown}
            rows={1}
          />
          <button className="send-button" onClick={handleSubmit} disabled={loading}>
            <svg xmlns="http://www.w3.org/2000/svg" height="22px" viewBox="0 -960 960 960" width="22px" fill="#FFFFFF">
              <path d="M120-160v-640l760 320-760 320Zm80-120 474-200-474-200v140l240 60-240 60v140Zm0 0v-400 400Z" />
            </svg>
          </button>
        </div>
      </div>
    </div>
  );
};

export default CurriculumQ;
