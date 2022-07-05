package com.atguigu.gmall.cart.rpc;


import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.vo.cart.AddSuccessVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rpc/inner/cart")
public class CartController {

    @Autowired
    CartService cartService;

    @GetMapping("/add/{skuId}")
    public Result<AddSuccessVo> addCart(@PathVariable("skuId") Long skuId,
                                        @RequestParam("num") Integer num){

        AddSuccessVo vo = cartService.addCart(skuId,num);

        return Result.ok(vo);
    }
}
