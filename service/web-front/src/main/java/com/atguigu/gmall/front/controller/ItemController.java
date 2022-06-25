package com.atguigu.gmall.front.controller;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.item.ItemFeignClient;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.vo.CategoryView;
import com.atguigu.gmall.model.vo.SkuDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品详情控制器
 */
@Controller
public class ItemController {

    @Autowired
    ItemFeignClient itemFeignClient;

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
        //categoryView 分类信息
        CategoryView categoryView = skuDetail.getCategoryView();
        model.addAttribute("categoryView" ,categoryView);
        //skuInfo sku信息
        SkuInfo skuInfo = skuDetail.getSkuInfo();
        model.addAttribute("skuInfo",skuInfo);
        //price 价格
        BigDecimal price = skuInfo.getPrice();
        model.addAttribute("price",price);
        //spuSaleAttrList spu销售信息
        List<SpuSaleAttr> spuSaleAttrList = skuDetail.getSpuSaleAttrList();
        model.addAttribute("spuSaleAttrList",spuSaleAttrList);
        //valuesSkuJson 销售属性json
        String valuesSkuJson = skuDetail.getValuesSkuJson();
        model.addAttribute("valuesSkuJson",valuesSkuJson);
        return "item/index";
    }
}
