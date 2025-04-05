// src/api.js

import axios from 'axios';
import CONFIG from './config';

export const springApi = axios.create({
  baseURL: CONFIG.SPRING_BOOT.BASE_URL,
  headers: { 'Content-Type': 'application/json' },
});

export const fastApi = axios.create({
  baseURL: CONFIG.FAST_API.BASE_URL,
  headers: { 'Content-Type': 'application/json' },
});

export const fetchRecommendedCurriculum = async (keyword, add_info) => {
  const response = await springApi.post(CONFIG.SPRING_BOOT.ENDPOINTS.RECOMMEND, { keyword, add_info });
  return response.data;
};

export const addQuestionWithAI = async (question) => {
  const response = await springApi.post(CONFIG.SPRING_BOOT.ENDPOINTS.ADD_QUESTION, { question });
  return response.data;
};

export const getGraduationResult = async (formdata) => {
  const response = await springApi.post(CONFIG.SPRING_BOOT.ENDPOINTS.GRADUATION, formdata);
  return response.data; 
};

export const getTimetable = async (images) => {
  const response = await springApi.post(CONFIG.SPRING_BOOT.ENDPOINTS.TIMETABLE, { images });
  return response.data; 
};