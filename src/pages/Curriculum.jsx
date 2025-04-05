// src/pages/Curriculum.jsx

import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { fetchRecommendedCurriculum } from '../api';
import Header from '../components/Header';
import '../styles/curriculum.css';

const Curriculum = () => {
  const [keyword, setKeyword] = useState('');
  const [addInfo, setAddInfo] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const popularKeywords = [
    '데이터 사이언스',
    '백엔드',
    '프론트엔드',
    '인공지능',
    '머신러닝',
    '보안',
    '네트워크',
  ];

  const handleKeywordClick = (selectedKeyword) => {
    setKeyword(selectedKeyword);
  };

  const handleSearch = async () => {
    if (!keyword.trim()) {
      alert('키워드를 선택하거나 입력해 주세요.');
      return;
    }

    console.log('keyword:', keyword);
    console.log('additional info:', addInfo);

    setLoading(true);

    try {
      const response = await fetchRecommendedCurriculum(keyword, addInfo);

      console.log('response:', response);

      navigate('/curriculum-q', {
        state: { keyword, addInfo, aiResponse: response.ai_response },
      });
    } catch (error) {
      console.error('Request failed:', error);

      if (error.response) {
        if (error.response.status === 500) {
          alert('서버에서 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.');
        } else if (error.response.status === 404) {
          alert('데이터를 찾을 수 없습니다. 입력한 키워드를 확인해 주세요.');
        } else {
          alert('요청을 처리할 수 없습니다. 다시 시도해 주세요.');
        }
      } else {
        alert('네트워크 연결을 확인해 주세요.');
      }

      if (keyword.trim()) {
        navigate('/curriculum-q', {
          state: {
            keyword,
            add_info: addInfo,
          },
        });
      }
    }

    setLoading(false);
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
        <div className="interest-section">
          <h2 className="section-title">관심사 입력</h2>
          <div className="interest-input-group">
            <input
              type="text"
              className="interest-input"
              placeholder="관심 키워드를 입력하세요"
              value={keyword}
              onChange={(e) => setKeyword(e.target.value)}
            />
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

        <div className="popular-keywords">
          <h2 className="section-title">인기 키워드</h2>
          <div className="keyword-buttons">
            {popularKeywords.map((kw) => (
              <button
                key={kw}
                className="keyword-button"
                onClick={() => handleKeywordClick(kw)}
              >
                {kw}
              </button>
            ))}
          </div>
        </div>

        <div className="extra-info">
          <h2 className="section-title">추가 정보 (선택사항)</h2>
          <textarea
            className="extra-info-input"
            placeholder="추가 정보를 입력하세요"
            value={addInfo}
            onChange={(e) => setAddInfo(e.target.value)}
          ></textarea>
        </div>
      </div>
    </div>
  );
};

export default Curriculum;