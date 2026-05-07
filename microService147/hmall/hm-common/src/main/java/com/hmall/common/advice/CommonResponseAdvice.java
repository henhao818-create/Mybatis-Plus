package com.hmall.common.advice;

import com.hmall.common.domain.R;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice(basePackages = "com.hmall.controller")
@RequiredArgsConstructor
public class CommonResponseAdvice implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 已经是 R 类型的不重复包装
        return !returnType.getParameterType().equals(R.class);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {

        // 1. 如果返回值是 String 类型，必须手动转 JSON，否则会类型转换异常
        if (body instanceof String) {
            try {
                return objectMapper.writeValueAsString(R.ok(body));
            } catch (Exception e) {
                return R.ok(body);
            }
        }

        // 2. 其他类型直接包装
        return R.ok(body);
    }
}