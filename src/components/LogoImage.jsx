// src/components/LogoImage.jsx

import React from 'react';
import epiclogo from '../assets/epic-logo.svg';
import epiclogopng from '../assets/epiclogo.png';
const LogoImage = () => {

  return (
    <div className="logo-image">
      {/* <img src={epiclogo} alt="Epic Logo" className="logoimage" /> */}
      <img src={epiclogopng} alt="Epic Logo" className="logoimage" width={105} height={30}/>
    </div>
  );
};

export default LogoImage;