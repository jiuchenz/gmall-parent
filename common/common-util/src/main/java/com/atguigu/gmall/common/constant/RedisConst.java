package com.atguigu.gmall.common.constant;


import lombok.Data;

@Data
public class RedisConst {
    public static final String SKU_BLOOM_FILTER_NAME = "bloom:skuid";//商品id布隆存储名
    public static final String SKU_INFO_LOCK_PREFIX = "lock:sku:info";//分布式锁名
    public static final long SKU_INFO_CACHE_TIMEOUT = 1000*60*60*24*7L;//商品信息缓存存活时间
    public static final String LOCK_PREFIX = "lock:";
    public static final String SKU_INFO_CACHE_KEY_PREFIX = "sku:info:";//sku商品缓存信息
    public static final String SKU_HOTSCORE = "hotscore:";
    public static final String USER_LOGIN_PREFIX = "user:login:";
    public static final String CART_INFO_PREFIX = "cart:info:";
    public static final Long CART_SIZE_LIMIT = 200L;
    public static final String SKU_PRICE_CACHE_PREFIX = "sku:price:";
}
