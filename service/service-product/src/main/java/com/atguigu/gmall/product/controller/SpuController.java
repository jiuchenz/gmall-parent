package com.atguigu.gmall.product.controller;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.service.SpuImageService;
import com.atguigu.gmall.product.service.SpuInfoService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/admin/product")
@RestController
public class SpuController {

    @Autowired
    SpuInfoService spuInfoService;

    @Autowired
    SpuImageService spuImageService;

    @Autowired
    SpuSaleAttrService spuSaleAttrService;

    @GetMapping("/{page}/{limit}")
    public Result getSpuByCategoryId(@PathVariable("page") Long page,
                                     @PathVariable("limit") Long limit,
                                     @RequestParam("category3Id") Long category3Id){
        Page<SpuInfo> p = new Page<>(page,limit);
        QueryWrapper<SpuInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("category3_id" ,category3Id);
        Page<SpuInfo> spuInfoPage = spuInfoService.page(p, wrapper);
        return Result.ok(spuInfoPage);
    }

    @PostMapping("/saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfo spuInfo){
        spuInfoService.saveSpuInfo(spuInfo);
        return Result.ok();
    }

    //spuImageList/{spuId}
    @GetMapping("/spuImageList/{spuId}")
    public Result getSpuImageList(@PathVariable("spuId") Long spuId){
        QueryWrapper<SpuImage> wrapper = new QueryWrapper<>();
        wrapper.eq("spu_id", spuId);
        List<SpuImage> list = spuImageService.list(wrapper);
        return Result.ok(list);
    }

    //spuSaleAttrList/{spuId}
    @GetMapping("/spuSaleAttrList/{spuId}")
    public Result getSpuSaleAttrList(@PathVariable("spuId") Long spuId){
        List<SpuSaleAttr> list = spuSaleAttrService.getSpuSaleAttrList(spuId);
        return Result.ok(list);
    }


}
