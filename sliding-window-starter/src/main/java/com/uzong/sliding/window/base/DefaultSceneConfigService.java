package com.uzong.sliding.window.base;

import com.uzong.sliding.window.SceneConfigService;
import com.uzong.sliding.window.dto.WindowLinThresholdPair;

/**
 * @author sky
 * @since 2025/7/20
 */
public class DefaultSceneConfigService implements SceneConfigService {

    @Override
    public WindowLinThresholdPair getSceneConfig(String scene) {
        throw new IllegalArgumentException("no support");
    }
}
