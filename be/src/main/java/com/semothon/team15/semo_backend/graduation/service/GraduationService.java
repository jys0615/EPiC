package com.semothon.team15.semo_backend.graduation.service;

import com.semothon.team15.semo_backend.graduation.dto.GraduationCheckRequestDto;
import com.semothon.team15.semo_backend.graduation.dto.GraduationCheckResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class GraduationService {

    private final WebClient webClient;

    @Autowired
    public GraduationService(WebClient webClient) {
        this.webClient = webClient;
    }

    public String isAvaliable(GraduationCheckRequestDto requestDto) {
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
            byte[] fileBytes = requestDto.getFile().getBytes();

            ByteArrayResource fileResource = new ByteArrayResource(fileBytes) {
                @Override
                public String getFilename() {
                    return requestDto.getFile().getOriginalFilename();
                }
            };

            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("file", fileResource).contentType(MediaType.APPLICATION_PDF);
            builder.part("department", requestDto.getDepartment());
            builder.part("studentId", requestDto.getStudentId());

            return webClient.post()
                    .uri("/analyze-pdf")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(builder.build()))
                    .retrieve()
                    .bodyToMono(GraduationCheckResponseDto.class)
                    .block();

        } catch (Exception e) {
            throw new RuntimeException("FastAPI 호출 중 오류 발생: " + e.getMessage(), e);
        }
    }
}
