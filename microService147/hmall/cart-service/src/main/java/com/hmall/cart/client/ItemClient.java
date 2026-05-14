package com.hmall.cart.client;

import com.hmall.cart.domain.dto.ItemDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;

//标注是一个Feign 客户端：然后制定了微服务的名称 这样的话：可以获取到该微服务的服务实例列表
//基于负载均衡选择有个服务实例
@FeignClient("item-service")
public interface ItemClient {
    //在接口内：编写要远程调用的方法，这些方法都可以参考自 服务提供者对于的接口

    //根据商品id综合获取商品dto列表
    @GetMapping("/items")
    public List<ItemDTO> queryItemByIds(@RequestParam("ids") Collection<Long> ids);

}
