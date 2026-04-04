// src/pages/SignUp.jsx

import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/signup.css';
import '@fontsource/alkatra';
import CONFIG from '../config';

const SignUp = () => {
  const [loginId, setLoginId] = useState('');
  const [password, setPassword] = useState('');
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [emailCode, setEmailCode] = useState('');
  const [errorMessage, setErrorMessage] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const [isEmailVerified, setIsEmailVerified] = useState(false);

  const navigate = useNavigate();

  const handleSendAuthEmail = async () => {
    try {
      const response = await fetch(`${CONFIG.EMAIL_SERVICE.BASE_URL}${CONFIG.EMAIL_SERVICE.ENDPOINTS.SEND_AUTH_EMAIL}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email }),
      });

      if (response.ok) {
        setSuccessMessage('인증 이메일이 발송되었습니다.');
        setErrorMessage('');
      } else {
        const errorData = await response.json();
        setErrorMessage(errorData.message || '인증 이메일 발송 실패');
      }
    } catch (error) {
      console.error('에러 발생:', error);
      setErrorMessage('인증 이메일 발송 중 오류 발생');
    }
  };

  const handleVerifyEmailCode = async () => {
    try {
      const response = await fetch(
        `${CONFIG.EMAIL_SERVICE.BASE_URL}${CONFIG.EMAIL_SERVICE.ENDPOINTS.VERIFY_EMAIL_CODE}?receiver=${email}&code=${emailCode}`,
        { method: 'GET' }
      );

      if (response.ok) {
        setSuccessMessage('이메일 인증이 완료되었습니다.');
        setErrorMessage('');
        setIsEmailVerified(true);
      } else {
        const errorData = await response.json();
        setErrorMessage(errorData.message || '인증 코드 불일치');
      }
    } catch (error) {
      console.error('에러 발생:', error);
      setErrorMessage('인증 코드 확인 중 오류 발생');
    }
  };

  const handleSignUp = async () => {
    if (!isEmailVerified) {
      setErrorMessage('이메일 인증을 먼저 완료해주세요.');
      return;
    }

    try {
      const response = await fetch(
        `${CONFIG.SPRING_BOOT.BASE_URL}${CONFIG.SPRING_BOOT.ENDPOINTS.SIGNUP}`,
        {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ loginId, password, name, email }),
        }
      );

      if (response.ok) {
        navigate('/login');
      } else {
        const errorData = await response.json();
        setErrorMessage(errorData.message || '회원가입 실패');
      }
    } catch (error) {
      console.error('에러 발생:', error);
      setErrorMessage('회원가입 중 오류 발생');
    }
  };

  return (
    <div className="signup-container">
      <div className="epic-logo-text">
        <h2>🎓 Edu Path in CS</h2>
      </div>

      <div className="signup-header">
        <h2>계정 만들기</h2>
      </div>

      <div className="signup-box">
        <input
          type="text"
          placeholder="아이디"
          className="input-field"
          value={loginId}
          onChange={(e) => setLoginId(e.target.value)}
        />
        <input
          type="password"
          placeholder="비밀번호 (8~16자의 영문, 숫자, 특수 기호)"
          className="input-field"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
        <input
          type="text"
          placeholder="이름(실명)"
          className="input-field"
          value={name}
          onChange={(e) => setName(e.target.value)}
        />

        <div className="verification-container">
          <input
            type="email"
            placeholder="경희대학교 이메일"
            className="email-field"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
          <button className="verification-button" onClick={handleSendAuthEmail}>
            인증 메일 발송
          </button>
        </div>

        <div className="verification-container">
          <input
            type="text"
            placeholder="인증 코드"
            className="email-field"
            value={emailCode}
            onChange={(e) => setEmailCode(e.target.value)}
          />
          <button className="verification-button" onClick={handleVerifyEmailCode}>
            인증 코드 확인
          </button>
        </div>

        {successMessage && <div className="success-message">{successMessage}</div>}
        {errorMessage && <div className="error-message">{errorMessage}</div>}

        <div className="signup-button-container">
          <button className="signup-button" onClick={handleSignUp}>
            회원가입
          </button>
        </div>
      </div>
    </div>
  );
};

export default SignUp;
