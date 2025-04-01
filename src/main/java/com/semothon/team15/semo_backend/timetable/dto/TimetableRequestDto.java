package com.semothon.team15.semo_backend.timetable.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimetableRequestDto {
    private List<MultipartFile> images;
    public int getImagesCount(){
        return images==null ? 0 : images.size();
    }
}
