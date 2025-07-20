package com.uzong.sliding.window;

import com.uzong.sliding.window.dto.WindowLinThresholdPair;

/**
 * @author sky
 * @since 2025/7/20
 */
public interface SceneConfigService {

    WindowLinThresholdPair getSceneConfig(String scene);
}
