package com.atguigu.gmall.feign.ware;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(url="http://localhost:9001",value = "ware-manage")
public interface WareFeignClient {
    //http://localhost:9001/hasStock?skuId=43&num=1
    @GetMapping("/hasStock")
    public String hasStock(@RequestParam("skuId") Long skuId,
                           @RequestParam("num") Integer num);
}
