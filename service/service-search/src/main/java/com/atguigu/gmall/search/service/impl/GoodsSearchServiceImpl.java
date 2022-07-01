package com.atguigu.gmall.search.service.impl;

import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchResponseVo;
import com.atguigu.gmall.model.vo.search.SearchParam;
import com.atguigu.gmall.search.repo.GoodsRepositry;
import com.atguigu.gmall.search.service.GoodsSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GoodsSearchServiceImpl implements GoodsSearchService {

    @Autowired
    GoodsRepositry goodsRepositry;

    @Override
    public void upGoods(Goods goods) {
        goodsRepositry.save(goods);
    }

    @Override
    public void downGoods(Long skuId) {
        goodsRepositry.deleteById(skuId);
    }

    @Override
    public SearchResponseVo search(SearchParam searchParam) {
        //TODO 真正检索
        return null;
    }
}
