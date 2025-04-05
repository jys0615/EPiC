// src/config.js

const CONFIG = {
  // Spring Boot server

  SPRING_BOOT: {
    BASE_URL: 'http://35.216.79.131:8090/api',
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
    BASE_URL: 'http://35.216.79.131:8090/api/email',
    ENDPOINTS: {
      SEND_AUTH_EMAIL: '/send',
      VERIFY_EMAIL_CODE: '/auth',
    },
  },

  // FastAPI AI server
  FAST_API: {
    BASE_URL: 'http://localhost:8000',
    ENDPOINTS: {
      RECOMMEND: '/recommend',
      CHAT: '/chat',
    },
  },
};

export default CONFIG;