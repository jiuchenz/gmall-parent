package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.product.SpuSaleAttrValue;
import com.atguigu.gmall.product.mapper.SpuInfoMapper;
import com.atguigu.gmall.product.service.SpuImageService;
import com.atguigu.gmall.product.service.SpuInfoService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import com.atguigu.gmall.product.service.SpuSaleAttrValueService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 */
@Service
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoMapper, SpuInfo>
    implements SpuInfoService{
    @Autowired
    SpuImageService spuImageService;
    @Autowired
    SpuSaleAttrService spuSaleAttrService;
    @Autowired
    SpuSaleAttrValueService spuSaleAttrValueService;


    @Override
    @Transactional
    public void saveSpuInfo(SpuInfo spuInfo) {
        //存储spuInfo信息
        save(spuInfo);
        //取出spuId
        Long spuId = spuInfo.getId();
        //存储spuImageList
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        for (SpuImage spuImage : spuImageList) {
            spuImage.setSpuId(spuId);
        }
        spuImageService.saveBatch(spuImageList);
        //存储spuSaleAttrList
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
            spuSaleAttr.setSpuId(spuId);
            spuSaleAttrService.save(spuSaleAttr);
            String saleAttrName = spuSaleAttr.getSaleAttrName();
            List<SpuSaleAttrValue> valueList = spuSaleAttr.getSpuSaleAttrValueList();
            for (SpuSaleAttrValue value : valueList) {
                value.setSpuId(spuId);
                value.setSaleAttrName(saleAttrName);
                spuSaleAttrValueService.save(value);
            }
        }
    }
}




