package com.semothon.team15.semo_backend.member.service;

import com.semothon.team15.semo_backend.member.repository.RedisRepositoryImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RedisService {

    private final RedisRepositoryImpl redisRepositoryImpl;

    public RedisService(RedisRepositoryImpl redisRepositoryImpl) {
        this.redisRepositoryImpl = redisRepositoryImpl;
    }

    @Transactional(readOnly = true)
    public String getData(String key) {
        return redisRepositoryImpl.getData(key);
    }

    @Transactional(readOnly = true)
    public boolean existData(String key) {
        return redisRepositoryImpl.existData(key);
    }

    @Transactional
    public void setDataExpire(String key, String value, long timeout) {
        redisRepositoryImpl.setDataExpire(key, value, timeout);
    }

    @Transactional
    public void deleteData(String key) {
        redisRepositoryImpl.deleteData(key);
    }
}
