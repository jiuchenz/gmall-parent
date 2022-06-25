package com.atguigu.gmall.model.vo;


import lombok.Data;

import java.util.List;

@Data
public class CategoryVo {
    private Long categoryId;    //1
    private String categoryName; //手机
    private List<CategoryVo> categoryChild;
}
