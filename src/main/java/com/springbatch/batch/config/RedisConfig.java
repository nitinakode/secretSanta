package com.springbatch.batch.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {


    //consider redis and add your password
    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        LettuceConnectionFactory factory = new LettuceConnectionFactory("localhost", 6380);
        factory.setPassword("yourpassword");
        return factory;
    }


    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))  // Set a default TTL (Time-to-Live)
                .disableCachingNullValues();  // Optionally, avoid caching null values

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(cacheConfiguration)
                .build();
    }


    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Use StringRedisSerializer for the key (it works for String-based keys)
        template.setKeySerializer(new StringRedisSerializer());

        // Use Jackson2JsonRedisSerializer for the value (serialize Java objects to JSON)
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);

        // Set the serializer for values
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer); // If you store hash values (optional)

        return template;
    }

}
