package com.atguigu.gmall.item;


import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringCloudApplication
@EnableFeignClients(basePackages = {"com.atguigu.gmall.feign.product","com.atguigu.gmall.feign.search"})
public class ItemMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(ItemMainApplication.class,args);
    }
}
