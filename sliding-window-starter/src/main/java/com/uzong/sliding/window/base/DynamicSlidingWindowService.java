package com.uzong.sliding.window.base;

import com.uzong.sliding.window.SceneConfigService;
import com.uzong.sliding.window.SceneSlidingWindowService;
import com.uzong.sliding.window.SlidingWindowService;
import com.uzong.sliding.window.dto.WindowLinThresholdPair;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 基于动态窗口
 * @author sky
 * @since 2025/7/20
 */
public class DynamicSlidingWindowService implements SceneSlidingWindowService {

    @Autowired
    private SceneConfigService sceneConfigService;

    @Autowired
    private SlidingWindowService slidingWindowService;

    @Override
    public boolean slidingWindowCalculate(String key, String scene) {
        WindowLinThresholdPair pair = sceneConfigService.getSceneConfig(scene);
        if (pair == null) {
            throw new IllegalArgumentException("scene not found,scene:" + scene);
        }
        return slidingWindowService.slidingWindowCalculate(key,
                pair.getWindowLenInSeconds(), pair.getThreshold());
    }

    @Override
    public boolean slidingWindowCalculateAndCleanUp(String key, String scene) {
        WindowLinThresholdPair pair = sceneConfigService.getSceneConfig(scene);
        if (pair == null) {
            throw new IllegalArgumentException("scene not found,scene:" + scene);
        }
        return slidingWindowService.slidingWindowCalculateAndCleanUp(key,
                pair.getWindowLenInSeconds(), pair.getThreshold());
    }

    @Override
    public long slidingWindowCalculateCount(String key, String scene) {
        WindowLinThresholdPair pair = sceneConfigService.getSceneConfig(scene);
        if (pair == null) {
            throw new IllegalArgumentException("scene not found,scene:" + scene);
        }
        return slidingWindowService.slidingWindowCalculateCount(key, pair.getWindowLenInSeconds());
    }
}
