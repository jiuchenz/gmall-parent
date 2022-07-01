package com.atguigu.gmall.model.vo.search;


import lombok.Data;

@Data
public class SearchParam {
    private Long category1Id;
    private Long category2Id;
    private Long category3Id;
    private String keyword;
    private String trademark;
    private String order;
    private String[] props;
    private Long pageNo;
}
