// curriculum/dto/QuestionResponse.java

package com.semothon.team15.semo_backend.curriculum.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponseDto {
    private String question;
    private String ai_add_response;
}