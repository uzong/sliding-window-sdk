package com.uzong.sliding.window.dto;

import lombok.Data;

/**
 * @author sky
 * @since 2025/7/20
 */
@Data
public class WindowLinThresholdPair {

    /**
     * 窗口大小
     */
    private long windowLenInSeconds;

    /**
     * 阈值
     */
    private long threshold;
}
