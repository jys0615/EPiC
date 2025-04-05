package com.semothon.team15.semo_backend.timetable.service;

import com.semothon.team15.semo_backend.timetable.dto.TimetableRequestDto;
import com.semothon.team15.semo_backend.timetable.dto.TimetableResponseDto;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class TimetableService {

    private final RestTemplate restTemplate = new RestTemplate();

    public TimetableResponseDto processTimetables(TimetableRequestDto requestDto) {
        try {
            List<MultipartFile> files = requestDto.getImages();
            if (files == null || files.size() < 2) {
                throw new IllegalArgumentException("이미지 2개가 필요합니다.");
            }

            MultipartFile file1 = files.get(0);
            MultipartFile file2 = files.get(1);

            ByteArrayResource resource1 = new ByteArrayResource(file1.getBytes()) {
                @Override
                public String getFilename() {
                    return file1.getOriginalFilename();
                }
            };

            ByteArrayResource resource2 = new ByteArrayResource(file2.getBytes()) {
                @Override
                public String getFilename() {
                    return file2.getOriginalFilename();
                }
            };

            // 🔹 multipart/form-data로 구성
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("images", resource1);
            body.add("images", resource2); // 필드명이 "images" (FastAPI와 일치해야 함)

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // FastAPI 엔드포인트 주소
            String fastApiUrl = "http://localhost:8000/timetable";

            ResponseEntity<TimetableResponseDto> response = restTemplate.postForEntity(
                    fastApiUrl,
                    requestEntity,
                    TimetableResponseDto.class
            );

            return response.getBody();

        } catch (Exception e) {
            throw new RuntimeException("FastAPI 호출 오류: " + e.getMessage(), e);
        }
    }
}
