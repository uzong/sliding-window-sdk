# 滑动窗口 SDK

基于Redis实现的滑动窗口算法Spring Boot Starter，用于限流、频率控制等场景。

## 功能特性

- 基于Redis的分布式滑动窗口算法实现
- 支持自定义时间窗口长度和阈值
- 提供Spring Boot Starter方式快速集成
- 支持窗口数据自动清理功能
- 支持窗口内事件计数统计
- 使用雪花算法生成唯一ID，确保事件记录的唯一性

## 实现原理

本SDK基于Redis的有序集合(Sorted Set)实现滑动窗口算法：

1. 使用Redis的ZADD命令添加带时间戳的事件记录
2. 使用ZREMRANGEBYSCORE命令移除窗口期外的过期记录
3. 使用ZCARD命令统计窗口内的事件数量
4. 通过Lua脚本保证操作的原子性

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>com.uzong.sliding.window</groupId>
    <artifactId>sliding-window-starter</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### 2. 配置Redis

在`application.yml`中添加Redis配置：

```yaml
spring:
  redis:
    host: your-redis-host
    port: 6379
    database: 0
    timeout: 3000
    password: your-password  # 如果有密码
```

### 3. 配置滑动窗口（可选）

```yaml
sliding:
  window:
    enabled: true  # 默认为true
    key-prefix: "sl_:"  # Redis键前缀
    datacenter-id: 1  # 雪花算法数据中心ID
    machine-id: 1  # 雪花算法机器ID
```

### 4. 注入并使用SlidingWindowService

```java
@RestController
public class RateLimitController {

    @Autowired
    private SlidingWindowService slidingWindowService;
    
    @GetMapping("/api/resource")
    public ResponseEntity<?> accessResource(@RequestParam String userId) {
        // 检查用户在10秒内的访问次数是否超过5次
        boolean overLimit = slidingWindowService.slidingWindowCalculate(userId, 10, 5);
        
        if (overLimit) {
            return ResponseEntity.status(429).body("请求过于频繁，请稍后再试");
        }
        
        // 正常处理请求
        return ResponseEntity.ok("请求成功");
    }
}
```

## API说明

### SlidingWindowService接口

提供以下核心方法：

1. **slidingWindowCalculate**
   ```java
   boolean slidingWindowCalculate(String key, long windowLenInSeconds, long threshold);
   ```
   计算指定键在给定时间窗口内的事件发生次数，并与阈值比较。每次调用都会记录一次事件。

2. **slidingWindowCalculateAndCleanUp**
   ```java
   boolean slidingWindowCalculateAndCleanUp(String key, long windowLenInSeconds, long threshold);
   ```
   与上面方法类似，但在达到阈值时会清理窗口内的所有数据，适用于达到阈值后不再需要继续累计的场景。

3. **slidingWindowCalculateCount**
   ```java
   long slidingWindowCalculateCount(String key, long windowLenInSeconds);
   ```
   计算指定键在给定时间窗口内的事件发生次数，并返回计数结果。每次调用都会记录一次事件。

## 使用场景

1. **API限流**：限制单个用户或IP在特定时间窗口内的API调用次数
2. **登录尝试限制**：限制用户在一段时间内的登录尝试次数
3. **短信验证码发送限制**：控制短信验证码的发送频率
4. **防刷机制**：防止恶意用户短时间内大量请求
5. **流量控制**：对系统关键资源的访问进行流量控制

## 注意事项

1. 本SDK依赖Redis，请确保Redis服务可用
2. 合理设置时间窗口和阈值，避免过度限制正常用户
3. 在高并发场景下，可能需要调整Redis连接池配置
4. 雪花算法的数据中心ID和机器ID需要在分布式环境中保持唯一

## 许可证

[Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)