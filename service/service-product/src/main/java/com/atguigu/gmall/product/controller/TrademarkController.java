package com.atguigu.gmall.product.controller;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/admin/product")
@RestController
public class TrademarkController {

    @Autowired
    BaseTrademarkService baseTrademarkService;

    @GetMapping("/baseTrademark/{page}/{limit}")
    public Result getBaseTrademark(@PathVariable("page") Long page,
                                   @PathVariable("limit") Long limit){
        Page<BaseTrademark> p = new Page<>(page, limit);
        Page<BaseTrademark> result = baseTrademarkService.page(p);
        return Result.ok(result);
    }


    @PostMapping("/baseTrademark/save")
    public Result saveBaseTrademark(@RequestBody BaseTrademark baseTrademark){
        baseTrademarkService.save(baseTrademark);
        return Result.ok();
    }

    @PutMapping("/baseTrademark/update")
    public Result updateBaseTrademark(@RequestBody BaseTrademark baseTrademark){
        baseTrademarkService.updateById(baseTrademark);
        return Result.ok();
    }


    @DeleteMapping("/baseTrademark/remove/{id}")
    public Result deleteBaseTrademark(@PathVariable("id") Long id){
        baseTrademarkService.removeById(id);
        return Result.ok();
    }

    @GetMapping("/baseTrademark/get/{id}")
    public Result getBaseTrademark(@PathVariable("id") Long id){
        BaseTrademark baseTrademark = baseTrademarkService.getById(id);
        return Result.ok(baseTrademark);
    }

}
