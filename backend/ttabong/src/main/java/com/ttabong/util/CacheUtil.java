package com.ttabong.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class CacheUtil {

    private final RedisTemplate<String, Long> redisKeyTemplate;
    private final RedisTemplate<String, String> redisTemplate;
    private final String TEMP_CACHE = "temp:path";
    private final long TTL_IN_MINUTES = 5; //분 단위임

    public Long generatePostId() {
        return redisKeyTemplate.opsForValue().increment("tempId");
    }

    public void mapTempPresignedUrlwithObjectPath(String tempPreSignedUrl, String objectPath) {
        redisTemplate.opsForHash().put(TEMP_CACHE, tempPreSignedUrl, objectPath);
        redisTemplate.expire(TEMP_CACHE, TTL_IN_MINUTES, TimeUnit.MINUTES );
    }

    public String findObjectPath(String preSignedUrl) {
        return (String) redisTemplate.opsForHash().get(TEMP_CACHE, preSignedUrl);
    }

}
