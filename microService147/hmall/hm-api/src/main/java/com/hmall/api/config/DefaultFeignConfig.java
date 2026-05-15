package com.hmall.api.config;

import org.springframework.context.annotation.Bean;

public class DefaultFeignConfig {
//注册feign日志记录级别 none > basic > headers > full
    @Bean
    public System.Logger.Level feignLoggerLevel() {
        return System.Logger.Level.INFO;
    }
}
