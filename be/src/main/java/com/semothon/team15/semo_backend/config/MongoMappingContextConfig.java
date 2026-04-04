package com.semothon.team15.semo_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

@Configuration
public class MongoMappingContextConfig {
    @Bean
    public MongoMappingContext mongoMappingContext() {
        return new MongoMappingContext();
    }
}
