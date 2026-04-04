// src/pages/Index.jsx

import React from 'react';
import { motion } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import { ArrowRight, CheckCircle, BookOpen, Clock } from 'lucide-react';

import Header from '../components/Header';
// import LogoImage from '../components/LogoImage';
import '../styles/index.css';
import '@fontsource/itim';
import '@fontsource/tillana/700.css';
import '@fontsource/ibm-plex-sans-kr';
import '@fontsource/ibm-plex-sans-kr/700.css';
import '@fontsource/baloo-2';
import '@fontsource/baloo-2/700.css';

const Index = () => {
  const navigate = useNavigate();

  const features = [
    {
      title: '졸업 요건 확인',
      description: (
        <>
          소프트웨어융합대학의<br/>
          학번별 졸업 요건을 확인하고<br/>
          현재 이수 상황을 분석합니다.
        </>
      ),
        icon: <CheckCircle className="feature-icon" />,
      action: () => navigate('/graduation-check'),
    },

    {
      title: '커리큘럼 추천',
      description: (
        <>
          생성형 AI를 활용하여<br/>
          관심 키워드와 관련된<br/>
          커리큘럼을 추천해 드립니다.
        </>
      ),
      icon: <BookOpen className="feature-icon" />,
      action: () => navigate('/curriculum'),
    },

    {
      title: '시간표 비교',
      description: (
        <>
          두 명 이상의 시간표를 비교하여<br/>
          공통적으로 비어 있는 시간을<br/>
          확인할 수 있습니다.
        </>
      ),
      icon: <Clock className="feature-icon" />,
      action: () => navigate('/timetable'),
    },
  ];

  return (
    <div className="page-container">
      <Header />

      {/* animation header removed */}
      <h2 className="features-header">🎓 Edu Path in CS</h2>
      <p>소프트웨어융합대학 학생들의 학업 계획에 필요한 기능을 제공합니다.</p>

      {/* features */}
      <div className="features-container">
        {features.map((feature, index) => (
          <div key={index} className="feature-card">
            {feature.icon}
            <h3 className="feature-title">{feature.title}</h3>
            <p className="feature-description">{feature.description}</p>
            <button className="feature-button" onClick={feature.action}>
              바로가기 <ArrowRight />
            </button>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Index;