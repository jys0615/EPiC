# EPiC 데이터 자동화 파이프라인

## 개요

매년 새 교육과정 PDF가 발행될 때 과목 데이터와 졸업요건 JSON을 자동 업데이트하는 파이프라인.

**동작 방식**: 이미지 기반 PDF → PyMuPDF로 렌더링 → GPT-4o Vision OCR → JSON 업데이트

---

## 사용법

### 1. 새 교육과정 PDF 입수

경희대학교 소프트웨어융합대학 컴퓨터공학과 교육과정 페이지에서 PDF 다운로드:
- https://ce.khu.ac.kr/ce25/user/bbs/BMSR00048/list.do?menuNo=21600025

### 2. 파이프라인 실행

```bash
# 프로젝트 루트에서 실행
cd /path/to/EPiC

# 결과 미리 확인 (파일 수정 없음)
python3 scripts/pipeline/parse_cse_pdf.py \
  --pdf "path/to/2026 컴퓨터공학과 교육과정.pdf" \
  --dry-run

# 실제 업데이트 (description 파싱 포함, 약 $0.30~0.40 소요)
python3 scripts/pipeline/parse_cse_pdf.py \
  --pdf "path/to/2026 컴퓨터공학과 교육과정.pdf"

# description 파싱 건너뜀 (빠르고 저렴, 약 $0.05 소요)
python3 scripts/pipeline/parse_cse_pdf.py \
  --pdf "path/to/2026 컴퓨터공학과 교육과정.pdf" \
  --skip-desc
```

### 3. 업데이트 결과 확인 후 커밋

```bash
git add ai/data/cse_grad_2019_2025.json ai/data/cse_courses.json
git commit -m "data: 2026 컴퓨터공학과 교육과정 반영"
```

---

## 파싱 대상 페이지

| PDF 페이지 | 내용 | 업데이트 대상 |
|---|---|---|
| 7페이지 | [표5] 입학년도별 졸업이수 요건표 | `cse_grad_2019_2025.json` |
| 8-9페이지 | [별표1] 교육과정 편성표 (과목 목록) | `cse_courses.json` |
| 13-27페이지 | [별표5] 교과목 해설 (과목 설명) | `cse_courses.json` 의 `description` 필드 |

---

## 안전 설계

- **기존 졸업요건 보존**: 이미 JSON에 있는 연도는 덮어쓰지 않고 **새 연도만 추가**
- **기존 과목 설명 보존**: `description`이 이미 있는 과목은 덮어쓰지 않음
- **개설학기 업데이트**: `semester` 필드는 최신 PDF 기준으로 업데이트
- **dry-run 지원**: `--dry-run` 플래그로 실제 파일 수정 없이 결과 미리 확인

---

## 비용 (GPT-4o Vision)

| 실행 옵션 | 처리 페이지 수 | 예상 비용 |
|---|---|---|
| `--skip-desc` | 3페이지 | ~$0.05 |
| 전체 실행 | ~18페이지 | ~$0.30~0.40 |

---

## 의존성

```bash
pip3 install pymupdf openai
```

환경변수: `.env` 파일에 `OPENAI_API_KEY` 필요
