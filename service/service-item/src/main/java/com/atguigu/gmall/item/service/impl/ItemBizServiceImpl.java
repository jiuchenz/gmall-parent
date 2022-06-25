package com.atguigu.gmall.item.service.impl;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.product.SkuFeignClient;
import com.atguigu.gmall.item.service.ItemBizService;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.vo.CategoryView;
import com.atguigu.gmall.model.vo.SkuDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ItemBizServiceImpl implements ItemBizService {

    @Autowired
    SkuFeignClient skuFeignClient;
    @Override
    public SkuDetailVo getSkuDetail(Long skuId) {
        //商品详情页信息
        SkuDetailVo skuDetailVo = new SkuDetailVo();
        //skuInfo sku信息 *
        Result<SkuInfo> skuInfoResult = skuFeignClient.getSkuInfo(skuId);
        SkuInfo skuInfo = skuInfoResult.getData();
        skuDetailVo.setSkuInfo(skuInfo);

        //categoryView 分类信息 *
        Result<CategoryView> categoryViewResult = skuFeignClient.getCategoryView(skuInfo.getCategory3Id());
        CategoryView categoryView = categoryViewResult.getData();
        skuDetailVo.setCategoryView(categoryView);

        //price 价格 *
        BigDecimal price = skuInfo.getPrice();
        skuDetailVo.setPrice(price);

        //spuSaleAttrList spu销售信息
        Long spuId = skuInfo.getSpuId();
        Result<List<SpuSaleAttr>> spuSaleAttrListAndInfoResult = skuFeignClient.getSpuSaleAttrListAndInfo(skuId, spuId);
        List<SpuSaleAttr> spuSaleAttrList = spuSaleAttrListAndInfoResult.getData();
        skuDetailVo.setSpuSaleAttrList(spuSaleAttrList);

        //valuesSkuJson 销售属性json
        Result<String> jsonResult= skuFeignClient.getSpuAllSkuSaleValueJson(spuId);
        String json = jsonResult.getData();
        skuDetailVo.setValuesSkuJson(json);
        return skuDetailVo;
    }
}
