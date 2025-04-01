package com.semothon.team15.semo_backend.graduation.controller;

import com.semothon.team15.semo_backend.graduation.dto.GraduationCheckRequestDto;
import com.semothon.team15.semo_backend.graduation.dto.GraduationCheckResponseDto;
import com.semothon.team15.semo_backend.graduation.service.GraduationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/graduation")
public class GraduationController {

    private final GraduationService graduationService;

    public GraduationController(GraduationService graduationService) {
        this.graduationService = graduationService;
    }

    @PostMapping("/check")
    public ResponseEntity<GraduationCheckResponseDto> checkGraduationRequirements(@RequestBody GraduationCheckRequestDto requestDto) {
        GraduationCheckResponseDto response=graduationService.checkRequirements(requestDto);
        return ResponseEntity.ok(response);
    }
}