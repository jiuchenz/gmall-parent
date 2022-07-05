package com.atguigu.gmall.item.rpc;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.service.ItemBizService;
import com.atguigu.gmall.model.vo.SkuDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rpc/inner/item")
public class ItemRpcController {

    @Autowired
    ItemBizService itemBizService;

    @GetMapping("/sku/{skuId}")
    public Result<SkuDetailVo> getSkuDetail(@PathVariable("skuId") Long skuId){
        SkuDetailVo skuDetailVo = itemBizService.getSkuDetail(skuId);
        itemBizService.incrHotScore(skuId);
        return Result.ok(skuDetailVo);
    }
}
