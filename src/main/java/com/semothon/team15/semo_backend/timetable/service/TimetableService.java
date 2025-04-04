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

    public TimetableResponseDto processTimetables(TimetableRequestDto requestDto) {

        String endpoint="/get-timetable";

        return restClient.post()
                .uri(endpoint)
                .body(requestDto)
                .retrieve()
                .body(TimetableResponseDto.class);
    }
}
