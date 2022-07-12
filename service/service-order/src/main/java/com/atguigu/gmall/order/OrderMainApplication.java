package com.atguigu.gmall.order;


import com.atguigu.gmall.common.annotation.EnableAutoHandleException;
import com.atguigu.gmall.common.annotation.EnableFeignInterceptor;
import com.atguigu.gmall.common.annotation.EnableThreadPool;
import com.atguigu.gmall.common.config.AppMybatisPlusConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;


@Import(AppMybatisPlusConfig.class)
@EnableFeignClients(basePackages = {
        "com.atguigu.gmall.feign.user",
        "com.atguigu.gmall.feign.cart",
        "com.atguigu.gmall.feign.product",
        "com.atguigu.gmall.feign.ware"
})
@EnableAutoHandleException
@EnableRabbit
@EnableFeignInterceptor
@EnableThreadPool
@SpringCloudApplication
@MapperScan("com.atguigu.gmall.order.mapper")
public class OrderMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderMainApplication.class,args);
    }
}
