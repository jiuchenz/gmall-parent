package com.atguigu.gmall.feign.cart;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.vo.cart.AddSuccessVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("service-cart")
@RequestMapping("/rpc/inner/cart")
public interface CartFeignClient {

    @GetMapping("/add/{skuId}")
    public Result<AddSuccessVo> addCart(@PathVariable("skuId") Long skuId,
                                        @RequestParam("num") Integer num);
}
