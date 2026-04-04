// src/pages/LogIn.jsx

import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
// import Animation from '../components/Animation';
import '../styles/login.css';
import '@fontsource/alkatra';
import CONFIG from '../config';

const LogIn = () => {
  const [loginId, setLoginId] = useState('');
  const [password, setPassword] = useState('');
  const [errorMessage, setErrorMessage] = useState('');

  const navigate = useNavigate();

  const handleLogin = async () => {
    console.log('이메일:', loginId);
    console.log('비밀번호:', password);

    try {
      const response = await fetch(
        `${CONFIG.SPRING_BOOT.BASE_URL}${CONFIG.SPRING_BOOT.ENDPOINTS.LOGIN}`,
        {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({ loginId, password }),
        },
      );

      if (response.ok) {
        const data = await response.json();
        console.log('로그인 성공:', data);
        navigate('/index');
      } else {
        const errorData = await response.json();
        setErrorMessage(errorData.message || '로그인 실패');
      }
    } catch (error) {
      console.error('에러 발생:', error);
      setErrorMessage('로그인 중 오류 발생');
    }
  };

  return (
    <>
      <div className="login-container">
        <div></div>
        <div className="login-box">
          {/* <Animation text="🎓Edu Path in CS" /> */}
          
          <div className="epic-logo-text">
            <h2>🎓 Edu Path in CS</h2>
          </div>
          
          <div className="login-header">
            <h2>로그인</h2>
          </div>
          <input
            type="loginId"
            placeholder="이메일"
            className="input-field"
            value={loginId}
            onChange={(e) => setLoginId(e.target.value)}
          />
          <input
            type="password"
            placeholder="비밀번호"
            className="input-field"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
          {errorMessage && <div className="error-message">{errorMessage}</div>}
          <button className="login-button" onClick={handleLogin}>
            로그인
          </button>
        </div>
      </div>
    </>
  );
};

export default LogIn;
