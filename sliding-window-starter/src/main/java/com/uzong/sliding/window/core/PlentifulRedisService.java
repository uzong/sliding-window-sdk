package com.uzong.sliding.window.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.Collections;

/**
 * @author sky.
 */
@Slf4j
public class PlentifulRedisService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    @Qualifier("calculateAndCleanUpScript")
    private DefaultRedisScript<Boolean> calculateAndCleanUpScript;

    @Autowired
    @Qualifier("calculateScript")
    private DefaultRedisScript<Boolean> calculateScript;

    @Autowired
    @Qualifier("calculateCountScript")
    private DefaultRedisScript<Long> calculateCountScript;

    @Autowired
    @Qualifier("redisStringRedisSerializer")
    private RedisSerializer redisSerializer;

    @Autowired
    private SlSnowflakeIdGenerator slSnowflakeIdGenerator;

    public Boolean slidingWindowCalculate(
            String key, Long currentTime, Long windowLengthInMs,
            Long threshold, Long expireSeconds) {

        return doExecute(key, currentTime, windowLengthInMs,
                threshold, expireSeconds, calculateScript);
    }

    public Boolean slidingWindowCalculateAndCleanUp(
            String key, Long currentTime, Long windowLengthInMs,
            Long threshold, Long expireSeconds) {

        return doExecute(key, currentTime, windowLengthInMs,
                threshold, expireSeconds, calculateAndCleanUpScript
        );
    }

    public Long slidingWindowCalculateCount(
            String key, Long currentTime, Long windowLengthInMs,
            Long expireSeconds) {
        return doExecute(key, currentTime, windowLengthInMs,
                0L, expireSeconds, calculateCountScript
        );
    }

    private <T> T doExecute(
            String key, Long currentTime, Long windowLengthInMs, Long threshold,
            Long expireSeconds, DefaultRedisScript<T> defaultRedisScript) {

        String currentTimeScore = Long.toString(currentTime);

        String preTimeScore = Long.toString(currentTime - windowLengthInMs);

        return (T) redisTemplate.execute(
                defaultRedisScript,
                redisSerializer,
                redisSerializer,
                Collections.singletonList(key),
                currentTimeScore,
                preTimeScore,
                Long.toString(expireSeconds),
                Long.toString(threshold),
                Long.toString(slSnowflakeIdGenerator.nextId())
        );
    }
}