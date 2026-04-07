// src/pages/CurriculumQ.jsx
// Curriculum.jsx로 통합됨 — /curriculum으로 리다이렉트

import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

const CurriculumQ = () => {
  const navigate = useNavigate();
  useEffect(() => {
    navigate('/curriculum', { replace: true });
  }, []);
  return null;
};

export default CurriculumQ;
