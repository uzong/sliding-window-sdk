package com.uzong.sliding.window.example;

import com.uzong.sliding.window.SlidingWindowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 滑动窗口示例控制器
 * @author sky
 */
@RestController
@RequestMapping("/sliding-window")
public class SlidingWindowExampleController {

    @Autowired
    private SlidingWindowService slidingWindowService;

    /**
     * 检查是否超过限制。案例：http://localhost:8080/sliding-window/check?key=demoData&window=3&threshold=3
     * @param key 键
     * @param window 窗口时间（秒）
     * @param threshold 阈值
     * @return 结果
     */
    @GetMapping("/check")
    public Map<String, Object> check(
            @RequestParam(value = "key",defaultValue = "AK") String key,
            @RequestParam(value = "window", defaultValue = "5") long window,
            @RequestParam(value = "threshold", defaultValue = "3") long threshold) {
        
        boolean overLimit = slidingWindowService.slidingWindowCalculate(key, window, threshold);

        Map<String, Object> result = new HashMap<>();
        result.put("key", key);
        result.put("window", window);
        result.put("threshold", threshold);
        result.put("overLimit", overLimit);
        result.put("message", overLimit ? "请求过于频繁，请稍后再试" : "请求成功");
        
        return result;
    }

    /**
     * 检查是否超过限制并清理。http://localhost:8080/sliding-window/check-and-clean?key=keys&window=3&threshold=3
     * @param key 键
     * @param window 窗口时间（秒）
     * @param threshold 阈值
     * @return 结果
     */
    @GetMapping("/check-and-clean")
    public Map<String, Object> checkAndClean(
            @RequestParam(value = "key",defaultValue = "key") String key,
            @RequestParam(value = "window", defaultValue = "3") long window,
            @RequestParam(value = "threshold", defaultValue = "3") long threshold) {
        
        boolean overLimit = slidingWindowService.slidingWindowCalculateAndCleanUp(key, window, threshold);
        Map<String, Object> result = new HashMap<>();
        result.put("key", key);
        result.put("window", window);
        result.put("threshold", threshold);
        result.put("overLimit", overLimit);
        result.put("message", overLimit ? "请求过于频繁，请稍后再试" : "请求成功");
        
        return result;
    }

    /**
     * 获取计数。http://localhost:8080/sliding-window/count?key=demoData1&window=4
     * @param key 键
     * @param window 窗口时间（秒）
     * @return 结果
     */
    @GetMapping("/count")
    public Map<String, Object> count(
            @RequestParam("key") String key,
            @RequestParam(value = "window", defaultValue = "3") long window) {
        
        long count = slidingWindowService.slidingWindowCalculateCount(key, window);
        
        Map<String, Object> result = new HashMap<>();
        result.put("key", key);
        result.put("window", window);
        result.put("count", count);
        
        return result;
    }
}