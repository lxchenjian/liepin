package com.liepin;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CaffeineConfig {

    /**
     * 声明统一缓存bean，所有数据都可以使用本cache
     * @return
     */
    @Bean
    public Cache<String, Object> cache() {
        return Caffeine.newBuilder()
                    .initialCapacity(50)    // 初始的缓存空间大小
                    .maximumSize(1000)
                    .build();
    }

    /**
     * 专门用于设置[简历刷新次数]的缓存
     * @return
     */
    @Bean
    public Cache<String, Integer> resumeRefreshCountsCache() {
        return Caffeine.newBuilder()
                    //.initialCapacity(1)  // 初始化
                    .maximumSize(1) // 最大
                    .build();
    }
}
