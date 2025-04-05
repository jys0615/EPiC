// src/components/Header.jsx

import React from 'react';

// import Logo from './Logo';
import LogoImage from './LogoImage';
import NavLinks from './NavLinks';
import AccountIcon from './AccountIcon';
import '../styles/header.css';
import '@fontsource/alkatra';
import '@fontsource/lexend';


const Header = () => {
  return (
    <header className="header-container">
      <div className="header-content">
        {/* <Logo className="app-name" /> */}
        <LogoImage className="logo-image" />

        {/* top navigation */}
        <nav className="nav-links">
          <NavLinks />

          {/* account icon */}
          <div className="account-btn">
            <span className="account-icon">
              <AccountIcon />
            </span>
          </div>
        </nav>
      </div>
    </header>
  );
};

export default Header;