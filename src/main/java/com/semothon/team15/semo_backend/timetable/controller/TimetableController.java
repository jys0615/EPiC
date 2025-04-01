package com.semothon.team15.semo_backend.timetable.controller;

import com.semothon.team15.semo_backend.timetable.dto.TimetableRequestDto;
import com.semothon.team15.semo_backend.timetable.dto.TimetableResponseDto;
import com.semothon.team15.semo_backend.timetable.service.TimetableService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/timetable")
public class TimetableController {

    private final TimetableService timetableService;

    public TimetableController(TimetableService timetableService) {
        this.timetableService = timetableService;
    }

    @PostMapping("/get-timetable")
    public ResponseEntity<TimetableResponseDto> checkGraduationRequirements(@RequestBody TimetableRequestDto requestDto) {
        TimetableResponseDto response=timetableService.processTimetables(requestDto);
        return ResponseEntity.ok(response);
    }
}
