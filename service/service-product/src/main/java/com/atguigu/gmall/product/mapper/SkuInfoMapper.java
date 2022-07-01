package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.product.SkuInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Entity com.atguigu.gmall.product.domain.SkuInfo
 */
public interface SkuInfoMapper extends BaseMapper<SkuInfo> {

    void updateSale(@Param("skuId") Long skuId, @Param("isSale") int isSale);

    List<Long> getSkuAllId();

    BigDecimal getSkuPrice(@Param("skuId") Long skuId);

    Goods getGoodsInfoBySkuId(@Param("skuId") Long skuId);
}




