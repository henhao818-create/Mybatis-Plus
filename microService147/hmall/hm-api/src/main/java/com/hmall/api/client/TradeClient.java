package com.hmall.api.client;

import com.hmall.api.config.DefaultFeignConfig;
import com.hmall.api.dto.OrderDetailDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@FeignClient(value = "trade-service",configuration = DefaultFeignConfig.class)
public interface TradeClient {
    @PutMapping("/orders/{orderId}")
    public void markOrderPaySuccess(@PathVariable("orderId") Long orderId);
}
