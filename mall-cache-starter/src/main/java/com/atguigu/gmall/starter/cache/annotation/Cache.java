package com.atguigu.gmall.starter.cache.annotation;


import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Cache {

    //缓存名
    String key() default "";
    //布隆过滤器名字
    String bloomName() default "";
    //布隆判定时用的值
    String bloomIf() default "";
    //默认缓存过期时间30分钟
    long ttl() default 1000*60*30L;



}
