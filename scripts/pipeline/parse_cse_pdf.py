#!/usr/bin/env python3
"""
컴퓨터공학과 교육과정 PDF 파서 (GPT Vision 기반 자동화 파이프라인)

[동작 방식]
  이미지 기반 PDF이므로 pdfplumber로 직접 텍스트 추출 불가.
  PyMuPDF로 각 페이지를 이미지로 렌더링 → GPT-4o Vision으로 OCR + 구조화.

[처리 페이지]
  - Page 7  : [표5]   입학년도별 졸업이수 요건표
  - Page 8-9: [별표1] 교육과정 편성표 (과목 목록)
  - Page 13~: [별표5] 교과목 해설 (description)

[사용법]
  python parse_cse_pdf.py --pdf "2026 컴퓨터공학과 교육과정.pdf"
  python parse_cse_pdf.py --pdf "2026 컴퓨터공학과 교육과정.pdf" --dry-run

[출력]
  - ai/data/cse_grad_2019_2025.json  (졸업요건 업데이트)
  - ai/data/cse_courses.json          (과목 목록 업데이트)

[비용]
  GPT-4o-mini Vision: 약 $0.01~0.02 / 페이지
  전체 실행 시 약 $0.20~0.40 (34페이지 중 핵심 페이지만 처리)
"""

from __future__ import annotations

import base64
import json
import os
import re
import argparse
import sys
import time
from typing import Optional

import fitz  # PyMuPDF
from openai import OpenAI

# ──────────────────────────────────────────
# 설정
# ──────────────────────────────────────────

BASE_DIR = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
DATA_DIR = os.path.join(BASE_DIR, "ai", "data")
ENV_PATH = os.path.join(BASE_DIR, ".env")

GRAD_JSON_PATH = os.path.join(DATA_DIR, "cse_grad_2019_2025.json")
COURSES_JSON_PATH = os.path.join(DATA_DIR, "cse_courses.json")

# 처리할 페이지 인덱스 (0-based)
PAGE_GRAD_TABLE = 6          # [표5] 졸업요건 (7번째 페이지)
PAGE_COURSES_START = 7       # [별표1] 과목 편성표 시작 (8번째 페이지)
PAGE_COURSES_END = 8         # [별표1] 끝 (9번째 페이지)
PAGE_DESC_START = 12         # [별표5] 교과목 해설 시작 (13번째 페이지)
PAGE_DESC_END = 26           # [별표5] 끝 (27번째 페이지)

RENDER_DPI = 2.0             # 렌더링 배율 (높을수록 정확하나 느림)
GPT_MODEL = "gpt-4o"        # Vision은 gpt-4o 필요 (mini 지원 확인 필요)


# ──────────────────────────────────────────
# 환경 변수 로딩
# ──────────────────────────────────────────

def load_env(path: str) -> dict:
    """간단한 .env 파서"""
    env = {}
    if not os.path.exists(path):
        return env
    with open(path, "r") as f:
        for line in f:
            line = line.strip()
            if line and not line.startswith("#") and "=" in line:
                k, v = line.split("=", 1)
                env[k.strip()] = v.strip()
    return env


# ──────────────────────────────────────────
# PDF → 이미지 변환
# ──────────────────────────────────────────

def pdf_page_to_base64(pdf_path: str, page_idx: int, dpi: float = 2.0) -> str:
    """PDF 특정 페이지를 base64 인코딩 PNG로 변환"""
    doc = fitz.open(pdf_path)
    page = doc[page_idx]
    mat = fitz.Matrix(dpi, dpi)
    pix = page.get_pixmap(matrix=mat)
    img_bytes = pix.tobytes("png")
    return base64.standard_b64encode(img_bytes).decode("utf-8")


# ──────────────────────────────────────────
# GPT Vision 호출
# ──────────────────────────────────────────

def ask_gpt_vision(client: OpenAI, image_b64: str, prompt: str) -> str:
    """GPT-4o Vision으로 이미지 분석"""
    response = client.chat.completions.create(
        model=GPT_MODEL,
        messages=[
            {
                "role": "user",
                "content": [
                    {
                        "type": "image_url",
                        "image_url": {
                            "url": f"data:image/png;base64,{image_b64}",
                            "detail": "high"
                        }
                    },
                    {"type": "text", "text": prompt}
                ]
            }
        ],
        max_tokens=4096,
        temperature=0
    )
    return response.choices[0].message.content


def extract_json_from_response(text: str) -> any:
    """GPT 응답에서 JSON 블록 추출"""
    # ```json ... ``` 블록 우선 추출
    match = re.search(r'```json\s*([\s\S]+?)\s*```', text)
    if match:
        return json.loads(match.group(1))
    # 직접 JSON 파싱 시도
    try:
        return json.loads(text.strip())
    except json.JSONDecodeError:
        # { 또는 [ 로 시작하는 부분 찾기
        for start_char in ['{', '[']:
            idx = text.find(start_char)
            if idx != -1:
                try:
                    return json.loads(text[idx:])
                except json.JSONDecodeError:
                    pass
    return None


# ──────────────────────────────────────────
# 1. [표5] 졸업요건 파싱
# ──────────────────────────────────────────

GRAD_PROMPT = """
이 이미지는 경희대학교 컴퓨터공학과 [표5] '입학년도에 따른 컴퓨터공학과 졸업이수 요건표'입니다.

테이블 컬럼 순서 (단일전공과정 기준):
  입학년도 | 졸업이수학점 | 전공기초 | 전공필수 | 전공선택 | 합계 | (다전공 컬럼들...) | 타전공인정학점

단일전공과정의 첫 4개 컬럼(전공기초, 전공필수, 전공선택, 합계)에서:
- foundation_credits = "전공기초" 학점 (두 번째 숫자 컬럼)
- major_required_credits = "전공필수" 학점 (세 번째 숫자 컬럼)
- major_elective_credits = "전공선택" 학점 (네 번째 숫자 컬럼)
- total_credits = "졸업이수학점" (첫 번째 숫자 컬럼)

예시 (2024년 행): 130 | 15 | 45 | 27 | 87 → total=130, foundation=15, required=45, elective=27

각 입학년도별로 아래 JSON 형식으로 반환. 키는 "컴퓨터공학과{연도}".
입학년도가 범위(예: "2004 - 2005년")이면 각 연도 별도 키로 분리.

```json
{
  "컴퓨터공학과2024": {
    "total_credits": 130,
    "foundation_credits": 15,
    "major_required_credits": 45,
    "major_elective_credits": 27,
    "industry_required_credits": 12,
    "required_courses": ["캡스톤디자인", "졸업프로젝트", "졸업논문(컴퓨터공학)"],
    "english_required": {"regular": 3, "transfer": 1}
  }
}
```

industry_required_credits는 항상 12. required_courses와 english_required는 위 고정값 사용.
JSON만 반환.
"""


def parse_grad_table_page(client: OpenAI, pdf_path: str) -> dict:
    """[표5] 졸업요건 테이블 파싱"""
    print(f"   페이지 {PAGE_GRAD_TABLE + 1} 이미지 변환 중...")
    img_b64 = pdf_page_to_base64(pdf_path, PAGE_GRAD_TABLE, RENDER_DPI)

    print("   GPT Vision 분석 중...")
    response = ask_gpt_vision(client, img_b64, GRAD_PROMPT)

    result = extract_json_from_response(response)
    if not result:
        print(f"   ⚠️ JSON 파싱 실패. 응답:\n{response[:300]}")
        return {}

    return result


# ──────────────────────────────────────────
# 2. [별표1] 과목 편성표 파싱
# ──────────────────────────────────────────

COURSES_PROMPT = """
이 이미지는 경희대학교 컴퓨터공학과 교육과정 [별표1] '교육과정 편성표'입니다.

테이블의 모든 과목을 아래 JSON 배열 형식으로 추출해주세요.

```json
[
  {
    "name": "미분방정식",
    "code": "AMTH1001",
    "credit": 3,
    "year": 1,
    "semester": [2],
    "category": "전공기초",
    "department": "컴퓨터공학과",
    "description": "",
    "keywords": ["미분방정식"],
    "prerequisite": ""
  }
]
```

규칙:
- name: 교과목명 (한글)
- code: 학수번호 (예: CSE103, AMTH1001, SWCON104, AI1002, EE209)
- credit: 학점 (숫자)
- year: 이수학년 (1~4, 범위는 첫 번째 숫자)
- semester: 개설학기 배열. 1학기 ○이면 [1], 2학기 ○이면 [2], 둘 다면 [1, 2]
- category: 이수구분 ("전공기초", "전공필수", "전공선택", "산학필수" 중 하나)
- department: 항상 "컴퓨터공학과"
- description: 빈 문자열 ""
- keywords: [name]
- prerequisite: 빈 문자열 ""

이수구분 셀이 비어 있으면 위 행의 이수구분을 이어받습니다.
JSON 배열만 반환하세요.
"""


def parse_course_pages(client: OpenAI, pdf_path: str) -> list:
    """[별표1] 과목 편성표 파싱 (여러 페이지)"""
    all_courses = []
    seen_codes = set()

    for page_idx in range(PAGE_COURSES_START, PAGE_COURSES_END + 1):
        print(f"   페이지 {page_idx + 1} 처리 중...")
        img_b64 = pdf_page_to_base64(pdf_path, page_idx, RENDER_DPI)

        time.sleep(1)  # API 속도 제한 방지
        response = ask_gpt_vision(client, img_b64, COURSES_PROMPT)

        courses = extract_json_from_response(response)
        if not isinstance(courses, list):
            print(f"   ⚠️ 페이지 {page_idx + 1} JSON 파싱 실패")
            continue

        for c in courses:
            code = c.get("code", "")
            if code and code not in seen_codes:
                seen_codes.add(code)
                all_courses.append(c)

        print(f"   페이지 {page_idx + 1}: {len(courses)}개 과목 (누적: {len(all_courses)}개)")

    return all_courses


# ──────────────────────────────────────────
# 3. [별표5] 교과목 해설 파싱
# ──────────────────────────────────────────

DESC_PROMPT = """
이 이미지는 경희대학교 컴퓨터공학과 [별표5] '교과목 해설' 페이지입니다.

각 과목의 이름과 설명을 추출해서 아래 JSON 형식으로 반환하세요.

```json
{
  "미분방정식": "Homogeneous와 non-homogeneous Linear Differential Equations의 해, 미분방정식의 응용...",
  "선형대수": "역행렬, 선형계, 행렬식, 가우스 소거법, 내적, 벡터공간..."
}
```

- 키: 과목명 (한글, 영문명 제외)
- 값: 과목 설명 전체 (한국어 + 영어 모두 포함)
- • 기호로 시작하는 과목명을 찾아 그 아래 설명 텍스트를 포함
- JSON만 반환하세요.
"""


def parse_description_pages(client: OpenAI, pdf_path: str) -> dict:
    """[별표5] 교과목 해설 파싱"""
    all_descs = {}
    doc = fitz.open(pdf_path)

    desc_end = min(PAGE_DESC_END, doc.page_count - 1)

    for page_idx in range(PAGE_DESC_START, desc_end + 1):
        print(f"   페이지 {page_idx + 1} 처리 중... ({page_idx - PAGE_DESC_START + 1}/{desc_end - PAGE_DESC_START + 1})")
        img_b64 = pdf_page_to_base64(pdf_path, page_idx, RENDER_DPI)

        time.sleep(1)
        response = ask_gpt_vision(client, img_b64, DESC_PROMPT)

        descs = extract_json_from_response(response)
        if isinstance(descs, dict):
            all_descs.update(descs)
            print(f"   페이지 {page_idx + 1}: {len(descs)}개 설명 추출 (누적: {len(all_descs)}개)")
        else:
            print(f"   ⚠️ 페이지 {page_idx + 1} 파싱 실패")

    return all_descs


# ──────────────────────────────────────────
# 4. JSON 파일 업데이트
# ──────────────────────────────────────────

def update_grad_json(new_data: dict, path: str, dry_run: bool) -> int:
    """
    졸업요건 JSON 업데이트.
    기존 연도 데이터는 보존하고, 새 연도만 추가.
    (GPT OCR 오류로 기존 데이터가 손상되는 것 방지)
    """
    existing = {}
    if os.path.exists(path):
        with open(path, "r", encoding="utf-8") as f:
            existing = json.load(f)

    added = 0
    for key, val in new_data.items():
        if key not in existing:
            existing[key] = val
            added += 1
            print(f"   ➕ 신규 추가: {key}")
        else:
            print(f"   ⏭️  이미 존재 (스킵): {key}")

    if not dry_run and added > 0:
        with open(path, "w", encoding="utf-8") as f:
            json.dump(existing, f, ensure_ascii=False, indent=2)

    return added


def update_courses_json(
    new_courses: list,
    descriptions: dict,
    path: str,
    dry_run: bool
) -> tuple[int, int]:
    existing = []
    if os.path.exists(path):
        with open(path, "r", encoding="utf-8") as f:
            existing = json.load(f)

    existing_map = {c["code"]: i for i, c in enumerate(existing)}
    added, updated = 0, 0

    for course in new_courses:
        # 설명 주입
        name = course.get("name", "")
        if name in descriptions and not course.get("description"):
            course["description"] = descriptions[name]

        code = course.get("code", "")
        if code in existing_map:
            idx = existing_map[code]
            if course.get("description") and not existing[idx].get("description"):
                existing[idx]["description"] = course["description"]
            existing[idx]["semester"] = course.get("semester", existing[idx].get("semester", [1, 2]))
            updated += 1
        elif code:
            existing.append(course)
            existing_map[code] = len(existing) - 1
            added += 1

    if not dry_run:
        with open(path, "w", encoding="utf-8") as f:
            json.dump(existing, f, ensure_ascii=False, indent=2)

    return added, updated


# ──────────────────────────────────────────
# 5. 메인
# ──────────────────────────────────────────

def main():
    parser = argparse.ArgumentParser(
        description="컴퓨터공학과 교육과정 PDF → JSON 자동 업데이트 (GPT Vision)"
    )
    parser.add_argument("--pdf", required=True, help="PDF 파일 경로")
    parser.add_argument("--dry-run", action="store_true", help="파싱 결과만 확인, 파일 수정 없음")
    parser.add_argument("--skip-desc", action="store_true", help="[별표5] 설명 파싱 건너뜀 (비용 절약)")
    args = parser.parse_args()

    if not os.path.exists(args.pdf):
        print(f"❌ PDF 없음: {args.pdf}")
        sys.exit(1)

    # API 키 로딩
    env = load_env(ENV_PATH)
    api_key = env.get("OPENAI_API_KEY") or os.environ.get("OPENAI_API_KEY")
    if not api_key:
        print("❌ OPENAI_API_KEY가 없습니다. .env 파일을 확인하세요.")
        sys.exit(1)

    client = OpenAI(api_key=api_key)

    print(f"📄 PDF: {args.pdf}")
    doc = fitz.open(args.pdf)
    print(f"   총 {doc.page_count}페이지")
    if args.dry_run:
        print("   [dry-run 모드: 파일 수정 없음]")

    # ── [표5] 졸업요건 ──
    print("\n📊 [표5] 졸업요건 파싱...")
    grad_data = parse_grad_table_page(client, args.pdf)

    if grad_data:
        print(f"   ✅ {len(grad_data)}개 학번 요건:")
        for key, val in sorted(grad_data.items()):
            print(f"      {key}: 총 {val.get('total_credits')}학점 "
                  f"(기초 {val.get('foundation_credits')} / "
                  f"필수 {val.get('major_required_credits')} / "
                  f"선택 {val.get('major_elective_credits')})")

        added = update_grad_json(grad_data, GRAD_JSON_PATH, args.dry_run)
        if not args.dry_run:
            print(f"   💾 저장 완료 (신규 {added}개)")
    else:
        print("   ⚠️ 졸업요건 파싱 실패")

    # ── [별표1] 과목 목록 ──
    print("\n📚 [별표1] 과목 편성표 파싱...")
    courses = parse_course_pages(client, args.pdf)
    print(f"   ✅ 총 {len(courses)}개 과목")

    # ── [별표5] 교과목 해설 ──
    descriptions = {}
    if not args.skip_desc:
        print("\n📝 [별표5] 교과목 해설 파싱...")
        descriptions = parse_description_pages(client, args.pdf)
        print(f"   ✅ 총 {len(descriptions)}개 설명")
    else:
        print("\n📝 [별표5] 건너뜀 (--skip-desc)")

    # ── 과목 JSON 업데이트 ──
    if courses:
        added, updated = update_courses_json(courses, descriptions, COURSES_JSON_PATH, args.dry_run)
        status = "[dry-run]" if args.dry_run else "💾 저장 완료"
        print(f"\n{status}: 신규 {added}개 추가, {updated}개 업데이트 → {COURSES_JSON_PATH}")

    print("\n✅ 파이프라인 완료!")


if __name__ == "__main__":
    main()
