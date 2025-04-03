// config/RestClient.java

package com.semothon.team15.semo_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${fastapi.url}")
    private String FASTAPI_BASE_URL;

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl(FASTAPI_BASE_URL)
                .defaultHeader("Content-Type", "multipart/form-data")
                .build();
    }
}