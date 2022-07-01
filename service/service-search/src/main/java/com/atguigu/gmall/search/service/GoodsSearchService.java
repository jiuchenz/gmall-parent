package com.atguigu.gmall.search.service;

import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchResponseVo;
import com.atguigu.gmall.model.vo.search.SearchParam;

public interface GoodsSearchService {
    void upGoods(Goods goods);

    void downGoods(Long skuId);

    SearchResponseVo search(SearchParam searchParam);
}
