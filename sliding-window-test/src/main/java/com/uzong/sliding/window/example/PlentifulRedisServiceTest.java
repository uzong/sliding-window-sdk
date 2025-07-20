package com.uzong.sliding.window.example;

import com.uzong.sliding.window.core.PlentifulRedisService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.TimeUnit;


/**
 * PlentifulRedisService测试类
 * 用于测试滑动窗口功能
 */
@SpringBootTest
@ActiveProfiles("test")
public class PlentifulRedisServiceTest {

    @Autowired
    private PlentifulRedisService plentifulRedisService;

    @Autowired
    private RedisTemplate redisTemplate;

    private static final String TEST_KEY_PREFIX = "sl_:";

    @BeforeEach
    public void setUp() {
        // 清理测试前的数据
        redisTemplate.delete(redisTemplate.keys(TEST_KEY_PREFIX + "*"));
    }

    /**
     * 测试滑动窗口计算功能
     * 验证在窗口期内是否正确统计请求次数并判断阈值。不会做清理工作
     */
    @Test
    public void testSlidingWindowCalculate() {
        String key = TEST_KEY_PREFIX + "calculate";
        long currentTime = System.currentTimeMillis();
        Long windowLength = 5000L; // 5秒的窗口期
        Long threshold = 5L; // 阈值为5次
        Long expireSeconds = 30L; // 30秒过期

        // 第一次请求，未达到阈值
        Boolean result1 = plentifulRedisService.slidingWindowCalculate(key, currentTime, windowLength, threshold, expireSeconds);
        Assertions.assertFalse(result1, "第一次请求应该未达到阈值");
        // 连续请求4次，仍未达到阈值
        for (int i = 0; i < 4; i++) {
            plentifulRedisService.slidingWindowCalculate(key, currentTime + i, windowLength, threshold, expireSeconds);
        }
        // 第6次请求，应该达到阈值
        Boolean result2 = plentifulRedisService.slidingWindowCalculate(key, currentTime + 5, windowLength, threshold, expireSeconds);
        Long count = redisTemplate.opsForZSet().zCard(key);
        System.out.println("第6次请求结果：" + count + ":" + result2);

        // 第7次请求，应该达到阈值
        Boolean result3 = plentifulRedisService.slidingWindowCalculate(key, currentTime + 5, windowLength, threshold, expireSeconds);
        Long count3 = redisTemplate.opsForZSet().zCard(key);
        System.out.println("第7次请求结果：" + count3 + ":" + result3);
    }

    /**
     * 测试滑动窗口计算并清理功能
     * 验证在达到阈值后是否正确清理了窗口内的数据
     */
    @Test
    public void testSlidingWindowCalculateAndCleanUp() {
        String key = TEST_KEY_PREFIX + "calculate_cleanup";
        long currentTime = System.currentTimeMillis();
        Long windowLength = 5000L; // 5秒的窗口期
        Long threshold = 5L; // 阈值为5次
        Long expireSeconds = 10L; // 10秒过期

        // 连续请求5次，达到阈值并清理
        for (int i = 0; i < 5; i++) {
            Boolean Result = plentifulRedisService.slidingWindowCalculateAndCleanUp(key, currentTime + i, windowLength, threshold, expireSeconds);
            System.out.println("第" + (i + 1) + "次请求结果" + Result);
        }

        // 验证Redis中的数据是否被清理
        Long count = redisTemplate.opsForZSet().zCard(key);
        Assertions.assertEquals(0, count, "Redis中的数据应该被清理");
    }

    /**
     * 测试滑动窗口计数功能
     * 验证是否正确统计了窗口内的请求次数
     */
    @Test
    public void testSlidingWindowCalculateCount() {
        String key = TEST_KEY_PREFIX + "calculate_count";
        long currentTime = System.currentTimeMillis();
        Long windowLength = 2000L; // 1秒的窗口期
        Long expireSeconds = 10L; // 10秒过期

        // 连续请求3次
        for (int i = 0; i < 3; i++) {
            plentifulRedisService.slidingWindowCalculateCount(key, currentTime + i, windowLength, expireSeconds);
        }

        // 验证计数结果
        Long count = plentifulRedisService.slidingWindowCalculateCount(key, currentTime + 3, windowLength, expireSeconds);
        System.out.println("计数结果：" + count);
        Assertions.assertEquals(4, count, "窗口内应该有4次请求");
    }

    /**
     * 测试窗口滑动功能
     * 验证当时间超过窗口期时，是否正确移除了过期的数据
     */
    @Test
    public void testWindowSliding() throws InterruptedException {
        String key = TEST_KEY_PREFIX + "sliding";
        Long currentTime = System.currentTimeMillis();
        Long windowLength = 500L; // 500毫秒的窗口期
        Long threshold = 10L; // 阈值为10次
        Long expireSeconds = 10L; // 10秒过期

        // 第一批请求：5次
        for (int i = 0; i < 5; i++) {
            plentifulRedisService.slidingWindowCalculate(key, currentTime + i, windowLength, threshold, expireSeconds);
        }

        // 等待窗口期过去
        TimeUnit.MILLISECONDS.sleep(600);

        // 第二批请求：3次，使用新的时间戳
        Long newTime = System.currentTimeMillis();
        for (int i = 0; i < 3; i++) {
            plentifulRedisService.slidingWindowCalculate(key, newTime + i, windowLength, threshold, expireSeconds);
        }

        // 验证Redis中的数据，应该只有第二批的3条
        Long count = redisTemplate.opsForZSet().zCard(key);
        Assertions.assertEquals(3, count, "Redis中应该只有第二批的3条数据");
    }

    /**
     * 测试高并发场景
     * 模拟多个请求同时访问滑动窗口
     */
    @Test
    public void testConcurrentRequests() throws InterruptedException {
        String key = TEST_KEY_PREFIX + "concurrent";
        Long windowLength = 1000L; // 1秒的窗口期
        Long threshold = 50L; // 阈值为50次
        Long expireSeconds = 10L; // 10秒过期

        // 模拟30个并发请求
        Thread[] threads = new Thread[30];
        for (int i = 0; i < threads.length; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                Long currentTime = System.currentTimeMillis() + index;
                plentifulRedisService.slidingWindowCalculate(key, currentTime, windowLength, threshold, expireSeconds);
            });
            threads[i].start();
        }

        // 等待所有线程完成
        for (Thread thread : threads) {
            thread.join();
        }

        // 验证Redis中的数据
        Long count = redisTemplate.opsForZSet().zCard(key);
        System.out.println("Redis中数据数量：" + count);
        Assertions.assertEquals(30, count, "Redis中应该有30条数据");

        // 验证未达到阈值
        Boolean result = plentifulRedisService.slidingWindowCalculate(key, System.currentTimeMillis(), windowLength, threshold, expireSeconds);
        System.out.println("未达到阈值结果：" + result);
        Assertions.assertFalse(result, "30+1=31次请求，未达到50的阈值，应该返回false");
    }
}