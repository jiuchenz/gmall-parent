package com.atguigu.gmall.feign.order;


import com.atguigu.gmall.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@FeignClient("service-order")
@RequestMapping("/rpc/inner/order")
public interface OrderFeignClient {

    @GetMapping("/confirm/data")
    Result<Map<String,Object>> getOrderConfirmData();
}
