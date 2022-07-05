package com.atguigu.gmall.front;


import com.atguigu.gmall.common.annotation.EnableFeignInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringCloudApplication
@EnableFeignClients(basePackages = "com.atguigu.gmall.feign")
@EnableFeignInterceptor
public class FrontMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(FrontMainApplication.class,args);
    }
}
