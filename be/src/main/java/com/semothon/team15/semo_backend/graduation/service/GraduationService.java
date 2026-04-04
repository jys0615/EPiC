package com.semothon.team15.semo_backend.graduation.service;

import com.semothon.team15.semo_backend.graduation.dto.GraduationCheckRequestDto;
import com.semothon.team15.semo_backend.graduation.dto.GraduationCheckResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class GraduationService {

    @Value("${fastapi.url}")
    private String fastapiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public String isAvaliable(GraduationCheckRequestDto requestDto){
        if (requestDto.getFile() == null || requestDto.getFile().isEmpty()) {
            return "PDF 파일이 업로드되지 않았습니다.";
        }

        if (requestDto.getDepartment() == null || requestDto.getDepartment().isBlank()) {
            return "학과 정보가 누락되었습니다.";
        }

        if (requestDto.getStudentId() == null || requestDto.getStudentId().isBlank()) {
            return "학번 정보가 누락되었습니다.";
        }

        return "굿";
    }

    public GraduationCheckResponseDto checkRequirements(GraduationCheckRequestDto requestDto) {
        String temp = isAvaliable(requestDto);
        if (!"굿".equals(temp)) {
            throw new IllegalArgumentException(temp);
        }

        try {
            // 🔹 파일 바이트로 변환
            byte[] fileBytes = requestDto.getFile().getBytes();

            // 🔹 Multipart 파일 포장 (filename 포함)
            ByteArrayResource fileResource = new ByteArrayResource(fileBytes) {
                @Override
                public String getFilename() {
                    return requestDto.getFile().getOriginalFilename();
                }
            };

            // 🔹 multipart/form-data 구성
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", fileResource);
            body.add("department", requestDto.getDepartment());
            body.add("studentId", requestDto.getStudentId());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // 🔹 FastAPI 서버 URL
            String fastApiUrl = fastapiUrl + "/analyze-pdf";

            // 🔹 요청 전송
            ResponseEntity<GraduationCheckResponseDto> response = restTemplate.postForEntity(
                    fastApiUrl,
                    requestEntity,
                    GraduationCheckResponseDto.class
            );

            return response.getBody();

        } catch (Exception e) {
            throw new RuntimeException("FastAPI 호출 중 오류 발생: " + e.getMessage(), e);
        }
    }
}