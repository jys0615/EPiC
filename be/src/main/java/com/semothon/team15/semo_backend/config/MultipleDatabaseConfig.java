package com.semothon.team15.semo_backend.config;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.*;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

@Configuration
public class MultipleDatabaseConfig {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    private final MongoMappingContext mongoMappingContext;

    public MultipleDatabaseConfig(MongoMappingContext mongoMappingContext) {
        this.mongoMappingContext = mongoMappingContext;
    }

    // 공통 메서드들
    private MongoDatabaseFactory createDatabaseFactory(String databaseName) {
        return new SimpleMongoClientDatabaseFactory(
                MongoClients.create(new ConnectionString(mongoUri)),
                databaseName
        );
    }

    private MappingMongoConverter createMappingMongoConverter(MongoDatabaseFactory factory) {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, mongoMappingContext);
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        return converter;
    }

    private MongoTemplate createMongoTemplate(MongoDatabaseFactory factory, MappingMongoConverter converter) {
        return new MongoTemplate(factory, converter);
    }

    // Member Database 설정
    @Bean(name = "memberDatabaseFactory")
    @Primary
    public MongoDatabaseFactory memberDatabaseFactory() {
        return createDatabaseFactory("member");
    }

    @Bean(name = "memberMappingMongoConverter")
    @Primary
    public MappingMongoConverter memberMappingMongoConverter() {
        return createMappingMongoConverter(memberDatabaseFactory());
    }

    @Bean(name = "memberMongoTemplate")
    @Primary
    public MongoTemplate memberMongoTemplate() {
        return createMongoTemplate(memberDatabaseFactory(), memberMappingMongoConverter());
    }
}
