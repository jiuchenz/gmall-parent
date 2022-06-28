package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.product.service.BloomService;
import com.atguigu.gmall.product.service.SkuInfoService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class BloomServiceImpl implements BloomService {

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    SkuInfoService skuInfoService;


    @Override
    public void initBloom() {
        //获取bloom过滤器
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter(RedisConst.SKU_BLOOM_FILTER_NAME);
        if (bloomFilter.isExists()) {
            //过滤器存在
            log.info("{} 过滤器已经存在，跳过初始化",RedisConst.SKU_BLOOM_FILTER_NAME);
            return;
        }
        //过滤器不存在，初始化bloom
        bloomFilter.tryInit(1000000, 0.00001);
        //查询所有商品的id，存到bloom中
        List<Long> idsList =  skuInfoService.getSkuAllId();
        for (Long id : idsList) {
            bloomFilter.add(id);
        }
        //初始化完成
        log.info("{} 已经初始化完成，可以正常使用",RedisConst.SKU_BLOOM_FILTER_NAME);
    }

    @Override
    public void rebuildSkuBloom() {
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter(RedisConst.SKU_BLOOM_FILTER_NAME);
        bloomFilter.delete();
        initBloom();
    }
}
