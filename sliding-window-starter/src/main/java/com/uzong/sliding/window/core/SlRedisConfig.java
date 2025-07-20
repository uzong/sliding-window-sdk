package com.uzong.sliding.window.core;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scripting.support.ResourceScriptSource;

/**
 * @author sky
 */
public class SlRedisConfig {

    @Bean
    @Qualifier("calculateAndCleanUpScript")
    public DefaultRedisScript<Boolean> calculateAndCleanUpScript() {
        DefaultRedisScript<Boolean> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(
                new ClassPathResource("META-INF/scripts/calculate_and_cleanup.lua")));
        redisScript.setResultType(Boolean.class);
        return redisScript;
    }

    @Bean
    @Qualifier("calculateScript")
    public DefaultRedisScript<Boolean> calculateScript() {
        DefaultRedisScript<Boolean> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(
                new ClassPathResource("META-INF/scripts/calculate.lua")));
        redisScript.setResultType(Boolean.class);
        return redisScript;
    }

    @Bean
    @Qualifier("calculateCountScript")
    public DefaultRedisScript<Long> calculateCountScript() {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(
                new ClassPathResource("META-INF/scripts/calculate_count.lua")));
        redisScript.setResultType(Long.class);
        return redisScript;
    }

    @Bean
    @Qualifier("redisStringRedisSerializer")
    public RedisSerializer redisSerializer() {
        return new StringRedisSerializer();
    }
}

