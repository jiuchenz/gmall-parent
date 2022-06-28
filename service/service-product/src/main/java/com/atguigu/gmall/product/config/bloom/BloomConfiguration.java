package com.atguigu.gmall.product.config.bloom;


import com.atguigu.gmall.product.service.BloomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BloomConfiguration {
    @Autowired
    BloomService bloomService;

    @Bean
    public ApplicationRunner applicationRunner(){
        return new ApplicationRunner(){

            @Override
            public void run(ApplicationArguments args) throws Exception {
                bloomService.initBloom();
            }
        };
    }
}
