// curriculum/dto/CurriculumResponseDto.java

package com.semothon.team15.semo_backend.curriculum.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CurriculumResponseDto {
    private String keyword;
    private String add_info;
    private String ai_response;
}