package com.atguigu.gmall.front.controller;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.cart.CartFeignClient;
import com.atguigu.gmall.model.vo.cart.AddSuccessVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CartController {

    @Autowired
    CartFeignClient cartFeignClient;


    @GetMapping("/addCart.html")
    public String addCart(@RequestParam("skuId") Long skuId,
                          @RequestParam("skuNum") Integer skuNum,
                          Model model){
        Result<AddSuccessVo> addSuccessVoResult = cartFeignClient.addCart(skuId, skuNum);
        AddSuccessVo vo = addSuccessVoResult.getData();
        model.addAttribute("skuInfo",vo);
        model.addAttribute("skuNum",skuNum);
        return "cart/addCart.html";
    }

    @GetMapping("/cart.html")
    public String cartList(){

        return "cart/index";
    }

    @GetMapping("/cart/deleteChecked")
    public String deleteChecked(){
        cartFeignClient.deleteChecked();
        return "cart/index";
    }
}
