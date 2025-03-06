package com.springbatch.batch.service;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {
    private final HashOperations<String, String, Object> hashOperations;

    @Autowired
    public RedisService(RedisTemplate<String, Object> redisTemplate) {
        this.hashOperations = redisTemplate.opsForHash();
    }

    public void putValue(String bucket, String key, Object value) {
        this.hashOperations.put(bucket, key, value);
    }

    public Object getValue(String bucket, String key) {
        return this.hashOperations.get(bucket, key);
    }

    public Map<String, Object> getValue(String bucket) {
        return this.hashOperations.entries(bucket);
    }


    public void deleteCache(String bucket, String key) {
        this.hashOperations.delete(bucket, new Object[]{key});
    }

    public boolean checkIfKeyExists(String bucket, String key) {
        return this.hashOperations.hasKey(bucket, key);
    }
}
