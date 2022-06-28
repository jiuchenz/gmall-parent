package com.atguigu.gmall.item.component;

import java.util.concurrent.TimeUnit;

public interface CacheService {
    <T>T getDate(String cacheName, Class<T> skuDetailVoClass);

    <T>void saveDate(String cacheName, T skuDetailFromRpc, Long CacheTimeout, TimeUnit timeUnit);

    <T>void saveDate(String cacheName, T skuDetailFromRpc);
}
