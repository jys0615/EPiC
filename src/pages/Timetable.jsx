// src/pages/Timetable.jsx

import React, { useState, useCallback } from 'react';
import { useDropzone } from 'react-dropzone'; // react-dropzone 가져오기
import { useNavigate } from 'react-router-dom'; // 페이지 이동을 위한 useNavigate 가져오기
import Header from '../components/Header';
import '../styles/timetable.css';

const Timetable = () => {
  const [uploadBoxes, setUploadBoxes] = useState([
    { id: 1, files: [] }, // 첫 번째 업로드 박스
    { id: 2, files: [] }  // 두 번째 업로드 박스 추가
  ]);
  const navigate = useNavigate();

  const addUploadBox = () => {
    setUploadBoxes((prevBoxes) => [
      ...prevBoxes,
      { id: prevBoxes.length + 1, files: [] },
    ]);
  };

  const onDrop = useCallback((id, acceptedFiles) => {
    const imageFiles = acceptedFiles.map((file) => ({
      src: URL.createObjectURL(file),
    }));

    setUploadBoxes((prevBoxes) =>
      prevBoxes.map((box) =>
        box.id === id ? { ...box, files: imageFiles } : box
      )
    );
  }, []);

  const handleAnalyzeClick = () => {
    const hasFiles = uploadBoxes.every((box) => box.files.length > 0);

    if (hasFiles) {
      navigate('/timetable-result', { state: { uploadBoxes } });
    } else {
      alert('모든 박스에 시간표를 업로드해주세요!');
    }
  };

  return (
    <div>
      <Header />
      <div className="time-container">
      <div className="timetable-intro">
        <h1 className="timetable-title">시간표 비교</h1>
        <p className="timetable-description">
          두 명 이상의 시간표를 업로드하여 공통으로 비어있는 시간을 확인하세요.
        </p>
      </div>
      </div>

      <div className="upload-box-container">
        {uploadBoxes.map((box, index) => (
          <div key={box.id} className="upload-box">
            <h3>시간표 {index + 1}</h3>
            {box.files.length === 0 ? (
              <DropzoneBox id={box.id} onDrop={onDrop} />
            ) : (
              box.files.map((file, fileIndex) => (
                <img
                  key={fileIndex}
                  src={file.src}
                  alt={`Uploaded Timetable ${index + 1}`}
                  className="uploaded-image"
                />
              ))
            )}
          </div>
        ))}
        <button onClick={addUploadBox} className="add-box-button">
          + 업로드 박스 추가
        </button>
      </div>

      <button type="button" onClick={handleAnalyzeClick} className="submit-button">
        시간표 분석하기
      </button>
    </div>
  );
};

const DropzoneBox = ({ id, onDrop }) => {
  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop: (acceptedFiles) => onDrop(id, acceptedFiles),
    accept: {
      'image/jpeg': ['.jpg', '.jpeg'],
      'image/png': ['.png'],
    },
    maxSize: 5 * 1024 * 1024,
  });

  return (
    <div
      {...getRootProps()}
      className={`dropzone ${isDragActive ? 'active' : ''}`}
    >
      <input {...getInputProps()} />
      {isDragActive ? (
        <p>여기에 파일을 드롭하세요...</p>
      ) : (
        <p>파일을 드래그하거나 클릭하여 업로드하세요</p>
      )}
    </div>
  );
};

export default Timetable;