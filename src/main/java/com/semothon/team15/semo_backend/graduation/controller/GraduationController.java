package com.semothon.team15.semo_backend.graduation.controller;

import com.semothon.team15.semo_backend.graduation.dto.GraduationCheckRequestDto;
import com.semothon.team15.semo_backend.graduation.dto.GraduationCheckResponseDto;
import com.semothon.team15.semo_backend.graduation.service.GraduationService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/graduation")
public class GraduationController {

    private final GraduationService graduationService;

    public GraduationController(GraduationService graduationService) {
        this.graduationService = graduationService;
    }

    @PostMapping(value = "/check", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GraduationCheckResponseDto> checkGraduationRequirements(
            @RequestParam("file") MultipartFile file,
            @RequestParam("department") String department,
            @RequestParam("studentId") String studentId) {
        GraduationCheckRequestDto requestDto = new GraduationCheckRequestDto(file, department, studentId);
        GraduationCheckResponseDto response = graduationService.checkRequirements(requestDto);
        return ResponseEntity.ok(response);
    }
}