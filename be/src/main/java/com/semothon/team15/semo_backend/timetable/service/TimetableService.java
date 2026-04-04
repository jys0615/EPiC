package com.semothon.team15.semo_backend.timetable.service;

import com.semothon.team15.semo_backend.timetable.dto.TimetableRequestDto;
import com.semothon.team15.semo_backend.timetable.dto.TimetableResponseDto;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class TimetableService {

    private final WebClient webClient;

    public TimetableService(WebClient webClient) {
        this.webClient = webClient;
    }

    public TimetableResponseDto processTimetables(TimetableRequestDto requestDto) {
        try {
            List<MultipartFile> files = requestDto.getImages();
            if (files == null || files.size() < 2) {
                throw new IllegalArgumentException("이미지 2개가 필요합니다.");
            }

            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            for (MultipartFile file : files) {
                byte[] bytes = file.getBytes();
                String filename = file.getOriginalFilename();
                builder.part("images", new ByteArrayResource(bytes) {
                    @Override
                    public String getFilename() {
                        return filename;
                    }
                }).contentType(MediaType.IMAGE_JPEG);
            }

            return webClient.post()
                    .uri("/timetable")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(builder.build()))
                    .retrieve()
                    .bodyToMono(TimetableResponseDto.class)
                    .block();

        } catch (Exception e) {
            throw new RuntimeException("FastAPI 호출 오류: " + e.getMessage(), e);
        }
    }
}
/*
WebClient는 비동기가 기본이지만, .block() 메서드로 동기처럼 사용이 가능.
*/