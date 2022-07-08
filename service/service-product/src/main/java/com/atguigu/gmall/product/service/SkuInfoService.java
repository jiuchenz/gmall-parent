package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 */
public interface SkuInfoService extends IService<SkuInfo> {

    void saveSkuInfo(SkuInfo skuInfo);

    void onSale(Long skuId);

    void cancelSale(Long skuId);

    List<Long> getSkuAllId();

    BigDecimal getSkuPrice(Long skuId);

    void updateSkuInfo(SkuInfo skuInfo);

    CartInfo getCartInfoBySkuId(Long skuId);

    BigDecimal get1010SkuPrice(Long skuId);
}
