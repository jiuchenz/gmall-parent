package com.atguigu.gmall.front.controller;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.feign.cart.CartFeignClient;
import com.atguigu.gmall.model.vo.cart.AddSuccessVo;
import com.atguigu.gmall.model.vo.user.UserAuth;
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
        UserAuth userAuth = AuthContextHolder.getUserAuth();
        Long userId = userAuth.getUserId();
        String tempId = userAuth.getTempId();

        Result<AddSuccessVo> addSuccessVoResult = cartFeignClient.addCart(skuId, skuNum);
        AddSuccessVo vo = addSuccessVoResult.getData();
        model.addAttribute("skuInfo",vo);
        model.addAttribute("skuNum",skuNum);
        return "cart/addCart.html";
    }
}
