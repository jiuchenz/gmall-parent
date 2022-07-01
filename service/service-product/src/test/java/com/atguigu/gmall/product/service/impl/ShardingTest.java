package com.atguigu.gmall.product.service.impl;


import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ShardingTest {
    @Autowired
    SkuInfoMapper skuInfoMapper;

    @Test
    void Test01(){
        for (int i = 0; i < 2; i++) {
            SkuInfo skuInfo = skuInfoMapper.selectById(49);
        }
    }
}
