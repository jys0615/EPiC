# 🔨 EPiC Refactoring Plan

현재 아키텍처의 문제점을 분석하고, 성능·품질·안정성 향상을 위한 리팩토링 계획을 정리합니다.

---

## 📊 현재 구조 요약

```
Browser ──HTTP──▶ FE/Nginx ──HTTP(REST)──▶ BE/Spring Boot ──HTTP(REST)──▶ AI/FastAPI
                                                  │
                                           Redis / MongoDB Atlas
```

| 구간 | 현재 방식 | 문제점 |
|------|-----------|--------|
| Browser → Nginx | HTTP | 암호화 없음 |
| Nginx → BE | HTTP REST (JSON) | 매 요청마다 연결 수립 |
| BE → FastAPI | HTTP REST (RestTemplate, 동기) | 블로킹, 스레드 낭비 |
| 과목 검색 | 키워드 문자열 완전 일치 | 유사어/오타 대응 불가 |
| 대화 기록 | 전역 메모리 변수 | 사용자 간 공유, 재시작 시 소멸 |
| 데이터 | 정적 JSON 파일 | 학기마다 수동 업데이트 필요 |

---

## 🗂️ 리팩토링 항목

---

### 1. HTTPS 적용

**현재:** Browser ↔ 서버 간 HTTP 평문 통신
**문제:** 로그인 정보, API 요청/응답 데이터가 암호화 없이 전송됨

**개선 방향:**
- 도메인 연결 후 Let's Encrypt(Certbot)로 무료 SSL 인증서 발급
- Nginx에 HTTPS 설정 추가
- HTTP → HTTPS 자동 리다이렉트

**효과:** 보안 강화 + HTTP/2 활성화 전제 조건 충족

---

### 2. HTTP/2 적용 (Nginx)

**현재:** HTTP/1.1 — 요청마다 별도 연결, 헤더 중복 전송
**문제:** 여러 API를 동시에 호출하는 페이지에서 연결 오버헤드 발생

**개선 방향:**
- Nginx에서 HTTP/2 활성화 (HTTPS 적용 후 설정 한 줄로 가능)
- 멀티플렉싱으로 하나의 연결에서 여러 요청 동시 처리

**효과:** 초기 페이지 로딩 속도 개선, 연결 오버헤드 감소

---

### 3. Nginx Gzip 압축

**현재:** 응답 데이터를 압축 없이 전송
**문제:** JSON, JS, CSS 등 텍스트 기반 데이터가 불필요하게 큼

**개선 방향:**
```nginx
gzip on;
gzip_types text/plain application/json application/javascript text/css;
gzip_min_length 1000;
```

**효과:** 텍스트 응답 크기 30~70% 감소, 전송 속도 향상

---

### 4. BE → FastAPI 비동기 전환 (GraduationService)

**현재:** `RestTemplate` (동기 블로킹)
**문제:** FastAPI 응답을 기다리는 동안 스레드가 점유됨 → 동시 요청 처리 능력 저하

**개선 방향:**
- `GraduationService`의 `RestTemplate` → `WebClient` (비동기, 논블로킹)로 전환
- Timetable은 이미 `WebClient` 사용 중 → Graduation도 동일하게 통일

**효과:** 스레드 블로킹 제거, 동시 요청 처리 능력 향상

---

### 5. BE → FastAPI 통신: gRPC 도입

**현재:** HTTP REST (JSON)
**문제:**
- JSON 직렬화/역직렬화 오버헤드
- 매 요청마다 HTTP 헤더 전송
- PDF/이미지 파일 전송 시 multipart 처리 복잡

**개선 방향:**
- BE ↔ FastAPI 내부 통신을 gRPC (Protocol Buffers) 로 전환
- `.proto` 파일로 인터페이스 명세 → Java(BE), Python(AI) 양측 코드 자동 생성
- PDF/이미지는 gRPC Streaming으로 청크 전송

**효과:**
- JSON 대비 직렬화 속도 5~10배 향상
- 페이로드 크기 30~50% 감소
- 타입 안전성 보장

---

### 6. Redis 캐싱 확대

**현재:** Redis가 존재하나 세션/이메일 인증 외 활용 미흡
**문제:** 동일한 학과+학번 조합의 졸업 요건 분석, 동일 키워드 커리큘럼 추천을 매번 GPT 호출

**개선 방향:**

| 캐싱 대상 | 캐시 키 | TTL |
|-----------|---------|-----|
| 졸업 요건 분석 결과 | `grad:{department}:{studentId}:{fileHash}` | 24시간 |
| 커리큘럼 추천 결과 | `recommend:{keyword}:{addInfo_hash}` | 6시간 |
| 과목 목록 | `courses:all` | 24시간 |

**효과:** GPT API 호출 횟수 감소 → 비용 절감 + 응답시간 수초 → 수십ms

---

### 7. AI 사용자 세션 분리

**현재:** `conversation_history`, `user_interest_memory`가 전역 변수
**문제:**
- 모든 사용자가 동일한 대화 기록 공유
- 서버 재시작 시 모든 대화 기록 소멸

**개선 방향:**
- 요청 시 `session_id` (UUID) 발급 및 헤더로 전달
- Redis에 세션별 대화 기록 저장 (`TTL: 30분`)
- FastAPI에서 `session_id`로 개인 대화 컨텍스트 관리

**효과:** 사용자 간 대화 격리, 재시작 후에도 대화 유지

---

### 8. RAG (Retrieval-Augmented Generation) 도입

**현재:** 키워드 단순 문자열 완전 일치 → 상위 5개 과목 GPT에 전달
**문제:** `"백엔드"` 검색 시 keywords에 `"백엔드"`가 없으면 결과 없음. 유사어/오타/영한 혼용 불가

**개선 방향:**
1. 과목 데이터를 벡터 임베딩으로 변환 (`text-embedding-3-small`)
2. 벡터 DB에 저장 (`ChromaDB` 또는 `FAISS`)
3. 사용자 질의를 임베딩 → 코사인 유사도로 관련 과목 검색
4. 검색된 과목을 GPT 컨텍스트로 전달

**효과:** 의미 기반 검색으로 추천 품질 대폭 향상

---

### 9. GPT 모델 업그레이드

**현재:** `gpt-3.5-turbo`
**문제:** 복잡한 졸업 요건 분석, 다중 조건 판단에서 오류 빈번

**개선 방향:**
- 졸업 요건 분석: `gpt-4o-mini` 또는 `gpt-4o`로 전환
- 커리큘럼 추천/챗봇: `gpt-4o-mini` (비용·성능 균형)

**효과:** 분석 정확도 향상, 환각(Hallucination) 감소

---

### 10. 졸업 요건 계산 로직 분리

**현재:** PDF 텍스트 전체를 GPT에 전달 → GPT가 학점 계산까지 수행
**문제:** GPT 계산 오류 발생 가능, 토큰 낭비, 응답 느림

**개선 방향:**
- BE 또는 AI에서 PDF 파싱 후 학점 직접 계산 (규칙 기반)
- GPT는 계산된 결과를 자연어로 설명하는 역할만 담당
- 계산 결과를 구조화된 JSON으로 먼저 추출 후 GPT 호출

**효과:** 계산 정확도 100%, GPT 토큰 절감, 응답시간 단축

---

### 11. 시간표 비교 → Vision 기반 전환

**현재:** OpenCV 픽셀 분석 + Tesseract OCR
**문제:** 시간표 앱·스타일마다 색상 기준이 달라 오탐 발생. 2장만 지원

**개선 방향:**
- GPT-4o Vision API로 시간표 이미지 직접 분석
- 프롬프트로 요일/시간 구조 추출 → 공강 계산
- 이미지 수 제한 없이 N명 비교 가능

**효과:** 어떤 스타일의 시간표도 처리, 다인원 비교 지원

---

### 12. FE 번들 최적화

**현재:** 모든 페이지 코드가 `index-xxx.js` 단일 파일로 번들링
**문제:** 첫 페이지 로딩 시 불필요한 코드까지 전부 다운로드

**개선 방향:**
- Vite의 `React.lazy()` + `Suspense`로 페이지별 코드 스플리팅
- 각 페이지 진입 시 해당 청크만 로드
- 이미지 WebP 변환으로 용량 절감

**효과:** 초기 로딩 속도 향상, TTI(Time to Interactive) 개선

---

## 📅 단계별 실행 계획

### Phase 1 — 즉시 적용 가능 (1~2일)
> 코드 변경 최소, 효과 즉각

- [ ] Nginx Gzip 압축
- [ ] GPT 모델 업그레이드 (`gpt-3.5-turbo` → `gpt-4o-mini`)
- [ ] BE Graduation: `RestTemplate` → `WebClient` 전환

### Phase 2 — 단기 개선 (1~2주)
> 아키텍처 소폭 변경, 효과 큼

- [ ] AI 사용자 세션 분리 (Redis 활용)
- [ ] Redis 캐싱 확대 (졸업 분석 / 커리큘럼 추천)
- [ ] HTTPS + HTTP/2 적용 (도메인 필요)

### Phase 3 — 중장기 개선 (1개월~)
> 설계 변경 필요, 임팩트 매우 큼

- [ ] RAG 도입 (벡터 DB + 임베딩)
- [ ] 졸업 요건 계산 로직 분리
- [ ] gRPC 도입 (BE ↔ FastAPI)
- [ ] 시간표 비교 Vision 기반 전환
- [ ] FE 코드 스플리팅

---

## 📈 예상 효과 요약

| 항목 | 현재 | 개선 후 |
|------|------|---------|
| 졸업 분석 응답 (캐시 히트) | 5~10초 | < 100ms |
| 커리큘럼 추천 (캐시 히트) | 3~5초 | < 100ms |
| 과목 검색 품질 | 완전 일치만 | 의미 기반 유사도 검색 |
| 동시 사용자 처리 | 스레드 블로킹 | 논블로킹 비동기 |
| 데이터 보안 | HTTP 평문 | HTTPS 암호화 |
| 사용자 대화 격리 | 전역 공유 ❌ | 세션별 독립 ✅ |
