# 🛠️ Troubleshooting

개발 및 배포 과정에서 발생한 주요 이슈와 해결 방법을 정리합니다.

---

## 1. FE → BE API 호출 실패 (CORS / URL 문제)

**증상:** 브라우저에서 `localhost:8090`으로 직접 요청하여 배포 환경에서 API 호출 실패

**원인:** `config.js`에 `BASE_URL: 'http://localhost:8090/api'`로 하드코딩되어 있어, 브라우저가 서버 내부 주소로 요청을 시도함

**해결:** Nginx reverse proxy 도입
- FE 컨테이너의 nginx가 `/api/` 경로를 BE 컨테이너로 프록시
- `config.js`의 `BASE_URL`을 `/api`(상대경로)로 변경하여 컨테이너 간 내부 통신으로 처리

---

## 2. BE → AI FastAPI URL 하드코딩

**증상:** 졸업 요건 확인 기능에서 `500 Internal Server Error` 발생

**원인:** `GraduationService.java`에 FastAPI URL이 `http://localhost:8000`으로 하드코딩되어 있어, Docker 네트워크 내에서 AI 컨테이너에 접근 불가

**해결:** `@Value("${fastapi.url}")` 주입으로 변경
- `application.properties`에 `fastapi.url=${FASTAPI_URL:http://localhost:8000}` 설정
- `docker-compose.yml`의 BE 서비스에 `FASTAPI_URL: http://ai:8000` 환경변수 주입
- Docker 내부에서 서비스명(`ai`)으로 통신

---

## 3. 회원가입 버튼 동작 안 함

**증상:** 회원가입 폼 작성 후 버튼 클릭 시 아무 반응 없음

**원인:** `SignUp.jsx`의 `handleSignUp` 함수가 `logInputValues()` 호출만 하고 실제 API 요청 코드가 없었음

**해결:** `handleSignUp`에 `POST /api/member/signup` 호출 로직 추가
- 이메일 인증 완료 여부(`isEmailVerified`) 확인 후 요청
- 성공 시 `/login` 페이지로 이동

---

## 4. CSS 고정 픽셀로 인한 레이아웃 깨짐

**증상:** 배포 환경(다른 해상도)에서 UI가 header 뒤로 가려지거나 가로 스크롤 발생

**원인:** 모든 페이지가 `margin-top: -110px` 등 음수 마진 하드코딩, `min-width: 975px` 고정값 사용

**해결:** 전체 CSS 반응형 개선
- `--header-height: 70px` CSS 변수 도입
- 음수 마진 제거 → `padding-top: calc(var(--header-height) + 20px)` 적용
- 고정 픽셀 → `clamp()`, `%`, `max-width` 조합으로 교체
- 고정 `min-width` 제거

---

## 5. docker-compose `--force-recreate` 오류

**증상:** `docker-compose up -d --force-recreate` 실행 시 `KeyError: 'ContainerConfig'` 에러

**원인:** docker-compose v1.29.2가 최신 Docker Engine의 이미지 메타데이터 형식과 호환되지 않음

**해결:** `--force-recreate` 대신 `docker-compose down && docker-compose up -d` 사용

---

## 6. GCP / Oracle Cloud 배포 실패

**증상:** GCP 결제 오류(`OR-KCCSEH-11`), Oracle Cloud ARM 인스턴스 용량 부족(`Out of capacity`)

**원인:**
- GCP: 동일 Google 계정으로 이미 무료 크레딧 사용 이력 존재
- Oracle: 무료 ARM 인스턴스(Ampere)의 특정 리전 용량 소진

**해결:** Azure for Students 사용
- 학교 이메일로 $100 크레딧 신청 (신용카드 불필요)
- Standard_B1s VM (1 vCPU / 1GB RAM) 생성, Static IP 할당으로 VM 재시작 시 IP 변경 방지
