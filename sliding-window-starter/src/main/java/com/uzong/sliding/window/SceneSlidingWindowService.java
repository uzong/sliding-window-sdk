package com.uzong.sliding.window;

/**
 * @author sky,内置一些场景
 * @since 2025/7/20
 */
public interface SceneSlidingWindowService {


    /**
     * 使用滑动窗口算法计算指定键在给定时间窗口内的事件发生次数，并与阈值进行比较
     *
     * @param key 事件的唯一标识符
     */
    boolean slidingWindowCalculate(String key, String scene);

    /**
     * 使用滑动窗口算法计算指定键在给定时间窗口内的事件发生次数，并与阈值进行比较
     * 同时执行清理操作，以释放不必要的资源
     *
     * @param key 事件的唯一标识符
     * @return 如果事件发生次数超过阈值，则返回true；否则返回false
     */
    boolean slidingWindowCalculateAndCleanUp(String key, String scene);

    /**
     * 使用滑动窗口算法计算指定键在给定时间窗口内的事件发生次数
     *
     * @param key 事件的唯一标识符
     * @return 返回事件发生次数
     */
    long slidingWindowCalculateCount(String key, String scene);
}
