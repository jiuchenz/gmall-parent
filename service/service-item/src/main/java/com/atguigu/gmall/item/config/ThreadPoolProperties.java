package com.atguigu.gmall.item.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "app.threadpool")
@Data
@Component
public class ThreadPoolProperties {
    private int corePoolSize = 4;
    private int maximumPoolSize = 8;
    private long keepAliveTime = 60;
    //    private TimeUnit unit = TimeUnit.SECONDS;
    private int workQueueSize = 200;
}
