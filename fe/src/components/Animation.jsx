// src/components/Animation.jsx

import React from 'react';
import { motion } from 'framer-motion';
import '../styles/animation.css';

const Animation = ({ text }) => {
  return (
    <motion.div
      className="epic-animation"
      initial={{ opacity: 0, x: -20 }}
      animate={{ opacity: 1, x: 20 }}
      transition={{
        duration: 0.8,
        ease: 'backInOut',
        repeat: Infinity,
        repeatType: 'reverse',
      }}
    >
      <h2>{text}</h2>
    </motion.div>
  );
};

export default Animation;
