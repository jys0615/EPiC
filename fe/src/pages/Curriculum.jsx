// src/pages/Curriculum.jsx

import React, { useState, useEffect, useRef } from 'react';
import Header from '../components/Header';
import { fetchRecommendedCurriculum, addQuestionWithAI } from '../api';
import '../styles/curriculum.css';

const POPULAR_KEYWORDS = ['데이터 사이언스', '백엔드', '프론트엔드', '인공지능', '머신러닝', '보안', '네트워크'];

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

const Curriculum = () => {
  const [messages, setMessages] = useState([
    {
      role: 'epic',
      content: '안녕하세요! 저는 EPiC AI 조교입니다.\n관심 있는 분야의 키워드를 입력하거나 아래에서 선택해보세요.',
      showChips: true,
    }
  ]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);
  const [phase, setPhase] = useState('keyword');
  const bottomRef = useRef(null);
  const textareaRef = useRef(null);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages, loading]);

  const sendMessage = async (text) => {
    if (!text.trim() || loading) return;

    const userText = text.trim();
    setInput('');
    if (textareaRef.current) {
      textareaRef.current.style.height = 'auto';
    }

    setMessages(prev => [...prev, { role: 'user', content: userText }]);
    setLoading(true);

    try {
      if (phase === 'keyword') {
        const response = await fetchRecommendedCurriculum(userText, '');
        setMessages(prev => [...prev, { role: 'epic', content: response.ai_response }]);
        setPhase('chat');
      } else {
        const response = await addQuestionWithAI(userText);
        setMessages(prev => [...prev, { role: 'epic', content: response.ai_add_response }]);
      }
    } catch {
      setMessages(prev => [...prev, {
        role: 'epic',
        content: '오류가 발생했습니다. 다시 시도해 주세요.'
      }]);
    } finally {
      setLoading(false);
    }
  };

  const handleKeyDown = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      sendMessage(input);
    }
  };

  const handleTextareaChange = (e) => {
    setInput(e.target.value);
    e.target.style.height = 'auto';
    e.target.style.height = Math.min(e.target.scrollHeight, 120) + 'px';
  };

  return (
    <div className="curriculum-page">
      <Header />
      <div className="curriculum-intro">
        <h2 className="curriculum-title">커리큘럼 추천</h2>
        <p className="curriculum-description">
          관심 키워드를 입력하면 AI가 최적의 커리큘럼을 추천해 드립니다.
        </p>
      </div>

      <div className="curriculum-container">
        <div className="chat-window">
          {messages.map((msg, i) => (
            <div key={i} className={`chat-row ${msg.role}`}>
              {msg.role === 'epic' && <div className="chat-avatar">E</div>}
              <div className={`chat-bubble ${msg.role}`}>
                {renderMessage(msg.content)}
                {msg.showChips && phase === 'keyword' && (
                  <div className="keyword-chips">
                    {POPULAR_KEYWORDS.map(kw => (
                      <button
                        key={kw}
                        className="keyword-chip"
                        onClick={() => sendMessage(kw)}
                        disabled={loading}
                      >
                        {kw}
                      </button>
                    ))}
                  </div>
                )}
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
            placeholder={phase === 'keyword' ? '관심 키워드를 입력하세요' : '추가 질문을 입력하세요'}
            value={input}
            onChange={handleTextareaChange}
            onKeyDown={handleKeyDown}
            rows={1}
          />
          <button
            className="send-button"
            onClick={() => sendMessage(input)}
            disabled={loading}
          >
            <svg xmlns="http://www.w3.org/2000/svg" height="22px" viewBox="0 -960 960 960" width="22px" fill="#FFFFFF">
              <path d="M120-160v-640l760 320-760 320Zm80-120 474-200-474-200v140l240 60-240 60v140Zm0 0v-400 400Z" />
            </svg>
          </button>
        </div>
      </div>
    </div>
  );
};

export default Curriculum;
