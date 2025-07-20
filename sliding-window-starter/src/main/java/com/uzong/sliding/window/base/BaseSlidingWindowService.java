package com.uzong.sliding.window.base;

import com.uzong.sliding.window.SlidingWindowService;
import com.uzong.sliding.window.core.PlentifulRedisService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author sky
 * @since 2025/7/19
 */
@Slf4j
@Data
public class BaseSlidingWindowService implements SlidingWindowService {

    @Autowired
    private PlentifulRedisService plentifulRedisService;

    private String keyPrefix = "sliding:window:";

    @Override
    public boolean slidingWindowCalculate(
            String key, long windowLenInSeconds, long threshold
    ) {
        log.debug("===>>> slidingWindowCalculate, key:{}, windowLenInSeconds:{}, threshold:{}", key, windowLenInSeconds, threshold);
        return plentifulRedisService.slidingWindowCalculate(keyPrefix + key,
                System.currentTimeMillis(),
                windowLenInSeconds * 1000,
                threshold, windowLenInSeconds);
    }

    @Override
    public boolean slidingWindowCalculateAndCleanUp(
            String key, long windowLenInSeconds, long threshold
    ) {
        log.debug("===>>> slidingWindowCalculateAndCleanUp, key:{}, windowLenInSeconds:{}, threshold:{}", key, windowLenInSeconds, threshold);
        return plentifulRedisService.slidingWindowCalculateAndCleanUp(keyPrefix + key,
                System.currentTimeMillis(),
                windowLenInSeconds * 1000,
                threshold, windowLenInSeconds);
    }

    @Override
    public long slidingWindowCalculateCount(
            String key, long windowLenInSeconds
    ) {
        log.debug("===>>> slidingWindowCalculateCount, key:{}, windowLenInSeconds:{}", key, windowLenInSeconds);
        return plentifulRedisService.slidingWindowCalculateCount(keyPrefix + key,
                System.currentTimeMillis(),
                windowLenInSeconds * 1000, windowLenInSeconds);
    }
}
