package com.atguigu.gmall.product.biz.impl;

import com.atguigu.gmall.common.util.JSONs;
import com.atguigu.gmall.model.vo.ValueSkuVo;
import com.atguigu.gmall.product.biz.SpuAllSkuSaleValueService;
import com.atguigu.gmall.product.mapper.SkuSaleAttrValueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class SpuAllSkuSaleValueServiceImpl implements SpuAllSkuSaleValueService {

    @Autowired
    SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Override
    public String getSpuAllSkuSaleValueJson(Long spuId) {
        List<ValueSkuVo> valueSkuVoList = skuSaleAttrValueMapper.getSpuAllSkuSaleValueJson(spuId);
        HashMap<String, String> map = new HashMap<>();
        for (ValueSkuVo valueSkuVo : valueSkuVoList) {
            String skuId = valueSkuVo.getSkuId();
            String skuValues = valueSkuVo.getSkuValues();
            map.put(skuValues,skuId);
        }
        String json = JSONs.toStr(map);
        return json;
    }
}
