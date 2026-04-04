package com.semothon.team15.semo_backend.member.repository;

public interface RedisRepository {
    String getData(String key);
    boolean existData(String key);
    void setDataExpire(String key, String value, long timeout);
    void deleteData(String key);
}
