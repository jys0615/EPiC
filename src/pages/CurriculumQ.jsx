// src/pages/CurriculumQ.jsx

import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import Header from '../components/Header';
import { addQuestionWithAI } from '../api';
import '../styles/curriculum-q.css';

const CurriculumQ = () => {
  const location = useLocation();
  const navigate = useNavigate();

  const { keyword, addInfo, aiResponse } = location.state || {};

  const [keyword_1, setKeyword] = useState('');
  const [addInfo_1, setAddInfo] = useState('');
  const [aiResponse_1, setAiResponse] = useState(aiResponse || '');
  const [question, setQuestion] = useState('');

  useEffect(() => {

    if (keyword) {
      console.log("키워드:",keyword);
      setKeyword(keyword);
    }
    if (addInfo) {
      console.log("에드 인포:",addInfo);
      setAddInfo(addInfo);
    }
    else{
      console.log("에드 인포 없음음")
    }
    if (aiResponse) {
      console.log("ai reponse:",aiResponse);
      setAiResponse(aiResponse);
    }
    else{
      console.log("ai response 없어어")
    }
  }, [keyword, addInfo, aiResponse]);

  const handleSubmit = async () => {

    console.log('keyword:', keyword_1);
    console.log('additional info:', addInfo_1);
    console.log('question:', question);
    console.log('AI response:', aiResponse_1);

    try {
      console.log("일단 요청 보냄");
      const response = await addQuestionWithAI(question);
      console.log("요청 잘 받음");
      console.log("q->result response : ",response)
      navigate('/curriculum-result', {
        state: {
          keyword: keyword_1,
          aiAddResponse: response.ai_add_response
        },
      });
    } catch (error) {
      console.error(error);
      alert('질문 전송 중 오류가 발생했습니다.');
      navigate('/curriculum-result', {
        state: {
          keyword: keyword_1,
          aiAddResponse: ''
        },
      });
    }
  };

  const formatText = (text) => {
    // 숫자(1., 2., ...) 기준으로 섹션 분리
    const sections = text.split(/\d\.\s/);
  
    return sections.map((section, index) => (
      <div key={index} style={{ marginBottom: "20px" }}>
        {section.split("-").map((line, idx) => (
          <p key={idx} style={{ margin: "5px 0" }}>
            {line.trim()}
          </p>
        ))}
      </div>
    ));
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
        <div className="prev-info-section">
          <h2 className="section-title">나의 정보</h2>
          <div className="prev-info-group">
            <div className="form-group-horizontal">
              <div className="form-group-text" id="interest-text">
                <h4>선택 관심사</h4>
              </div>
              <div className="form-group">
                <input
                  type="text"
                  className="form-input"
                  placeholder="관심사"
                  id="interest-field"
                  value={keyword_1}
                  readOnly
                />
              </div>

              <div
                className="form-group-text"
                id="q-info-text"
                style={{ marginLeft: '30px' }}
              >
                <h4>추가 정보</h4>
              </div>
              <div className="form-group">
                <input
                  type="text"
                  className="form-input"
                  placeholder="추가 정보"
                  id="q-info-field"
                  value={addInfo_1}
                  readOnly
                />
              </div>
            </div>
          </div>
        </div>

        <div className="ai-response-group">
          <div className="ai-response-box" id="ai-response" style={{ whiteSpace: 'pre-wrap' }}>
            {formatText(aiResponse_1)}
          </div>
        </div>

        <div className="question-input-group">
          <textarea
            className="question-input"
            placeholder="질문을 입력하세요"
            value={question}
            onChange={(e) => setQuestion(e.target.value)}
          ></textarea>
          <button className="search-button" onClick={handleSubmit}>
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

export default CurriculumQ;