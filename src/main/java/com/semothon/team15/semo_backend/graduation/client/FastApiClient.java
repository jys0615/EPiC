package com.semothon.team15.semo_backend.graduation.client;

import com.semothon.team15.semo_backend.common.util.Utility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Component
public class FastApiClient {

    private final Utility utility;
    @Value("${fastapi.url}")
    private String fastApiUrl;

    @Value("${fastapi.analyze-pdf.endpoint}")
    private String analyzePdfEndpoint;

    @Value("${fastapi.ask.endpoint}")
    private String askEndpoint;

    @Value("${fastapi.upload-curriculum.endpoint}")
    private String uploadCurriculumEndpoint;

    private final RestTemplate restTemplate;

    public FastApiClient(RestTemplate restTemplate, Utility utility) {
        this.restTemplate = restTemplate;
        this.utility = utility;
    }

    public Map<String, Object> analyzePdf(MultipartFile file, String department, String studentId) {
        String url = fastApiUrl + analyzePdfEndpoint;

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file.getResource()); // PDF 파일
        body.add("department", department);   // 학과
        body.add("studentId", studentId);     // 학번


        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "multipart/form-data");

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        return utility.toMap(restTemplate.postForObject(url, requestEntity, String.class));
    }
}
