package com.atguigu.gmall.search.rpc;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchResponseVo;
import com.atguigu.gmall.model.vo.search.SearchParam;
import com.atguigu.gmall.search.service.GoodsSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rpc/inner/search")
public class SearchController {

    @Autowired
    GoodsSearchService goodsSearchService;


    @PostMapping("/goods")
    public Result<SearchResponseVo> search(@RequestBody SearchParam searchParam){
        SearchResponseVo vo = goodsSearchService.search(searchParam);
        return Result.ok(vo);
    }

    @PostMapping("/upGoods")
    public Result upGoods(@RequestBody Goods goods){
        goodsSearchService.upGoods(goods);
        return Result.ok();
    }

    @GetMapping("/downGoods/{skuId}")
    public Result downGoods(@PathVariable("skuId") Long skuId){
        goodsSearchService.downGoods(skuId);
        return Result.ok();
    }



}
