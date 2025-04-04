// curriculum/controller/CurriculumController.java

package com.semothon.team15.semo_backend.curriculum.controller;

import com.semothon.team15.semo_backend.curriculum.dto.CurriculumRequestDto;
import com.semothon.team15.semo_backend.curriculum.dto.CurriculumResponseDto;
import com.semothon.team15.semo_backend.curriculum.service.CurriculumService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/curriculum")
public class CurriculumController {

    private final CurriculumService curriculumService;

    public CurriculumController(CurriculumService curriculumService) {
        this.curriculumService = curriculumService;
    }

    @PostMapping("/recommend")
    public ResponseEntity<CurriculumResponseDto> getCurriculumRecommendation(@RequestBody CurriculumRequestDto requestDto) {

        if (requestDto.getAdd_info() == null) {
            requestDto.setAdd_info("");
        }

        CurriculumResponseDto response = curriculumService.getCurriculumRecommendation(requestDto);
        return ResponseEntity.ok(response);
    }
}