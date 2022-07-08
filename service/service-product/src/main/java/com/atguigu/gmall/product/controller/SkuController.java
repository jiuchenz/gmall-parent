package com.atguigu.gmall.product.controller;


import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.atguigu.gmall.starter.cache.component.CacheService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/product")
public class SkuController {

    @Autowired
    SkuInfoService skuInfoService;

    @Autowired
    CacheService cacheService;

    @PostMapping("/saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfo skuInfo){
        skuInfoService.saveSkuInfo(skuInfo);
        return Result.ok();
    }
    ///list/{page}/{limit}
    @GetMapping("/list/{page}/{limit}")
    public Result getSkuList(@PathVariable("page") Long page,
                             @PathVariable("limit") Long limit){
        Page<SkuInfo> p = new Page<>(page,limit);
        Page<SkuInfo> skuList = skuInfoService.page(p);
        return Result.ok(skuList);
    }

    @GetMapping("/onSale/{skuId}")
    public Result onSale(@PathVariable("skuId") Long skuId){
        skuInfoService.onSale(skuId);
        return Result.ok();
    }

    @GetMapping("/cancelSale/{skuId}")
    public Result cancelSale(@PathVariable("skuId") Long skuId){
        skuInfoService.cancelSale(skuId);
        return Result.ok();
    }

    //@GetMapping("/change/price/{skuId}")
    public Result changePrice(@PathVariable("skuId") Long skuId){
        cacheService.delayDoubleDelete(RedisConst.SKU_PRICE_CACHE_PREFIX+skuId);
        return Result.ok();
    }
}
