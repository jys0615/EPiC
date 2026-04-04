// src/config.js

const CONFIG = {
  SPRING_BOOT: {
    BASE_URL: '/api',
    ENDPOINTS: {
      LOGIN: '/member/login',
      SIGNUP: '/member/signup',
      CHECK_LOGIN_ID: '/member/check-login-id',
      VERIFY_EMAIL: '/member/verify-email',

      RECOMMEND: '/curriculum/recommend',
      ADD_QUESTION: '/curriculum/add-ques',

      GRADUATION: '/graduation/check',

      TIMETABLE: '/timetable/get-timetable',
    },
  },

  EMAIL_SERVICE: {
    BASE_URL: '/api/email',
    ENDPOINTS: {
      SEND_AUTH_EMAIL: '/send',
      VERIFY_EMAIL_CODE: '/auth',
    },
  },
};

export default CONFIG;