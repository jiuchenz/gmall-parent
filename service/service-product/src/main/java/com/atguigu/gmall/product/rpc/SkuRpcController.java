package com.atguigu.gmall.product.rpc;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.vo.CategoryView;
import com.atguigu.gmall.product.biz.SpuAllSkuSaleValueService;
import com.atguigu.gmall.product.service.CategoryViewDoService;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/rpc/inner/product")
public class SkuRpcController {

    @Autowired
    SkuInfoService skuInfoService;
    @Autowired
    CategoryViewDoService categoryViewDoService;
    @Autowired
    SpuSaleAttrService spuSaleAttrService;
    @Autowired
    SpuAllSkuSaleValueService spuAllSkuSaleValueService;

    /**
     * 查询skuinfo
     * @param skuId
     * @return
     */
    @GetMapping("/skuInfo/{skuId}")
    public Result<SkuInfo> getSkuInfo(@PathVariable("skuId") Long skuId){
        SkuInfo skuInfo = skuInfoService.getById(skuId);
        return Result.ok(skuInfo);
    }

    /**
     * 查询categoryView
     * @param c3id
     * @return
     */
    @GetMapping("/category/{c3id}")
    public Result<CategoryView> getCategoryView(@PathVariable("c3id") Long c3id){
        CategoryView categoryView = categoryViewDoService.getCategoryViewByC3Id(c3id);
        return Result.ok(categoryView);
    }

    /**
     * 通过spuid和skuid查询指定sku所属spu下的所有销售属性并将指定sku标识
     * @return
     */
    @GetMapping("/sku/spuInfo/{skuId}/{spuId}")
    public Result<List<SpuSaleAttr>> getSpuSaleAttrListAndInfo(@PathVariable("skuId") Long skuId,
                                                               @PathVariable("spuId") Long spuId){
        List<SpuSaleAttr> spuSaleAttrList =  spuSaleAttrService.getSpuSaleAttrListAndInfo(skuId,spuId);
        return Result.ok(spuSaleAttrList);
    }

    /**
     * 通过spu返回spu下所有sku销售属性组合json字符串
     * @param spuId
     * @return
     */
    @GetMapping("/sku/saleattrvalue/json/{spuId}")
    public Result<String> getSpuAllSkuSaleValueJson(@PathVariable("spuId") Long spuId){
        String json = spuAllSkuSaleValueService.getSpuAllSkuSaleValueJson(spuId);
        return Result.ok(json);
    }

    @GetMapping("/sku/info/getPrice/{skuId}")
    public Result<BigDecimal> getSkuPrice(@PathVariable("skuId") Long skuId){
        BigDecimal price = skuInfoService.getSkuPrice(skuId);
        return Result.ok();
    }
}
