package com.semothon.team15.semo_backend.curriculum.controller;

import com.semothon.team15.semo_backend.curriculum.dto.QuestionRequestDto;
import com.semothon.team15.semo_backend.curriculum.dto.QuestionResponseDto;
import com.semothon.team15.semo_backend.curriculum.service.QuestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/curriculum")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping("/add-ques")
    public ResponseEntity<QuestionResponseDto> askAdditionalQuestion(@RequestBody QuestionRequestDto requestDto) {
        
        QuestionResponseDto response = questionService.askAdditionalQuestion(requestDto);
        return ResponseEntity.ok(response);
    }
}