package com.semothon.team15.semo_backend.graduation.service;

import com.semothon.team15.semo_backend.graduation.dto.GraduationCheckRequestDto;
import com.semothon.team15.semo_backend.graduation.dto.GraduationCheckResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class GraduationService {

    private final RestClient restClient;

    public GraduationService(RestClient restClient) {
        this.restClient = restClient;
    }

    public String isAvaliable(GraduationCheckRequestDto requestDto){
        if (requestDto.getFile().isEmpty()) {
            return "PDF 파일이 업로드되지 않았습니다.";
        }

        String department=requestDto.getDepartment();
        if (department == null || department.isBlank()) {
            return "학과 정보가 누락되었습니다.";
        }

        String studentId=requestDto.getStudentId();
        if (studentId == null || studentId.isBlank()) {
            return "학번 정보가 누락되었습니다.";
        }
        return "굿";
    }

    public GraduationCheckResponseDto checkRequirements(GraduationCheckRequestDto requestDto) {

        String temp = isAvaliable(requestDto);
        if(!"굿".equals(temp)){
            throw new IllegalArgumentException(temp);
        }

        String endpoint="/analyze-pdf";

        return restClient.post()
                .uri(endpoint)
                .body(requestDto)
                .retrieve()
                .body(GraduationCheckResponseDto.class);
    }
}