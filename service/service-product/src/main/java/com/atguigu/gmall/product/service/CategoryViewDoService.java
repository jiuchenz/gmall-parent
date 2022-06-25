package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.dto.CategoryViewDo;
import com.atguigu.gmall.model.vo.CategoryView;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *
 */
public interface CategoryViewDoService extends IService<CategoryViewDo> {

    CategoryView getCategoryViewByC3Id(Long c3Id);
}
