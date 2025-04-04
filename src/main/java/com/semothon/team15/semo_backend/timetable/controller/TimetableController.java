package com.semothon.team15.semo_backend.timetable.controller;

import com.semothon.team15.semo_backend.timetable.dto.TimetableRequestDto;
import com.semothon.team15.semo_backend.timetable.dto.TimetableResponseDto;
import com.semothon.team15.semo_backend.timetable.service.TimetableService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/timetable")
public class TimetableController {

    private final TimetableService timetableService;

    public TimetableController(TimetableService timetableService) {
        this.timetableService = timetableService;
    }

    @PostMapping(value = "/get-timetable", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TimetableResponseDto> checkGraduationRequirements(
            @RequestParam("images") List<MultipartFile> images
    ) {

        TimetableRequestDto requestDto=new TimetableRequestDto(images);
        TimetableResponseDto response=timetableService.processTimetables(requestDto);
        return ResponseEntity.ok(response);
    }
}