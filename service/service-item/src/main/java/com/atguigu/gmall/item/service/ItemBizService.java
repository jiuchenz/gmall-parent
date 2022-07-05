package com.atguigu.gmall.item.service;

import com.atguigu.gmall.model.vo.SkuDetailVo;

public interface ItemBizService {
    SkuDetailVo getSkuDetail(Long skuId);

    void incrHotScore(Long skuId);
}
