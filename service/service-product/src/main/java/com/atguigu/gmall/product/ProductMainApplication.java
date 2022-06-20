package com.atguigu.gmall.product;


import com.atguigu.gmall.common.config.Swagger2Config;
import com.atguigu.gmall.model.product.BaseCategory1;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.Import;
@Import(Swagger2Config.class)
@MapperScan(basePackages = "com.atguigu.gmall.product.mapper")
@SpringCloudApplication
public class ProductMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductMainApplication.class,args);
    }
}
