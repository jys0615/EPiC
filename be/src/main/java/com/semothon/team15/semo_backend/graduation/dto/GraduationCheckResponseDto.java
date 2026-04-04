package com.semothon.team15.semo_backend.graduation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GraduationCheckResponseDto {
    private Map<String, Object> responseData;
}