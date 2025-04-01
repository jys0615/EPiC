package com.semothon.team15.semo_backend.timetable.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import com.semothon.team15.semo_backend.timetable.dto.TimetableRequestDto;
import com.semothon.team15.semo_backend.timetable.dto.TimetableResponseDto;

@Service
public class TimetableService {

    private final RestClient restClient;

    public TimetableService(RestClient restClient) {
        this.restClient = restClient;
    }

    public String isAvaliable(TimetableRequestDto requestDto){
        if(requestDto.getImagesCount()<2){
            return "시간표 이미지 개수가 부족합니다.";
        }
        return "굿";
    }

    public TimetableResponseDto processTimetables(TimetableRequestDto requestDto) {

        String temp=isAvaliable(requestDto);
        if(!"굿".equals(temp)){
            throw new IllegalArgumentException(temp);
        }

        String endpoint="/get-timetable";

        return restClient.post()
                .uri(endpoint)
                .body(requestDto)
                .retrieve()
                .body(TimetableResponseDto.class);
    }
}
