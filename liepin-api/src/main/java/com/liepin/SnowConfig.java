package com.liepin;

import com.a3test.component.idworker.IdWorkerConfigBean;
import com.a3test.component.idworker.Snowflake;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SnowConfig {

    /**
     * 生命雪花算法实例化bean
     * @return
     */
    @Bean
    public Snowflake snowflake() {
        return new Snowflake(new IdWorkerConfigBean());
    }

}
