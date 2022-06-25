package com.atguigu.gmall.feign.item;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.vo.SkuDetailVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("service-item")
@RequestMapping("/rpc/inner/item")
public interface ItemFeignClient {

    @GetMapping("/sku/{skuId}")
    Result<SkuDetailVo> getSkuDetail(@PathVariable("skuId") Long skuId);
}
