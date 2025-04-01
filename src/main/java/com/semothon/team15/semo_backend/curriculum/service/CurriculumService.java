// curriculum/service/CurriculumService.java

package com.semothon.team15.semo_backend.curriculum.service;

import com.semothon.team15.semo_backend.curriculum.dto.CurriculumRequestDto;
import com.semothon.team15.semo_backend.curriculum.dto.CurriculumResponseDto;
import com.semothon.team15.semo_backend.curriculum.dto.QuestionRequestDto;
import com.semothon.team15.semo_backend.curriculum.dto.QuestionResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CurriculumService {

    private final RestTemplate restTemplate;

    @Value("${fastapi.url}")
    private String aiServerUrl;

    public CurriculumService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // 키워드에 대한 커리큘럼 추천
    public CurriculumResponseDto getCurriculumRecommendation(CurriculumRequestDto requestDto, String aiServerUrl) {
        
        String addInfo = (requestDto.getAdd_info() == null) ? "" : requestDto.getAdd_info();
        
        // [UPDATE-REQ] FastAPI 서버의 endpoint
        String aiResponse = restTemplate.postForObject(
            aiServerUrl + "/recommend",
            new CurriculumRequestDto(requestDto.getKeyword(), addInfo), String.class
        );
        
        return new CurriculumResponseDto(requestDto.getKeyword(), addInfo, aiResponse);
    }

    // 커리큘럼에 대한 추가 질문
    public QuestionResponseDto askAdditionalQuestion(QuestionRequestDto requestDto, String aiServerUrl) {

        String aiAddResponse = restTemplate.postForObject(
            // [UPDATE-REQ] FastAPI 서버의 endpoint
            aiServerUrl + "/add-ques", requestDto, String.class
        );
        
        return new QuestionResponseDto(requestDto.getQuestion(), aiAddResponse);
    }
}