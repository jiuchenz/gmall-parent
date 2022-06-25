package com.atguigu.gmall.product.biz.impl;

import com.atguigu.gmall.model.vo.CategoryVo;
import com.atguigu.gmall.product.biz.CategoryBizService;
import com.atguigu.gmall.product.mapper.BaseCategory1Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryBizServiceImpl implements CategoryBizService {

    @Autowired
    BaseCategory1Mapper baseCategory1Mapper;

    @Override
    public List<CategoryVo> getCategorys() {
        List<CategoryVo> list = baseCategory1Mapper.getCategorys();
        return list;
    }
}
