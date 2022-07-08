package com.atguigu.gmall.starter.cache.component.impl;

import com.atguigu.gmall.common.util.JSONs;
import com.atguigu.gmall.starter.cache.component.CacheService;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class CacheServiceImpl implements CacheService {

    @Autowired
    StringRedisTemplate redisTemplate;
    ScheduledExecutorService delayExecutor = Executors.newScheduledThreadPool(4);

    @Override
    public <T> T getDate(String cacheKey, Class<T> t) {
        String json = redisTemplate.opsForValue().get(cacheKey);
        if(StringUtils.isEmpty(json)){
            return null;
        }
        T t1 = JSONs.toObj(json, t);
        return t1;
    }


    @Override
    public <T> T getData(String cacheKey, TypeReference<T> t) {
        String json = redisTemplate.opsForValue().get(cacheKey);
        if(StringUtils.isEmpty(json)){
            return null;
        }
        T obj = JSONs.toObj(json, t);
        return obj;
    }

    @Override
    public <T> void saveDate(String cacheName, T Detail, Long CacheTimeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(cacheName,JSONs.toStr(Detail),CacheTimeout,timeUnit);
    }

    @Override
    public <T> void saveDate(String cacheName, T Detail) {
        redisTemplate.opsForValue().set(cacheName,JSONs.toStr(Detail));
    }

    @Override
    public void delayDoubleDelete(String cacheKey) {
        //延迟双删
        //1.立即删除
        redisTemplate.delete(cacheKey);
        //2.延迟删除
        delayExecutor.schedule(()->{
            redisTemplate.delete(cacheKey);
        },10,TimeUnit.SECONDS);
    }
}
