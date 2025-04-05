// src/pages/GraduationResult.jsx

import React from 'react';
import { useLocation } from 'react-router-dom';
import Header from '../components/Header';
import '../styles/graduation-result.css';
import { Link } from 'react-router-dom';

const Graduation = () => {
  const location = useLocation();
  const { department, studentId } = location.state || {}; // 전달된 데이터 추출
  const temp_string = 'ㅁㅁㅁㅁㅁㅁㅁㅁㅁ';

  return (
    <div>
      <Header />
      <div className="graduation-intro">
        <h1 className="graduation-title">졸업 요건 확인</h1>
        <p className="graduation-description">
          학과와 학번을 선택하고 성적표를 업로드하여 졸업 요건을 확인하세요.
        </p>
      </div>
      <div className="graduation-container">
        {/* 정보 입력 섹션 */}
        <div className="info-section">
          <h2 className="info-section-title">내 정보</h2>
          <div className="form-group-horizontal">
            <div className="form-group-text" id="department-title">
              <h4>학과</h4>
            </div>
            <div className="form-group-box">
              <p>{department}</p>
            </div>
            <div className="form-group-text" id="studentId-title">
              <h4>학번</h4>
            </div>
            <div className="form-group-box">
              <p>{studentId}</p>
            </div>
          </div>
        </div>

        {/* 성적표 업로드 섹션 */}
        <div className="result-section">
          <h2 className="info-section-title">졸업 요건 결과</h2>
          <div className="result-box">
            <p>{temp_string}</p>
          </div>
        </div>

        {/* 분석 버튼 */}
        <Link to="/curriculum">
          <button type="button" className="submit-button">
            추천 커리큘럼 알아보기
          </button>
        </Link>
      </div>
    </div>
  );
};

export default Graduation;