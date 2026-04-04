// src/api.js

import axios from 'axios';
import CONFIG from './config';

export const springApi = axios.create({
  baseURL: CONFIG.SPRING_BOOT.BASE_URL,
  headers: { 'Content-Type': 'application/json' },
});

export const fetchRecommendedCurriculum = async (keyword, add_info) => {
  const response = await springApi.post(CONFIG.SPRING_BOOT.ENDPOINTS.RECOMMEND, { keyword, add_info });
  return response.data;
};

export const addQuestionWithAI = async (question) => {
  console.log("🔴 [React → Spring] Sending:", { question }); // 디버깅용 출력
  const response = await springApi.post(
    CONFIG.SPRING_BOOT.ENDPOINTS.ADD_QUESTION, 
    {question} // { question: "텍스트" } 형식으로 수정
  );
  return response.data;
};

export const getGraduationResult = async (formdata) => {
  const response = await springApi.post(CONFIG.SPRING_BOOT.ENDPOINTS.GRADUATION, formdata, {headers: {
    "Content-Type": "multipart/form-data",
}});
  return response.data; 
};

export const getTimetable = async (images) => {
  const formData = new FormData();

  images.forEach((imageFile, index) => {
    // 실제 파일 객체가 아닌 경우는 제외
    if (imageFile instanceof File) {
      formData.append('images', imageFile);
    }
  });

  const response = await springApi.post(
    CONFIG.SPRING_BOOT.ENDPOINTS.TIMETABLE,
    formData,
    {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    }
  );

  return response.data.imageUrl; // 받아온 결과에서 imageUrl 꺼내기
};