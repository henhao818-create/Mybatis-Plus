package com.hmall.api.client;

import com.hmall.api.config.DefaultFeignConfig;
import com.hmall.api.dto.OrderDetailDTO;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;

@FeignClient(value = "cart-service",configuration = DefaultFeignConfig.class)
public interface CartClient {

    //对应的方法名可以不同feign主要是根据方法路径，参数进行地址构建
    @DeleteMapping("/carts")
    public void deleteCartItemByIds(@RequestParam("ids") Collection<Long> ids);


    @PutMapping("/stock/deduct")
    public void deductStock(@RequestBody List<OrderDetailDTO> items);

}
