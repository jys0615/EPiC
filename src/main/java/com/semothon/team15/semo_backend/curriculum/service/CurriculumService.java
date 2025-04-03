// curriculum/service/CurriculumService.java

package com.semothon.team15.semo_backend.curriculum.service;

import com.semothon.team15.semo_backend.curriculum.dto.CurriculumRequestDto;
import com.semothon.team15.semo_backend.curriculum.dto.CurriculumResponseDto;
import com.semothon.team15.semo_backend.curriculum.dto.QuestionRequestDto;
import com.semothon.team15.semo_backend.curriculum.dto.QuestionResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class CurriculumService {

    private final RestClient restClient;

    public CurriculumService(RestClient restClient) {
        this.restClient = restClient;
    }

    // 키워드에 대한 커리큘럼 추천
    public CurriculumResponseDto getCurriculumRecommendation(CurriculumRequestDto requestDto) {

        String addInfo = (requestDto.getAdd_info() == null) ? "" : requestDto.getAdd_info();

        String endpoint = "/recommend";
        
        try {
            String aiResponse = restClient.post()
                .uri(endpoint)
                .header("Content-Type", "application/json")
                .body(requestDto)
                .retrieve()
                .body(String.class); 

        return new CurriculumResponseDto(requestDto.getKeyword(), addInfo, aiResponse);
    } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException("AI response request failed", e);
    }
}

    // 커리큘럼에 대한 추가 질문
    public QuestionResponseDto askAdditionalQuestion(QuestionRequestDto requestDto) {

        String endpoint = "/chat";

        try {
            String aiAddResponse = restClient.post()
                    .uri(endpoint)
                    .body(requestDto)
                    .retrieve()
                    .body(String.class);

            return new QuestionResponseDto(requestDto.getQuestion(), aiAddResponse);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("AI response request failed", e);
        }
    }
}