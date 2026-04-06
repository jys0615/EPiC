#!/bin/bash

# ============================================================
# EPiC Performance Measurement Script
# 사용법: ./scripts/measure_performance.sh [서버URL]
# 예시:   ./scripts/measure_performance.sh http://epic15.koreacentral.cloudapp.azure.com
# ============================================================

BASE_URL="${1:-http://epic15.koreacentral.cloudapp.azure.com}"
REPEAT=3

# 색상 출력
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
BOLD='\033[1m'
RESET='\033[0m'

echo ""
echo -e "${BOLD}============================================================${RESET}"
echo -e "${BOLD}  EPiC Performance Measurement${RESET}"
echo -e "${BOLD}  Target: ${BASE_URL}${RESET}"
echo -e "${BOLD}  Date: $(date '+%Y-%m-%d %H:%M:%S')${RESET}"
echo -e "${BOLD}============================================================${RESET}"
echo ""

# ------------------------------------------------------------
# 함수: 엔드포인트 N회 측정 후 평균 반환
# measure <label> <method> <url> <body>
# ------------------------------------------------------------
measure() {
    local label="$1"
    local method="$2"
    local url="$3"
    local body="$4"
    local total=0
    local results=()

    echo -e "${CYAN}▶ ${label}${RESET}"

    for i in $(seq 1 $REPEAT); do
        if [ "$method" = "GET" ]; then
            result=$(curl -o /dev/null -s -w "%{http_code} %{time_total} %{size_download}" \
                "$url")
        else
            result=$(curl -o /dev/null -s -w "%{http_code} %{time_total} %{size_download}" \
                -X POST "$url" \
                -H "Content-Type: application/json" \
                -d "$body")
        fi

        http_code=$(echo "$result" | awk '{print $1}')
        time_val=$(echo "$result" | awk '{print $2}')
        size=$(echo "$result" | awk '{print $3}')

        if [ "$http_code" = "200" ]; then
            status="${GREEN}${http_code}${RESET}"
        else
            status="${RED}${http_code}${RESET}"
        fi

        echo -e "  요청 ${i}: HTTP ${status} | ${time_val}s | ${size} bytes"
        results+=("$time_val")
        total=$(echo "$total + $time_val" | bc)
    done

    avg=$(echo "scale=3; $total / $REPEAT" | bc)
    echo -e "  ${BOLD}평균: ${avg}s${RESET}"
    echo ""

    # 전역 변수로 평균값 저장
    LAST_AVG="$avg"
}

# ------------------------------------------------------------
# 함수: Gzip 적용 여부 확인
# ------------------------------------------------------------
check_gzip() {
    local url="$1"
    local body="$2"

    echo -e "${CYAN}▶ Gzip 압축 현황${RESET}"

    encoding=$(curl -s -I -X POST "$url" \
        -H "Content-Type: application/json" \
        -H "Accept-Encoding: gzip, deflate" \
        -d "$body" | grep -i "content-encoding" | tr -d '\r')

    size_compressed=$(curl -s -o /dev/null -w "%{size_download}" \
        -X POST "$url" \
        -H "Content-Type: application/json" \
        -H "Accept-Encoding: gzip, deflate" \
        -d "$body")

    size_plain=$(curl -s -o /dev/null -w "%{size_download}" \
        -X POST "$url" \
        -H "Content-Type: application/json" \
        -d "$body")

    if [ -n "$encoding" ]; then
        echo -e "  상태: ${GREEN}활성화${RESET} ($encoding)"
    else
        echo -e "  상태: ${RED}비활성화${RESET} (Content-Encoding 헤더 없음)"
    fi

    echo -e "  압축 요청 시 크기: ${size_compressed} bytes"
    echo -e "  압축 없이 크기:    ${size_plain} bytes"

    if [ "$size_plain" -gt 0 ] 2>/dev/null; then
        ratio=$(echo "scale=1; (1 - $size_compressed / $size_plain) * 100" | bc 2>/dev/null || echo "N/A")
        echo -e "  압축률: ${ratio}%"
    fi
    echo ""
}

# ------------------------------------------------------------
# 함수: 서버 연결 확인
# ------------------------------------------------------------
check_server() {
    echo -e "${CYAN}▶ 서버 연결 확인${RESET}"
    result=$(curl -o /dev/null -s -w "%{http_code} %{time_total}" "$BASE_URL")
    http_code=$(echo "$result" | awk '{print $1}')
    time_val=$(echo "$result" | awk '{print $2}')

    if [ "$http_code" = "200" ]; then
        echo -e "  상태: ${GREEN}정상 (HTTP ${http_code}, ${time_val}s)${RESET}"
    else
        echo -e "  상태: ${RED}오류 (HTTP ${http_code})${RESET}"
        echo -e "  서버가 응답하지 않습니다. URL을 확인해주세요."
        exit 1
    fi
    echo ""
}

# ------------------------------------------------------------
# 측정 시작
# ------------------------------------------------------------

check_server

echo -e "${BOLD}[ API 응답 시간 측정 (각 ${REPEAT}회 평균) ]${RESET}"
echo ""

# 커리큘럼 추천
measure \
    "커리큘럼 추천 (/api/curriculum/recommend)" \
    "POST" \
    "${BASE_URL}/api/curriculum/recommend" \
    '{"keyword":"백엔드","add_info":"스프링을 배우고 싶어"}'
AVG_RECOMMEND="$LAST_AVG"

# 챗봇 Q&A
measure \
    "챗봇 Q&A (/api/curriculum/add-ques)" \
    "POST" \
    "${BASE_URL}/api/curriculum/add-ques" \
    '{"question":"자료구조 과목 추천해줘"}'
AVG_CHAT="$LAST_AVG"

# ------------------------------------------------------------
echo -e "${BOLD}[ 네트워크 현황 ]${RESET}"
echo ""

check_gzip \
    "${BASE_URL}/api/curriculum/recommend" \
    '{"keyword":"운영체제","add_info":""}'

# ------------------------------------------------------------
echo -e "${BOLD}============================================================${RESET}"
echo -e "${BOLD}  측정 결과 요약${RESET}"
echo -e "${BOLD}============================================================${RESET}"
echo ""
printf "  %-35s %s\n" "커리큘럼 추천 평균 응답시간:" "${AVG_RECOMMEND}s"
printf "  %-35s %s\n" "챗봇 Q&A 평균 응답시간:" "${AVG_CHAT}s"
echo ""
echo -e "${YELLOW}  Baseline (리팩토링 전)${RESET}"
printf "  %-35s %s\n" "  커리큘럼 추천:" "5.800s"
printf "  %-35s %s\n" "  챗봇 Q&A:" "2.100s"
echo ""

# 개선율 계산
if command -v bc &>/dev/null; then
    improve_recommend=$(echo "scale=1; (1 - $AVG_RECOMMEND / 5.800) * 100" | bc 2>/dev/null)
    improve_chat=$(echo "scale=1; (1 - $AVG_CHAT / 2.100) * 100" | bc 2>/dev/null)

    echo -e "${GREEN}  개선율${RESET}"
    printf "  %-35s %s\n" "  커리큘럼 추천:" "${improve_recommend}%"
    printf "  %-35s %s\n" "  챗봇 Q&A:" "${improve_chat}%"
fi

echo ""
echo -e "${BOLD}============================================================${RESET}"
echo ""
