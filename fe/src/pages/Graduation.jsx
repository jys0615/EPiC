// src/pages/GraduationResult.jsx

import React, { useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { useDropzone } from 'react-dropzone'; // react-dropzone 가져오기
import Header from '../components/Header';
import '../styles/graduation.css';

const Graduation = () => {
  const [department, setDepartment] = useState('');
  const [studentId, setStudentId] = useState('');
  const [file, setFile] = useState(null); // 파일 객체 상태 추가
  const navigate = useNavigate();

  // 파일 드롭 핸들러
  const onDrop = useCallback((acceptedFiles) => {
    if (acceptedFiles.length > 0) {
      setFile(acceptedFiles[0]); // 첫 번째 파일 객체 저장
    }
  }, []);

  // react-dropzone 설정
  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    accept: { 'application/pdf': ['.pdf'] }, // MIME 타입과 확장자 설정
    maxSize: 10 * 1024 * 1024, // 최대 파일 크기: 10MB
  });

  // 버튼 클릭 핸들러
  const handleAnalyzeClick = () => {
    if (department && studentId && file) {
      console.log("전달 데이터:", department, studentId, file);
      navigate('/graduation-result', {
        state: { department, studentId, file }, // 파일 객체 전달
      });
    } else {
      alert('모든 정보를 입력해주세요!');
    }
  };

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
          <h2 className="info-section-title">정보 입력</h2>
          <div className="form-group-horizontal">
            <div className="form-group-text">
              <h4>학과 선택</h4>
            </div>
            <div className="form-group">
              <select
                id="department"
                className="form-select"
                value={department}
                onChange={(e) => setDepartment(e.target.value)}
              >
                <option value="">학과를 선택하세요.</option>
                <option value="컴퓨터공학과">컴퓨터공학과</option>
                <option value="소프트웨어융합학과">소프트웨어융합학과</option>
                <option value="인공지능학과">인공지능학과</option>
              </select>
            </div>
            <div className="form-group-text">
              <h4>학번 선택</h4>
            </div>
            <div className="form-group">
              <select
                id="studentId"
                className="form-select"
                value={studentId}
                onChange={(e) => setStudentId(e.target.value)}
              >
                <option value="">학번을 선택하세요.</option>
                <option value="2019">2019</option>
                <option value="2020">2020</option>
                <option value="2021">2021</option>
                <option value="2022">2022</option>
                <option value="2023">2023</option>
                <option value="2024">2024</option>
                <option value="2025">2025</option>
              </select>
            </div>
          </div>
        </div>

        {/* 성적표 업로드 섹션 */}
        <div className="upload-section">
          <h2 className="info-section-title">진단표 업로드(PDF)</h2>
          {/* 드래그 앤 드롭 영역 */}
          <div
            {...getRootProps()}
            className={`upload-box ${isDragActive ? 'active' : ''}`}
          >
            <input {...getInputProps()} />
            {isDragActive ? (
              <p>파일을 여기에 놓으세요...</p>
            ) : (
              <>
                <p>PDF 파일은 드래그하거나 클릭하여 업로드하세요.</p>
                {file && (
                  <p style={{ color: '#333' }}>업로드된 파일: {file.name}</p>
                )}
              </>
            )}
          </div>
        </div>

        {/* 분석 버튼 */}
        <button type="button" onClick={handleAnalyzeClick} className="grad-submit-button">
          진단표 분석하기
        </button>

        {/* <button type="button" onClick={handleAnalyzeClick} className="submit-button">
          성적표 분석하기
        </button> */}
      </div>
    </div>
  );
};

export default Graduation;