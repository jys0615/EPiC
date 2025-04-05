// src/pages/Timetable-result.jsx

import React, { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom'; // 데이터 전달받기 위한 useLocation 가져오기
import { getTimetable } from '../api';
import Header from '../components/Header';
import '../styles/timetable-result.css';

const TimetableResult = () => {
  const location = useLocation(); // 이전 페이지에서 전달된 state 접근
  const { uploadBoxes } = location.state || { uploadBoxes: [] }; // 전달받은 업로드 박스 데이터

  const [resultImage, setResultImage] = useState(''); // API 결과 이미지 URL 저장
  const [isLoading, setIsLoading] = useState(false); // 로딩 상태 관리
  
  // 공강 시간표 API 호출
  useEffect(() => {
    const fetchTimetableResult = async () => {
      try {
        setIsLoading(true); // 로딩 시작
        const images = uploadBoxes.flatMap((box) => box.files.map((file) => file.src)); // 업로드된 이미지 URL 배열로 변환
        const response = await getTimetable(images); // API 호출
        setResultImage(response); // API에서 받은 결과 이미지 URL 저장
      } catch (error) {
        console.error('시간표 결과 가져오기 실패:', error);
      } finally {
        setIsLoading(false); // 로딩 종료
      }
    };

    if (uploadBoxes.length > 0) {
      fetchTimetableResult(); // 업로드된 이미지가 있을 경우에만 호출
    }
  }, [uploadBoxes]);


  //const temp_Image = 'https://placecats.com/g/300/200'; // 임시 이미지 URL

  const handleDownloadResultImage = async () => {
    try {
      const response = await fetch(resultImage, { mode: 'cors' });
      const blob = await response.blob();
      const link = document.createElement('a');
      link.href = URL.createObjectURL(blob);
      link.download = 'result_image.png';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    } catch (error) {
      console.error('이미지 다운로드 실패:', error);
    }
  };

  return (
    <div>
      <Header />
      <div className="timetable-intro">
        <h1 className="timetable-title">시간표 비교 결과</h1>
        <p className="timetable-description">
          겹치는 공강 시간을 확인하세요.
        </p>
      </div>

      {/* 업로드된 이미지와 temp_Image 표시 */}
      <div className="result-container">
      {/* 업로드된 이미지 */}
      <div className="uploaded-images">
        {uploadBoxes.map((box, index) => (
          <div key={box.id} className="image-group">
            <h3>시간표 {index + 1}</h3>
            <div className="image-row">
              {box.files.map((file, fileIndex) => (
                <img
                  key={fileIndex}
                  src={file.src}
                  alt={`Uploaded Timetable ${index + 1}`}
                  className="uploaded-image"
                />
              ))}
            </div>
          </div>
        ))}
      </div>
      
      <div className="temp-image-container">
          <h3>공강 시간표</h3>
          {isLoading ? (
            <p>결과를 불러오는 중입니다...</p>
          ) : resultImage ? (
            <>
              <img src={resultImage} alt="Result Timetable" className="temp-image" />
              <button onClick={handleDownloadResultImage} className="download-button">
                이미지 다운로드
              </button>
            </>
          ) : (
            <p>결과를 불러올 수 없습니다.</p>
          )}
        </div>
      </div>
    </div>
  );
};

export default TimetableResult;