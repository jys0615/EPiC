package com.semothon.team15.semo_backend.graduation.service;

import com.semothon.team15.semo_backend.graduation.client.FastApiClient;
import com.semothon.team15.semo_backend.graduation.dto.GraduationCheckResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class GraduationService {

    private final FastApiClient fastApiClient;

    public GraduationService(FastApiClient fastApiClient) {
        this.fastApiClient = fastApiClient;
    }

    public GraduationCheckResponseDto checkRequirements(MultipartFile file, String department, String studentId) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("PDF 파일이 업로드되지 않았습니다.");
        }
        if (department == null || department.isBlank()) {
            throw new IllegalArgumentException("학과 정보가 누락되었습니다.");
        }
        if (studentId == null || studentId.isBlank()) {
            throw new IllegalArgumentException("학번 정보가 누락되었습니다.");
        }
        Map<String,Object> responsedata= fastApiClient.analyzePdf(file, department, studentId);

        return new GraduationCheckResponseDto(responsedata);

    }
}