package com.atguigu.gmall.feign.search;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.vo.search.SearchParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient("service-search")
@RequestMapping("/rpc/inner/search")
public interface SearchFeignClient {

    @PostMapping("/upGoods")
    Result upGoods(@RequestBody Goods goods);

    @GetMapping("/downGoods/{skuId}")
    Result downGoods(@PathVariable("skuId") Long skuId);

    @PostMapping("/goods")
    Result<Map<String,Object>> search(@RequestBody SearchParam searchParam);

    @GetMapping("/incr/hotScore/{skuId}")
    public Result incrHotScore(@PathVariable("skuId") Long skuId,
                               @RequestParam("score") Long score);
}
