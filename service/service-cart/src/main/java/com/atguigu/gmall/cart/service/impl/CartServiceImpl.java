package com.atguigu.gmall.cart.service.impl;

import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.common.util.JSONs;
import com.atguigu.gmall.feign.product.SkuFeignClient;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.vo.cart.AddSuccessVo;
import com.atguigu.gmall.model.vo.user.UserAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    SkuFeignClient skuFeignClient;

    @Override
    public AddSuccessVo addCart(Long skuId, Integer num) {
        AddSuccessVo vo = new AddSuccessVo();
        //1.判断使用哪个缓存
        String cacheKey = determinCacheKey();
        //2.从redis缓存中获取商品
        CartInfo item = getCartItem(cacheKey,skuId);
        if (item==null){
            //没用查询到商品信息，新增商品
            CartInfo cartInfo = getCartInfoFromRpc(skuId);
            cartInfo.setSkuNum(num);
            //保存到redis缓存中
            saveItemToCart(cacheKey,cartInfo);
            //保存到vo中
            vo.setSkuDefaultImg(cartInfo.getImgUrl());
            vo.setSkuName(cartInfo.getSkuName());
            vo.setId(cartInfo.getId());
        }else {
            //查到缓存，新增数量
            item.setSkuNum(item.getSkuNum()+num);
            saveItemToCart(cacheKey,item);
            vo.setSkuDefaultImg(item.getImgUrl());
            vo.setSkuName(item.getSkuName());
            vo.setId(item.getId());
        }
        return vo;
    }

    @Override
    public void saveItemToCart(String cacheKey, CartInfo cartInfo) {
        HashOperations<String, Object, Object> ops = redisTemplate.opsForHash();
        ops.put(cacheKey,cartInfo.getSkuId().toString(), JSONs.toStr(cartInfo));

    }

    @Override
    public CartInfo getCartInfoFromRpc(Long skuId) {
        Result<CartInfo> cartInfoResult = skuFeignClient.getCartInfoBySkuId(skuId);
        return cartInfoResult.getData();
    }

    @Override
    public CartInfo getCartItem(String cacheKey,Long skuId) {
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();
        String json = ops.get(cacheKey, skuId.toString());
        if (StringUtils.isEmpty(json)){
            return null;
        }
        CartInfo cartInfo = JSONs.toObj(json,CartInfo.class);
        return cartInfo;
    }

    @Override
    public String determinCacheKey() {
        //1.拿到用户信息
        UserAuth userAuth = AuthContextHolder.getUserAuth();
        String cacheKey = "";
        if (userAuth.getUserId()!=null){
            cacheKey = RedisConst.CART_INFO_PREFIX + userAuth.getUserId();
        }else {
            cacheKey = RedisConst.CART_INFO_PREFIX + userAuth.getTempId();
        }

        return cacheKey;
    }
}
