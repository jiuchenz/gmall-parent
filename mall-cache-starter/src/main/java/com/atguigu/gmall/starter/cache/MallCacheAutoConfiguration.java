package com.atguigu.gmall.starter.cache;


import com.atguigu.gmall.starter.cache.apsect.CacheAspect;
import com.atguigu.gmall.starter.cache.component.CacheService;
import com.atguigu.gmall.starter.cache.component.impl.CacheServiceImpl;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(RedissonConfiguration.class)
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class MallCacheAutoConfiguration {

    @Bean
    public CacheService cacheService(){
        return new CacheServiceImpl();
    }

    @Bean
    public CacheAspect cacheAspect(){
        return new CacheAspect();
    }
}
