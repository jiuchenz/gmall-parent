package com.atguigu.gmall.feign.product;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.vo.CategoryView;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

@RequestMapping("/rpc/inner/product")
@FeignClient("service-product")
public interface SkuFeignClient {

    /**
     * 查询skuinfo
     * @param skuId
     * @return
     */
    @GetMapping("/skuInfo/{skuId}")
    Result<SkuInfo> getSkuInfo(@PathVariable("skuId") Long skuId);
    /**
     * 查询categoryView
     * @param c3id
     * @return
     */
    @GetMapping("/category/{c3id}")
    Result<CategoryView> getCategoryView(@PathVariable("c3id") Long c3id);

    /**
     * 通过spuid和skuid查询指定sku所属spu下的所有销售属性并将指定sku标识
     * @return
     */
    @GetMapping("/sku/spuInfo/{skuId}/{spuId}")
    Result<List<SpuSaleAttr>> getSpuSaleAttrListAndInfo(@PathVariable("skuId") Long skuId,
                                                               @PathVariable("spuId") Long spuId);

    /**
     * 通过spu返回spu下所有sku销售属性组合json字符串
     * @param spuId
     * @return
     */
    @GetMapping("/sku/saleattrvalue/json/{spuId}")
    Result<String> getSpuAllSkuSaleValueJson(@PathVariable("spuId") Long spuId);

    @GetMapping("/sku/info/getPrice/{skuId}")
    Result<BigDecimal> getSkuPrice(@PathVariable("skuId") Long skuId);

    @GetMapping("/cartinfo/{skuId}")
    Result<CartInfo> getCartInfoBySkuId(@PathVariable("skuId") Long skuId);


    @GetMapping("/sku/price/shishi/{skuId}")
    Result<BigDecimal> get1010SkuPrice(@PathVariable("skuId") Long skuId);
}
