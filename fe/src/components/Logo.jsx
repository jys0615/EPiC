// src/components/Logo.jsx

import React from 'react';
import { Link } from 'react-router-dom';
import '@fontsource/alkatra';
import '@fontsource/noto-sans';
import '@fontsource/noto-sans/700.css';
import '@fontsource/noto-sans-kr';
import '@fontsource/lexend';
import '@fontsource/lexend/700.css';

const Logo = ({ className }) => {
  return (
    <Link to="/index" className={className}>
      🎓EPiC
    </Link>
  );
};

export default Logo;