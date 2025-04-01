package com.semothon.team15.semo_backend.graduation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GraduationCheckRequestDto {
    private MultipartFile file; // PDF 파일
    private String department;  // 학과명
    private String studentId;   // 학번
}