package com.semothon.team15.semo_backend.curriculum.service;

import com.semothon.team15.semo_backend.curriculum.dto.CurriculumRequestDto;
import com.semothon.team15.semo_backend.curriculum.dto.CurriculumResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class CurriculumService {

    private final WebClient webClient;

    public CurriculumService(WebClient webClient) {
        this.webClient = webClient;
    }

    public CurriculumResponseDto getCurriculumRecommendation(CurriculumRequestDto requestDto) {
        return webClient.post()
                .uri("/recommend")
                .bodyValue(requestDto)
                .retrieve()
                .bodyToMono(CurriculumResponseDto.class)
                .block();
    }
}