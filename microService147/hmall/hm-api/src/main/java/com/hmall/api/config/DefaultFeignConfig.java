package com.hmall.api.config;

import com.hmall.common.utils.UserContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;

public class DefaultFeignConfig {
//注册feign日志记录级别 none > basic > headers > full
    @Bean
    public System.Logger.Level feignLoggerLevel() {
        return System.Logger.Level.INFO;
    }

    @Bean
    public RequestInterceptor userContextInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                Long userId = UserContext.getUser();
                if (userId != null) {
                    template.header("user-info", userId.toString());
                }
            }
        };
    }
}
