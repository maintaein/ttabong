package com.ttabong.redis.redis.service;

import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RedisHash
public class RedisServiceImpl implements RedisService {

    private static final String SEARCH_KEYWORD_RANK = "search:keyword:rank";
    private static final String SEARCH_KEYWORD_META = "search:keyword:meta";
    private final String STREAM_CLIENT = "ranking:stream";
    private final Integer RANK_NUM = 10;
    private final RedisTemplate<String, String> redisTemplate;
    private RecordId lastCreatedRanking = RecordId.of("0-0");

    RedisServiceImpl(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        //this.redisTemplate.opsForStream().createGroup(SEARCH_KEYWORD_RANK, STREAM_CLIENT);
    }

    @Override
    public String searchKeyWord(String q) {
        /*
        Long rankBefore = redisTemplate.opsForZSet().rank(SEARCH_KEYWORD_RANK, q);
        redisTemplate.opsForHash().putIfAbsent(SEARCH_KEYWORD_META, q, KeyWord.of(q));
        redisTemplate.opsForZSet().addIfAbsent(SEARCH_KEYWORD_RANK, q, 0);
        redisTemplate.opsForZSet().incrementScore(SEARCH_KEYWORD_RANK, q, 1);
        if (rankBefore != redisTemplate.opsForZSet().rank(SEARCH_KEYWORD_RANK, q)) {
            updateRankingStream(rankingKeyWord());
        }
        return q;

         */
        System.out.println("happy~!!!!!!!!!!");
        return "happy";
    }

    private void updateRankingStream(Object o) {
        /*
        //MapRecord<String, String, Integer> record = MapRecord.create(SEARCH_KEYWORD_RANK,);
        Map<String, Integer> ranking = new HashMap<>();
        Set<String> redisRank = redisTemplate.opsForZSet().range(SEARCH_KEYWORD_RANK, 0, RANK_NUM);
        int[] i = {1};
        redisRank.forEach(k -> {
            ranking.put(k, i[0]++);
        });
        lastCreatedRanking = redisTemplate.opsForStream().add(MapRecord.create(SEARCH_KEYWORD_RANK, ranking));
        System.out.println(lastCreatedRanking);

         */
    }

    public Object getupdatedRanking() {
        return redisTemplate.opsForStream().read(StreamOffset.create(SEARCH_KEYWORD_RANK, ReadOffset.from(lastCreatedRanking)));
    }

    @Override
    public Object rankingKeyWord() {
        Set<ZSetOperations.TypedTuple<String>> topKeywords = redisTemplate.opsForZSet().reverseRangeWithScores(SEARCH_KEYWORD_RANK, 0, RANK_NUM);
        return topKeywords;
    }
}
