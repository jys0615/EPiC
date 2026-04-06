// src/pages/CurriculumResult.jsx
// CurriculumQ로 통합됨 — /curriculum-q로 리다이렉트

import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

const CurriculumResult = () => {
  const navigate = useNavigate();

  useEffect(() => {
    navigate('/curriculum-q', { replace: true });
  }, []);

  return null;
};

export default CurriculumResult;