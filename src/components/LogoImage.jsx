// src/components/LogoImage.jsx

import React from 'react';
import { Link } from 'react-router-dom';

import epiclogo from '../assets/epic-logo.svg';
import epiclogopng from '../assets/epiclogo.png';
const LogoImage = () => {

  return (
    <div className="logo-image">
      {/* <img src={epiclogo} alt="Epic Logo" className="logoimage" /> */}
      <Link to="/index">
        <img src={epiclogopng} alt="Epic Logo" className="logoimage" width={105} height={30} />
      </Link>
    </div>
  );
};

export default LogoImage;