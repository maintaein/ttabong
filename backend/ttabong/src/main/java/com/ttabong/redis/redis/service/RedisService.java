package com.ttabong.redis.redis.service;

public interface RedisService {
    String searchKeyWord(String q);

    Object rankingKeyWord();
}
