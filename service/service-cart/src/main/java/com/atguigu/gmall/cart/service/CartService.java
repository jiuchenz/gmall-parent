package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.vo.cart.AddSuccessVo;

public interface CartService {
    AddSuccessVo addCart(Long skuId, Integer num);

    String determinCacheKey();

    CartInfo getCartItem(String cacheKey,Long skuId);

    void saveItemToCart(String cartKey,CartInfo item);

    CartInfo getCartInfoFromRpc(Long skuId);
}
