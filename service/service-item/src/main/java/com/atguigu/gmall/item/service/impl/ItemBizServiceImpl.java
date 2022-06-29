package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.product.SkuFeignClient;
import com.atguigu.gmall.item.service.ItemBizService;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.vo.CategoryView;
import com.atguigu.gmall.model.vo.SkuDetailVo;
import com.atguigu.gmall.starter.cache.annotation.Cache;
import com.atguigu.gmall.starter.cache.component.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ItemBizServiceImpl implements ItemBizService {

    @Autowired
    SkuFeignClient skuFeignClient;

    @Autowired
    CacheService cacheService;

    @Autowired
    RedissonClient redissonClient;

    @Cache(
            key = RedisConst.SKU_INFO_CACHE_KEY_PREFIX+"#{#params[0]}",
            bloomName = RedisConst.SKU_BLOOM_FILTER_NAME,
            bloomIf = "#{#params[0]}",
            ttl = RedisConst.SKU_INFO_CACHE_TIMEOUT
    )  //提升通用性。那些问题？  //sku:info:skuid的值
    @Override
    public SkuDetailVo getSkuDetail(Long skuId) {
        return getSkuDetailFromRpc(skuId);
    }

    /**
     * 查询商品信息时添加缓存以及锁以及布隆
     * @param skuId
     * @return
     */
    public SkuDetailVo getSkuDetailByRedission(Long skuId) {
        //定义缓存名称
        String cacheName = RedisConst.SKU_INFO_CACHE_KEY_PREFIX + skuId;
        //1.先查询缓存
        SkuDetailVo vo = cacheService.getDate(cacheName,SkuDetailVo.class);
        //3.缓存不存在
        if (vo==null){
            //从布隆里面查询商品id是否存在
            log.info("sku{} ：缓存不命中，正在查询布隆中是否有该商品..",skuId);
            RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter(RedisConst.SKU_BLOOM_FILTER_NAME);
            if (!bloomFilter.contains(skuId)) {
                log.info("{} 布隆查询不存在",skuId);
                //如果不存在
                return null;
            }
            //布隆存在，开启分布式锁，回调
            RLock lock = redissonClient.getLock(RedisConst.SKU_INFO_LOCK_PREFIX + skuId);
            boolean tryLock = lock.tryLock();//加锁
            if (tryLock){
                //获得锁，回调
                SkuDetailVo skuDetailFromRpc = getSkuDetailFromRpc(skuId);
                //保存到缓存中
                cacheService.saveDate(cacheName,skuDetailFromRpc,RedisConst.SKU_INFO_CACHE_TIMEOUT, TimeUnit.MILLISECONDS);
                //释放锁
                lock.unlock();
                return skuDetailFromRpc;
            }else {
                try {
                    //没获得锁
                    TimeUnit.MILLISECONDS.sleep(300);
                    return cacheService.getDate(cacheName,SkuDetailVo.class);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        //2.缓存存在
        return vo;
    }

    public SkuDetailVo getSkuDetailFromRpc(Long skuId) {
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
