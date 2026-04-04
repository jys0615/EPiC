package com.semothon.team15.semo_backend.member.repository;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;
import java.time.Duration;
//import java.util.concurrent.TimeUnit;

@Repository
public class RedisRepositoryImpl implements RedisRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisRepositoryImpl(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String getData(String key) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        return valueOperations.get(key);
    }

    @Override
    public boolean existData(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    @Override
    public void setDataExpire(String key, String value, long timeout) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, value, Duration.ofMillis(timeout));
        // 또는 다음 방식도 가능
        // valueOperations.set(key, value, timeout, TimeUnit.MILLISECONDS);
    }

    @Override
    public void deleteData(String key) {
        redisTemplate.delete(key);
    }
}
