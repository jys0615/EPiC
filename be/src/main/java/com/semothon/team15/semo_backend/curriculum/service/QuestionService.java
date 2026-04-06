package com.semothon.team15.semo_backend.curriculum.service;

import com.semothon.team15.semo_backend.curriculum.dto.QuestionRequestDto;
import com.semothon.team15.semo_backend.curriculum.dto.QuestionResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class QuestionService {

    private final WebClient webClient;

    public QuestionService(WebClient webClient) {
        this.webClient = webClient;
    }

    public QuestionResponseDto askAdditionalQuestion(QuestionRequestDto requestDto, String sessionId) {
        try {
            return webClient.post()
                    .uri("/chat")
                    .header("session-id", sessionId != null ? sessionId : "")
                    .bodyValue(requestDto)
                    .retrieve()
                    .bodyToMono(QuestionResponseDto.class)
                    .block();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to call external API", ex);
        }
    }
}