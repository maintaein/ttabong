package com.ttabong.connection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class RedisConnectionTest {
/*
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void testRedisConnection() {
        String testKey = "testKey";
        String testValue = "testValue";

        redisTemplate.opsForValue().set(testKey, testValue);
        String value = redisTemplate.opsForValue().get(testKey);

        assertEquals(testValue, value);
        System.out.println("Redis 연결 성공");
    }

 */
}

