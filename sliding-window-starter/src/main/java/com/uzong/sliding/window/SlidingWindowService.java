package com.uzong.sliding.window;

/**
 * 接口定义了滑动窗口算法的服务，用于在特定时间窗口内统计和限制事件的发生次数
 * @author sky
 */
public interface SlidingWindowService {

    /**
     * 使用滑动窗口算法计算指定键在给定时间窗口内的事件发生次数，并与阈值进行比较
     *
     * @param key 事件的唯一标识符
     * @param windowLenInSeconds 时间窗口的长度，以秒为单位
     * @param threshold 事件发生的阈值次数
     * @return 如果事件发生次数超过阈值，则返回true；否则返回false
     */
    boolean slidingWindowCalculate(String key, long windowLenInSeconds, long threshold);

    /**
     * 使用滑动窗口算法计算指定键在给定时间窗口内的事件发生次数，并与阈值进行比较
     * 同时执行清理操作，以释放不必要的资源
     *
     * @param key 事件的唯一标识符
     * @param windowLenInSeconds 时间窗口的长度，以秒为单位
     * @param threshold 事件发生的阈值次数
     * @return 如果事件发生次数超过阈值，则返回true；否则返回false
     */
    boolean slidingWindowCalculateAndCleanUp(String key, long windowLenInSeconds, long threshold);

    /**
     * 使用滑动窗口算法计算指定键在给定时间窗口内的事件发生次数
     *
     * @param key 事件的唯一标识符
     * @param windowLenInSeconds 时间窗口的长度，以秒为单位
     * @return 返回事件发生次数
     */
    long slidingWindowCalculateCount(String key, long windowLenInSeconds);
}
