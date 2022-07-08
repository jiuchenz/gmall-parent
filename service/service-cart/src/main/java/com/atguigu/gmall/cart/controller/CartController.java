package com.atguigu.gmall.cart.controller;


import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.cart.CartInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    CartService cartService;

    @GetMapping("/cartList")
    public Result cartList(){
        //1.查出当前用户购物车的所有数据
        List<CartInfo> infos = cartService.getCartAllItem();
        //异步更新价格
        return Result.ok(infos);
    }

    @PostMapping("/addToCart/{skuId}/{skuNum}")
    public Result addToCart(@PathVariable("skuId") Long skuId,
                            @PathVariable("skuNum") Integer skuNum){
        cartService.addCart(skuId,skuNum);
        return Result.ok();
    }

    @GetMapping("/checkCart/{skuId}/{status}")
    public Result checkCart(@PathVariable("skuId") Long skuId,
                            @PathVariable("status") Integer status){

        cartService.updateCartStatus(skuId,status);
        return Result.ok();
    }
    @DeleteMapping("/deleteCart/{skuId}")
    public Result deleteCartItem(@PathVariable("skuId") Long skuId){
        cartService.deleteCartItem(skuId);
        return Result.ok();
    }


}
