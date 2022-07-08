package com.atguigu.gmall.cart.service.impl;

import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.common.util.JSONs;
import com.atguigu.gmall.feign.product.SkuFeignClient;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.vo.cart.AddSuccessVo;
import com.atguigu.gmall.model.vo.user.UserAuth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
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

        //设置过期时间
        setTempCartExpire();
        return vo;
    }

    @Override
    public void saveItemToCart(String cacheKey, CartInfo cartInfo) {
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();
        if (ops.size(cacheKey)<RedisConst.CART_SIZE_LIMIT){
            ops.put(cacheKey,cartInfo.getSkuId().toString(), JSONs.toStr(cartInfo));
        }else {
            throw new GmallException(ResultCodeEnum.OUT_OF_CART);
        }
    }

    @Override
    public CartInfo getCartInfoFromRpc(Long skuId) {
        Result<CartInfo> cartInfoResult = skuFeignClient.getCartInfoBySkuId(skuId);
        return cartInfoResult.getData();
    }

    @Override
    public List<CartInfo> getCartAllItem() {
        //判断是否存在临时用户购物车，还是登录用户购物车
        UserAuth userAuth = AuthContextHolder.getUserAuth();
        if (userAuth.getUserId()!=null&&userAuth.getTempId()!=null){
            //同时存在userId和tempId,查看tempId购物车有没有东西
            Boolean hasKey = redisTemplate.hasKey(RedisConst.CART_INFO_PREFIX + userAuth.getTempId());
            if (hasKey){
                //说明有临时购物车，需要合并
                List<CartInfo> tempInfos = getCartAllItem(RedisConst.CART_INFO_PREFIX + userAuth.getTempId());
                tempInfos.forEach(tempInfo->
                        addCart(tempInfo.getSkuId(),tempInfo.getSkuNum()));
                //删除临时购物车
                redisTemplate.delete(RedisConst.CART_INFO_PREFIX+userAuth.getTempId());
            }
        }
        //不同时存在，查询到哪个就是哪个
        String cacheKey = determinCacheKey();
        List<CartInfo> allItem = getCartAllItem(cacheKey);
        //异步查询价格，
        RequestAttributes oldAttributes = RequestContextHolder.currentRequestAttributes();
        CompletableFuture.runAsync(()->{
            log.info("提交了一个异步任务，查询商品价格");
            allItem.forEach(item->{
                //将原线程，共享给新线程
                RequestContextHolder.setRequestAttributes(oldAttributes);
                //一旦异步，因为在异步线程中 RequestContextHolder.getRequestAttributes() 是获取不到老请求，
                // 1、feign拦截器就拿不到老请求  2、feign拦截器啥都不做（tempId，userId）都无法继续透传下去
                Result<BigDecimal> skuPrice = skuFeignClient.getSkuPrice(item.getSkuId());
                RequestContextHolder.resetRequestAttributes();//释放线程
                if (!item.getSkuPrice().equals(skuPrice.getData())){
                    //更新价格
                    item.setSkuPrice(skuPrice.getData());
                    saveItemToCart(cacheKey,item);
                }
            });
        });

        return allItem;
    }

    @Override
    public List<CartInfo> getCartAllItem(String cacheKey) {
        //根据指定key获取商品信息
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();
        List<CartInfo> cartInfos = ops.values(cacheKey).stream().map(valStr -> JSONs.toObj(valStr, CartInfo.class))
                .collect(Collectors.toList());
        return cartInfos;
    }

    @Override
    public void updateCartStatus(Long skuId, Integer status) {
        String cacheKey = determinCacheKey();
        CartInfo cartInfo = getCartItem(cacheKey, skuId);
        cartInfo.setIsChecked(status);
        saveItemToCart(cacheKey,cartInfo);
    }

    @Override
    public void deleteCartItem(Long skuId) {
        String cacheKey = determinCacheKey();
        redisTemplate.opsForHash().delete(cacheKey,skuId.toString());
    }

    @Override
    public void deleteChecked() {
        String cacheKey = determinCacheKey();
        //找出所有被选中的，isChecked = 1 ；
        List<CartInfo> cartAllItem = getAllCheckedItem(cacheKey);
        Object[] ids = cartAllItem.stream().map(cartInfo -> cartInfo.getSkuId().toString())
                .toArray();
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();
        //选中删除
        if (ids!=null&&ids.length>0){
            ops.delete(cacheKey,ids);
        }
    }

    @Override
    public List<CartInfo> getAllCheckedItem(String cacheKey) {
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();
        List<CartInfo> collect = ops.values(cacheKey).stream().map(jsonStr -> JSONs.toObj(jsonStr, CartInfo.class))
                .filter(info -> info.getIsChecked() == 1)
                .collect(Collectors.toList());
        return collect;
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

    @Override
    public void setTempCartExpire() {
        UserAuth userAuth = AuthContextHolder.getUserAuth();
        if (!StringUtils.isEmpty(userAuth.getTempId())&&userAuth.getUserId()==null){
            //用户只操作临时购物车
            Boolean hasKey = redisTemplate.hasKey(RedisConst.CART_INFO_PREFIX + userAuth.getTempId());
            if (hasKey){
                redisTemplate.expire(RedisConst.CART_INFO_PREFIX+userAuth.getTempId(),365, TimeUnit.DAYS);
            }
        }
    }
}
