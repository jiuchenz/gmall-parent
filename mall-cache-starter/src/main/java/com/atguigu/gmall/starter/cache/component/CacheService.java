package com.atguigu.gmall.starter.cache.component;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.concurrent.TimeUnit;

public interface CacheService {
    <T> T getDate(String cacheKey, Class<T> t);

    <T> T getData(String cacheKey, TypeReference<T> t);

    <T>void saveDate(String cacheName, T detail, Long CacheTimeout, TimeUnit timeUnit);

    <T>void saveDate(String cacheName, T detail);
}
