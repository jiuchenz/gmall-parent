package com.atguigu.gmall.starter.cache.apsect;


import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.util.DateUtil;
import com.atguigu.gmall.starter.cache.annotation.Cache;
import com.atguigu.gmall.starter.cache.component.CacheService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Order
public class CacheAspect {

    @Autowired
    CacheService cacheService;

    @Autowired
    RedissonClient redissonClient;

    @Around("@annotation(com.atguigu.gmall.starter.cache.annotation.Cache)")
    public Object doBasicProfiling(ProceedingJoinPoint pjp) throws Throwable {
        //获取目标方法传入参数
        Object[] args = pjp.getArgs();
        //定义切面返回值
        Object retValue = null;
        try {
            //前置通知
            //1.查询目标方法缓存名cacheKey
            String cacheKey = calculateCacheKey(pjp);
            //2.查询缓存中是否有缓存内容
            Object cacheDate = cacheService.getData(cacheKey, new TypeReference<Object>() {
                @Override
                public Type getType() {
                    //从pjp中获取方法签名
                    MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
                    //从方法签名中获取方法，从方法中获取返回值类型
                    return methodSignature.getMethod().getGenericReturnType();
                }
            });
            //3.缓存命中
            if (cacheDate != null) {
                return cacheDate;
            }
            //4.缓存不命中
            //4.1从注解参数中获取bloom名
            Cache cacheAnnotation = getCacheAnnotation(pjp, Cache.class);
            if (StringUtils.isEmpty(cacheAnnotation.bloomName())) {
                //4.1.1如果bloomName为空，不使用bloom过滤器，直接加锁回调
                return getDateWithLock(pjp, args, cacheKey);
            } else {
                //4.2如果bloomName不为空，使用bloom过滤器
                RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter(cacheAnnotation.bloomName());
                //4.2.1获取bloomIf的值进行判断
                Object bloomIfValue = getBloomIfValue(pjp);
                if (bloomFilter.contains(bloomIfValue)) {
                    //布隆说有，加锁回调
                    return getDateWithLock(pjp, args, cacheKey);
                } else {
                    //布隆说没有
                    return null;
                }
            }
        } catch (Exception e) {
            //异常通知
            log.info("切面炸了..{}", e);
            throw new RuntimeException(e);
        } finally {
            //后置通知
        }
    }

    //获取bloomIf的值
    private Object getBloomIfValue(ProceedingJoinPoint pjp) {
        //拿到目标方法上的cache
        Cache cacheAnnotation = getCacheAnnotation(pjp,Cache.class);
        //获取表达式
        String bloomIf = cacheAnnotation.bloomIf();
        //计算表达式
        Object spElValue = calculateExpressuib(bloomIf,pjp);
        return spElValue;
    }

    //查询目标方法缓存名cacheKey
    private String calculateCacheKey(ProceedingJoinPoint pjp) {
        //拿到目标方法上的cache
        Cache cacheAnnotation = getCacheAnnotation(pjp,Cache.class);
        //获取表达式
        String key = cacheAnnotation.key();
        //计算表达式
        String spElValue = calculateExpressuib(key,pjp).toString();
        return spElValue;
    }

    //计算表达式
    private Object calculateExpressuib(String expressionStr, ProceedingJoinPoint pjp) {
        //拿到一个表达式解析器
        SpelExpressionParser parser = new SpelExpressionParser();
        //1.得到一个表达式
        Expression expression = parser.parseExpression(expressionStr, new TemplateParserContext());
        //2.准备一个计算上下文
        EvaluationContext context = new StandardEvaluationContext();
        //支持的所有语法
        context.setVariable("params",pjp.getArgs());//所有的参数列表
        context.setVariable("currentDate", DateUtil.formatDate(new Date()));
        context.setVariable("redisson",redissonClient);  //指向一个组件，可以无限调方法
        Object value = expression.getValue(context, Object.class);
        return value;
    }

    //拿到目标方法上的cache
    private <T extends Annotation> T getCacheAnnotation(ProceedingJoinPoint pjp, Class<T> tclass) {
        //从pjp中获取方法签名
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        //从方法签名中获取目标方法
        Method method = methodSignature.getMethod();
        //从方法中获取缓存注解
        T cache = method.getDeclaredAnnotation(tclass);
        return cache;
    }

    //加锁回调目标方法
    private Object getDateWithLock(ProceedingJoinPoint pjp, Object[] args, String cacheKey) throws Throwable {
        Object retValue;
        //获取锁名
        String lockKey = RedisConst.LOCK_PREFIX+cacheKey;
        //获取锁
        RLock lock = redissonClient.getLock(lockKey);
        //加锁
        boolean tryLock = lock.tryLock();
        //得到锁后，执行回调
        if (tryLock){
            //获取锁，回源
            retValue = pjp.proceed(args);//回源，执行目标方法
            //将数据放入缓存中
            //获取缓存过期时间
            Cache cacheAnnotation = getCacheAnnotation(pjp,Cache.class);
            cacheService.saveDate(cacheKey,retValue,cacheAnnotation.ttl(), TimeUnit.MILLISECONDS);
            //解锁
            lock.unlock();
            //返回数据
            return retValue;
        }
        //没得到锁，等待一段时间，查询缓存
        TimeUnit.MILLISECONDS.sleep(300);
        retValue = cacheService.getData(cacheKey, new TypeReference<Object>() {
            @Override
            public Type getType() {
                //从pjp中获取方法签名
                MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
                //从方法签名中获取方法，从方法中获取返回值类型
                return methodSignature.getMethod().getGenericReturnType();
            }
        });
        return retValue;
    }
}
