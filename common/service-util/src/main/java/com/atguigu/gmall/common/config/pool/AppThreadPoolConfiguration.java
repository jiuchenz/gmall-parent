package com.atguigu.gmall.common.config.pool;


import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
@EnableConfigurationProperties(ThreadPoolProperties.class)
public class AppThreadPoolConfiguration {


    @Bean
    public ThreadPoolExecutor threadPoolExecutor(ThreadPoolProperties properties){
        //1.核心线程池
        ThreadFactory threadFactory = new ThreadFactory() {
            int i = 0;
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("service-item-thread"+i++);
                return thread;
            }
        };
        return new ThreadPoolExecutor(properties.getCorePoolSize(),
                properties.getMaximumPoolSize(),
                properties.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(properties.getWorkQueueSize()),
                threadFactory,
                new ThreadPoolExecutor.CallerRunsPolicy());
    }
}
