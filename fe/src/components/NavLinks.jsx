// src/components/NavLinks.jsx

import React from 'react';
import { Link } from 'react-router-dom';

const NavLinks = () => {
  return (
    <nav className="nav-links">
      <Link to="/graduation-check" className="nav-link">졸업 요건 확인</Link>
      <Link to="/curriculum" className="nav-link">커리큘럼 추천</Link>
      <Link to="/timetable" className="nav-link">시간표 비교</Link>
    </nav>
  );
};

export default NavLinks;