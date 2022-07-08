package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.feign.search.SearchFeignClient;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.product.SkuAttrValue;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SkuSaleAttrValue;
import com.atguigu.gmall.model.vo.user.UserAuth;
import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import com.atguigu.gmall.product.service.SkuAttrValueService;
import com.atguigu.gmall.product.service.SkuImageService;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.atguigu.gmall.product.service.SkuSaleAttrValueService;
import com.atguigu.gmall.starter.cache.annotation.Cache;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 */
@Service
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoMapper, SkuInfo>
    implements SkuInfoService{

    @Autowired
    SkuInfoMapper skuInfoMapper;

    @Autowired
    SkuImageService skuImageService;

    @Autowired
    SkuAttrValueService skuAttrValueService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    StringRedisTemplate redisTemplatel;

    @Autowired
    SearchFeignClient searchFeignClient;

    static ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(4);

    @Override
    public void updateSkuInfo(SkuInfo skuInfo) {
        //1.改数据库
        //2.双删缓存
        //2.1,立即删除，可以解决80%问题
        redisTemplatel.delete(RedisConst.SKU_INFO_CACHE_KEY_PREFIX+skuInfo.getId());
        //2.2,延迟删除，解决99%的问题
        //拿到一个线程的延迟任务
        threadPool.schedule(()->redisTemplatel.delete(RedisConst.SKU_INFO_CACHE_KEY_PREFIX+skuInfo.getId()),10, TimeUnit.SECONDS);
        //立即结束
    }

    @Transactional
    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {
        //1.保存skuInfo并提取skuId和spuId
        save(skuInfo);
        Long skuId = skuInfo.getId();
        Long spuId = skuInfo.getSpuId();
        //2.保存skuImageList
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        for (SkuImage skuImage : skuImageList) {
            skuImage.setSkuId(skuId);
        }
        skuImageService.saveBatch(skuImageList);
        //3.保存skuAttrValue
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        for (SkuAttrValue skuAttrValue : skuAttrValueList) {
            skuAttrValue.setSkuId(skuId);
        }
        skuAttrValueService.saveBatch(skuAttrValueList);
        //4.保存skuSaleAttrValue
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
            skuSaleAttrValue.setSkuId(skuId);
            skuSaleAttrValue.setSpuId(spuId);
        }
        skuSaleAttrValueService.saveBatch(skuSaleAttrValueList);
    }

    @Override
    public void onSale(Long skuId) {
        skuInfoMapper.updateSale(skuId,1);
        //修改elasticsearch,将商品数据添加到es库中
        Goods goods = this.getGoodsInfoBySkuId(skuId);
        searchFeignClient.upGoods(goods);
    }

    private Goods getGoodsInfoBySkuId(Long skuId) {
        return skuInfoMapper.getGoodsInfoBySkuId(skuId);
    }

    @Override
    public void cancelSale(Long skuId) {
        skuInfoMapper.updateSale(skuId,0);
        //修改elasticsearch,将商品数据从es库中删除
        searchFeignClient.downGoods(skuId);
    }

    @Override
    public List<Long> getSkuAllId() {

        return skuInfoMapper.getSkuAllId();
    }

    @Cache(
            key = RedisConst.SKU_PRICE_CACHE_PREFIX+"#{#params[0]}",
            bloomName = RedisConst.SKU_BLOOM_FILTER_NAME,
            bloomIf = "#{#params[0]}"
    )
    @Override
    public BigDecimal getSkuPrice(Long skuId) {
        return skuInfoMapper.getSkuPrice(skuId);
    }

    @Override
    public CartInfo getCartInfoBySkuId(Long skuId) {
        CartInfo cartInfo = new CartInfo();
        UserAuth userAuth = AuthContextHolder.getUserAuth();
        if(userAuth.getUserId()!=null){
            cartInfo.setUserId(userAuth.getUserId().toString());
        }else {
            cartInfo.setUserId(userAuth.getTempId());
        }
        cartInfo.setSkuId(skuId);
        cartInfo.setId(cartInfo.getId());
        BigDecimal skuPrice = skuInfoMapper.getSkuPrice(skuId);
        cartInfo.setCartPrice(skuPrice);
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        cartInfo.setSkuNum(null);
        cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
        cartInfo.setSkuName(skuInfo.getSkuName());
        cartInfo.setIsChecked(1);
        cartInfo.setCreateTime(new Date());
        cartInfo.setUpdateTime(new Date());
        cartInfo.setSkuPrice(skuPrice);
        cartInfo.setCouponInfoList(null);


        return cartInfo;
    }

    @Override
    public BigDecimal get1010SkuPrice(Long skuId) {
        return skuInfoMapper.getSkuPrice(skuId);
    }
}




