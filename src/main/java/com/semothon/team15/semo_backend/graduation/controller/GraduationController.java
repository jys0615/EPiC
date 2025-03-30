package com.semothon.team15.semo_backend.graduation.controller;

import com.semothon.team15.semo_backend.common.dto.BaseResponse;
import com.semothon.team15.semo_backend.graduation.dto.GraduationCheckResponseDto;
import com.semothon.team15.semo_backend.graduation.service.GraduationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/graduation")
public class GraduationController {

    private final GraduationService graduationService;

    public GraduationController(GraduationService graduationService) {
        this.graduationService = graduationService;
    }

    @PostMapping("/check")
    public ResponseEntity<BaseResponse<GraduationCheckResponseDto>> checkGraduationRequirements(
            @RequestParam("file") MultipartFile file,
            @RequestParam("department") String department,
            @RequestParam("studentId") String studentId) {
        GraduationCheckResponseDto result = graduationService.checkRequirements(file, department, studentId);
        BaseResponse<GraduationCheckResponseDto> response = new BaseResponse<>("SUCCESS",result,"졸업 요건 응답");
        return ResponseEntity.ok(response);
    }
}