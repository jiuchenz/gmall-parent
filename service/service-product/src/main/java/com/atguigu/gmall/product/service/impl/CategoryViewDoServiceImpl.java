package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.dto.CategoryViewDo;
import com.atguigu.gmall.model.vo.CategoryView;
import com.atguigu.gmall.product.mapper.CategoryViewDoMapper;
import com.atguigu.gmall.product.service.CategoryViewDoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class CategoryViewDoServiceImpl extends ServiceImpl<CategoryViewDoMapper, CategoryViewDo>
    implements CategoryViewDoService{

    @Autowired
    CategoryViewDoMapper categoryViewDoMapper;

    @Override
    public CategoryView getCategoryViewByC3Id(Long c3Id) {
        QueryWrapper<CategoryViewDo> wrapper = new QueryWrapper<>();
        wrapper.eq("bc3id" ,c3Id);
        CategoryViewDo categoryViewDo = categoryViewDoMapper.selectOne(wrapper);
        CategoryView view = new CategoryView();
        view.setCategory1Id(categoryViewDo.getId());
        view.setCategory1Name(categoryViewDo.getName());
        view.setCategory2Id(categoryViewDo.getBc2id());
        view.setCategory2Name(categoryViewDo.getBc2name());
        view.setCategory3Id(categoryViewDo.getBc3id());
        view.setCategory3Name(categoryViewDo.getBc3name());

        return view;
    }
}




