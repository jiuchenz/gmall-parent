package com.atguigu.gmall.front.controller;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.item.ItemFeignClient;
import com.atguigu.gmall.feign.product.SkuFeignClient;
import com.atguigu.gmall.model.vo.SkuDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;

/**
 * 商品详情控制器
 */
@Controller
public class ItemController {

    @Autowired
    ItemFeignClient itemFeignClient;

    @Autowired
    SkuFeignClient skuFeignClient;

    /**
     * 根据id查询指定商品信息
     * @param skuId
     * @return
     */
    @GetMapping("/{skuId}.html")
    public String item(@PathVariable("skuId") Long skuId, Model model){
        //商品详情页信息
        Result<SkuDetailVo> result = itemFeignClient.getSkuDetail(skuId);
        SkuDetailVo skuDetail = result.getData();
        if (skuDetail!=null){
            //categoryView 分类信息
            model.addAttribute("categoryView" ,skuDetail.getCategoryView());
            //skuInfo sku信息
            model.addAttribute("skuInfo",skuDetail.getSkuInfo());
            //price 价格
            Result<BigDecimal> priceResult = skuFeignClient.getSkuPrice(skuId);
            model.addAttribute("price",priceResult.getData());
            //spuSaleAttrList spu销售信息
            model.addAttribute("spuSaleAttrList",skuDetail.getSpuSaleAttrList());
            //valuesSkuJson 销售属性json
            model.addAttribute("valuesSkuJson",skuDetail.getValuesSkuJson());
            return "item/index";
        }
        return "item/error";
    }
}
