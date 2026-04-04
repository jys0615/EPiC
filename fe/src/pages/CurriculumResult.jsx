// src/pages/CurriculumResult.jsx

import React, { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import Header from '../components/Header';
import { addQuestionWithAI } from '../api';
import '../styles/curriculum-result.css';

const CurriculumResult = () => {
  const location = useLocation();
  const { keyword, aiAddResponse} = location.state || {};

  const [loading, setLoading] = useState(false);
  const [showAlert, setShowAlert] = useState(false);
  const [aiPrintResponse, setAiPrintResponse] = useState(false);
  const [question, setQuestion] = useState('');

  useEffect(() => {
    if (aiAddResponse) {
      console.log("ai reponse:",aiAddResponse);
      setAiPrintResponse(aiAddResponse);
    }
    else{
      console.log("result창 ai response없음");
    }
  }, [keyword, aiAddResponse]);

  const handleSearch = async () => {
    if (!question.trim()) {
      alert('추가 질문을 입력해 주세요.');
      return;
    }

    setLoading(true);
    try {
      console.log("요청은 보냄");
      const response = await addQuestionWithAI(question);
      console.log("요청 좋다");

      console.log('AI response:', response.ai_add_response);

      const aiAddResponse = response.ai_add_response;
      setAiPrintResponse(aiAddResponse);

      console.log('AI additional response:', aiPrintResponse);
    } catch (error) {
      console.error('Error during API request:', error);
      setAiPrintResponse('');
    } finally {
      setLoading(false);
    }
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
        <div className="prev-question-section">
          <h2 className="section-title">질문 내용</h2>
          <div className="prev-question-group">
            <input
              type="text"
              className="prev-question"
              value={`${keyword} 관련 커리큘럼 추천`}
              readOnly
            />
          </div>
        </div>

        <div className="ai-response-group">
          <div className="ai-response-box">
            {aiPrintResponse || '커리큘럼 추천에 대한 AI 답변이 없습니다.'}
          </div>
        </div>

        <div className="question-input-group">
          <textarea
            className="question-input"
            placeholder="질문을 입력하세요"
            value={question}
            onChange={(e) => setQuestion(e.target.value)}
          ></textarea>
          <button className="search-button" onClick={handleSearch}>
            검색 &nbsp;
            <svg
              xmlns="http://www.w3.org/2000/svg"
              height="24px"
              viewBox="0 -960 960 960"
              width="24px"
              fill="#FFFFFF"
            >
              <path d="M784-120 532-372q-30 24-69 38t-83 14q-109 0-184.5-75.5T120-580q0-109 75.5-184.5T380-840q109 0 184.5 75.5T640-580q0 44-14 83t-38 69l252 252-56 56ZM380-400q75 0 127.5-52.5T560-580q0-75-52.5-127.5T380-760q-75 0-127.5 52.5T200-580q0 75 52.5 127.5T380-400Z" />
            </svg>
          </button>
        </div>
      </div>
    </div>
  );
};

export default CurriculumResult;