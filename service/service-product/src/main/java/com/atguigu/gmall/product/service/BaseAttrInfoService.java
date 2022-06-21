package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseAttrInfo;

import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 *
 */
public interface BaseAttrInfoService extends IService<BaseAttrInfo> {

    List<BaseAttrInfo> getArrInfoList(Long c1Id, Long c2Id, Long c3Id);

    void saveAttrInfo(BaseAttrInfo baseAttrInfo);

    void updateAttrInfo(BaseAttrInfo baseAttrInfo);
}
