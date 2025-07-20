package com.uzong.sliding.window.example;

import com.uzong.sliding.window.base.BaseSlidingWindowService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.TimeUnit;


/**
 * BaseSlidingWindowService测试类
 * 用于测试基础滑动窗口服务功能
 */
@SpringBootTest
@ActiveProfiles("test")
public class BaseSlidingWindowServiceTest {

    @Autowired
    private BaseSlidingWindowService baseSlidingWindowService;

    @Autowired
    private RedisTemplate redisTemplate;

    private static final String TEST_KEY_PREFIX = "sl_:";

    @BeforeEach
    public void setUp() {
        // 清理测试前的数据
        redisTemplate.delete(redisTemplate.keys(TEST_KEY_PREFIX + "*"));
    }

    /**
     * 测试基础滑动窗口计算功能
     * 验证在窗口期内是否正确统计请求次数并判断阈值
     */
    @Test
    public void testSlidingWindowCalculate() throws InterruptedException {
        String key = "base_calculate_test";
        long windowLenInSeconds = 5; // 5秒的窗口期
        long threshold = 5; // 阈值为5次

        // 第一次请求，未达到阈值
        boolean result1 = baseSlidingWindowService.slidingWindowCalculate(key, windowLenInSeconds, threshold);
        Assertions.assertFalse(result1, "第一次请求应该未达到阈值");

        // 连续请求4次，仍未达到阈值
        for (int i = 0; i < 4; i++) {
            baseSlidingWindowService.slidingWindowCalculate(key, windowLenInSeconds, threshold);
            // 短暂延迟，避免时间戳完全相同
            TimeUnit.MILLISECONDS.sleep(10);
        }

        // 第6次请求，应该达到阈值
        boolean result6 = baseSlidingWindowService.slidingWindowCalculate(key, windowLenInSeconds, threshold);
        Long count6 = redisTemplate.opsForZSet().zCard(TEST_KEY_PREFIX + key);
        System.out.println("第6次请求结果：" + result6 + "count:" + count6);

        // 验证Redis中的数据
        boolean result7 = baseSlidingWindowService.slidingWindowCalculate(key, windowLenInSeconds, threshold);
        Long count7 = redisTemplate.opsForZSet().zCard(TEST_KEY_PREFIX + key);
        Assertions.assertTrue(result7, "第7次请求应该达到阈值");
        System.out.println("第7次请求结果：" + result7 + "count:" + count7);
    }

    /**
     * 测试基础滑动窗口计算并清理功能
     * 验证在达到阈值后是否正确清理了窗口内的数据
     */
    @Test
    public void testSlidingWindowCalculateAndCleanUp() throws InterruptedException {
        String key = "base_calculate_cleanup_test";
        long windowLenInSeconds = 5; // 5秒的窗口期
        long threshold = 5; // 阈值为5次

        // 连续请求5次，达到阈值并清理
        for (int i = 0; i < 5; i++) {
            boolean result = baseSlidingWindowService.slidingWindowCalculateAndCleanUp(key, windowLenInSeconds, threshold);
            if (i == 4) {
                Assertions.assertTrue(result, "第5次请求应该达到阈值并清理数据");
            } else {
                Assertions.assertFalse(result, "前4次请求应该未达到阈值");
            }
            // 短暂延迟，避免时间戳完全相同
            TimeUnit.MILLISECONDS.sleep(10);
        }

        // 验证Redis中的数据是否被清理
        Long count = redisTemplate.opsForZSet().zCard(TEST_KEY_PREFIX + key);
        System.out.println("Redis中记录的请求数：" + count);
        Assertions.assertEquals(0, count, "Redis中的数据应该被清理");
    }

    /**
     * 测试基础滑动窗口计数功能
     * 验证是否正确统计了窗口内的请求次数
     */
    @Test
    public void testSlidingWindowCalculateCount() throws InterruptedException {
        String key = "base_calculate_count_test";
        long windowLenInSeconds = 5; // 5秒的窗口期

        // 连续请求3次
        for (int i = 0; i < 3; i++) {
            long count = baseSlidingWindowService.slidingWindowCalculateCount(key, windowLenInSeconds);
            Assertions.assertEquals(i + 1, count, "窗口内应该有" + (i + 1) + "次请求");
            // 短暂延迟，避免时间戳完全相同
            TimeUnit.MILLISECONDS.sleep(10);
        }

        // 验证计数结果
        long finalCount = baseSlidingWindowService.slidingWindowCalculateCount(key, windowLenInSeconds);
        Assertions.assertEquals(4, finalCount, "窗口内应该有4次请求");
    }

    /**
     * 测试窗口滑动功能
     * 验证当时间超过窗口期时，是否正确移除了过期的数据
     */
    @Test
    public void testWindowSliding() throws InterruptedException {
        String key = "base_sliding_test";
        long windowLenInSeconds = 1; // 1秒的窗口期
        long threshold = 10; // 阈值为10次

        // 第一批请求：5次
        for (int i = 0; i < 5; i++) {
            baseSlidingWindowService.slidingWindowCalculate(key, windowLenInSeconds, threshold);
            // 短暂延迟，避免时间戳完全相同
            TimeUnit.MILLISECONDS.sleep(10);
        }

        // 等待窗口期过去
        TimeUnit.SECONDS.sleep(2);

        // 第二批请求：3次
        for (int i = 0; i < 3; i++) {
            baseSlidingWindowService.slidingWindowCalculate(key, windowLenInSeconds, threshold);
            // 短暂延迟，避免时间戳完全相同
            TimeUnit.MILLISECONDS.sleep(10);
        }

        // 验证Redis中的数据，应该只有第二批的3条
        Long count = redisTemplate.opsForZSet().zCard(TEST_KEY_PREFIX + key);
        Assertions.assertEquals(3, count, "Redis中应该只有第二批的3条数据");
    }

    /**
     * 测试高并发场景
     * 模拟多个请求同时访问滑动窗口
     */
    @Test
    public void testConcurrentRequests() throws InterruptedException {
        String key = "sl_base_concurrent_test";
        long windowLenInSeconds = 100; // 10秒的窗口期
        long threshold = 50; // 阈值为50次

        // 模拟30个并发请求
        Thread[] threads = new Thread[30];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                baseSlidingWindowService.slidingWindowCalculate(key, windowLenInSeconds, threshold);
            });
            threads[i].start();
        }

        // 等待所有线程完成
        for (Thread thread : threads) {
            thread.join();
        }

        // 验证Redis中的数据
        Long count = redisTemplate.opsForZSet().zCard(TEST_KEY_PREFIX + key);
        System.out.println("Redis中记录的请求数：" + count);
        Assertions.assertEquals(30, count, "Redis中应该有30条数据");

        // 验证未达到阈值
        boolean result = baseSlidingWindowService.slidingWindowCalculate(key, windowLenInSeconds, threshold);
        Assertions.assertFalse(result, "30+1=31次请求，未达到50的阈值，应该返回false");
    }
}