package com.uzong.sliding.window.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 滑动窗口配置属性
 * @author sky
 */
@Data
@ConfigurationProperties(prefix = "sliding.window")
public class SlidingWindowProperties {
    
    /**
     * 是否启用滑动窗口功能
     */
    private boolean enabled = true;
    
    /**
     * Redis键前缀
     */
    private String keyPrefix = "sl_:";
    
    /**
     * 雪花算法数据中心ID
     */
    private long datacenterId = 1;
    
    /**
     * 雪花算法机器ID
     */
    private long machineId = 1;
}