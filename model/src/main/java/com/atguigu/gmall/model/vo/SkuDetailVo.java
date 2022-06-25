package com.atguigu.gmall.model.vo;


import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuDetailVo {
    //商品详情页信息
    //categoryView 分类信息
    private CategoryView categoryView;
    //skuInfo sku信息
    private SkuInfo skuInfo;
    //price 价格
    private BigDecimal price;
    //spuSaleAttrList spu销售信息
    private List<SpuSaleAttr> spuSaleAttrList;
    //valuesSkuJson 销售属性json
    private String valuesSkuJson;
}
