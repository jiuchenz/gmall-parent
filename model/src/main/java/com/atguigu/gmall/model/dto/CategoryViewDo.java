package com.atguigu.gmall.model.dto;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("category_view")
public class CategoryViewDo {

    @TableField("id")
    private Long id;
    @TableField("name")
    private String name;
    @TableField("bc2id")
    private Long bc2id;
    @TableField("bc2name")
    private String bc2name;
    @TableField("bc3id")
    private Long bc3id;
    @TableField("bc3name")
    private String bc3name;
}
