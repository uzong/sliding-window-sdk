package com.uzong.sliding.window.config;

import com.uzong.sliding.window.SceneConfigService;
import com.uzong.sliding.window.base.BaseSlidingWindowService;
import com.uzong.sliding.window.base.DefaultSceneConfigService;
import com.uzong.sliding.window.core.PlentifulRedisService;
import com.uzong.sliding.window.core.SlRedisConfig;
import com.uzong.sliding.window.core.SlSnowflakeIdGenerator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author sky
 */
@Configuration
@EnableConfigurationProperties(SlidingWindowProperties.class)
@Import(SlRedisConfig.class)
@ConditionalOnProperty(prefix = "sliding.window", name = "enabled", havingValue = "true", matchIfMissing = true)
public class SlidingWindowAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SlSnowflakeIdGenerator slSnowflakeIdGenerator(SlidingWindowProperties properties) {
        return new SlSnowflakeIdGenerator(properties.getDatacenterId(), properties.getMachineId());
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(RedisTemplate.class)
    public PlentifulRedisService plentifulRedisService() {
        return new PlentifulRedisService();
    }

    @Bean
    @ConditionalOnMissingBean
    public BaseSlidingWindowService baseSlidingWindowService(SlidingWindowProperties properties) {
        BaseSlidingWindowService service = new BaseSlidingWindowService();
        service.setKeyPrefix(properties.getKeyPrefix());
        return service;
    }
    
    @Bean
    @ConditionalOnMissingBean
    public SceneConfigService sceneConfigService() {
        return new DefaultSceneConfigService();
    }
}