package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.vo.cart.AddSuccessVo;

import java.util.List;

public interface CartService {
    AddSuccessVo addCart(Long skuId, Integer num);

    String determinCacheKey();

    CartInfo getCartItem(String cacheKey,Long skuId);

    void saveItemToCart(String cartKey,CartInfo item);

    CartInfo getCartInfoFromRpc(Long skuId);

    List<CartInfo> getCartAllItem();

    List<CartInfo> getCartAllItem(String cacheKey);

    void updateCartStatus(Long skuId, Integer status);

    void deleteCartItem(Long skuId);

    void deleteChecked();

    List<CartInfo> getAllCheckedItem(String cacheKey);

    void setTempCartExpire();
}
